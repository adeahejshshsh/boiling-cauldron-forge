# Accept syscall shim（Android 上跑 JVM 构建工具）

## 问题

Android 应用进程的 seccomp 策略**拦截了老式 `accept(2)`**（arm64 系统调用号 202），
但放行 `accept4(2)`（242）。表现为：

```
java.io.IOException: Function not implemented
    at java.base/sun.nio.ch.Net.accept(Native Method)
```

任何需要监听本地 TCP 端口的 JVM 工具都会挂掉，最典型的就是 **Gradle**：

```
Could not connect to the Gradle daemon.
```

（Gradle 4.0 起即使 `--no-daemon` 也会 fork 一个 single-use daemon，并通过本地 TCP 回连。）

反证：同一台设备上 Node 的 `net.createServer` 工作正常——libuv 用的是 `accept4`。

## 解决

用 `LD_PRELOAD` 把 `accept()` 转发到 `accept4()`，绕开被拦截的 syscall。

```bash
gcc -shared -fPIC -O2 -o libacceptfix.so accept_shim.c
LD_PRELOAD=/path/to/libacceptfix.so ./gradlew build
```

## 验证

```java
ServerSocket ss = new ServerSocket(0, 50, InetAddress.getByName("127.0.0.1"));
ss.accept();
```

| 条件 | 结果 |
|---|---|
| 不加 shim | `IOException: Function not implemented` |
| 加 shim | `OK` |

Gradle 侧实测：

```
不加 shim → Could not connect to the Gradle daemon.
加 shim   → BUILD SUCCESSFUL
```

## 注意

- 只影响 `accept()`；`connect()`、`listen()` 本来就正常，不需要处理。
- 与 bionilux 无关：socket 直接交给宿主内核，兼容层的 `LD_PRELOAD` 钩的是 libc 公开函数，
  管不到系统调用层。
- 如果宿主环境已经自带类似的 shim，不要重复注入。

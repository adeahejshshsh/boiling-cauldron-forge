# 发布辅助脚本

用于把构建产物上传到 CurseForge。**不包含任何令牌**，令牌通过环境变量传入。

## 为什么需要手动创建项目

CurseForge 的 [Upload API](https://support.curseforge.com/support/solutions/articles/9000197321-curseforge-upload-api)
只提供以下能力：

| 能力 | 接口 |
|---|---|
| 上传文件到**已存在**的项目 | `POST /api/projects/{projectId}/upload-file` |
| 修改已有文件 | `POST /api/projects/{projectId}/update-file` |
| 查询游戏版本 / 依赖 | `GET /api/game/versions`、`/api/game/dependencies` |

**没有"创建项目"的接口**，项目必须先在
[authors.curseforge.com](https://authors.curseforge.com/) 网页端手动创建。

## 使用步骤

```bash
# 1) 构建
./gradlew build

# 2) 设置令牌与项目 ID（仅当前 shell 有效，不会写入仓库）
export CURSEFORGE_TOKEN='你的 API Token'
export PROJECT_ID='123456'

# 3) 上传
./release/curseforge-upload.sh
```

脚本会：

1. 调用 `/api/game/versions` 解析 `1.20.1` 与 `Forge` 的版本 ID，并打印出来供人工确认
2. 用 `release/release-notes-1.0.0-forge.md` 生成 metadata
3. 声明 `kaleidoscope-cookery` 为必需前置
4. `POST` 上传 jar，打印返回的文件 ID

## 安全注意

- **不要把 `CURSEFORGE_TOKEN` 写进脚本、`.env` 或提交记录**
- 上传完成后可执行 `unset CURSEFORGE_TOKEN`
- 令牌泄露请立即到 [API Tokens](https://authors.curseforge.com/#/settings/api-tokens) 吊销

## 其他文件

| 文件 | 用途 |
|---|---|
| `project-page.md` | 创建项目时要填的简介与描述文案 |
| `release-notes-1.0.0-forge.md` | 上传时作为 changelog 的内容 |
| `resolve_versions.py` | 版本 ID 解析器（被主脚本调用） |

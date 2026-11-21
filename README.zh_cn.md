# 沸鼎烟火（Boiling Cauldron）· NeoForge 版

以**麻辣烫**与**钵钵鸡**为主题的 Minecraft 烹饪模组。
本仓库是 **NeoForge 1.21.1** 版本。

> 最初的 Fabric 版本由 [Xkbsx413](https://github.com/Xkbsx413/boiling-cauldron) 创作。
> Forge 1.20.1 移植在 `main` 分支；本分支是 NeoForge 1.21.1 移植。

[English](README.md)

---

## 环境要求

| 项目 | 版本 |
|---|---|
| Minecraft | 1.21.1 |
| NeoForge | 21.1.0 或更新（开发使用 21.1.267） |
| Java | 21 |
| 必需前置 | [森罗厨房](https://modrinth.com/mod/kaleidoscope-cookery) `1.0.1+`（NeoForge 版） |

JEI 为**可选**：安装后会出现「钵钵鸡浸泡」配方分类，不装也能正常游玩。

---

## 内容

### 食材

- **四种作物**：姜、蒜、贡菜、花椒（4–5 个生长阶段）。
- **冬笋**：六个阶段；成熟后对其使用骨粉会转化为原版竹子。
- **竹根殖块**：定时生成冬笋作物或竹荪菌落，最多同时存在 5 个存活产物。
- **盛花荷叶**：在沼泽与红树林沼泽自然生成；成熟后会把下方的泥土、沙子或砂砾转化为藏藕泥块。
- **藏藕泥块**：手持锹右键获得 3 个莲藕；挖掘掉落 1–4 个。
- **繁茂草丛**：在多种生物群系生成；35% 概率掉落随机作物种子。

种子可通过村庄箱子（按村庄类型区分，另有 8% 的「调料包」池）、流浪商人或繁茂草丛获得。

### 底料与钵钵鸡

1. 用森罗厨房的**炒锅**烹饪三种汤底：蘑菇、麻辣、藤椒。
2. 放置**钵钵鸡锅**（8 个红砖 + 1 个碗）。
3. 用水桶注水，再放入一份汤底（可用 64 次浸泡）。
4. 把食物放进界面；每格独立浸泡 60 秒。
5. 取出即得**钵钵鸡**；食用后附加底料对应的药水效果。

口味、次数、浸泡时长、产出物品与效果**完全由数据包配方驱动**，可自由扩展。

---

## 自定义配方（数据包）

配方类型：`boiling-cauldron:bobo_soak`

```json
{
  "type": "boiling-cauldron:bobo_soak",
  "base": { "item": "boiling-cauldron:mushroom_base" },
  "flavor": {
    "id": "mushroom",
    "name": { "translate": "tooltip.boiling-cauldron.bobo_chicken.flavor.mushroom" },
    "sort": 0,
    "color": 13073214,
    "effects": [
      { "id": "minecraft:regeneration", "duration": 600, "amplifier": 0 }
    ]
  },
  "result": { "id": "boiling-cauldron:bobo_chicken", "count": 1 },
  "soak_time": 1200,
  "uses": 64
}
```

| 字段 | 说明 |
|---|---|
| `base` | 原料；支持 `item` 或 `tag` |
| `flavor.id` | 唯一口味 id，存于物品的 `CUSTOM_DATA` 组件 |
| `flavor.name` | 显示名：`{"translate": ...}`、`{"text": ...}` 或纯字符串 |
| `flavor.sort` | 多口味合并时的排序（数值小的在前） |
| `flavor.color` | 界面中底料名称的颜色，十进制 RGB |
| `flavor.effects` | 任意数量的药水效果 |
| `soak_time` | 可选，默认 1200 tick（60 秒） |
| `uses` | 可选，默认 64 |

> **1.21 格式差异** —— 配方内部不再带 `id`；物品堆改用 `{"id": ..., "count": ...}`
> 而非 `{"item": ...}`；药水效果用 `id` 而非 `effect` 引用。

内置三份配方位于
`src/generated/resources/data/boiling-cauldron/recipe/bobo_soak/`。

---

## 构建

```bash
./gradlew build
```

产物：`build/libs/boiling-cauldron-1.0.1-neoforge.jar`

### 数据生成

```bash
./gradlew runData
```

输出到 `src/generated/resources`，该目录已提交，并通过
`sourceSets.main.resources { srcDir 'src/generated/resources' }` 纳入构建。

> **注意**：`runData` 会启动 Minecraft，因此会校验必需前置。
> 本地未安装森罗厨房时，请先临时移除 `src/main/resources/META-INF/neoforge.mods.toml`
> 中的 `kaleidoscope_cookery` 条目。

### 开发运行

```bash
./gradlew runClient
./gradlew runServer
```

---

## 工程结构

```
├── build.gradle                 构建脚本（ModDevGradle 2.0.148）
├── gradle.properties            版本与模组元数据（仅 ASCII）
├── src/main/java/               源码（40 个类）
│   └── com/adeahejshshsh/boilingcauldron/
│       ├── block/               方块
│       │   └── entity/          方块实体
│       ├── item/                物品与口味档案
│       ├── recipe/              自定义配方 bobo_soak（Codec 驱动）
│       ├── screen/              钵钵鸡锅 GUI
│       ├── datagen/             NeoForge 数据生成（8 个 Provider）
│       ├── loot/ trade/ tag/    战利品注入、交易、标签
│       └── compat/jei/          可选 JEI 19 兼容
├── src/main/resources/          手写资源（贴图、模型、neoforge.mods.toml）
├── src/generated/resources/     数据生成输出（已提交）
├── tools/accept-shim/           Android/Termux 构建绕过方案
└── docs/                        设计与移植文档
```

---

## 文档

- [设计文档](docs/design.md) —— 最终玩法规则
- [NeoForge 移植笔记](docs/neoforge-porting.md) —— 1.20.1 → 1.21.1 API 变更
- [源码分析](docs/source-analysis.md) —— 原 Fabric 版本分析

---

## 许可

双授权：

| 范围 | 许可 | 文件 |
|---|---|---|
| 源代码、构建脚本、文档 | **MIT** | [LICENSE](LICENSE) |
| 游戏资源（贴图、模型、语言文件、配方、战利品表、标签、世界生成） | **CC0-1.0** | [LICENSE-ASSETS](LICENSE-ASSETS) |

文件级划分见 [LICENSING.md](LICENSING.md)，署名见 [NOTICE](NOTICE)。

第三方内容（森罗厨房、Minecraft、NeoForge）不在上述许可范围内。

## 致谢

- 原作者：[Xkbsx413](https://github.com/Xkbsx413/boiling-cauldron) 与画师们
- Forge 1.20.1 移植与 NeoForge 1.21.1 移植：[adeahejshshsh](https://github.com/adeahejshshsh)

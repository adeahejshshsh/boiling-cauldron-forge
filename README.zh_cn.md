# 沸鼎烟火（Boiling Cauldron）· Forge 版

> 本文为中文备份，英文版见 [English](README.md)

以 **麻辣烫 / 钵钵鸡** 为主题的 Minecraft 烹饪模组，本仓库是 **Forge 1.20.1** 版本。

> 原始 Fabric 版本由 [Xkbsx413](https://github.com/Xkbsx413/boiling-cauldron) 开发，本仓库为其 Forge 移植。

---

## 环境要求

| 项目 | 版本 |
|---|---|
| Minecraft | 1.20.1 |
| Forge | 47.3.0 及以上（开发使用 47.4.0） |
| Java | 17 |
| 前置模组 | [森罗厨房 Kaleidoscope Cookery](https://modrinth.com/mod/kaleidoscope-cookery) `1.0.1-forge+mc1.20.1` |

JEI 为**可选**兼容：安装后可在 JEI 中查看「钵钵鸡浸泡」配方；未安装不影响模组运行。

---

## 玩法内容

### 食材链

- **四种作物**：生姜、大蒜、贡菜、花椒（4~5 阶段随机生长）
- **冬笋**：6 阶段，成熟后可用骨粉转为原版竹子
- **竹根殖块**：定时生成冬笋作物或竹荪菌落，最多同时存在 5 个产物
- **盛花荷叶**：沼泽 / 红树林沼泽自然生成，成熟后把下方泥土 / 沙子 / 砂砾转为藏藕泥块
- **藏藕泥块**：铲子右键取 3 个莲藕，普通挖掘掉 1~4 个
- **繁茂草丛**：多种群系生成，35% 概率掉落随机作物种子

种子获取途径：村庄箱子（按村庄类型主题投放 + 8% 调料包）、流浪商人交易、繁茂草丛掉落。

### 底料与钵钵鸡

1. 用森罗厨房的**炒锅**把材料炒成三种底料：菌菇底料、麻辣底料、藤椒底料
2. 放下**钵钵鸡锅**（8 红砖 + 1 碗）
3. 水桶加水 → 放入底料（提供 64 次浸泡额度）
4. 从 GUI 放入食物，每格独立浸泡 60 秒
5. 取出得到**钵钵鸡**，食用获得对应口味效果

口味、可用次数、浸泡时长、产物、效果**全部由数据包配方驱动**，可自行扩展。

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
    "color": "#C77B3E",
    "effects": [
      { "effect": "minecraft:regeneration", "duration": 600, "amplifier": 0 }
    ]
  },
  "result": { "item": "boiling-cauldron:bobo_chicken" },
  "soak_time": 1200,
  "uses": 64
}
```

| 字段 | 说明 |
|---|---|
| `base` | Ingredient，支持 `item` 或 `tag` |
| `flavor.id` | 口味唯一 id，写入成品 NBT |
| `flavor.name` | 显示名，支持 `{"translate":...}` / `{"text":...}` / 纯字符串 |
| `flavor.sort` | 多口味时的排列顺序，越小越靠前 |
| `flavor.color` | GUI 中底料名颜色，`#RRGGBB` 或十进制 |
| `flavor.effects` | 任意数量药水效果 |
| `soak_time` | 可选，默认 1200 tick（60 秒） |
| `uses` | 可选，默认 64 次 |

内置 3 条配方位于 `src/generated/resources/data/boiling-cauldron/recipes/bobo_soak/`。

---

## 构建

```bash
./gradlew build
```

产物：`build/libs/boiling-cauldron-1.0.0-forge.jar`

### 数据生成

```bash
./gradlew runData
```

输出到 `src/generated/resources`，该目录已随仓库提交，并通过
`sourceSets.main.resources { srcDir 'src/generated/resources' }` 参与打包。

> **注意**：`runData` 会启动 Minecraft 引导流程并校验强制依赖。
> 若本地未安装森罗厨房，需临时注释 `src/main/resources/META-INF/mods.toml`
> 中的 `kaleidoscope_cookery` 依赖项后再运行。

### 开发运行

```bash
./gradlew runClient
./gradlew runServer
```

---

## 项目结构

```
├── build.gradle                 构建脚本（ForgeGradle 6）
├── gradle.properties            版本与模组元数据
├── src/main/java/               源码（41 个类）
│   └── com/boilingcauldron/
│       ├── block/               方块与方块实体
│       ├── item/                物品、口味档案
│       ├── recipe/              自定义配方类型 bobo_soak
│       ├── screen/              钵钵鸡锅 GUI
│       ├── datagen/             Forge 数据生成（8 个 Provider）
│       ├── loot/ trade/ tag/    战利品注入、交易、标签
│       └── compat/jei/          JEI 兼容（可选）
├── src/main/resources/          手写资源（贴图、模型、mods.toml）
├── src/generated/resources/     数据生成输出（已提交）
└── docs/                        设计与移植文档
```

---

## 文档

- [设计文档](docs/design.md)：玩法规则定稿
- [移植报告](docs/porting-report.md)：Fabric → Forge 的对照、验证记录与已知限制
- [源码分析](docs/source-analysis.md)：对原始 Fabric 版本的分析

---

## 许可证

本项目采用**双授权**：

| 范围 | 协议 | 文件 |
|---|---|---|
| 源代码、构建脚本、文档 | **MIT** | [LICENSE](LICENSE) |
| 游戏资源（贴图、模型、语言、配方、掉落表、标签、世界生成） | **CC0-1.0** | [LICENSE-ASSETS](LICENSE-ASSETS) |

划分细则见 [LICENSING.md](LICENSING.md)。

第三方内容（森罗厨房 / Minecraft / Forge）不在此授权范围内。

## 致谢

- 原作者 [Xkbsx413](https://github.com/Xkbsx413/boiling-cauldron) 及美术
- Forge 移植：[adeahejshshsh](https://github.com/adeahejshshsh)
- [森罗厨房 Kaleidoscope Cookery](https://github.com/KaleidoscopeMods/KaleidoscopeCookery) 团队

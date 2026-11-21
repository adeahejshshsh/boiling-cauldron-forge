# 沸鼎烟火（boiling-cauldron）源码分析报告

> 分析对象：https://github.com/Xkbsx413/boiling-cauldron
> 拉取位置：`repo/`，分支 `main`，提交 `88c8fbb`（2026-11-08，*feat: 正式更新1.0.0 完善目前已有功能 麻辣烫作为后续更新添加*）
> 分析方式：全量源码通读 + 资源/数据静态校验脚本（未执行 Gradle 构建，原因见 §6.3）

---

## 1. 项目定位

一个 **Minecraft 1.20.1 / Fabric** 的烹饪模组，主题是川渝小吃 **麻辣烫 / 钵钵鸡**。
核心循环被设计为三段：

```
种植/采集食材  →  森罗厨房炒锅做「底料」  →  钵钵鸡锅冷泡出成品
```

| 项目 | 值 |
|---|---|
| mod id | `boiling-cauldron` |
| 版本 | `1.0.0-1.20.1` |
| 加载器 / API | Fabric Loader 0.19.3+，Fabric API 0.92.11+1.20.1 |
| 强制前置 | 森罗厨房 Kaleidoscope Cookery `1.0.1-fabric+mc1.20.1`（`fabric.mod.json` 里是硬依赖） |
| 构建 | Gradle 9.5.1 + Loom `1.17-SNAPSHOT`（`fabric-loom-remap` 新插件 id），`release=17` |
| 许可 | CC0-1.0 |
| 规模 | Java 3,753 行 / 39 个类；生成数据 97 个 JSON；手写资源 8 个 JSON + 51 张 PNG |

值得注意的是：**仓库只有一个提交**。CHANGELOG 里引用的 `7a1fd7d` / `f311ce0` / `83cffde` 等历史提交并不存在，说明这是一次 squash 或重新发布，无法用 git 追溯演进过程（但 `docs/development-log.md` 用文字补上了这段历史，共 530 行）。

---

## 2. 架构分层

```
com.boilingcauldron
├─ BoilingCauldron            主入口：按序触发方块/物品/BE/GUI/战利品/交易/世界生成注册
├─ BoilingCauldronClient      客户端入口：cutout 渲染层 + GUI 绑定
├─ BoilingCauldronDataGenerator  datagen 入口（6 个 provider）
│
├─ block/         11 个方块 + 5 个 BE 相关注册
│   ├─ ModBlocks / ModCropBlocks（四作物共用 4 阶段实现）
│   ├─ SichuanPepperCropBlock（独立 5 阶段 + 右键采摘）
│   ├─ BambooShootCropBlock（6 阶段 + 成熟骨粉转原版竹子）
│   ├─ BambooRootBlock(+BE) / BloomingLotusLeafBlock(+BE) / LotusMudBlock
│   ├─ BambooFungusColonyBlock / LushGrassBlock
│   └─ BoboChickenPotBlock(+BE)
├─ item/          食材、种子（SeedItem）、三种底料、BoboChickenItem、BoboFlavor、创造栏
├─ screen/        钵钵鸡锅 ScreenHandler / Screen / 注册
├─ datagen/       Model / Chinese / English / LootTable / Recipe / ItemTag / WorldGenerator
├─ worldgen/      两个 placed feature 的 key + BiomeModifications 注入
├─ loot/          村庄箱子奖池注入（LootTableEvents.MODIFY）
├─ trade/         流浪商人种子交易
└─ mixin/         ExampleMixin（模板残留，空实现）
```

分层是清晰的：**注册层 / 内容层 / 玩法层 / 数据层 / 集成层**边界分明，每个文件头部都有统一的「文件用途 + 更新日期」注释，可读性明显高于一般个人模组。

---

## 3. 核心系统拆解

### 3.1 内容获取链（解决"生存模式断链"）

作者在 `docs/development-log.md` 里明确诊断过一个设计缺陷：种子只能由作物合成、作物只能由种子种植 → **新世界生存模式无法获得第一批种子**。P1 用三条并行入口解决：

1. **村庄箱子**：按村庄类型主题投放（平原→大蒜、热带草原→生姜…），再加 5 种民居通用的 8% "调料包"奖池。
2. **流浪商人**：4 种种子各 1 绿宝石 / 库存 2，作为保底。
3. **繁茂草丛**（`lush_grass`）：35% 概率掉随机种子，作为野外来源。

实现细节到位：`ModLootTableModifiers` 用 `LootTableEvents.MODIFY` **追加**奖池，并用 `source.isBuiltin()` 保护数据包覆盖，符合"不覆盖他人战利品表"的工程规范。

其他材料链也闭环了：
- 竹根殖块：每 1200 tick 有 55% 基础成功率（附近竹子每格 +1%，上限 +20%）生成冬笋作物或竹荪菌落，最多同时存在 5 个产物，产物位置写 NBT、被采走后计数回落。
- 盛花荷叶：5 阶段生长，成熟后每 600 tick 尝试把下方 2~4 格的泥土/沙子/砂砾转为藏藕泥块；骨粉可在同层 5×5 扩散。
- 藏藕泥块：普通挖掉 1~4 莲藕，铲子右键变泥巴固定给 3 莲藕，精准采集/锄头掉方块本身。

### 3.2 森罗厨房集成

- 3 个 `kaleidoscope_cookery:pot` 炒锅配方由 datagen 生成，配方里显式 `throw IllegalStateException` 处理 `oil_block` 缺失 —— 比静默失败好。
- 把自己的材料 + 原版蘑菇标签 + 森罗油脂块注入 `kaleidoscope_cookery:pot_ingredient`。
- 依赖 `c:crops/chilipepper` 约定标签来吃森罗的辣椒（红/青皆可）。

### 3.3 钵钵鸡锅（项目最复杂的部分，约 1,000 行）

**状态机**：`空 → 水 → 底料(64 次) → 浸泡中 → 成品`，另有"冲洗"和"耗尽"两条边。

**数据模型**（`BoboChickenPotBlockEntity`）：

| 字段 | 含义 |
|---|---|
| `DefaultedList<ItemStack> items` (16) | 每格 1 个食物 |
| `int[16] progress` | 每格独立 1200 tick 计时，`-1` = 空 |
| `hasWater` / `baseFlavor` / `baseUses` | 水、底料类型、剩余次数 |

**计费模型**是这里最微妙的地方：扣 1 次发生在「物品进入槽位」时，而不是完成时。为此作者在 `BoboChickenPotScreenHandler` 里做了一个 `lastTakenFromPot` / `movingWithinPot` 的启发式状态机，用来区分"玩家把锅里的东西挪到锅里另一格"（不重复计费）和"从背包放入"（计费）。这是整个项目里最容易出边界 bug 的一段代码。

**同步**：服务端 `updateListeners` 推 `BlockEntityUpdateS2CPacket`，进度按 10 tick 节流；客户端 GUI 直接读客户端 BE 的字段画进度条（不走 `ScreenHandler` 属性同步）。区块卸载即停止 tick，天然实现"暂停计时"。

**防刷设计**（有意识做的）：
- `SidedInventory` 的 `getAvailableSlots` 返回空数组 → 漏斗等自动化完全禁用。
- `readNbt` 对 `BaseUses`、`Progress` 做 clamp；`baseFlavor` 解析失败则清零次数。
- 取出/移动未完成食物 → 进度清零且不返还次数。
- 破坏方块 → 掉落锅 + 全部内容物，状态清零。

**成品**（`BoboChickenItem`）：统一物品 + NBT（`OriginalItem`、最多 3 个 `Flavors`），按枚举 ordinal 固定顺序生成 1/2/3 味名称，回锅可追加口味（同味/满 3 味拒绝），食用时施加 `生命恢复 I / 力量 I / 抗性提升 I`（30 秒），并复刻原料 `FoodComponent` 的概率状态效果。效果刷新规则按"剩余 <30s 刷新、≥30s 不动、不降级"实现。

---

## 4. 与设计文档的一致性

`docs/design.md`（154 行）把钵钵鸡规则定到了"每格 60 秒""1 底料 = 64 次""冲洗条件""NBT 字段名""效果刷新规则"的粒度。我逐条对照了实现：

- 16 格 / 60 秒 / 64 次 / 冲洗 / 耗尽后水消失但已付费食物继续泡 / 16 格全空才能重新加水 —— **全部一致**。
- 显示名组合顺序、tooltip、标签、回锅规则 —— **一致**。
- 未实现项（文档里也标了未完成）：音效、粒子、比较器输出、正式模型贴图。

也就是说，**设计文档不是事后补的装饰，而是实现的对齐依据**，这一点在个人模组里相当罕见，是本项目最突出的优点。

---

## 5. 静态校验结果（实际执行）

我写脚本对全部资源做了交叉校验：

| 检查项 | 结果 |
|---|---|
| JSON 语法（105 个文件） | ✅ 0 错误 |
| blockstate → 引用的模型是否存在 | ✅ 0 缺失 |
| 模型 → parent / textures 是否存在 | ✅ 0 缺失 |
| 语言文件键值对齐（zh_cn / en_us 各 43 键） | ✅ 完全对齐 |
| 方块数 vs 掉落表数 | ❌ **11 个方块只有 10 个掉落表** |
| 未被引用的贴图 | ⚠️ 2 张（`block/blooming_lotus_leaf.png`、`item/lotus_root_seeds.png`） |

---

## 6. 问题清单（按严重度）

### 6.1 P0 — 确定的功能缺陷

**① `bamboo_root` 没有掉落表 → 挖掉不掉落任何东西**

`ModBlocks` 注册了 11 个方块，`ModLootTableProvider.generate()` 只生成了 10 个掉落表，唯独漏了 `BAMBOO_ROOT`：

```java
// ModLootTableProvider.generate() 中不存在这一行
this.addDrop(ModBlocks.BAMBOO_ROOT);
```

MC 1.20.1 对缺失的掉落表返回 `LootTable.EMPTY`，所以玩家辛苦合成（8 竹子 + 1 泥土）并摆下的竹根殖块，**一挖就永久消失**。它是创造栏和配方里都有的物品，属于纯丢失性 bug。
修复：`ModLootTableProvider` 里补 `this.addDrop(ModBlocks.BAMBOO_ROOT);` 并重跑 datagen。

**② 四个种子物品模型疑似"陈旧产物"**

`src/main/generated/.../models/item/` 下有 `ginger_seeds` / `garlic_seeds` / `gong_cai_seeds` / `sichuan_pepper_seeds` 四个模型，但 `Model.generateItemModels()` 里**没有任何一行注册它们**（注释写的是"种子为 BlockItem，自动从方块模型生成物品模型，无需手动注册"）。

而从生成结果看，这个假设不成立：作物方块没有 BlockItem，若 `registerCrop` 真的会生成物品模型，产物应该落在 `assets/minecraft/models/item/air.json`——该文件不存在；其余所有 BlockItem（`bamboo_root`、`lotus_mud`、`bamboo_fungus_colony`…）都是**逐个显式注册**的。

结论：这四个 JSON 很可能来自更早版本中曾被删除的 `itemModelGenerator.register(...)` 调用。当前 jar 能用（文件已提交），但**重跑 datagen 时若触发清理，四个种子会变成缺失模型（紫黑方块）**。
建议：在 `generateItemModels()` 显式补回四行 `itemModelGenerator.register(...)`，并重跑一次 datagen 验证文件被重新产出。

### 6.2 P1 — 一致性与性能

**③ 作物方块缺声音/地图色**
`ModBlocks.cropSettings()` 只设了 `noCollision / ticksRandomly / breakInstantly / pistonBehavior / nonOpaque`，没设 `.sounds()` 和 `.mapColor()`。`Settings` 默认是 `BlockSoundGroup.STONE` 和 `MapColor.CLEAR`，所以 5 种作物破坏时是**石头声**，在地图上不显示颜色。

**④ 花椒可以种在泥土/草方块/沙子上，其余作物只能种在耕地上**
`SichuanPepperCropBlock.canPlaceAt` 为了去掉光照要求，把整个方法重写成只判断地基：

```java
return blockState.isOf(Blocks.FARMLAND) || blockState.isOf(Blocks.DIRT)
    || blockState.isOf(Blocks.GRASS_BLOCK) || blockState.isOf(Blocks.SAND);
```

副作用是花椒成为唯一不需要耕地的作物，与其他三种（`canPlantOnTop` 只允许 `FARMLAND`）不一致。如果是有意设计应写进文档，否则应改为"只去掉光照判断、保留耕地要求"。

**⑤ 竹根殖块 BE 每 tick 做全量扫描**
`BambooRootBlockEntity.tick` 每 tick 都执行 `pruneSpawned()`（最多 5 次 `getBlockState`）+ `syncCount()`（1 次 `getBlockState`），而真正的业务逻辑 1200 tick 才跑一次。单个方块影响不大，但竹根殖块是"可批量摆放"的农场方块，几十个就是每 tick 数百次方块查询。建议加 20 tick 节流。

**⑥ 竹根殖块 BE 从不 `markDirty()`**
`ticks` 和 `spawnedPositions` 的变更没有标记脏，只有 `syncCount` 恰好触发 `setBlockState` 时才会连带保存。重进存档后繁殖计时可能回退。另外 `syncCount` 用的是 `flag=3`（`NOTIFY_ALL`），在 BE 自己的 tick 里触发邻居更新，用 `NOTIFY_LISTENERS` 更合适。

### 6.3 P2 — 工程质量与可维护性

**⑦ 依赖不可复现**：`loom_version=1.17-SNAPSHOT` 是移动靶；`maven.modrinth:kaleidoscope-cookery:1.0.1-fabric+mc1.20.1` 没有 checksum。同一份源码在不同时间可能构建出不同结果。

**⑧ 死代码 / 死资源**：
- `mixin/ExampleMixin.java` 是 Fabric 模板残留，注入 `MinecraftServer.loadWorld` 但方法体是空的，还在 `mixins.json` 里注册着。
- `ModTags.KALEIDOSCOPE_OIL` 标注了 `@SuppressWarnings("unused")`，没有任何使用点。
- 2 张未被引用的贴图（见 §5），其中 `lotus_root_seeds.png` 对应的物品根本不存在。

**⑨ 命名风格不统一**：`ModItems.Ginger`、`ModBlocks.GINGER_CROP`、`ModItemGroups.RegisterGroups()` 混用 PascalCase / SCREAMING_CASE；`ModFoodComponents.Ginger` 与物品字段同名易混。

**⑩ 无自动化测试**：CI（`.github/workflows/build.yml`）只跑 `./gradlew build`，没有任何单元测试或游戏内测试。而本项目最容易出错的部分（BE 状态同步、计费状态机、NBT 迁移）恰恰是最需要测试的。§6.1 的两个 bug 都是"编译通过但运行错"的类型。

**⑪ 无法追溯的 git 历史**：单提交仓库，无法 review 演进、无法二分定位回归。

**⑫ P3 麻辣烫完全空白**：`docs/design.md` 第 3 节只有 4 条待定项（热源判定、食材范围、盛取方式、食用效果），代码里没有任何对应实现。README 也如实标注了"麻辣烫等玩法仍在设计中"。

### 6.4 细微逻辑边角（不构成刷物品，但语义不严）

- **底料耗尽后仍可"免费重泡"已付费食物**：`canAcceptFood`/`startSoak` 在 `movingWithinPot` 时不校验 `baseUses > 0`，所以在底料归零后，把锅里一个**已扣过次数、尚未完成**的普通食物挪到另一格，会免费重新开始计时。物品总量守恒，不能复制，但"已付费"这个状态实际上没有落在物品上，而是靠 UI 交互推断的。
- `BoboChickenPotBlock.onUse` 在客户端无条件返回 `SUCCESS`，即使 BE 不存在也不返回 `PASS`，会让客户端产生一次错误的挥手动画。
- 客户端 `BoboChickenPotScreenHandler` 直接持有 `BlockEntity` 引用（而非独立的槽位同步容器）。区块重载导致 BE 实例被替换时，GUI 里的进度和状态会冻结到关闭为止。
- 花椒作物右键采摘与掉落表都能产出果实（采摘 1~2 个且回退到阶段 1，挖掉 1~2 个 + 1~3 种子），两条路径的收益关系没有文档说明。

---

## 7. 结论

**这是一份完成度明显高于"个人练手模组"平均水平的代码。**

优点集中在三点：
1. **设计先行**：`design.md` 把玩法规则定到可实现的粒度，实现后再逐条回填"已实现/待实现"，`development-log.md` 甚至记录了"为什么放弃野外地物方案、改用村庄箱子+流浪商人"的决策过程。
2. **数据驱动**：模型、方块状态、掉落表、配方、标签、双语翻译、世界生成全部走 Fabric datagen，`src/main/generated` 随代码提交，资源引用零缺失（我用脚本验证过）。
3. **有防御意识**：服务端权威校验、NBT clamp、禁用漏斗自动化（主动规避复制漏洞）、战利品表用追加而非替换、缺前置时显式抛异常。

短板集中在"最后一公里"：
- 两个 **P0 级资源缺口**（`bamboo_root` 掉落表、种子物品模型来源），都属于"编译通过但玩家会吃亏"的类型；
- 一批 **P1 级手感/性能问题**（作物石头声、花椒种植不一致、竹根殖块每 tick 全扫）；
- 工程侧缺少测试、依赖版本未钉死、git 历史被 squash。

如果把 §6.1 的两条修掉，并补上 `docs/design.md` 里列出的音效/粒子/比较器，这个 1.0.0 就基本站得住了；之后再动 P3 麻辣烫，最好先补几条针对 `BoboChickenPotBlockEntity` 的状态机测试——因为那 370 行是整个项目风险最集中的地方。

---

*注：本次分析未执行 Gradle 构建。当前 Linux 环境未安装 JDK，且 Loom 需要联网拉取 Minecraft 与森罗厨房依赖（数百 MB），故采用"全量源码通读 + 资源/数据交叉校验"的方式完成静态分析；构建结论以 CHANGELOG 中记录的历史构建结果为准，未独立复现。*

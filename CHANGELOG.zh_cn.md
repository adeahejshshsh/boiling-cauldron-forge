# 变更日志

> 本文为中文备份，英文版见 [English](CHANGELOG.md)

## 1.0.4-neoforge（2026-11-28）

移植上游 **1.0.1**（麻辣烫更新）。

### 新增

- **麻辣烫锅** —— 16 格热煮容器。正下方必须有热源（营火、灵魂营火、火、灵魂火、
  岩浆块，或点燃的熔炉）。每份食材煮 **20 秒**，煮好后留在锅内等待盛取。
- **麻辣烫** —— 手持空碗右击煮好的食材起一份，之后可继续右击最多装入 **4** 种食材。
  饱食度、饱和度与药水效果逐份累加；吃完返还碗。
- **幻色五香粉** —— 与钵钵鸡或麻辣烫合成，可隐藏成品提示里的原料列表。
- 新配方类型 `boiling-cauldron:mask_dish`（Codec 驱动，无状态）。

### 重构

- 钵钵鸡锅与麻辣烫锅改为共用 `AbstractSeasoningPotBlock`、
  `AbstractSeasoningPotBlockEntity` 与 `AbstractSeasoningPotMenu`（与上游一致）。
  钵钵鸡锅保持 60 秒浸泡，麻辣烫锅固定 20 秒。

### 说明

- 上游用枚举存口味；本移植保留数据包驱动的 `FlavorProfile`，
  底料口味通过 `bobo_soak` 配方解析。
- 物品数据仍存于 `DataComponents.CUSTOM_DATA`，与 1.0.3 一致。

## 1.0.3-neoforge（2026-11-22）

### 新增

- **JEI「去哪里找」说明页** —— 9 条自然生成/野外获取途径说明：
  - 繁茂草丛、盛花荷叶（自然生成的两个方块）
  - 藏藕泥块、竹根殖块、竹荪菌落（由世界机制衍生）
  - 冬笋、莲藕、竹荪（产物）
  - 四种种子（合并为一条，说明草丛掉落 / 村庄箱子 / 流浪商人三种途径）

  文案中的群系列表与概率均取自实际 worldgen 与战利品数据，不是估计值。

### 修复

- **自然生成完全不生效** —— `biome_modifier` 沿用了 Forge 1.20.1 的路径与命名空间：
  - 路径 `data/boiling-cauldron/forge/biome_modifier/` → 应为 `neoforge/biome_modifier/`
  - 类型 `forge:add_features` → 应为 `neoforge:add_features`

  NeoForge 1.20.2+ 已把自身注册表命名空间从 `forge:` 改为 `neoforge:`，
  旧路径不会被加载。结果是**繁茂草丛与盛花荷叶在 NeoForge 版里从未自然生成过**。
- **森罗厨房辣椒标签** —— `forge:crops/chilipepper` → `c:crops/chilipepper`。
  NeoForge 的通用标签用 `c:` 命名空间，旧写法会让麻辣底料配方失效。

## 1.0.2-neoforge（2026-11-22）

### 新增

- **Jade 支持** —— 三个功能方块现在会在 Jade 提示中显示状态：
  - 竹根殖块：当前存活产物数 / 5
  - 盛花荷叶：生长阶段 / 4，成熟时提示正在转化下方方块
  - 钵钵鸡锅：水量、底料名称、剩余次数、正在浸泡的格数
- Jade 为**可选**依赖；未安装时提示不显示，模组功能不受影响

## 1.0.1-neoforge（2026-11-21）

### 修复

- **钵钵鸡锅界面中物品悬浮提示不显示** —— `AbstractContainerScreen` 本身不渲染 tooltip，
  需要每个容器界面在自己的 `render` 里调用 `renderTooltip`；原版 `ContainerScreen` 就是这么做的。
  现在鼠标悬浮槽位会显示物品名称与口味信息，与背包界面一致。
- **三个方块没有出现在创造模式物品栏** —— `bamboo_root`、`blooming_lotus_leaf`、
  `bobo_chicken_pot` 已注册但未加入创造栏。现在与 Forge 版本一致，共 21 项。

## 1.0.0-neoforge（2026-11-21）

首个 NeoForge 1.21.1 版本，基于 Forge 1.20.1 移植。

### 移植

- 构建体系从 ForgeGradle 6 换到 ModDevGradle 2.0.148，Java 17 升到 Java 21
- 元数据从 `mods.toml` 换到 `neoforge.mods.toml`
- 物品 NBT 改为**数据组件** —— 口味存于 `DataComponents.CUSTOM_DATA`
- 配方改为 Codec 驱动：`MapCodec` + `StreamCodec`，配方内部不再带 `ResourceLocation` id
- `MobEffect` 改为 `Holder<MobEffect>`；`FoodProperties#saturationMod` 更名为 `saturationModifier`
- 方块实体改用 `saveAdditional(CompoundTag, HolderLookup.Provider)` /
  `loadAdditional(CompoundTag, HolderLookup.Provider)`
- `Block#use` 改为 `BlockBehaviour#useItemOn(ItemStack, ...)`，返回 `ItemInteractionResult`
- 菜单通过 `IMenuTypeExtension` 注册；界面通过 `RegisterMenuScreensEvent` 注册
- 数据目录改为 1.21 的单数形式：`recipe`、`loot_table`、`advancement`、`tags/item`
- JEI 从 15.x 升级到 19.71

### 新增

- **数据生成** —— 8 个 Provider，产出 95 个文件
- **JEI 兼容** —— 浸泡分类直接读取 `RecipeManager` 中的配方
- **战利品注入与商人交易** —— 村庄箱子种子池、流浪商人种子交易

### 已知限制

- 森罗炒锅配方引用 `kaleidoscope_cookery:pot`，未安装该模组的 NeoForge 版本时会跳过
- 麻辣烫未实现
- 音效、粒子、比较器输出未实现

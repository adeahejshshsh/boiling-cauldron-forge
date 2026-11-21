# 变更日志

> 本文为中文备份，英文版见 [English](CHANGELOG.md)

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

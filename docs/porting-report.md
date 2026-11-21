# 沸鼎烟火 · Forge 1.20.1 移植报告

移植目标：Fabric 1.20.1 → **Forge 1.20.1（47.4.0）**
产物：`boiling-cauldron-1.0.0-forge.jar`（157 KB，已验证可构建）
工程目录：`forge/`

---

## 1. 先说资料核实结论

按你"不要瞎猜、查官方手册"的要求，动工前我做了资料核查，结论如下。

### 1.1 依赖模组：森罗厨房有 Forge 1.20.1 官方版本

| 项目 | 结论 |
|---|---|
| 版本 | `1.0.1-forge+mc1.20.1` 存在，与 Fabric 版 `1.0.1-fabric+mc1.20.1` **同版本号** |
| 来源 | Modrinth 官方发布（[版本页](https://modrinth.com/mod/kaleidoscope-cookery/version/1.0.1-forge+mc1.20.1)） |
| 官方仓库 | [KaleidoscopeMods/KaleidoscopeCookery](https://github.com/KaleidoscopeMods/KaleidoscopeCookery)，但**只有** `1.20.1-fabric` / `1.21.1-fabric` / `1.21.1-neoforge` 分支，**没有 `1.20.1-forge` 分支** |
| 官方 API 手册 | **不存在**（README 只有功能介绍与许可，无 API 文档/wiki） |
| 实际依据 | 官方发布的 **sources jar**（`kaleidoscopecookery-1.0.1-forge+mc1.20.1-sources.jar`），我逐项读了真实源码 |

从官方 sources 核实到的事实（不是推测）：

- 配方类型 `kaleidoscope_cookery:pot` 在 Forge 版同样存在（`ModRecipes.POT_RECIPE`）
- 配料标签 `kaleidoscope_cookery:pot_ingredient` 存在（`TagItem.POT_INGREDIENT`）
- 油脂块 `kaleidoscope_cookery:oil_block` 存在（`ModBlocks.OIL_BLOCK` / `ModItems.OIL_BLOCK`）
- `PotRecipeBuilder` **在二进制 jar 里也有**（36 个 datagen 类），datagen 可以直接用
- **关键差异**：Fabric 版辣椒用 `c:crops/chilipepper`，Forge 版用 **`forge:crops/chilipepper`**
  （`TagCommon.itemTag()` 写死 `new ResourceLocation("forge", name)`）

> 这一条正是"不能猜"的典型：如果照搬 `c:` 命名空间，麻辣底料的炒锅配方会**静默失效**——游戏里只表现为"炒不出来"，控制台不报错。

### 1.2 Forge 官方手册：存在，但有缺口

[Forge 1.20.1 官方文档](https://docs.minecraftforge.net/en/1.20.1/) 在线可用。我把它的源仓库 [MinecraftForge/Documentation 的 `1.20.1` 分支](https://github.com/MinecraftForge/Documentation/tree/1.20.1) 完整拉下来核对，共 **70 篇**：

- ✅ 有：Registries / DeferredRegister、Menus（`NetworkHooks`、`IForgeMenuType`）、Datagen、Access Transformers、Loot Tables、Global Loot Modifiers、Block Entities、Render Types、`ItemBlockRenderTypes`
- ❌ **完全没有**：`BiomeModifier`（世界生成注入）、`VillagerTradesEvent` / `WandererTradesEvent`（交易）、`ComposterBlock`（堆肥）
  （文档站搜索索引 567 页零命中；官方 MDK 也无示例）

经你确认，这三块改为**以 Forge 官方源码为准**（`forge-1.20.1-47.4.0-sources.jar`），逐类阅读实现，而不是凭印象写。核实到的写法：

- 生物群系注入 JSON 格式（来自 `ForgeBiomeModifiers.AddFeaturesBiomeModifier` 的 javadoc）：
  ```json
  { "type": "forge:add_features", "biomes": [...], "features": "...", "step": "vegetal_decoration" }
  ```
- 流浪商人用 `WandererTradesEvent`（**不是** `VillagerTradesEvent`，后者是职业村民），
  `BasicItemListing(ItemStack price, ItemStack forSale, int maxTrades, int xp, float priceMult)`
- Forge **没有**堆肥 API —— 因此这部分**未移植**（见 §5 已知差异）

---

## 2. 移植对照（Fabric → Forge）

| 能力 | Fabric | Forge 1.20.1 |
|---|---|---|
| 入口 | `ModInitializer` | `@Mod` 构造器 + `FMLJavaModLoadingContext` |
| 注册 | `Registry.register` | `DeferredRegister` + `RegistryObject` |
| 方块实体 | `FabricBlockEntityTypeBuilder` | `BlockEntityType.Builder.of(...).build(null)` |
| 方块基类 | `BlockWithEntity` | `BaseEntityBlock` |
| 打开 GUI | `ExtendedScreenHandlerFactory` | `SimpleMenuProvider` + `NetworkHooks.openScreen(...)` |
| 菜单类型 | `ExtendedScreenHandlerType` | `IForgeMenuType.create(...)` |
| 屏幕注册 | `HandledScreens.register` | `MenuScreens.register`（`FMLClientSetupEvent`） |
| 渲染层 | `BlockRenderLayerMap` | 模型 JSON 的 `"render_type": "minecraft:cutout"`（官方推荐做法） |
| 容器 | `SidedInventory` | `WorldlyContainer` |
| 战利品注入 | `LootTableEvents.MODIFY` | `LootTableLoadEvent` |
| 商人交易 | `TradeOfferHelper` | `WandererTradesEvent` |
| 世界生成 | `BiomeModifications.addFeature` | `forge:add_features` biome_modifier JSON |
| 堆肥 | `CompostingChanceRegistry` | ❌ 无对应 API |
| 数据生成 | Fabric datagen | 未移植，改为静态资源（见 §4） |

---

## 3. 代码结构

28 个 Java 文件 / 2,840 行：

```
com.boilingcauldron
├─ BoilingCauldron            @Mod 入口
├─ BoilingCauldronClient      客户端（注册 GUI 屏幕）
├─ block/
│   ├─ ModBlocks              11 个方块 + BlockItem（DeferredRegister）
│   ├─ ModCropBlocks          四作物共用 4 阶段
│   ├─ SichuanPepperCropBlock 5 阶段 + 右键采摘
│   ├─ BambooShootCropBlock   6 阶段 + 骨粉转竹子
│   ├─ BambooRootBlock / BambooFungusColonyBlock / LushGrassBlock
│   ├─ BloomingLotusLeafBlock / LotusMudBlock / BoboChickenPotBlock
│   └─ entity/  3 个 BlockEntity + 注册
├─ item/       ModItems / SeedItem / ModFoods / BoboChickenItem / BoboFlavor / ModCreativeTabs
├─ screen/     ModMenus / BoboChickenPotMenu / BoboChickenPotScreen
├─ loot/       ModLootTableModifiers（村庄箱子）
├─ trade/      ModTradeOffers（流浪商人）
└─ tag/        ModTags
```

数据：106 个 JSON + 53 张 PNG，全部放在 `src/main/resources`（不再有 `generated` 目录）。

---

## 4. 关于数据生成（datagen）

**没有移植 datagen**，把原 `src/main/generated` 的产物直接作为静态资源放进 `resources`。

理由：
1. 这些 JSON 是 1.20.1 的数据格式，与加载器无关，原样可用；
2. Forge datagen 需要重写 7 个 Provider（`RecipeProvider` / `BlockLootSubProvider` / `ModelProvider` / `LanguageProvider` / `ItemTagsProvider` / `DatapackBuiltinEntriesProvider`…），每个都是 Forge 专有 API，风险与工作量都高；
3. 静态资源可以被直接校验（见 §6），不依赖"跑一遍 datagen 才知道对不对"。

需要改数据生成时，仍可后续补 Forge datagen；`build.gradle` 里没有删除相关能力。

---

## 5. 已知差异（务必知悉）

1. **堆肥未移植**：竹荪菌落（0.65）、繁茂草丛（0.3）在 Forge 版不可堆肥。Forge 1.20.1 没有堆肥注册 API，唯一正规做法是对 `ComposterBlock.COMPOSTABLES` 做 Access Transformer——但 AT 对 MC 类**必须用 SRG 名**（官方文档明确写了），我无法在不实际运行的环境下确证该字段的 SRG 名，为避免瞎猜，本次先不做。
2. **辣椒标签已改**为 `forge:crops/chilipepper`（这是修正，不是差异，但不改会坏）。
3. **移除了 `ExampleMixin`**：原项目里它是 Fabric 模板残留，注入 `MinecraftServer.loadWorld` 但方法体为空，无任何作用。
4. **渲染层改用模型 JSON**：官方文档明确写"自 1.19 起优先用 `render_type` 而非 `ItemBlockRenderTypes#setLayer`"，因此 31 个植物/锅模型加了 `"render_type": "minecraft:cutout"`。
5. 创造了 `ModFoods` / `ModCreativeTabs` / `ModMenus` 三个类，替代原来的 `ModFoodComponents` / `ModItemGroups` / `ModScreenHandlers` 命名（Forge 惯例）。

---

## 6. 顺带修掉的两个 P0 缺陷（你已确认）

| 缺陷 | 处理 |
|---|---|
| `bamboo_root` 缺掉落表 → 挖掉即消失 | 新增 `data/boiling-cauldron/loot_tables/blocks/bamboo_root.json` |
| 四个种子物品模型是"陈旧产物" | 直接以静态资源交付 `models/item/*_seeds.json`，不依赖 datagen 重新生成 |

校验结果：**11 个方块 ↔ 11 个掉落表，已对齐**。

---

## 7. 构建验证（真实执行，非声明）

在 Android/aarch64 上完整跑通 ForgeGradle 构建：

```
BUILD SUCCESSFUL
build/libs/boiling-cauldron-1.0.0-forge.jar   157 KB
```

构建过程中解决的环境问题（仅供复现参考）：
- Debian proot 下 Gradle daemon 的 `accept()` 返回 `ENOSYS`，改用 **Termux 原生 JDK 17 + Gradle** 构建；
- Termux JDK 缺 `cacerts`，补入标准信任库；
- `java.io.tmpdir` 指向不存在的路径，显式指定。

编译期间修正的真实错误（均由编译器反馈，非猜测）：
- `LilyPadBlock` → 实际类名 **`WaterlilyBlock`**
- `RandomChanceCondition` → 实际类名 **`LootItemRandomChanceCondition`**
- `BlockEntity.pos` → 实际字段 **`worldPosition`**（`pos` 在 mojmap 下不存在）
- `saveAdditional(CompoundTag, Provider)` → 1.20.1 只有 **`saveAdditional(CompoundTag)`**
- `LilyPadBlock` 子类无法用 `createTickerHelper`（那是 `BaseEntityBlock` 的保护方法）→ 改显式强转

资源校验（脚本）：**106 个 JSON 全部可解析，blockstate→模型→parent→纹理零缺失，中英语言键对齐**。

---

## 8. 怎么用

```bash
cd forge
# 需要 JDK 17
./gradlew build          # 产物在 build/libs/
./gradlew runClient      # 启动客户端
./gradlew runServer      # 启动服务端
```

运行前置：Forge 47.3.0+、Minecraft 1.20.1、**Kaleidoscope Cookery `1.0.1-forge+mc1.20.1`**（`mods.toml` 已声明为强制依赖）。

---

## 9. 还没做的

- 堆肥（见 §5.1）
- 麻辣烫（P3）：原项目本身也未实现，本次严格按 1.0.0 内容移植
- 游戏内实机测试：构建通过 ≠ 玩法正确。钵钵鸡锅的 16 格计时、底料扣次、NBT 持久化这些仍需进游戏验证

---

## 附：冒烟测试记录（2026-11-21 补）

### 发现并修复的 P0 启动崩溃

Forge 对 modId 有强校验：`ModInfo.VALID_MODID = ^[a-z][a-z0-9_]{1,63}$` —— **不允许连字符**。
不匹配时输出 `FATAL` 日志并 `throw InvalidModFileException`，游戏**直接崩溃**。
这是编译期完全发现不了的（原 Fabric mod id `boiling-cauldron` 在 Forge 下非法）。

修复方式（零资源改动）：

| 常量 | 值 | 用途 |
|---|---|---|
| `MOD_ID` | `boilingcauldron` | `@Mod`、`@EventBusSubscriber`（必须合规） |
| `NAMESPACE` | `boiling-cauldron` | `DeferredRegister`、`ResourceLocation`（命名空间允许连字符 `^[a-z][a-z0-9_.-]{1,63}$`） |

`mods.toml` 增加 `namespace = "boiling-cauldron"`，使 106 个资源文件路径、全部翻译键、标签与配方 ID 保持不变。

### 服务端实机验证

用 `./gradlew runServer` 实际启动专用服务器：

```
[modloading-worker-0/INFO] [boilingcauldron/]: 沸鼎烟火（Boiling Cauldron）Forge 版初始化完成
Auto-subscribing com.boilingcauldron.loot.ModLootTableModifiers to FORGE
Auto-subscribing com.boilingcauldron.trade.ModTradeOffers to FORGE
[Server thread/INFO]: Done (29.465s)! For help, type "help"
```

已验证：modId 校验通过、`DeferredRegister` 全部注册无报错、事件订阅注入成功、
数据包加载完成（`forge:biome_modifier_serializers` 冻结正常）、主世界生成完成（`Done`）。

日志中的 3 条 `Invalid or unsupported recipe type 'kaleidoscope_cookery:pot'` 是**预期结果**——
本次隔离测试刻意移除了森罗厨房前置，只为验证本模组自身；正式环境装上前置后这三条会消失。

> 注：带森罗厨房一起跑时，会崩在 `kaleidoscope_cookery.mixins.json:LivingEntityMixin`
> （`m_6041_()` 找不到目标）。这是**森罗自己的 mixin 在开发环境不兼容**，与本模组无关；
> 正式游玩请用发布版 jar，不要用 dev 环境跑。

---

## 附二：可发现性补强（2026-11-21）

### 背景

底料（菌菇/麻辣/藤椒）在 JEI 里查不到任何用途，物品也没有 tooltip，
玩家拿在手里无从得知它只能用于钵钵鸡锅。已确认原因：
底料的消费是 `BoboChickenPotBlock#use` 里的硬编码方块交互，**不是配方**，
所以 JEI 无法索引；10 个配方里底料只作为产物出现。

### 改动一：操作提示（actionbar）

`BoboChickenPotBlock` 在交互失败时给出具体原因，不再静默：

| 情形 | 提示 |
|---|---|
| 空锅直接放底料 | 需要先用水桶加水 |
| 已有底料时再放底料 | 锅里已有底料（剩余 N 次） |
| 锅里有食物 | 锅里有食物，请先取空 |
| 已有水再加满水桶 | 锅里已有水 |
| 打开空锅 GUI | 空锅：先用水桶加水，再放入底料 |
| 成功加水 / 加底料 / 冲洗 | 对应确认提示 |

新增 8 个翻译键，中英各 54 键，完全对齐。

### 改动二：JEI 展示

新增 `com.boilingcauldron.compat.jei` 包（3 个类）：

- `BoboChickenJeiPlugin` — `@JeiPlugin` 入口，注册分类、配方、信息页、催化剂
- `BoboSoakCategory` — 自定义分类「钵钵鸡浸泡」，布局为 底料 + 食物 → 钵钵鸡，并绘制「浸泡 60 秒」
- `BoboSoakRecipe` — 展示用数据载体（按口味生成 3 条配方）

效果：JEI 中按 `U` 查询底料或钵钵鸡锅，能看到完整产出关系；底料另有信息页说明 64 次额度。

**依赖策略**：JEI 为 `compileOnly`（`fg.deobf("mezz.jei:jei-1.20.1-forge:15.75.0.239")`），
不打进 jar、不写进 `mods.toml`。未安装 JEI 时这些类不会被加载。

### 验证

```
BUILD SUCCESSFUL
jar 内含 com/boilingcauldron/compat/jei/*.class（3 个），不含 mezz/jei/*
服务端（无 JEI、无森罗）启动：Done (11.595s)!，无 JEI 相关类加载错误
```

> 未验证项：JEI 分类的实际视觉效果需要客户端才能确认，本次只验证了编译通过与"无 JEI 不崩"。

---

## 附三：自定义配方类型（数据包驱动，2026-11-21）

原来「底料 → 钵钵鸡」的关系硬编码在 `BoboChickenPotBlockEntity` 里（枚举 → 固定产物）。
本次改为**注册自定义配方类型**，所有数值由数据包 JSON 决定。

### 配方类型

| 项目 | 值 |
|---|---|
| 类型 id | `boiling-cauldron:bobo_soak` |
| 序列化器 | `boiling-cauldron:bobo_soak` |
| 实现类 | `recipe/BoboSoakRecipe`、`recipe/ModRecipes`、`recipe/BoboSoakRecipeJson` |

### JSON 格式

```json
{
  "type": "boiling-cauldron:bobo_soak",
  "base":    { "item": "boiling-cauldron:mushroom_base" },
  "flavor":  "mushroom",
  "result":  { "item": "boiling-cauldron:bobo_chicken" },
  "soak_time": 1200,
  "uses": 64
}
```

- `base`：Ingredient，支持 `item` 或 `tag`
- `flavor`：`mushroom` / `mala` / `rattan_pepper`，决定成品名称与食用效果
- `soak_time`：可选，默认 1200 tick（60 秒）
- `uses`：可选，默认 64 次

内置 3 条：`data/boiling-cauldron/recipes/bobo_soak/{mushroom,mala,rattan_pepper}.json`

### 代码改动

- 新增 `ModRecipes`：`DeferredRegister` 注册 `RecipeType` 与 `RecipeSerializer`
- `BoboChickenPotBlockEntity`：删除 `BASE_USES` 硬编码，新增 `soakTicks` / `resultItem` 字段，
  `tryAddBase(BoboSoakRecipe)` 从配方取口味/次数/时长/产物；tick 与进度条改用实例 `soakTicks`
- `BoboChickenPotBlock`：用 `level.getRecipeManager().getRecipeFor(...)` 匹配手持底料
- `BoboSoakCategory` / `BoboChickenJeiPlugin`：JEI 直接读 `RecipeManager`，不再枚举 3 种口味
- 新增提示键 `pot.no_recipe`：底料存在但数据包缺配方时明确报错

### 验证

```
BUILD SUCCESSFUL
服务端启动：Done (9.879s)!
bobo_soak 解析错误数：0
```

并用"阳性对照"证明序列化器确实被调用——临时放入一条错误配方：

```
Parsing error loading recipe boiling-cauldron:bobo_soak/_probe_invalid
JsonSyntaxException: Recipe '...' uses unknown flavor 'not_a_real_flavor';
  at com.boilingcauldron.recipe.BoboSoakRecipeJson.read(BoboSoakRecipeJson.java:40)
  at com.boilingcauldron.recipe.ModRecipes$BoboSoakRecipeSerializer.fromJson(ModRecipes.java:45)
```

探针已删除，最终 jar 不含该文件。
同一次运行中剩余的 3 条解析错误全部来自 `kaleidoscope_cookery:pot`（本次测试未装森罗前置，属预期）。

---

## 附四：口味完全数据化 + JEI 食物槽（2026-11-21）

### ① 口味改为数据包定义

删除了 `BoboFlavor` 枚举，改为 `FlavorProfile` 记录（id / 显示名 / 排序 / 颜色 / 效果列表），
所有字段写进物品 NBT，因此**数据包新增任何口味都无需改代码**。

新的 recipe JSON 结构：

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

要点：

- `name` 支持 `{"translate": ...}`、`{"text": ...}` 或纯字符串
- `color` 支持 `"#RRGGBB"` 或十进制整数，用于 GUI 底料名着色
- `effects` 可以是任意数量、任意原版/模组药水效果
- 显示名改为**动态拼接**：各口味名按 `sort` 排序后连接（中文空串、英文 `-`），再接 `suffix` 键；
  满 3 种仍显示「三味钵钵鸡 / Three-Flavor Bobo Chicken」

新增语言键：`item.boiling-cauldron.bobo_chicken.suffix`、`.joiner`。

### ② JEI 食物槽

**`forge:food` 与 `forge:foods` 在 Forge 1.20.1 都不存在**——已核实
`data/forge/tags/items/` 下只有 armors / crops / dusts / ores 等，没有任何 food 标签。
因此改为在 JEI 中枚举 `ForgeRegistries.ITEMS` 里所有 `isEdible()` 的物品，
与方块侧 `stack.isEdible()` 的判定完全一致，语义上等价于"任意食物"，且不会漏。

### 验证

```
BUILD SUCCESSFUL
服务端启动：Done (10.475s)!
3 条真实配方解析错误：0
```

阳性对照（临时探针，已删除）：

```
Parsing error loading recipe boiling-cauldron:bobo_soak/_probe_bad_effect
JsonSyntaxException: Recipe '...' references unknown effect 'minecraft:does_not_exist'
  at com.boilingcauldron.recipe.BoboSoakRecipeJson.readFlavor(BoboSoakRecipeJson.java:104)
  at com.boilingcauldron.recipe.ModRecipes$BoboSoakRecipeSerializer.fromJson(ModRecipes.java:45)
```

证明新 schema 的 flavor 对象与 effects 校验确实生效。

---

## 附五：Forge 数据生成（2026-11-21）

### 已实现

`com.boilingcauldron.datagen` 共 8 个类，由 `GatherDataEvent` 统一调度：

| Provider | 产出 | 数量 |
|---|---|---:|
| `ModBlockStateProvider` | blockstate + 方块模型 | 11 + 27 |
| `ModItemModelProvider` | 物品模型 | 21 |
| `ModLanguageProvider` | zh_cn / en_us | 2 |
| `ModBlockLootProvider` | 方块掉落表 | 11 |
| `ModRecipeProvider` | 合成配方 + bobo_soak 自定义配方 | 7 + 3 |
| `ModItemTagsProvider` | item 标签 | 3 |
| `ModBlockTagsProvider` | 空（为 item 标签提供查询源） | 0 |

输出目录：`src/generated/resources`，通过
`sourceSets.main.resources { srcDir 'src/generated/resources'; exclude '.cache/**' }` 参与打包。

运行：`./gradlew runData`

### 仍为手写（有意保留）

| 资源 | 原因 |
|---|---|
| `models/block/blooming_lotus_leaf_stage0~4.json` | 自定义平面模型（0.25 高度、自定义 UV），datagen 重建易失真 |
| `models/block/bobo_chicken_pot.json` | Blockbench 导出的元素模型 |
| `recipes/pot/*.json` | 森罗厨房专用配方格式，需 KC 依赖才能用其 Builder |
| `data/kaleidoscope_cookery/tags/items/pot_ingredient.json` | 写入他人命名空间，datagen 无法直接生成 |
| `worldgen/**` | configured/placed feature 的 datagen 在 1.20.1 需要大量 codec 样板，本次未做，保留已验证的静态 JSON |
| `data/minecraft/tags/blocks/mineable/*` | 原版标签，手写更直观 |

### 迁移验证

把 89 个原手写文件移出后重新生成，再与迁移前的 jar 逐文件比对：

```
baseline: 203 个条目   after: 212 个条目
丢失文件：0
新增文件：9（均为 datagen 的 .class）
```

即**所有资源文件都被 datagen 完整重建，无遗漏**。

服务端（无 JEI、无森罗）启动验证：

```
Done (39.630s)!
datagen 类加载问题：0
```

### 已知限制

`runData` 会启动 Minecraft 引导流程，因此会检查 `mods.toml` 的强制依赖。
在未安装森罗厨房的环境下需要临时移除 `kaleidoscope_cookery` 依赖项才能跑通数据生成
（本次即如此操作）。已安装前置的环境可正常直接运行。

---

## 附六：堆肥实现（2026-11-21，更正此前结论）

### 此前判断有误

本报告 §5.1 与 §9 曾写「Forge 1.20.1 没有堆肥注册 API，需 AT 且必须用 SRG 名，无法可靠实现」。
经实际查证，**这个结论是错的**：原版 `ComposterBlock.COMPOSTABLES` 本身就是 `public static final`，
无需 Access Transformer，直接写入即可。

### 实际机制

```java
// ComposterBlock（1.20.1）
public static final Object2FloatMap<ItemLike> COMPOSTABLES;
```

- 值是 0~1 的堆肥概率（`bootStrap()` 里 `defaultReturnValue(0.0F)`，未登记物品一律返回 0）
- **查表键是 `Item`**：`insertItem` 内部走 `COMPOSTABLES.getFloat(stack.getItem())`
  → 所以必须用 `block.asItem()`（BlockItem），直接放 Block 无效
- Forge 侧没有任何封装：jar 内不含 compost 相关 API 类，源码中也搜不到 compost，
  也没有事件可供挂接

### 实现

`BoilingCauldron#registerCompostables`（`FMLCommonSetupEvent` 内执行）：

```java
ComposterBlock.COMPOSTABLES.put(ModBlocks.BAMBOO_FUNGUS_COLONY.get().asItem(), 0.65F);
ComposterBlock.COMPOSTABLES.put(ModBlocks.LUSH_GRASS.get().asItem(), 0.30F);
```

数值与 Fabric 版 `CompostingChanceRegistry` 保持一致（竹荪菌落 0.65、繁茂草丛 0.3）。

### 验证

编译通过（说明字段确实可访问，不需要 AT）；服务端运行时打印：

```
COMPOST CHECK size=103 fungus=0.65 lush=0.3
```

`size=103` = 原版 101 条 + 本模组 2 条，概率值也与设定一致，确认注册生效。
验证用临时日志已移除，最终 jar 不含该语句。

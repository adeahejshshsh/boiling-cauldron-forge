# NeoForge 1.21.1 移植笔记

> 记录 Forge 1.20.1 → NeoForge 1.21.1 的 API 变更与踩坑，供后续维护参考。
> 每一条都是编译器或运行时实际报错后确认的，不是推测。

## 构建体系

| 项目 | Forge 1.20.1 | NeoForge 1.21.1 |
|---|---|---|
| 构建插件 | ForgeGradle 6 | ModDevGradle 2.0.148 |
| Java | 17 | 21 |
| 元数据文件 | `META-INF/mods.toml` | `META-INF/neoforge.mods.toml` |
| 依赖声明 | `mandatory = true` | `type = "required"` |

## 注册

`DeferredRegister` 的命名空间必须与资源路径一致：

```java
// 正确：注册名 boiling-cauldron:ginger，资源路径 assets/boiling-cauldron/...
DeferredRegister.createItems(BoilingCauldron.NAMESPACE)

// 错误：注册名 boilingcauldron:ginger，找不到 assets/boiling-cauldron/... 下的贴图
DeferredRegister.createItems(BoilingCauldron.MOD_ID)
```

modId 与资源命名空间可以不同：modId 用 `boilingcauldron`（Forge/NeoForge 要求
`^[a-z][a-z0-9_]{1,63}$`，不允许连字符），资源命名空间沿用 `boiling-cauldron`。

## 方块

| 1.20.1 | 1.21.1 |
|---|---|
| `Block#use(state, level, pos, player, hand, hit)` → `InteractionResult` | `BlockBehaviour#useItemOn(stack, state, level, pos, player, hand, hit)` → `ItemInteractionResult` |
| 无 | `BushBlock` / `WaterlilyBlock` 等已有 `codec()` 实现，子类不可再覆写 |
| — | 自定义方块必须实现 `codec()`，用 `simpleCodec(XxxBlock::new)` |

`ItemInteractionResult` 的返回值：

- `SUCCESS` —— 已处理
- `PASS_TO_DEFAULT_BLOCK_INTERACTION` —— 交给默认方块交互
- `SKIP_DEFAULT_BLOCK_INTERACTION` —— 跳过默认交互

## 方块实体

```java
// 1.20.1
protected void saveAdditional(CompoundTag tag) { ... }
public void load(CompoundTag tag) { ... }

// 1.21.1
protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) { ... }
protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) { ... }
```

连带变化：

- `ContainerHelper.saveAllItems(tag, items)` → `saveAllItems(tag, items, registries)`
- `ItemStack#save()` → `save(registries)`；`ItemStack.parse` → `ItemStack.parse(registries, tag)`
- `getUpdateTag()` → `getUpdateTag(HolderLookup.Provider)`

`WaterlilyBlock` 子类不能用 `createTickerHelper`（那是 `BaseEntityBlock` 的保护方法），
需自行转换：

```java
return type == ModBlockEntities.BLOOMING_LOTUS_LEAF.get()
    ? (BlockEntityTicker<T>) (BlockEntityTicker<BloomingLotusLeafBlockEntity>)
        BloomingLotusLeafBlockEntity::tick
    : null;
```

## 物品

| 1.20.1 | 1.21.1 |
|---|---|
| `stack.getTag()` / `stack.setTag(tag)` | `DataComponents.CUSTOM_DATA` + `CustomData.of(tag)` |
| `FoodProperties#saturationMod(float)` | `saturationModifier(float)` |
| `FoodProperties#getEffects()` → `List<Pair<MobEffectInstance, Float>>` | `effects()` → `List<FoodProperties.PossibleEffect>`（`.effect()` / `.probability()`） |
| `MobEffect` | `Holder<MobEffect>` |
| `new ResourceLocation(ns, path)` | `ResourceLocation.fromNamespaceAndPath(ns, path)` |

## 配方

1.21 起配方完全 Codec 驱动，`Recipe` 不再持有 `ResourceLocation id`：

```java
public class BoboSoakRecipe implements Recipe<BoboSoakRecipe.Input> {
    public static final MapCodec<BoboSoakRecipe> CODEC = ...;
    public static final StreamCodec<RegistryFriendlyByteBuf, BoboSoakRecipe> STREAM_CODEC = ...;
}
```

`RecipeSerializer` 只需实现两个方法：

```java
public abstract MapCodec<T> codec();
public abstract StreamCodec<RegistryFriendlyByteBuf, T> streamCodec();
```

查询时返回的是 `RecipeHolder<T>`，需要解包：

```java
level.getRecipeManager()
     .getRecipeFor(type, input, level)
     .map(holder -> holder.value());
```

### 数据包目录与格式

| 1.20.1 | 1.21.1 |
|---|---|
| `data/<ns>/recipes/` | `data/<ns>/recipe/` |
| `data/<ns>/loot_tables/` | `data/<ns>/loot_table/` |
| `data/<ns>/advancements/` | `data/<ns>/advancement/` |
| `data/<ns>/tags/items/` | `data/<ns>/tags/item/` |
| `result: {"item": "..."}` | `result: {"id": "...", "count": 1}` |

## 界面

| 1.20.1 | 1.21.1 |
|---|---|
| `IForgeMenuType.create(...)` | `IMenuTypeExtension.create(...)` |
| `MenuScreens.register(...)`（客户端 `FMLClientSetupEvent`） | `RegisterMenuScreensEvent` |
| `NetworkHooks.openScreen(...)` | `player.openMenu(...)` |

**重要**：`AbstractContainerScreen` 不会自动渲染 tooltip，子类必须在 `render` 里显式调用：

```java
@Override
public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    super.render(guiGraphics, mouseX, mouseY, partialTick);
    this.renderTooltip(guiGraphics, mouseX, mouseY);
}
```

原版 `ContainerScreen` 就是这么写的。漏掉这一步，悬浮槽位不会有任何提示。

## 战利品

`ItemPredicate` 不再有 `withEnchantment`，精准采集判定改走子谓词：

```java
ItemPredicate.Builder.item().withSubPredicate(
    ItemSubPredicates.ENCHANTMENTS,
    ItemEnchantmentsPredicate.enchantments(List.of(
        new EnchantmentPredicate(
            registries.holderOrThrow(Enchantments.SILK_TOUCH),
            MinMaxBounds.Ints.atLeast(1)))));
```

## 数据生成

- 事件：`net.neoforged.neoforge.data.event.GatherDataEvent`
- `PackOutput` 取代旧的 `DataGenerator` 输出参数
- `BlockTagsProvider` 移至 `net.neoforged.neoforge.common.data`
- 配方写入用 `RecipeOutput#accept(ResourceLocation, Recipe, AdvancementHolder)`

## 编码陷阱

**Gradle 以 ISO-8859-1 读取 `gradle.properties`。** 任何中文字面量代入模板后都会变成乱码，
且双重编码会产出非法 UTF-8，导致：

1. jar 内 `neoforge.mods.toml` 的 `displayName` / `authors` / `description` 全是乱码
2. `META-INF/MANIFEST.MF` 的 Vendor 字段损坏

处理原则：

- `gradle.properties` 只写 ASCII
- 中文一律硬编码在 `neoforge.mods.toml`（加载器按 UTF-8 读取）
- MANIFEST 属性只用 ASCII，用专门的 `mod_vendor_ascii` 属性

## Android / Termux 构建

Android 应用进程的 seccomp 拦截了老式 `accept(2)`（arm64 #202），但放行 `accept4`（#242）。
Gradle 从 4.0 起即使 `--no-daemon` 也会 fork 一个 single-use daemon 并通过本地 TCP 回连，
因此会报 `Could not connect to the Gradle daemon`。

绕过方式见 [`tools/accept-shim/`](../tools/accept-shim/README.md)。

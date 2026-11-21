# Changelog

All notable changes to this project are documented here.

[中文备份](CHANGELOG.zh_cn.md)

## 1.0.1-neoforge (2026-11-21)

### Fixes

- **Item tooltips did not appear in the Bobo Chicken Pot GUI** — `AbstractContainerScreen`
  never renders tooltips on its own; each container screen must call `renderTooltip` from
  its `render` override. Vanilla `ContainerScreen` does exactly that. Hovering a slot now
  shows the item name and the flavour information, matching the inventory screen.
- **Three blocks were missing from the creative tab** — `bamboo_root`, `blooming_lotus_leaf`
  and `bobo_chicken_pot` were registered but never listed. The tab now matches the Forge
  build at 21 entries.

## 1.0.0-neoforge (2026-11-21)

First NeoForge 1.21.1 release, ported from the Forge 1.20.1 version.

### Porting

- Build system moved from ForgeGradle 6 to ModDevGradle 2.0.148, Java 17 to Java 21
- Metadata moved from `mods.toml` to `neoforge.mods.toml`
- Item NBT replaced by **data components** — flavours are stored in `DataComponents.CUSTOM_DATA`
- Recipes are codec-driven: `MapCodec` + `StreamCodec`, no `ResourceLocation` id inside a recipe
- `MobEffect` replaced by `Holder<MobEffect>`; `FoodProperties#saturationMod` renamed to `saturationModifier`
- Block entities moved to `saveAdditional(CompoundTag, HolderLookup.Provider)` /
  `loadAdditional(CompoundTag, HolderLookup.Provider)`
- `Block#use` replaced by `BlockBehaviour#useItemOn(ItemStack, ...)` returning `ItemInteractionResult`
- Menus registered through `IMenuTypeExtension`; screens through `RegisterMenuScreensEvent`
- Data folders renamed to their singular 1.21 forms: `recipe`, `loot_table`, `advancement`, `tags/item`
- JEI upgraded from 15.x to 19.71

### New

- **Data generation** — 8 providers producing 95 files
- **JEI integration** — soaking category reading recipes from the `RecipeManager`
- **Loot injection and trader offers** — village chest seed pools, wandering trader seed trades

### Known limitations

- The Kaleidoscope Cookery pot recipes reference `kaleidoscope_cookery:pot`; they are skipped
  unless the NeoForge build of that mod is installed
- Malatang is not implemented
- Sounds, particles and comparator output are not implemented

# Changelog

All notable changes to this project are documented here.

[中文备份](CHANGELOG.zh_cn.md)

## 1.0.4-neoforge (2026-11-28)

Ported upstream **1.0.1** (the Malatang update).

### New

- **Malatang Pot** — a 16-slot hot pot. Needs a heat source directly below
  (campfire, soul campfire, fire, soul fire, magma block, or a lit furnace).
  Each ingredient cooks in **20 seconds** and stays in the pot until taken.
- **Malatang** — right-click a finished ingredient with a bowl to start a bowl,
  then right-click more finished ingredients to add up to **4** of them.
  Hunger, saturation and mob effects accumulate; eating returns the bowl.
- **Phantom Five-Spice Powder** — craft with Bobo Chicken or Malatang to hide
  the ingredient list from the tooltip.
- New recipe type `boiling-cauldron:mask_dish` (codec-driven, stateless).

### Refactor

- Bobo Chicken Pot and Malatang Pot now share `AbstractSeasoningPotBlock`,
  `AbstractSeasoningPotBlockEntity` and `AbstractSeasoningPotMenu` (upstream design).
  The Bobo pot keeps its 60-second soak; the Malatang pot is fixed at 20 seconds.

### Notes

- Upstream stores flavour as an enum; this port keeps the datapack-driven
  `FlavorProfile`, so base flavours are resolved through the `bobo_soak` recipes.
- Item data still lives in `DataComponents.CUSTOM_DATA`, as in 1.0.3.

## 1.0.3-neoforge (2026-11-22)

### New

- **JEI "where to find it" info pages** — 9 entries covering natural generation and
  world-acquisition routes:
  - Lush Grass, Blooming Lotus Leaf (the two naturally generating blocks)
  - Lotus Mud, Bamboo Root, Bamboo Fungus Colony (derived from world mechanics)
  - Winter Bamboo Shoots, Lotus Root, Bamboo Fungus (products)
  - The four seeds (one entry: grass drops / village chests / wandering trader)

  Biome lists and probabilities are taken from the actual worldgen and loot data,
  not estimated.

### Fixes

- **Natural generation never worked** — `biome_modifier` still used the Forge 1.20.1
  path and namespace:
  - path `data/boiling-cauldron/forge/biome_modifier/` should be `neoforge/biome_modifier/`
  - type `forge:add_features` should be `neoforge:add_features`

  NeoForge 1.20.2+ renamed its own registry namespace from `forge:` to `neoforge:`,
  so the old path was never loaded. As a result **Lush Grass and Blooming Lotus Leaf
  have never generated naturally in the NeoForge build**.
- **Kaleidoscope Cookery chili tag** — `forge:crops/chilipepper` → `c:crops/chilipepper`.
  NeoForge common tags use the `c:` namespace; the old form broke the mala base recipe.

## 1.0.2-neoforge (2026-11-22)

### New

- **Jade support** — the three functional blocks now show their state in Jade tooltips:
  - Bamboo Root: current alive offspring / 5
  - Blooming Lotus Leaf: growth stage / 4, plus a hint when it is converting the block below
  - Bobo Chicken Pot: water status, base name, uses left, and how many slots are soaking
- Jade is an **optional** dependency; without it the tooltips simply do not appear

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

# Changelog

All notable changes to this project are documented here.

[中文备份](CHANGELOG.zh_cn.md)

## 1.0.3-forge (2026-11-22)

### New

- **JEI "where to find it" info pages** — 9 entries covering natural generation and
  world-acquisition routes:
  - Lush Grass, Blooming Lotus Leaf (the two naturally generating blocks)
  - Lotus Mud, Bamboo Root, Bamboo Fungus Colony (derived from world mechanics)
  - Winter Bamboo Shoots, Lotus Root, Bamboo Fungus (products)
  - The four seeds (one entry: grass drops / village chests / wandering trader)

  Biome lists and probabilities are taken from the actual worldgen and loot data,
  not estimated.

## 1.0.2-forge (2026-11-22)

### New

- **Jade support** — the three functional blocks now show their state in Jade tooltips:
  - Bamboo Root: current alive offspring / 5
  - Blooming Lotus Leaf: growth stage / 4, plus a hint when it is converting the block below
  - Bobo Chicken Pot: water status, base name, uses left, and how many slots are soaking
- Jade is an **optional** dependency; without it the tooltips simply do not appear

## 1.0.1-forge (2026-11-21)

### Fixes

- **Item tooltips did not appear in the Bobo Chicken Pot GUI** — `AbstractContainerScreen`
  never renders tooltips on its own; each container screen must call `renderTooltip` from
  its `render` override. Vanilla `ContainerScreen` does exactly that. Hovering a slot now
  shows the item name and the flavour information, matching the inventory screen.

### Notes

- Version number bumped to `1.0.1-forge` (`gradle.properties` + `mods.toml`).

## 1.0.0-forge (2026-11-21)

First Forge 1.20.1 release, ported from the Fabric 1.0.0 version.

### Porting

- Registration moved to `DeferredRegister`; entry point is now `@Mod`
- Block entities now extend `BaseEntityBlock`; containers use `WorldlyContainer`
- GUI uses `IForgeMenuType` + `NetworkHooks.openScreen` + `MenuScreens`
- Render layers moved to the model JSON `render_type: cutout` field
- Loot injection uses `LootTableLoadEvent`; trader offers use `WandererTradesEvent`
- World generation uses a `forge:add_features` biome modifier
- Kaleidoscope Cookery chilli tag corrected from `c:crops/chilipepper` to `forge:crops/chilipepper`

### Licence

- Source code relicensed to **MIT**; game assets remain **CC0-1.0**
- Added `LICENSING.md` describing the file-level split and third-party boundaries

### Fixes

- **Valid mod id**: Forge requires `^[a-z][a-z0-9_]{1,63}$`. The original `boiling-cauldron`
  contains a hyphen and would crash at startup. The mod now uses modId `boilingcauldron`
  with `namespace = "boiling-cauldron"`, so every resource path and translation key is unchanged
- **Added the missing `bamboo_root` loot table** — it previously dropped nothing when mined
- **Added the seed item models** — they previously relied on stale datagen output

### New

- **Custom recipe type `boiling-cauldron:bobo_soak`** — base to Bobo Chicken is now datapack-driven
- **Fully data-driven flavours** — `BoboFlavor` enum replaced by `FlavorProfile` stored in NBT
- **Interaction hints** — the pot explains failures on the action bar
- **JEI integration** — soaking category, full edible-item slot, per-base info pages
- **Composting** — bamboo fungus colony 0.65, lush grass 0.30
- **Forge data generation** — 8 providers, 92 generated files committed

### Verification

- `./gradlew build` passes
- A dedicated server starts successfully with recipes, tags and loot tables loading
- File-by-file comparison before and after the datagen migration: all 203 resources preserved

### Known limitations

- Malatang is not implemented
- Lotus leaf plane models, the Blockbench pot model, the Kaleidoscope wok recipes and the world
  generation JSON remain hand-written
- Sounds, particles and comparator output are not implemented

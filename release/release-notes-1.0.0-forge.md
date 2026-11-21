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

### Fixes

- **Valid mod id**: Forge requires `^[a-z][a-z0-9_]{1,63}$`. The original `boiling-cauldron`
  contains a hyphen and would crash at startup. The mod now uses modId `boilingcauldron`
  with `namespace = "boiling-cauldron"`, so every resource path and translation key is unchanged
- **Added the missing `bamboo_root` loot table** — it previously dropped nothing when mined
- **Added the seed item models** — they previously relied on stale datagen output

### New

- **Custom recipe type `boiling-cauldron:bobo_soak`** — base → Bobo Chicken is now datapack-driven;
  flavour, effects, uses, soak time and output can all be defined in JSON
- **Fully data-driven flavours** — the `BoboFlavor` enum is gone, replaced by `FlavorProfile`
  stored in the item's NBT, so datapacks can add arbitrary flavours
- **Interaction hints** — the pot now explains failures on the action bar
  (needs water first / already has a base / pot still contains food)
- **JEI integration** — a "Bobo Chicken Soaking" category showing base → Bobo Chicken,
  an ingredient slot listing every edible item, and an info page for each base
- **Composting** — bamboo fungus colony 0.65, lush grass 0.30
- **Forge data generation** — 8 providers covering models, language files, loot tables, recipes and
  tags; the 92 generated files are committed under `src/generated/resources`

### Verification

- `./gradlew build` passes; the artifact is about 195 KB
- A dedicated server starts successfully (`Done`), with recipes, tags and loot tables all loading
- File-by-file comparison before and after the datagen migration: **all 203 resources preserved**

### Known limitations

- Malatang (the planned third gameplay branch) is not implemented
- The blooming lotus leaf plane models, the Blockbench pot model, the Kaleidoscope wok recipes and
  the world generation JSON remain hand-written
- Sounds, particles and comparator output are not implemented

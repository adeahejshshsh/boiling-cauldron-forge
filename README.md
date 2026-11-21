# Boiling Cauldron (沸鼎烟火) · Forge Edition

A Minecraft cooking mod themed around **Malatang** and **Bobo Chicken (钵钵鸡)**.
This repository is the **Forge 1.20.1** edition.

> The original Fabric version was created by [Xkbsx413](https://github.com/Xkbsx413/boiling-cauldron).
> This repository is a Forge port of it.

[中文说明](README.zh_cn.md)

---

## Requirements

| Item | Version |
|---|---|
| Minecraft | 1.20.1 |
| Forge | 47.3.0 or newer (47.4.0 used for development) |
| Java | 17 |
| Required dependency | [Kaleidoscope Cookery](https://modrinth.com/mod/kaleidoscope-cookery) `1.0.1-forge+mc1.20.1` |

JEI is **optional**: if installed, the "Bobo Chicken Soaking" recipe category becomes available.
The mod works normally without it.

---

## Content

### Ingredients

- **Four crops**: ginger, garlic, gong cai (stem lettuce), Sichuan pepper (4–5 growth stages).
- **Winter bamboo shoots**: six stages; bone meal on a mature crop turns it into vanilla bamboo.
- **Bamboo root block**: periodically spawns winter bamboo shoot crops or bamboo fungus colonies,
  up to five living offspring at a time.
- **Blooming lotus leaf**: generates naturally in swamps and mangrove swamps; once mature it converts
  dirt, sand or gravel beneath it into lotus mud.
- **Lotus mud**: right-click with a shovel for 3 lotus roots; mining drops 1–4.
- **Lush grass**: generates in many biomes; 35% chance to drop a random crop seed.

Seeds can be obtained from village chests (themed per village type, plus a 8% "seasoning pack" pool),
from the wandering trader, or from lush grass.

### Bases and Bobo Chicken

1. Use the **wok** from Kaleidoscope Cookery to cook three kinds of soup base:
   mushroom, mala, and rattan pepper.
2. Place a **Bobo Chicken Pot** (8 bricks + 1 bowl).
3. Fill it with a water bucket, then add one soup base (grants 64 soaks).
4. Put food into the GUI; each slot soaks independently for 60 seconds.
5. Take the result out to get **Bobo Chicken**; eating it applies the base's effect.

Flavour, uses, soak time, output item and effects are **entirely driven by datapack recipes**,
so they can be extended freely.

---

## Custom recipes (datapack)

Recipe type: `boiling-cauldron:bobo_soak`

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

| Field | Description |
|---|---|
| `base` | An ingredient; accepts `item` or `tag` |
| `flavor.id` | Unique flavour id, stored in the item's NBT |
| `flavor.name` | Display name: `{"translate": ...}`, `{"text": ...}`, or a plain string |
| `flavor.sort` | Ordering when multiple flavours are combined (lower comes first) |
| `flavor.color` | Colour of the base name in the GUI, `#RRGGBB` or decimal |
| `flavor.effects` | Any number of mob effects |
| `soak_time` | Optional, defaults to 1200 ticks (60 s) |
| `uses` | Optional, defaults to 64 |

Three built-in recipes live in
`src/generated/resources/data/boiling-cauldron/recipes/bobo_soak/`.

---

## Building

```bash
./gradlew build
```

Output: `build/libs/boiling-cauldron-1.0.0-forge.jar`

### Data generation

```bash
./gradlew runData
```

Output goes to `src/generated/resources`, which is committed and included in the build via
`sourceSets.main.resources { srcDir 'src/generated/resources' }`.

> **Note**: `runData` boots Minecraft and therefore validates mandatory dependencies.
> Without Kaleidoscope Cookery installed locally, temporarily comment out the
> `kaleidoscope_cookery` entry in `src/main/resources/META-INF/mods.toml` before running it.

### Development runs

```bash
./gradlew runClient
./gradlew runServer
```

---

## Project layout

```
├── build.gradle                 Build script (ForgeGradle 6)
├── gradle.properties            Versions and mod metadata
├── src/main/java/               Sources (41 classes)
│   └── com/boilingcauldron/
│       ├── block/               Blocks and block entities
│       ├── item/                Items and flavour profiles
│       ├── recipe/              Custom recipe type bobo_soak
│       ├── screen/              Bobo Chicken Pot GUI
│       ├── datagen/             Forge data generation (8 providers)
│       ├── loot/ trade/ tag/    Loot injection, trades, tags
│       └── compat/jei/          Optional JEI integration
├── src/main/resources/          Hand-written assets (textures, models, mods.toml)
├── src/generated/resources/     Data generation output (committed)
└── docs/                        Design and porting documents
```

---

## Documentation

- [Design document](docs/design.md) — finalised gameplay rules (Chinese)
- [Porting report](docs/porting-report.md) — Fabric → Forge mapping, verification, known limits (Chinese)
- [Source analysis](docs/source-analysis.md) — analysis of the original Fabric version (Chinese)

---

## Licence

Dual-licensed:

| Scope | Licence | File |
|---|---|---|
| Source code, build scripts, documentation | **MIT** | [LICENSE](LICENSE) |
| Game assets (textures, models, language files, recipes, loot tables, tags, worldgen) | **CC0-1.0** | [LICENSE-ASSETS](LICENSE-ASSETS) |

See [LICENSING.md](LICENSING.md) for the exact file split, and [NOTICE](NOTICE) for attribution.

Third-party content (Kaleidoscope Cookery, Minecraft, Forge) is not covered by these licences.

## Credits

- Original author: [Xkbsx413](https://github.com/Xkbsx413/boiling-cauldron) and the artists
- Forge port: [adeahejshshsh](https://github.com/adeahejshshsh)

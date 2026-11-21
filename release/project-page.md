# CurseForge project materials (copy & paste)

Ready-to-use copy for creating and maintaining the CurseForge project page.

[中文备份](project-page.zh_cn.md)

---

## Basic information

| Field | Suggested value |
|---|---|
| Project name | Boiling Cauldron |
| Project type | Minecraft → Mods |
| Category | Food / Agriculture (optionally World Gen) |
| Game version | 1.20.1 |
| Mod loader | Forge |
| Licence | MIT (code) + CC0-1.0 (assets) |
| Source | https://github.com/adeahejshshsh/boiling-cauldron-forge |
| Issues | Same repository, Issues tab |
| Required dependency | Kaleidoscope Cookery (slug: `kaleidoscope-cookery`, `requiredDependency`) |

---

## Summary (about 200 characters)

```
A Forge cooking mod about Malatang and Bobo Chicken: grow spices, brew soup bases in a wok, then cold-soak your food in the Bobo Chicken Pot. Requires Kaleidoscope Cookery.
```

---

## Description (Markdown)

```markdown
# Boiling Cauldron

A Forge cooking mod themed around **Malatang** and **Bobo Chicken**.

## Gameplay

1. **Grow and gather** — four crops (ginger, garlic, gong cai, Sichuan pepper), plus winter bamboo
   shoots, bamboo fungus and lotus root obtained from the bamboo root block and blooming lotus leaves.
2. **Brew soup bases** — cook mushroom, mala and rattan pepper bases in the Kaleidoscope Cookery wok.
3. **Cold-soak** — place a Bobo Chicken Pot, add water and a soup base, then soak food in its 4×4 grid
   for 60 seconds per slot.
4. **Eat** — the result grants Regeneration, Strength or Resistance depending on the base, and up to
   three flavours can be stacked on a single item.

## Features

- Bases, flavours, soak time and output items are **fully datapack-driven** and easy to extend
- Flavour effects accept any vanilla or modded mob effect
- Built-in JEI support (optional) with a dedicated "Bobo Chicken Soaking" category
- Supports Forge 47.3.0+ on Minecraft 1.20.1

## Requirements

**[Kaleidoscope Cookery](https://www.curseforge.com/minecraft/mc-mods/kaleidoscope-cookery)**
is required — without it the soup bases cannot be crafted.

JEI is optional.

## Licence

- Source code: MIT
- Game assets: CC0-1.0

The original Fabric version was created by Xkbsx413 and the artist team, and released under CC0.
```

---

## After uploading — check these manually

1. **Project review**: a brand-new project may go through manual review, typically a few hours to a few days.
2. **File review**: the first file may also be scanned/reviewed before it becomes public.
3. **Dependency relation**: the upload script already declares `kaleidoscope-cookery` as
   `requiredDependency`; confirm it appears correctly on the file page.
4. **Icon**: use `src/main/resources/icon.png` from the repository.

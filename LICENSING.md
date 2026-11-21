# Licensing

This project is **dual-licensed**: source code and game assets fall under different terms.

[中文备份](LICENSING.zh_cn.md)

> Attribution and provenance are recorded in [NOTICE](NOTICE).
> `LICENSE` is kept as the verbatim MIT template — GitHub matches licence texts with an exact
> template comparison, and any extra paragraph causes it to report the licence as "Other".

## Source code — MIT License

The following is covered by [LICENSE](LICENSE) (MIT):

```
src/main/java/**                               All Java sources
src/main/java/com/boilingcauldron/datagen/**   Data generators
build.gradle
settings.gradle
gradle.properties
gradlew / gradlew.bat
gradle/wrapper/**
.github/**
docs/**                                        Documentation
```

## Game assets — CC0 1.0 Universal (public domain)

The following is covered by [LICENSE-ASSETS](LICENSE-ASSETS) (CC0 1.0):

```
src/main/resources/assets/**           Textures, models, blockstates, language files
src/generated/resources/assets/**      Generated models and language files
src/main/resources/data/**             Recipes, loot tables, tags, world generation
src/generated/resources/data/**        Generated recipes, loot tables, tags
src/main/resources/pack.mcmeta
```

CC0 places these assets in the public domain: you may use, modify, sell and relicense them
**without attribution**. Crediting the project is appreciated but not required.

## Third-party content

This project **does not bundle** any third-party mod code or assets. The following are
interoperability references only and do not constitute copying:

- **Kaleidoscope Cookery** — recipe type `kaleidoscope_cookery:pot`, item tag
  `kaleidoscope_cookery:pot_ingredient`, item id `kaleidoscope_cookery:oil_block`,
  tag `forge:crops/chilipepper`.
  That mod is licensed BSD-3-Clause (code) and CC BY-NC-SA 4.0 (assets); **its terms do not apply
  to this project**. If you intend to bundle its assets you must comply with CC BY-NC-SA 4.0
  (non-commercial, attribution, share-alike).

- **Minecraft / Minecraft Forge** — copyright Mojang Studios and the Forge team.
  The MIT / CC0 grants here **do not cover** them.

## Provenance

The original Fabric version "Boiling Cauldron (沸鼎烟火)" was released by
[Xkbsx413](https://github.com/Xkbsx413/boiling-cauldron) and the artist team under CC0-1.0.
Because it was public domain, this port is able to distribute the code portion under MIT.

> Note: the original repository's **art assets** were produced with contributions from multiple
> artists. Although the repository as a whole is declared CC0, if you intend to use these textures
> commercially, it is worth confirming the artists' consent with the original author.

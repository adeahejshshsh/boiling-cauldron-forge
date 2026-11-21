# 授权说明（Licensing）

本项目采用**双授权**：源代码与游戏资源分别适用不同协议。

> 版权归属与来源记录见 [NOTICE](NOTICE)。
> `LICENSE` 保持 MIT 标准模板原文，不做任何改写——GitHub 通过精确模板匹配识别许可证，
> 任何附加段落都会导致其把协议识别为 "Other"。

## 源代码 —— MIT License

以下内容适用 [LICENSE](LICENSE)（MIT）：

```
src/main/java/**            所有 Java 源码
src/main/java/com/boilingcauldron/datagen/**   数据生成器
build.gradle
settings.gradle
gradle.properties
gradlew / gradlew.bat
gradle/wrapper/**
.github/**
docs/**                     文档与说明
```

## 游戏资源 —— CC0 1.0 Universal（公有领域）

以下内容适用 [LICENSE-ASSETS](LICENSE-ASSETS)（CC0 1.0）：

```
src/main/resources/assets/**           贴图、模型、方块状态、语言文件
src/generated/resources/assets/**      数据生成产出的模型与语言文件
src/main/resources/data/**             配方、战利品表、标签、世界生成
src/generated/resources/data/**        数据生成产出的配方、战利品表、标签
src/main/resources/pack.mcmeta
```

CC0 意味着这些资源已进入公有领域：可以自由使用、修改、商用、闭源，**无需署名**。
署名不是义务，但如果你愿意，欢迎注明来源。

## 第三方内容

本项目**未打包**任何第三方模组的代码或资源。以下仅为互操作引用，不构成复制：

- **森罗厨房（Kaleidoscope Cookery）** —— 配方类型 `kaleidoscope_cookery:pot`、
  物品标签 `kaleidoscope_cookery:pot_ingredient`、物品 ID `kaleidoscope_cookery:oil_block`、
  标签 `forge:crops/chilipepper`。
  该模组代码为 BSD-3-Clause、资源为 CC BY-NC-SA 4.0，**其授权不适用于本项目**，
  但若你打算打包它的资源，必须遵守其 CC BY-NC-SA 4.0（非商用 + 署名 + 相同方式共享）。

- **Minecraft / Minecraft Forge** —— 版权所有 Mojang Studios 与 Forge 开发团队。
  本项目的 MIT / CC0 声明**不覆盖**它们。

## 来源说明

原始 Fabric 版本 "沸鼎烟火（Boiling Cauldron）" 由 [Xkbsx413](https://github.com/Xkbsx413/boiling-cauldron)
及画师以 CC0-1.0 发布。因其为公有领域，本移植版得以重新以 MIT 分发代码部分。

> 提醒：原始仓库的**美术资源**由多位画师参与创作。虽然原仓库整体声明 CC0，
> 若你需要将这些贴图用于商业用途，建议先与原作者确认画师方的授权意愿。

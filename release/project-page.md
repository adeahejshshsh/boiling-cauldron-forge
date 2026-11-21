# CurseForge 项目资料（复制粘贴用）

本文件存放创建 CurseForge 项目时需要填写的文案，避免临时拼凑。

---

## 基本信息

| 字段 | 建议值 |
|---|---|
| 项目名称 | 沸鼎烟火（Boiling Cauldron） |
| 项目类型 | Minecraft → Mods |
| 分类 | Food / Agriculture（可再选 World Gen） |
| 游戏版本 | 1.20.1 |
| 模组加载器 | Forge |
| 许可证 | MIT（代码）+ CC0-1.0（资源） |
| 源码地址 | https://github.com/adeahejshshsh/boiling-cauldron-forge |
| 问题反馈 | 同上 Issues |
| 前置依赖 | Kaleidoscope Cookery（slug: `kaleidoscope-cookery`，requiredDependency） |

---

## 简介（Summary，约 200 字符）

```
以麻辣烫、钵钵鸡为主题的 Forge 烹饪模组：种植香料、用炒锅熬制底料、在钵钵鸡锅里冷泡出成品。需要森罗厨房作为前置。
```

---

## 详细描述（Description，Markdown）

```markdown
# 沸鼎烟火（Boiling Cauldron）

以 **麻辣烫 / 钵钵鸡** 为主题的 Forge 烹饪模组。

## 玩法

1. **种植与采集**：生姜、大蒜、贡菜、花椒四种作物；冬笋、竹荪、莲藕来自竹根殖块与盛花荷叶。
2. **熬制底料**：在森罗厨房的炒锅里制作菌菇、麻辣、藤椒三种底料。
3. **冷泡成菜**：放下钵钵鸡锅，加水、投底料，把食物放进 4×4 格中浸泡 60 秒。
4. **食用**：成品按口味提供生命恢复 / 力量 / 抗性提升效果，最多可叠加 3 种口味。

## 特性

- 底料、口味、浸泡时长、产出物品**全部由数据包配方驱动**，可自由扩展
- 口味效果支持任意原版/模组药水效果
- 内置 JEI 支持（可选安装），可查询「钵钵鸡浸泡」配方
- 兼容 Forge 47.3.0+ / Minecraft 1.20.1

## 前置

**[森罗厨房 Kaleidoscope Cookery](https://www.curseforge.com/minecraft/mc-mods/kaleidoscope-cookery)**
必须安装，否则底料无法制作。

JEI 为可选。

## 许可

- 源代码：MIT
- 游戏资源：CC0-1.0

原始 Fabric 版本由 Xkbsx413 及画师团队创作，以 CC0 发布。
```

---

## 上传后需要手动确认的项

1. 项目审核：新项目首次提交可能进入人工审核，通常数小时到数天。
2. 文件审核：首个文件也可能被扫描/审核后才公开。
3. 前置关系：脚本已在 metadata 中声明 `kaleidoscope-cookery` 为 `requiredDependency`，
   上传后可在文件页确认是否正确关联。
4. 图标：使用仓库中的 `src/main/resources/icon.png`。

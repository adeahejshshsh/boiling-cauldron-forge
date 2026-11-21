# 变更日志

## 1.0.0-forge（2026-11-21）

首个 Forge 1.20.1 版本，基于 Fabric 1.0.0 移植。

### 移植

- 注册体系改为 `DeferredRegister`，入口改为 `@Mod`
- 方块实体基类改为 `BaseEntityBlock`，容器接口改为 `WorldlyContainer`
- GUI 改为 `IForgeMenuType` + `NetworkHooks.openScreen` + `MenuScreens`
- 渲染层改用模型 JSON 的 `render_type: cutout`
- 战利品注入改为 `LootTableLoadEvent`，商人交易改为 `WandererTradesEvent`
- 世界生成改用 `forge:add_features` biome modifier
- 森罗厨房辣椒标签由 `c:crops/chilipepper` 修正为 `forge:crops/chilipepper`

### 修复

- **modId 合规**：Forge 要求 `^[a-z][a-z0-9_]{1,63}$`，原 `boiling-cauldron` 含连字符会导致启动崩溃。
  现使用 modId `boilingcauldron` + `namespace = "boiling-cauldron"`，资源路径与翻译键保持不变
- **补 `bamboo_root` 掉落表**：原版本缺失，导致挖掉即消失
- **补种子物品模型**：原先依赖 datagen 残留产物

### 新增

- **自定义配方类型 `boiling-cauldron:bobo_soak`**：底料 → 钵钵鸡 改为数据包驱动，
  口味、效果、次数、浸泡时长、产物全部可由 JSON 定义
- **口味完全数据化**：删除 `BoboFlavor` 枚举，改为 `FlavorProfile`（存入物品 NBT），
  数据包可新增任意口味
- **操作提示**：锅交互失败时通过 actionbar 说明原因（需先加水 / 已有底料 / 锅内有食物等）
- **JEI 兼容**：「钵钵鸡浸泡」分类展示底料 → 钵钵鸡，食物槽位枚举所有可食用物品；
  底料附带信息页
- **堆肥**：竹荪菌落 0.65、繁茂草丛 0.30
- **Forge 数据生成**：8 个 Provider 覆盖模型、语言、掉落表、配方、标签；
  92 个产物已提交至 `src/generated/resources`

### 验证

- `./gradlew build` 通过，产物约 195 KB
- 专用服务端启动通过（`Done`），配方/标签/战利品表均正常加载
- 迁移到 datagen 前后逐文件比对：**203 个资源零丢失**

### 授权

- 源代码改为 **MIT**，游戏资源继续保持 **CC0-1.0**
- 新增 `LICENSING.md` 说明双授权的文件划分与第三方内容边界

### 已知限制

- 麻辣烫（P3）尚未实现
- 盛花荷叶平面模型、钵钵鸡锅 Blockbench 模型、森罗炒锅配方、世界生成 JSON 仍为手写
- 音效、粒子、比较器输出未实现

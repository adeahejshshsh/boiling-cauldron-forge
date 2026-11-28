/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：定义物品标签常量。
 * 移植：Fabric → Forge 1.20.1（辣椒标签由 c: 改为 forge:）
 */
package com.boilingcauldron.tag;

import com.boilingcauldron.BoilingCauldron;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public final class ModTags {

    /** 原版蘑菇物品标签：红蘑菇和棕色蘑菇 */
    public static final TagKey<Item> MUSHROOMS = TagKey.create(Registries.ITEM,
        BoilingCauldron.id("mushrooms"));

    /** 可作为钵钵鸡底料的物品（仅用于缺配方时给出提示） */
    public static final TagKey<Item> BOBO_BASE = TagKey.create(Registries.ITEM,
        BoilingCauldron.id("bobo_base"));

    /** 钵钵鸡成品标签；口味由物品 NBT 区分 */
    public static final TagKey<Item> BOBO_CHICKEN = TagKey.create(Registries.ITEM,
        BoilingCauldron.id("bobo_chicken"));

    /** 森罗炒锅允许放入的配料标签 */
    public static final TagKey<Item> KALEIDOSCOPE_POT_INGREDIENT = TagKey.create(Registries.ITEM,
        new ResourceLocation("kaleidoscope_cookery", "pot_ingredient"));

    /**
     * 森罗辣椒作物标签。
     * 注意：Fabric 版为 c:crops/chilipepper，Forge 版森罗厨房使用 forge:crops/chilipepper。
     */
    public static final TagKey<Item> KALEIDOSCOPE_CHILI = TagKey.create(Registries.ITEM,
        new ResourceLocation("forge", "crops/chilipepper"));

    /** 可被幻色五香粉隐藏原料的成品标签 */
    public static final TagKey<Item> MASKABLE_DISHES = TagKey.create(Registries.ITEM,
        BoilingCauldron.id("maskable_dishes"));

    /** 幻色五香粉可使用的染料标签 */
    public static final TagKey<Item> DYES = TagKey.create(Registries.ITEM,
        BoilingCauldron.id("dyes"));

    /** 麻辣烫锅正下方可识别的基础热源；点燃的熔炉等由代码额外检测 */
    public static final TagKey<Block> MALATANG_HEAT_SOURCES = TagKey.create(Registries.BLOCK,
        BoilingCauldron.id("malatang_heat_sources"));

    private ModTags() {
    }
}

package com.adeahejshshsh.boilingcauldron.tag;

import com.adeahejshshsh.boilingcauldron.BoilingCauldron;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class ModTags {

    /** 原版蘑菇物品标签：红蘑菇和棕色蘑菇 */
    public static final TagKey<Item> MUSHROOMS = TagKey.create(Registries.ITEM,
        BoilingCauldron.id("mushrooms"));

    /** 可作为钵钵鸡底料的物品 */
    public static final TagKey<Item> BOBO_BASE = TagKey.create(Registries.ITEM,
        BoilingCauldron.id("bobo_base"));

    /** 钵钵鸡成品标签 */
    public static final TagKey<Item> BOBO_CHICKEN = TagKey.create(Registries.ITEM,
        BoilingCauldron.id("bobo_chicken"));

    /** 森罗炒锅允许放入的配料标签 */
    public static final TagKey<Item> KALEIDOSCOPE_POT_INGREDIENT = TagKey.create(Registries.ITEM,
        ResourceLocation.fromNamespaceAndPath("kaleidoscope_cookery", "pot_ingredient"));

    /** 森罗辣椒作物标签（NeoForge 侧为 c:） */
    public static final TagKey<Item> KALEIDOSCOPE_CHILI = TagKey.create(Registries.ITEM,
        ResourceLocation.fromNamespaceAndPath("c", "crops/chilipepper"));

    private ModTags() {
    }
}

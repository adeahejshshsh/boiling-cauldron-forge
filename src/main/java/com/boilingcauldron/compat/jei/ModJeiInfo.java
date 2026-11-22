package com.boilingcauldron.compat.jei;

import com.boilingcauldron.block.ModBlocks;
import com.boilingcauldron.item.ModItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.Arrays;
import java.util.List;

/**
 * 自然生成物与野外获取途径的 JEI 说明页。
 *
 * <p>文案里的群系与概率必须与实际 worldgen 数据一致：
 * <ul>
 *   <li>{@code forge/biome_modifier/patch_lush_grass.json} —— 10 个群系，rarity 1/4</li>
 *   <li>{@code forge/biome_modifier/patch_blooming_lotus_leaf.json} —— 沼泽与红树林沼泽</li>
 * </ul>
 * 改动任何一边都要同步另一边，否则说明页会骗人。
 */
public final class ModJeiInfo {

    private ModJeiInfo() {
    }

    public static void register(IRecipeRegistration registration) {
        // 自然生成的两个方块
        info(registration, "lush_grass", ModBlocks.LUSH_GRASS.get());
        info(registration, "blooming_lotus_leaf", ModBlocks.BLOOMING_LOTUS_LEAF.get());

        // 由世界机制衍生、但玩家仍需「去哪里找」的方块
        info(registration, "lotus_mud", ModBlocks.LOTUS_MUD.get());
        info(registration, "bamboo_root", ModBlocks.BAMBOO_ROOT.get());
        info(registration, "bamboo_fungus_colony", ModBlocks.BAMBOO_FUNGUS_COLONY.get());

        // 产物
        info(registration, "winter_bamboo_shoots", ModItems.WINTER_BAMBOO_SHOOTS.get());
        info(registration, "lotus_root", ModItems.LOTUS_ROOT.get());
        info(registration, "bamboo_fungus", ModItems.BAMBOO_FUNGUS.get());

        // 四种种子共用一条说明
        info(registration, "seeds",
            ModItems.GINGER_SEEDS.get(),
            ModItems.GARLIC_SEEDS.get(),
            ModItems.GONG_CAI_SEEDS.get(),
            ModItems.SICHUAN_PEPPER_SEEDS.get());
    }

    private static void info(IRecipeRegistration registration, String key, ItemLike... items) {
        List<ItemStack> stacks = Arrays.stream(items).map(ItemStack::new).toList();
        registration.addIngredientInfo(stacks, VanillaTypes.ITEM_STACK,
            Component.translatable("jei.boiling-cauldron.info." + key));
    }
}

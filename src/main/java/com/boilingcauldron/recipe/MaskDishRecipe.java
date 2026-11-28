/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：幻色五香粉隐藏成品原料信息的特殊合成配方。
 * 移植：Fabric → Forge 1.20.1
 * 更新：2026-11-28 上游 1.0.1 新增
 */
package com.boilingcauldron.recipe;

import com.boilingcauldron.item.DishMasking;
import com.boilingcauldron.item.ModItems;
import com.boilingcauldron.tag.ModTags;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

/**
 * 幻色五香粉 + 一份成品 → 隐藏原料信息的同一份成品。
 *
 * <p>消耗五香粉，成品本身保留（只是打上隐藏标记）。
 */
public class MaskDishRecipe extends CustomRecipe {

    public MaskDishRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        ItemStack powder = ItemStack.EMPTY;
        ItemStack dish = ItemStack.EMPTY;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }

            if (stack.is(ModItems.PHANTOM_FIVE_SPICE_POWDER.get())) {
                if (!powder.isEmpty()) {
                    return false;
                }
                powder = stack;
            } else if (stack.is(ModTags.MASKABLE_DISHES)) {
                if (!dish.isEmpty() || DishMasking.isMasked(stack)) {
                    return false;
                }
                dish = stack;
            } else {
                return false;
            }
        }

        return !powder.isEmpty() && !dish.isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registries) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.is(ModTags.MASKABLE_DISHES)) {
                return DishMasking.createMaskedCopy(stack);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.MASK_DISH_SERIALIZER.get();
    }
}

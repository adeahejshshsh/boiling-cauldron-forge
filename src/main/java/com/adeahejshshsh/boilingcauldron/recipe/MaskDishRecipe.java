/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：幻色五香粉隐藏成品原料信息的特殊合成配方。
 * 移植：Forge 1.20.1 → NeoForge 1.21.1
 * 更新：2026-11-28 上游 1.0.1 新增
 */
package com.adeahejshshsh.boilingcauldron.recipe;

import com.adeahejshshsh.boilingcauldron.item.DishMasking;
import com.adeahejshshsh.boilingcauldron.item.ModItems;
import com.adeahejshshsh.boilingcauldron.tag.ModTags;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

/**
 * 幻色五香粉 + 一份成品 → 隐藏原料信息的同一份成品。
 *
 * <p>消耗五香粉，成品本身保留（只是打上隐藏标记）。
 * 无状态，所以 Codec 用 unit 即可。
 */
public class MaskDishRecipe implements CraftingRecipe {

    public static final MaskDishRecipe INSTANCE = new MaskDishRecipe();

    public static final MapCodec<MaskDishRecipe> CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<RegistryFriendlyByteBuf, MaskDishRecipe> STREAM_CODEC =
        StreamCodec.unit(INSTANCE);

    public MaskDishRecipe() {
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        ItemStack powder = ItemStack.EMPTY;
        ItemStack dish = ItemStack.EMPTY;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
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
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
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
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.create();
    }

    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.MISC;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.MASK_DISH_SERIALIZER.get();
    }
}

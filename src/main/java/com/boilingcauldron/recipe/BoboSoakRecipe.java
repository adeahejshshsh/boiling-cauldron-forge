/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：自定义配方类型「钵钵鸡浸泡」——底料 + 锅 → 可产出的成品与浸泡参数。
 * 说明：本配方取代原先硬编码在 BoboChickenPotBlockEntity 里的「底料→口味→产物」映射，
 *       所有数值（浸泡时长、可用次数、产物）都由数据包 JSON 决定。
 * 更新：2026-11-21
 */
package com.boilingcauldron.recipe;

import com.boilingcauldron.item.FlavorProfile;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

/**
 * 一条「底料 → 钵钵鸡」配方。
 *
 * <pre>
 * {
 *   "type": "boiling-cauldron:bobo_soak",
 *   "base": { "item": "boiling-cauldron:mushroom_base" },
 *   "flavor": "mushroom",
 *   "result": { "item": "boiling-cauldron:bobo_chicken" },
 *   "soak_time": 1200,
 *   "uses": 64
 * }
 * </pre>
 */
public class BoboSoakRecipe implements Recipe<SimpleContainer> {

    private final ResourceLocation id;
    private final Ingredient base;
    private final FlavorProfile flavor;
    private final ItemStack result;
    private final int soakTime;
    private final int uses;

    public BoboSoakRecipe(ResourceLocation id, Ingredient base, FlavorProfile flavor,
                          ItemStack result, int soakTime, int uses) {
        this.id = id;
        this.base = base;
        this.flavor = flavor;
        this.result = result;
        this.soakTime = soakTime;
        this.uses = uses;
    }

    public Ingredient getBase() {
        return this.base;
    }

    public FlavorProfile getFlavor() {
        return this.flavor;
    }

    public int getSoakTime() {
        return this.soakTime;
    }

    public int getUses() {
        return this.uses;
    }

    public ItemStack getResult() {
        return this.result;
    }

    /** 判断某个底料物品能否匹配本配方 */
    public boolean matchesBase(ItemStack stack) {
        return this.base.test(stack);
    }

    @Override
    public boolean matches(SimpleContainer container, Level level) {
        return this.base.test(container.getItem(0));
    }

    @Override
    public ItemStack assemble(SimpleContainer container, RegistryAccess registries) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registries) {
        return this.result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, this.base);
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.BOBO_SOAK_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.BOBO_SOAK_TYPE.get();
    }
}

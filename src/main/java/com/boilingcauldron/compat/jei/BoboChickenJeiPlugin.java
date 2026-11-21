/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：JEI 插件入口——注册「钵钵鸡浸泡」分类、配方、信息页与催化剂。
 * 说明：配方从数据包（RecipeManager）读取 boiling-cauldron:bobo_soak，不做任何硬编码。
 *       仅在安装 JEI 时被加载，未安装 JEI 不影响模组运行。
 * 更新：2026-11-21
 */
package com.boilingcauldron.compat.jei;

import com.boilingcauldron.BoilingCauldron;
import com.boilingcauldron.block.ModBlocks;
import com.boilingcauldron.recipe.BoboSoakRecipe;
import com.boilingcauldron.recipe.ModRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;

import java.util.List;

@JeiPlugin
public class BoboChickenJeiPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return BoilingCauldron.id("jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
            new BoboSoakCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<BoboSoakRecipe> recipes = collectRecipes();
        if (recipes.isEmpty()) {
            return;
        }
        registration.addRecipes(BoboSoakCategory.RECIPE_TYPE, recipes);

        // 信息页：按 U 查询底料时，除配方外再给出玩法说明
        for (BoboSoakRecipe recipe : recipes) {
            List<ItemStack> bases = recipe.getBase().getItems().length == 0
                ? List.of()
                : List.of(recipe.getBase().getItems());
            if (!bases.isEmpty()) {
                registration.addIngredientInfo(bases, mezz.jei.api.constants.VanillaTypes.ITEM_STACK,
                    Component.translatable("jei.boiling-cauldron.bobo_soak.info", recipe.getUses()));
            }
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(
            new ItemStack(ModBlocks.BOBO_CHICKEN_POT.get()),
            BoboSoakCategory.RECIPE_TYPE);
    }

    /** 从当前世界的配方管理器读取；世界未加载时返回空列表（JEI 会在进入世界后重载） */
    private static List<BoboSoakRecipe> collectRecipes() {
        Level level = Minecraft.getInstance().level;
        if (level == null) {
            return List.of();
        }
        RecipeManager manager = level.getRecipeManager();
        return manager.getAllRecipesFor(ModRecipes.BOBO_SOAK_TYPE.get());
    }
}

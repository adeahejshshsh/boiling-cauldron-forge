package com.adeahejshshsh.boilingcauldron.compat.jei;

import com.adeahejshshsh.boilingcauldron.BoilingCauldron;
import com.adeahejshshsh.boilingcauldron.block.ModBlocks;
import com.adeahejshshsh.boilingcauldron.recipe.BoboSoakRecipe;
import com.adeahejshshsh.boilingcauldron.recipe.ModRecipes;
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

/** JEI 插件入口：配方从 RecipeManager 读取，不做硬编码。 */
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
        // 自然生成物的「去哪里找」说明，与是否有自定义配方无关，先注册
        ModJeiInfo.register(registration);

        List<BoboSoakRecipe> recipes = collectRecipes();
        if (recipes.isEmpty()) {
            return;
        }
        registration.addRecipes(BoboSoakCategory.RECIPE_TYPE, recipes);

        for (BoboSoakRecipe recipe : recipes) {
            ItemStack[] bases = recipe.getBase().getItems();
            if (bases.length > 0) {
                registration.addIngredientInfo(List.of(bases),
                    mezz.jei.api.constants.VanillaTypes.ITEM_STACK,
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

    private static List<BoboSoakRecipe> collectRecipes() {
        Level level = Minecraft.getInstance().level;
        if (level == null) {
            return List.of();
        }
        RecipeManager manager = level.getRecipeManager();
        return manager.getAllRecipesFor(ModRecipes.BOBO_SOAK_TYPE.get())
            .stream()
            .map(holder -> holder.value())
            .toList();
    }
}

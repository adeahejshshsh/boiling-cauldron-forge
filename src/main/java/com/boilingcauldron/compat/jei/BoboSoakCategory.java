/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：JEI 配方分类「钵钵鸡浸泡」，直接展示数据包中的 bobo_soak 配方。
 * 说明：不再硬编码任何口味/产物，全部读取 boiling-cauldron:bobo_soak 配方。
 * 更新：2026-11-21
 */
package com.boilingcauldron.compat.jei;

import com.boilingcauldron.BoilingCauldron;
import com.boilingcauldron.item.ModItems;
import com.boilingcauldron.recipe.BoboSoakRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class BoboSoakCategory implements IRecipeCategory<BoboSoakRecipe> {

    public static final RecipeType<BoboSoakRecipe> RECIPE_TYPE = RecipeType.create(
        BoilingCauldron.NAMESPACE, "bobo_soak", BoboSoakRecipe.class);

    private static final int WIDTH = 132;
    private static final int HEIGHT = 54;
    private static final int TEXT_COLOR = 0x404040;

    private final IDrawable background;
    private final IDrawable icon;

    public BoboSoakCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModItems.BOBO_CHICKEN.get()));
    }

    @Override
    public RecipeType<BoboSoakRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.boiling-cauldron.bobo_soak");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BoboSoakRecipe recipe, IFocusGroup focuses) {
        // 输入：底料（来自配方 base，支持 item 或 tag）
        builder.addInputSlot(6, 20)
            .addIngredients(recipe.getBase());

        // 输入：任意食物（Forge 没有 food 标签，这里直接枚举所有可食用物品）
        builder.addInputSlot(54, 20)
            .addIngredients(VanillaTypes.ITEM_STACK, edibleItems());

        // 输出：配方 result
        builder.addOutputSlot(104, 20)
            .addIngredients(VanillaTypes.ITEM_STACK, List.of(recipe.getResult().copy()));
    }

    @Override
    public void draw(BoboSoakRecipe recipe, IRecipeSlotsView slotsView, GuiGraphics guiGraphics,
                     double mouseX, double mouseY) {
        var font = Minecraft.getInstance().font;
        int seconds = Math.max(1, recipe.getSoakTime() / 20);
        guiGraphics.drawString(font,
            Component.translatable("jei.boiling-cauldron.bobo_soak.time", seconds)
                .withStyle(ChatFormatting.DARK_GRAY),
            6, 4, TEXT_COLOR, false);
        guiGraphics.drawString(font,
            Component.translatable("jei.boiling-cauldron.bobo_soak.uses", recipe.getUses())
                .withStyle(ChatFormatting.DARK_GRAY),
            6, 42, TEXT_COLOR, false);
        guiGraphics.drawString(font, "→", 34, 22, TEXT_COLOR, false);
        guiGraphics.drawString(font, "→", 86, 22, TEXT_COLOR, false);
    }

    /** 所有可食用物品；与方块侧 canAcceptFood 的 stack.isEdible() 判定一致 */
    private static List<ItemStack> edibleItems() {
        List<ItemStack> items = new ArrayList<>();
        for (Item item : ForgeRegistries.ITEMS) {
            if (item.isEdible()) {
                items.add(new ItemStack(item));
            }
        }
        return items;
    }

    @Override
    public ResourceLocation getRegistryName(BoboSoakRecipe recipe) {
        return recipe.getId();
    }
}

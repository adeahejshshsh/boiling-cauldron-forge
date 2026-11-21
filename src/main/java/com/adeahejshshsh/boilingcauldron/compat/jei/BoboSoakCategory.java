package com.adeahejshshsh.boilingcauldron.compat.jei;

import com.adeahejshshsh.boilingcauldron.BoilingCauldron;
import com.adeahejshshsh.boilingcauldron.item.ModItems;
import com.adeahejshshsh.boilingcauldron.recipe.BoboSoakRecipe;
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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/** JEI 分类「钵钵鸡浸泡」，直接展示数据包中的 bobo_soak 配方。 */
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
        builder.addInputSlot(6, 20).addIngredients(recipe.getBase());
        builder.addInputSlot(54, 20).addIngredients(VanillaTypes.ITEM_STACK, edibleItems());
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

    @Override
    public ResourceLocation getRegistryName(BoboSoakRecipe recipe) {
        return BoilingCauldron.id("bobo_soak/" + recipe.getFlavor().id());
    }

    /** 所有可食用物品；与方块侧判定一致 */
    private static List<ItemStack> edibleItems() {
        List<ItemStack> items = new ArrayList<>();
        for (Item item : BuiltInRegistries.ITEM) {
            if (item.components().has(net.minecraft.core.component.DataComponents.FOOD)) {
                items.add(new ItemStack(item));
            }
        }
        return items;
    }
}

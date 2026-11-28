package com.adeahejshshsh.boilingcauldron.datagen;

import com.adeahejshshsh.boilingcauldron.BoilingCauldron;
import com.adeahejshshsh.boilingcauldron.block.ModBlocks;
import com.adeahejshshsh.boilingcauldron.item.FlavorProfile;
import com.adeahejshshsh.boilingcauldron.item.ModItems;
import com.adeahejshshsh.boilingcauldron.recipe.BoboSoakRecipe;
import com.adeahejshshsh.boilingcauldron.tag.ModTags;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/** 配方生成：原版合成 + 自定义 bobo_soak。 */
public class ModRecipeProvider extends RecipeProvider {

    private final CompletableFuture<HolderLookup.Provider> registries;

    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
        this.registries = registries;
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.BAMBOO_ROOT.get())
            .pattern("BBB").pattern("BDB").pattern("BBB")
            .define('B', Items.BAMBOO).define('D', Items.DIRT)
            .unlockedBy("has_bamboo", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BAMBOO))
            .save(output, BoilingCauldron.id("bamboo_root"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, ModBlocks.BLOOMING_LOTUS_LEAF.get())
            .requires(Blocks.LILY_PAD).requires(net.minecraft.tags.ItemTags.FLOWERS)
            .unlockedBy("has_lily_pad", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.LILY_PAD))
            .save(output, BoilingCauldron.id("blooming_lotus_leaf"));

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.BOBO_CHICKEN_POT.get())
            .pattern("BBB").pattern("BTB").pattern("BBB")
            .define('B', Items.BRICK).define('T', Items.BOWL)
            .unlockedBy("has_brick", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BRICK))
            .save(output, BoilingCauldron.id("bobo_chicken_pot"));

        // 麻辣烫锅：8 红砖 + 1 炼药锅
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.MALATANG_POT.get())
            .pattern("BBB").pattern("BCB").pattern("BBB")
            .define('B', Items.BRICK).define('C', Items.CAULDRON)
            .unlockedBy("has_brick", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BRICK))
            .save(output, BoilingCauldron.id("malatang_pot"));

        // 幻色五香粉：花椒 + 姜 + 蒜 + 糖 + 墨囊 + 任意染料
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.PHANTOM_FIVE_SPICE_POWDER.get())
            .requires(ModItems.SICHUAN_PEPPER.get())
            .requires(ModItems.GINGER.get())
            .requires(ModItems.GARLIC.get())
            .requires(Items.SUGAR)
            .requires(Items.GLOW_INK_SAC)
            .requires(ModTags.DYES)
            .unlockedBy("has_sichuan_pepper",
                InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.SICHUAN_PEPPER.get()))
            .save(output, BoilingCauldron.id("phantom_five_spice_powder"));

        seedRecipe(output, ModItems.GINGER.get(), ModItems.GINGER_SEEDS.get(), "ginger");
        seedRecipe(output, ModItems.GARLIC.get(), ModItems.GARLIC_SEEDS.get(), "garlic");
        seedRecipe(output, ModItems.GONG_CAI.get(), ModItems.GONG_CAI_SEEDS.get(), "gong_cai");
        seedRecipe(output, ModItems.SICHUAN_PEPPER.get(), ModItems.SICHUAN_PEPPER_SEEDS.get(), "pepper");

        HolderLookup.Provider lookup = this.registries.join();
        boboSoak(output, lookup, "mushroom", ModItems.MUSHROOM_BASE.get(),
            Component.translatable("tooltip.boiling-cauldron.bobo_chicken.flavor.mushroom"),
            0, 0xC77B3E, MobEffects.REGENERATION);
        boboSoak(output, lookup, "mala", ModItems.MALA_BASE.get(),
            Component.translatable("tooltip.boiling-cauldron.bobo_chicken.flavor.mala"),
            1, 0xE03A2F, MobEffects.DAMAGE_BOOST);
        boboSoak(output, lookup, "rattan_pepper", ModItems.RATTAN_PEPPER_BASE.get(),
            Component.translatable("tooltip.boiling-cauldron.bobo_chicken.flavor.rattan_pepper"),
            2, 0x5FAE3F, MobEffects.DAMAGE_RESISTANCE);
    }

    private void seedRecipe(RecipeOutput output, Item crop, Item seeds, String name) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, seeds)
            .requires(crop)
            .unlockedBy("has_" + name, InventoryChangeTrigger.TriggerInstance.hasItems(crop))
            .save(output, BoilingCauldron.id(name));
    }

    private void boboSoak(RecipeOutput output, HolderLookup.Provider lookup, String name, Item base,
                          Component flavorName, int sort, int color, Holder<MobEffect> effect) {
        BoboSoakRecipe recipe = new BoboSoakRecipe(
            Ingredient.of(base),
            new FlavorProfile(name, flavorName, sort, color,
                List.of(new MobEffectInstance(effect, 20 * 30, 0))),
            new ItemStack(ModItems.BOBO_CHICKEN.get()),
            20 * 60,
            64);
        output.accept(BoilingCauldron.id("bobo_soak/" + name), recipe,
            output.advancement()
                .addCriterion("has_" + name, InventoryChangeTrigger.TriggerInstance.hasItems(base))
                .build(BoilingCauldron.id("recipes/bobo_soak/" + name)));
    }
}

/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：配方生成——原版合成配方 + 自定义 bobo_soak 配方。
 * 说明：森罗厨房的 3 个炒锅配方仍以静态 JSON 提供（需 KC 的配方格式）。
 * 更新：2026-11-21
 */
package com.boilingcauldron.datagen;

import com.boilingcauldron.BoilingCauldron;
import com.boilingcauldron.block.ModBlocks;
import com.boilingcauldron.item.ModItems;
import com.boilingcauldron.tag.ModTags;
import com.boilingcauldron.recipe.BoboSoakRecipe;
import com.boilingcauldron.recipe.ModRecipes;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {

    public ModRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> output) {
        // 竹根殖块：8 竹子 + 1 泥土
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.BAMBOO_ROOT.get())
            .pattern("BBB").pattern("BDB").pattern("BBB")
            .define('B', Items.BAMBOO).define('D', Items.DIRT)
            .unlockedBy("has_bamboo", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BAMBOO))
            .save(output, BoilingCauldron.id("bamboo_root"));

        // 盛花荷叶：荷叶 + 任意花
        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, ModBlocks.BLOOMING_LOTUS_LEAF.get())
            .requires(Blocks.LILY_PAD).requires(ItemTags.FLOWERS)
            .unlockedBy("has_lily_pad", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.LILY_PAD))
            .save(output, BoilingCauldron.id("blooming_lotus_leaf"));

        // 钵钵鸡锅：8 红砖 + 1 碗
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

        // 作物 → 种子
        seedRecipe(output, ModItems.GINGER.get(), ModItems.GINGER_SEEDS.get(), "ginger");
        seedRecipe(output, ModItems.GARLIC.get(), ModItems.GARLIC_SEEDS.get(), "garlic");
        seedRecipe(output, ModItems.GONG_CAI.get(), ModItems.GONG_CAI_SEEDS.get(), "gong_cai");
        seedRecipe(output, ModItems.SICHUAN_PEPPER.get(), ModItems.SICHUAN_PEPPER_SEEDS.get(), "pepper");

        // 自定义配方：底料 → 钵钵鸡（数据包驱动）
        boboSoak(output, "mushroom", ModItems.MUSHROOM_BASE.get(),
            Component.translatable("tooltip.boiling-cauldron.bobo_chicken.flavor.mushroom"),
            0, 0xC77B3E, MobEffects.REGENERATION);
        boboSoak(output, "mala", ModItems.MALA_BASE.get(),
            Component.translatable("tooltip.boiling-cauldron.bobo_chicken.flavor.mala"),
            1, 0xE03A2F, MobEffects.DAMAGE_BOOST);
        boboSoak(output, "rattan_pepper", ModItems.RATTAN_PEPPER_BASE.get(),
            Component.translatable("tooltip.boiling-cauldron.bobo_chicken.flavor.rattan_pepper"),
            2, 0x5FAE3F, MobEffects.DAMAGE_RESISTANCE);
    }

    private void seedRecipe(Consumer<FinishedRecipe> output, Item crop, Item seeds, String name) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, seeds)
            .requires(crop)
            .unlockedBy("has_" + name, InventoryChangeTrigger.TriggerInstance.hasItems(crop))
            .save(output, BoilingCauldron.id(name));
    }

    private void boboSoak(Consumer<FinishedRecipe> output, String name, Item base, Component flavorName,
                           int sort, int color, net.minecraft.world.effect.MobEffect effect) {
        BoboSoakRecipe recipe = new BoboSoakRecipe(
            com.boilingcauldron.BoilingCauldron.id("bobo_soak/" + name),
            Ingredient.of(base),
            new com.boilingcauldron.item.FlavorProfile(
                name, flavorName, sort, color,
                List.of(new MobEffectInstance(effect, 20 * 30, 0))),
            new ItemStack(ModItems.BOBO_CHICKEN.get()),
            20 * 60,
            64);
        output.accept(new BoboSoakFinishedRecipe(recipe));
    }

    /** 把 BoboSoakRecipe 写成数据包 JSON */
    private record BoboSoakFinishedRecipe(BoboSoakRecipe recipe) implements FinishedRecipe {

        @Override
        public void serializeRecipeData(JsonObject json) {
            json.add("base", recipe.getBase().toJson());

            JsonObject flavor = new JsonObject();
            flavor.addProperty("id", recipe.getFlavor().id());
            flavor.add("name", Component.Serializer.toJsonTree(recipe.getFlavor().name()));
            flavor.addProperty("sort", recipe.getFlavor().sort());
            flavor.addProperty("color", String.format("#%06X", recipe.getFlavor().color() & 0xFFFFFF));
            JsonArray effects = new JsonArray();
            for (MobEffectInstance instance : recipe.getFlavor().effects()) {
                JsonObject effect = new JsonObject();
                ResourceLocation id = net.minecraftforge.registries.ForgeRegistries.MOB_EFFECTS
                    .getKey(instance.getEffect());
                effect.addProperty("effect", id == null ? "minecraft:luck" : id.toString());
                effect.addProperty("duration", instance.getDuration());
                effect.addProperty("amplifier", instance.getAmplifier());
                effects.add(effect);
            }
            flavor.add("effects", effects);
            json.add("flavor", flavor);

            JsonObject result = new JsonObject();
            ResourceLocation itemId = net.minecraftforge.registries.ForgeRegistries.ITEMS
                .getKey(recipe.getResult().getItem());
            result.addProperty("item", itemId == null ? "minecraft:air" : itemId.toString());
            json.add("result", result);

            json.addProperty("soak_time", recipe.getSoakTime());
            json.addProperty("uses", recipe.getUses());
        }

        @Override
        public ResourceLocation getId() {
            return recipe.getId();
        }

        @Override
        public RecipeSerializer<?> getType() {
            return ModRecipes.BOBO_SOAK_SERIALIZER.get();
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return null;
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return null;
        }
    }
}

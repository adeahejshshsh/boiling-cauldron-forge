package com.adeahejshshsh.boilingcauldron.recipe;

import com.adeahejshshsh.boilingcauldron.BoilingCauldron;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/** 注册自定义配方类型与序列化器。 */
public class ModRecipes {

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
        DeferredRegister.create(Registries.RECIPE_TYPE, BoilingCauldron.NAMESPACE);

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
        DeferredRegister.create(Registries.RECIPE_SERIALIZER, BoilingCauldron.NAMESPACE);

    public static final Supplier<RecipeType<BoboSoakRecipe>> BOBO_SOAK_TYPE =
        RECIPE_TYPES.register("bobo_soak",
            () -> RecipeType.simple(BoilingCauldron.id("bobo_soak")));

    public static final Supplier<RecipeSerializer<BoboSoakRecipe>> BOBO_SOAK_SERIALIZER =
        RECIPE_SERIALIZERS.register("bobo_soak", BoboSoakSerializer::new);

    /** 隐藏成品原料的特殊合成配方 boiling-cauldron:mask_dish */
    public static final Supplier<RecipeSerializer<MaskDishRecipe>> MASK_DISH_SERIALIZER =
        RECIPE_SERIALIZERS.register("mask_dish", MaskDishSerializer::new);

    public static void register(IEventBus modBus) {
        RECIPE_TYPES.register(modBus);
        RECIPE_SERIALIZERS.register(modBus);
    }

    /** 1.21 的序列化器只需提供 Codec 与 StreamCodec。 */
    public static class BoboSoakSerializer implements RecipeSerializer<BoboSoakRecipe> {

        @Override
        public MapCodec<BoboSoakRecipe> codec() {
            return BoboSoakRecipe.CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BoboSoakRecipe> streamCodec() {
            return BoboSoakRecipe.STREAM_CODEC;
        }
    }

    /** mask_dish 序列化器：无状态配方，Codec 用 unit。 */
    public static class MaskDishSerializer implements RecipeSerializer<MaskDishRecipe> {

        @Override
        public MapCodec<MaskDishRecipe> codec() {
            return MaskDishRecipe.CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, MaskDishRecipe> streamCodec() {
            return MaskDishRecipe.STREAM_CODEC;
        }
    }
}

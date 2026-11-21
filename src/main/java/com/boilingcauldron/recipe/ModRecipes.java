/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：注册自定义配方类型与序列化器（数据包 JSON 驱动）。
 * 更新：2026-11-21
 */
package com.boilingcauldron.recipe;

import com.boilingcauldron.BoilingCauldron;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
        DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, BoilingCauldron.NAMESPACE);

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
        DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, BoilingCauldron.NAMESPACE);

    /** 配方类型 id：boiling-cauldron:bobo_soak */
    public static final RegistryObject<RecipeType<BoboSoakRecipe>> BOBO_SOAK_TYPE =
        RECIPE_TYPES.register("bobo_soak",
            () -> RecipeType.simple(BoilingCauldron.id("bobo_soak")));

    public static final RegistryObject<RecipeSerializer<BoboSoakRecipe>> BOBO_SOAK_SERIALIZER =
        RECIPE_SERIALIZERS.register("bobo_soak", BoboSoakRecipeSerializer::new);

    public static void register(IEventBus modBus) {
        RECIPE_TYPES.register(modBus);
        RECIPE_SERIALIZERS.register(modBus);
    }

    /** 序列化器：负责把数据包 JSON / 网络包读成 BoboSoakRecipe */
    public static class BoboSoakRecipeSerializer implements RecipeSerializer<BoboSoakRecipe> {

        @Override
        public BoboSoakRecipe fromJson(ResourceLocation id, JsonObject json) {
            return BoboSoakRecipeJson.read(id, json);
        }

        @Override
        public BoboSoakRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            return BoboSoakRecipeJson.read(id, buf);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, BoboSoakRecipe recipe) {
            BoboSoakRecipeJson.write(buf, recipe);
        }
    }
}

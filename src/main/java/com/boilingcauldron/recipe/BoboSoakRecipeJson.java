/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：BoboSoakRecipe 的 JSON / 网络读写实现。
 * 说明：口味（id / 显示名 / 排序 / 效果）全部从数据包读取，
 *       因此数据包可新增任意口味，无需修改代码。
 * 更新：2026-11-21
 */
package com.boilingcauldron.recipe;

import com.boilingcauldron.item.FlavorProfile;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

final class BoboSoakRecipeJson {

    private static final String KEY_BASE = "base";
    private static final String KEY_FLAVOR = "flavor";
    private static final String KEY_RESULT = "result";
    private static final String KEY_SOAK_TIME = "soak_time";
    private static final String KEY_USES = "uses";

    private static final String F_ID = "id";
    private static final String F_NAME = "name";
    private static final String F_SORT = "sort";
    private static final String F_COLOR = "color";
    private static final String F_EFFECTS = "effects";
    private static final String E_EFFECT = "effect";
    private static final String E_DURATION = "duration";
    private static final String E_AMPLIFIER = "amplifier";

    private static final int DEFAULT_SOAK_TIME = 20 * 60;
    private static final int DEFAULT_USES = 64;

    private BoboSoakRecipeJson() {
    }

    // ---------------- JSON ----------------

    static BoboSoakRecipe read(ResourceLocation id, JsonObject json) {
        if (!json.has(KEY_BASE)) {
            throw new JsonSyntaxException("Recipe '" + id + "' is missing required field '" + KEY_BASE + "'");
        }
        Ingredient base = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, KEY_BASE));
        FlavorProfile flavor = readFlavor(id, GsonHelper.getAsJsonObject(json, KEY_FLAVOR));
        ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, KEY_RESULT));

        int soakTime = GsonHelper.getAsInt(json, KEY_SOAK_TIME, DEFAULT_SOAK_TIME);
        int uses = GsonHelper.getAsInt(json, KEY_USES, DEFAULT_USES);
        if (soakTime <= 0) {
            throw new JsonSyntaxException("Recipe '" + id + "' has non-positive soak_time");
        }
        if (uses <= 0) {
            throw new JsonSyntaxException("Recipe '" + id + "' has non-positive uses");
        }
        return new BoboSoakRecipe(id, base, flavor, result, soakTime, uses);
    }

    private static FlavorProfile readFlavor(ResourceLocation recipeId, JsonObject json) {
        String flavorId = GsonHelper.getAsString(json, F_ID);
        if (flavorId.isBlank()) {
            throw new JsonSyntaxException("Recipe '" + recipeId + "' has an empty flavor id");
        }

        Component name;
        JsonElement nameJson = json.get(F_NAME);
        if (nameJson == null) {
            name = Component.literal(flavorId);
        } else if (nameJson.isJsonPrimitive()) {
            name = Component.literal(nameJson.getAsString());
        } else {
            name = Component.Serializer.fromJson(nameJson);
        }

        int sort = GsonHelper.getAsInt(json, F_SORT, 0);
        int color = readColor(json);

        List<MobEffectInstance> effects = new ArrayList<>();
        JsonArray effectsJson = json.has(F_EFFECTS)
            ? GsonHelper.getAsJsonArray(json, F_EFFECTS)
            : new JsonArray();
        for (JsonElement element : effectsJson) {
            if (!element.isJsonObject()) {
                throw new JsonSyntaxException("Recipe '" + recipeId + "' has a non-object entry in flavor.effects");
            }
            JsonObject effectJson = element.getAsJsonObject();
            ResourceLocation effectId = new ResourceLocation(GsonHelper.getAsString(effectJson, E_EFFECT));
            MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(effectId);
            if (effect == null) {
                throw new JsonSyntaxException("Recipe '" + recipeId + "' references unknown effect '"
                    + effectId + "'");
            }
            int duration = GsonHelper.getAsInt(effectJson, E_DURATION, 20 * 30);
            int amplifier = GsonHelper.getAsInt(effectJson, E_AMPLIFIER, 0);
            effects.add(new MobEffectInstance(effect, duration, amplifier));
        }
        return new FlavorProfile(flavorId, name, sort, color, List.copyOf(effects));
    }

    private static int readColor(JsonObject json) {
        JsonElement element = json.get(F_COLOR);
        if (element == null || element.isJsonNull()) {
            return 0x404040;
        }
        try {
            if (element.getAsJsonPrimitive().isString()) {
                String raw = element.getAsString().replace("#", "").replace("0x", "");
                return (int) Long.parseLong(raw, 16);
            }
            return element.getAsInt();
        } catch (RuntimeException ex) {
            throw new JsonSyntaxException("Invalid flavor color: " + element, ex);
        }
    }

    // ---------------- 网络 ----------------

    static BoboSoakRecipe read(ResourceLocation id, FriendlyByteBuf buf) {
        Ingredient base = Ingredient.fromNetwork(buf);

        String flavorId = buf.readUtf();
        Component name = Component.Serializer.fromJson(buf.readUtf());
        int sort = buf.readVarInt();
        int color = buf.readVarInt();
        int effectCount = buf.readVarInt();
        List<MobEffectInstance> effects = new ArrayList<>();
        for (int i = 0; i < effectCount; i++) {
            ResourceLocation effectId = buf.readResourceLocation();
            MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(effectId);
            int duration = buf.readVarInt();
            int amplifier = buf.readVarInt();
            if (effect != null) {
                effects.add(new MobEffectInstance(effect, duration, amplifier));
            }
        }
        FlavorProfile flavor = new FlavorProfile(flavorId, name, sort, color, List.copyOf(effects));

        ItemStack result = buf.readItem();
        int soakTime = buf.readVarInt();
        int uses = buf.readVarInt();
        return new BoboSoakRecipe(id, base, flavor, result, soakTime, uses);
    }

    static void write(FriendlyByteBuf buf, BoboSoakRecipe recipe) {
        recipe.getBase().toNetwork(buf);

        FlavorProfile flavor = recipe.getFlavor();
        buf.writeUtf(flavor.id());
        buf.writeUtf(Component.Serializer.toJson(flavor.name()));
        buf.writeVarInt(flavor.sort());
        buf.writeVarInt(flavor.color());
        buf.writeVarInt(flavor.effects().size());
        for (MobEffectInstance instance : flavor.effects()) {
            ResourceLocation effectId = ForgeRegistries.MOB_EFFECTS.getKey(instance.getEffect());
            buf.writeResourceLocation(effectId == null
                ? new ResourceLocation("minecraft", "luck") : effectId);
            buf.writeVarInt(instance.getDuration());
            buf.writeVarInt(instance.getAmplifier());
        }

        buf.writeItem(recipe.getResult());
        buf.writeVarInt(recipe.getSoakTime());
        buf.writeVarInt(recipe.getUses());
    }
}

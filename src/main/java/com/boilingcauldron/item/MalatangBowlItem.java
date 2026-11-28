/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：麻辣烫成品物品，处理逐次装填、NBT、效果合并、tooltip 与食用返还。
 * 移植：Fabric → Forge 1.20.1
 * 更新：2026-11-28 上游 1.0.1 新增
 */
package com.boilingcauldron.item;

import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MalatangBowlItem extends Item {

    public static final String BASE_FLAVOR_KEY = "BaseFlavor";
    public static final String INGREDIENTS_KEY = "Ingredients";
    public static final String EFFECTS_KEY = "Effects";
    public static final String HUNGER_KEY = "Hunger";
    public static final String SATURATION_KEY = "Saturation";
    public static final int MAX_INGREDIENTS = 4;
    private static final int BASE_EFFECT_DURATION = 30 * 20;

    public MalatangBowlItem(Properties properties) {
        super(properties);
    }

    // ---------------- 展示 ----------------

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        if (DishMasking.isMasked(stack)) {
            return;
        }

        FlavorProfile base = getBaseFlavor(stack);
        if (base != null) {
            tooltip.add(Component.translatable("tooltip.boiling-cauldron.malatang_bowl.base", base.name())
                .withStyle(ChatFormatting.GOLD));
        }

        for (String id : getIngredientIds(stack)) {
            Item item = resolveItem(id);
            Component name = item == Items.AIR
                ? Component.translatable("tooltip.boiling-cauldron.malatang_bowl.unknown")
                : item.getDescription();
            tooltip.add(Component.translatable("tooltip.boiling-cauldron.malatang_bowl.ingredient", name)
                .withStyle(ChatFormatting.GRAY));
        }

        for (EffectEntry entry : getEffects(stack)) {
            if (entry.effect() == null) {
                continue;
            }
            Component duration = entry.duration() < 0
                ? Component.translatable("tooltip.boiling-cauldron.malatang_bowl.infinite")
                : Component.translatable("tooltip.boiling-cauldron.malatang_bowl.duration", entry.duration() / 20);
            int chance = Math.round(clampChance(entry.chance()) * 100.0F);
            tooltip.add(Component.translatable("tooltip.boiling-cauldron.malatang_bowl.effect",
                    entry.effect().getDisplayName(), entry.amplifier() + 1, duration, chance)
                .withStyle(ChatFormatting.BLUE));
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return DishMasking.isMasked(stack) || super.isFoil(stack);
    }

    // ---------------- 食用 ----------------

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
        int food = Math.max(0, getHunger(stack));
        float saturation = Math.max(0.0F, getSaturation(stack));
        List<EffectEntry> effects = List.copyOf(getEffects(stack));
        ItemStack result = super.finishUsingItem(stack, level, user);

        if (!level.isClientSide) {
            if (user instanceof Player player) {
                FoodData data = player.getFoodData();
                int newFood = Math.min(20, data.getFoodLevel() + food);
                float newSaturation = Math.min((float) newFood, data.getSaturationLevel() + saturation);
                data.setFoodLevel(newFood);
                data.setSaturation(newSaturation);
            }

            for (EffectEntry entry : effects) {
                if (entry.effect() != null
                    && (entry.chance() >= 1.0F || level.random.nextFloat() < clampChance(entry.chance()))) {
                    user.addEffect(new MobEffectInstance(entry.effect(), entry.duration(), entry.amplifier()));
                }
            }
        }
        // 吃完返还碗
        return new ItemStack(Items.BOWL);
    }

    // ---------------- 构建 ----------------

    public static ItemStack create(FlavorProfile base, ItemStack ingredient) {
        if (base == null || ingredient.isEmpty() || !ingredient.isEdible()) {
            return ItemStack.EMPTY;
        }

        ItemStack result = new ItemStack(ModItems.MALATANG_BOWL.get());
        CompoundTag tag = result.getOrCreateTag();
        tag.putString(BASE_FLAVOR_KEY, base.id());
        tag.putInt(HUNGER_KEY, 0);
        tag.putFloat(SATURATION_KEY, 0.0F);
        writeIngredients(result, List.of());
        writeEffects(result, List.of());

        addEffect(result, base.effects(), BASE_EFFECT_DURATION);
        if (!addIngredient(result, ingredient)) {
            return ItemStack.EMPTY;
        }
        return result;
    }

    /** 把一份已煮好的食材并入碗中，同时累加饱食度与效果。 */
    public static boolean addIngredient(ItemStack bowl, ItemStack ingredient) {
        if (bowl.isEmpty() || !(bowl.getItem() instanceof MalatangBowlItem)) {
            return false;
        }

        List<String> ingredients = new ArrayList<>(getIngredientIds(bowl));
        if (ingredients.size() >= MAX_INGREDIENTS) {
            return false;
        }

        FoodProperties food = ingredient.getItem().getFoodProperties();
        if (food == null) {
            return false;
        }

        ingredients.add(BuiltInRegistries.ITEM.getKey(ingredient.getItem()).toString());
        writeIngredients(bowl, ingredients);

        CompoundTag tag = bowl.getOrCreateTag();
        tag.putInt(HUNGER_KEY, getHunger(bowl) + food.getNutrition());
        tag.putFloat(SATURATION_KEY, getSaturation(bowl)
            + food.getNutrition() * food.getSaturationModifier() * 2.0F);

        for (Pair<MobEffectInstance, Float> entry : food.getEffects()) {
            MobEffectInstance effect = entry.getFirst();
            if (effect != null) {
                addOneEffect(bowl, effect.getEffect(), effect.getDuration(), effect.getAmplifier(),
                    entry.getSecond());
            }
        }
        return true;
    }

    public static boolean isFull(ItemStack stack) {
        return getIngredientIds(stack).size() >= MAX_INGREDIENTS;
    }

    public static boolean matchesBase(ItemStack stack, @Nullable FlavorProfile base) {
        if (base == null) {
            return false;
        }
        FlavorProfile current = getBaseFlavor(stack);
        return current != null && current.id().equals(base.id());
    }

    // ---------------- NBT 读取 ----------------

    @Nullable
    public static FlavorProfile getBaseFlavor(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return null;
        }
        if (tag.contains(BASE_FLAVOR_KEY, Tag.TAG_COMPOUND)) {
            return FlavorProfile.load(tag.getCompound(BASE_FLAVOR_KEY));
        }
        // 兼容旧格式：只存口味 id
        if (tag.contains(BASE_FLAVOR_KEY, Tag.TAG_STRING)) {
            String id = tag.getString(BASE_FLAVOR_KEY);
            return id.isEmpty()
                ? null
                : new FlavorProfile(id, Component.literal(id), 0, 0x404040, List.of());
        }
        return null;
    }

    public static List<String> getIngredientIds(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return List.of();
        }
        ListTag list = tag.getList(INGREDIENTS_KEY, Tag.TAG_STRING);
        List<String> ids = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            ids.add(list.getString(i));
        }
        return ids;
    }

    public static List<EffectEntry> getEffects(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return List.of();
        }
        ListTag list = tag.getList(EFFECTS_KEY, Tag.TAG_COMPOUND);
        List<EffectEntry> effects = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            ResourceLocation id = ResourceLocation.tryParse(entry.getString("Id"));
            if (id == null) {
                continue;
            }
            MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(id);
            if (effect == null) {
                continue;
            }
            effects.add(new EffectEntry(effect, entry.getInt("Duration"), entry.getInt("Amplifier"),
                entry.getFloat("Chance")));
        }
        return effects;
    }

    public static int getHunger(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag == null ? 0 : Math.max(0, tag.getInt(HUNGER_KEY));
    }

    public static float getSaturation(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag == null ? 0.0F : Math.max(0.0F, tag.getFloat(SATURATION_KEY));
    }

    // ---------------- NBT 写入 ----------------

    private static void addEffect(ItemStack stack, List<MobEffectInstance> effects, int defaultDuration) {
        for (MobEffectInstance instance : effects) {
            addOneEffect(stack, instance.getEffect(), defaultDuration, instance.getAmplifier(), 1.0F);
        }
    }

    private static void addOneEffect(ItemStack stack, MobEffect effect, int duration, int amplifier, float chance) {
        if (effect == null) {
            return;
        }
        List<EffectEntry> effects = new ArrayList<>(getEffects(stack));
        EffectEntry candidate = new EffectEntry(effect, duration, amplifier, clampChance(chance));
        for (int i = 0; i < effects.size(); i++) {
            EffectEntry current = effects.get(i);
            if (current.effect() == effect) {
                if (compare(candidate, current) > 0) {
                    effects.set(i, candidate);
                    writeEffects(stack, effects);
                }
                return;
            }
        }
        effects.add(candidate);
        writeEffects(stack, effects);
    }

    private static int compare(EffectEntry first, EffectEntry second) {
        int result = Integer.compare(first.amplifier(), second.amplifier());
        if (result != 0) {
            return result;
        }
        result = Integer.compare(durationRank(first.duration()), durationRank(second.duration()));
        if (result != 0) {
            return result;
        }
        return Float.compare(first.chance(), second.chance());
    }

    private static int durationRank(int duration) {
        return duration < 0 ? Integer.MAX_VALUE : duration;
    }

    private static float clampChance(float chance) {
        return Math.max(0.0F, Math.min(1.0F, chance));
    }

    private static void writeIngredients(ItemStack stack, List<String> ingredients) {
        ListTag list = new ListTag();
        for (String ingredient : ingredients) {
            list.add(net.minecraft.nbt.StringTag.valueOf(ingredient));
        }
        stack.getOrCreateTag().put(INGREDIENTS_KEY, list);
    }

    private static void writeEffects(ItemStack stack, List<EffectEntry> effects) {
        ListTag list = new ListTag();
        for (EffectEntry effect : effects) {
            ResourceLocation id = BuiltInRegistries.MOB_EFFECT.getKey(effect.effect());
            if (id == null) {
                continue;
            }
            CompoundTag entry = new CompoundTag();
            entry.putString("Id", id.toString());
            entry.putInt("Duration", effect.duration());
            entry.putInt("Amplifier", effect.amplifier());
            entry.putFloat("Chance", effect.chance());
            list.add(entry);
        }
        stack.getOrCreateTag().put(EFFECTS_KEY, list);
    }

    private static Item resolveItem(String id) {
        ResourceLocation location = ResourceLocation.tryParse(id);
        return location == null ? Items.AIR : BuiltInRegistries.ITEM.get(location);
    }

    public record EffectEntry(MobEffect effect, int duration, int amplifier, float chance) {
    }
}

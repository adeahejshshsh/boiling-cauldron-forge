/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：钵钵鸡成品物品，处理 NBT 口味、显示名、tooltip 和食用效果。
 * 说明：口味完全来自数据包配方（FlavorProfile），物品 NBT 里保存口味档案，
 *       因此数据包新增口味无需改代码。
 * 更新：2026-11-21
 */
package com.boilingcauldron.item;

import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BoboChickenItem extends Item {
    public static final String ORIGINAL_ITEM_KEY = "OriginalItem";
    public static final String FLAVORS_KEY = "Flavors";
    public static final int MAX_FLAVORS = 3;

    public BoboChickenItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        List<FlavorProfile> flavors = getFlavors(stack);
        if (flavors.isEmpty()) {
            return super.getName(stack);
        }
        if (flavors.size() >= MAX_FLAVORS) {
            return Component.translatable("item.boiling-cauldron.bobo_chicken.three_flavors");
        }

        MutableComponent names = Component.empty();
        for (int i = 0; i < flavors.size(); i++) {
            if (i > 0) {
                names.append(Component.translatable("item.boiling-cauldron.bobo_chicken.joiner"));
            }
            names.append(flavors.get(i).name());
        }
        names.append(Component.translatable("item.boiling-cauldron.bobo_chicken.suffix"));
        return names;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        Item original = getOriginalItem(stack);
        if (original != Items.AIR) {
            tooltip.add(Component.translatable("tooltip.boiling-cauldron.bobo_chicken.original",
                original.getDescription()));
        }

        List<FlavorProfile> flavors = getFlavors(stack);
        if (!flavors.isEmpty()) {
            MutableComponent names = Component.empty();
            for (int i = 0; i < flavors.size(); i++) {
                if (i > 0) {
                    names.append(Component.translatable("tooltip.boiling-cauldron.bobo_chicken.separator"));
                }
                names.append(flavors.get(i).name());
            }
            tooltip.add(Component.translatable("tooltip.boiling-cauldron.bobo_chicken.flavors", names)
                .withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
        List<FlavorProfile> flavors = List.copyOf(getFlavors(stack));
        Item original = getOriginalItem(stack);
        ItemStack result = super.finishUsingItem(stack, level, user);

        if (!level.isClientSide) {
            if (user instanceof Player player) {
                FoodData food = player.getFoodData();
                food.setSaturation(Math.min(food.getFoodLevel(), food.getSaturationLevel() + 2.0F));
            }
            applyOriginalFoodEffects(user, level, original);
            for (FlavorProfile flavor : flavors) {
                for (MobEffectInstance template : flavor.effects()) {
                    applyFlavorEffect(user, template);
                }
            }
        }
        return result;
    }

    private static void applyOriginalFoodEffects(LivingEntity user, Level level, Item original) {
        FoodProperties food = original.getFoodProperties();
        if (food == null) {
            return;
        }

        for (Pair<MobEffectInstance, Float> entry : food.getEffects()) {
            MobEffectInstance effect = entry.getFirst();
            if (effect != null && level.random.nextFloat() < entry.getSecond()) {
                user.addEffect(new MobEffectInstance(effect));
            }
        }
    }

    /** 不叠加规则：已有效果把剩余时长/等级都不低于本次时不处理，否则刷新 */
    private static void applyFlavorEffect(LivingEntity user, MobEffectInstance template) {
        MobEffectInstance current = user.getEffect(template.getEffect());
        if (current == null
            || (current.getDuration() < template.getDuration()
                && current.getAmplifier() <= template.getAmplifier())) {
            user.addEffect(new MobEffectInstance(template));
        }
    }

    // ---------------- 口味读写 ----------------

    public static List<FlavorProfile> getFlavors(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return List.of();
        }

        ListTag list = tag.getList(FLAVORS_KEY, Tag.TAG_COMPOUND);
        List<FlavorProfile> flavors = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            FlavorProfile profile = FlavorProfile.load(list.getCompound(i));
            if (profile.id().isEmpty()) {
                continue;
            }
            if (flavors.stream().noneMatch(profile::sameAs) && flavors.size() < MAX_FLAVORS) {
                flavors.add(profile);
            }
        }
        flavors.sort(Comparator.comparingInt(FlavorProfile::sort).thenComparing(FlavorProfile::id));
        return flavors;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean canAddFlavor(ItemStack stack, FlavorProfile flavor) {
        List<FlavorProfile> flavors = getFlavors(stack);
        return flavors.size() < MAX_FLAVORS && flavors.stream().noneMatch(flavor::sameAs);
    }

    public static boolean addFlavor(ItemStack stack, FlavorProfile flavor) {
        if (!canAddFlavor(stack, flavor)) {
            return false;
        }

        CompoundTag tag = stack.getOrCreateTag();
        ListTag list = tag.getList(FLAVORS_KEY, Tag.TAG_COMPOUND);
        list.add(flavor.save());
        tag.put(FLAVORS_KEY, list);
        return true;
    }

    public static void setOriginalItem(ItemStack stack, Item item) {
        if (item == Items.AIR) {
            return;
        }
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
        if (id != null) {
            stack.getOrCreateTag().putString(ORIGINAL_ITEM_KEY, id.toString());
        }
    }

    public static Item getOriginalItem(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(ORIGINAL_ITEM_KEY, Tag.TAG_STRING)) {
            return Items.AIR;
        }

        ResourceLocation id = ResourceLocation.tryParse(tag.getString(ORIGINAL_ITEM_KEY));
        if (id == null || !ForgeRegistries.ITEMS.containsKey(id)) {
            return Items.AIR;
        }
        Item item = ForgeRegistries.ITEMS.getValue(id);
        return item == null ? Items.AIR : item;
    }
}

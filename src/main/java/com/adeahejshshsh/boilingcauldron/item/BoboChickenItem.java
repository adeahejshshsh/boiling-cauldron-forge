package com.adeahejshshsh.boilingcauldron.item;

import net.minecraft.core.component.DataComponents;
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
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** 钵钵鸡成品：口味存于 CUSTOM_DATA，显示名与效果由口味档案驱动。 */
public class BoboChickenItem extends Item {

    public static final String ORIGINAL_ITEM_KEY = "OriginalItem";
    public static final String FLAVORS_KEY = "Flavors";
    public static final int MAX_FLAVORS = 3;

    public BoboChickenItem(Properties properties) {
        super(properties);
    }

    // ---------- 口味读写 ----------

    public static CompoundTag copyTagPublic(ItemStack stack) {
        return copyTag(stack);
    }

    private static CompoundTag copyTag(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data == null ? new CompoundTag() : data.copyTag();
    }

    private static void writeTag(ItemStack stack, CompoundTag tag) {
        if (tag.isEmpty()) {
            stack.remove(DataComponents.CUSTOM_DATA);
        } else {
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
    }

    public static List<FlavorProfile> getFlavors(ItemStack stack) {
        CompoundTag tag = copyTag(stack);
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

    public static boolean canAddFlavor(ItemStack stack, FlavorProfile flavor) {
        List<FlavorProfile> flavors = getFlavors(stack);
        return flavors.size() < MAX_FLAVORS && flavors.stream().noneMatch(flavor::sameAs);
    }

    public static boolean addFlavor(ItemStack stack, FlavorProfile flavor) {
        if (!canAddFlavor(stack, flavor)) {
            return false;
        }
        CompoundTag tag = copyTag(stack);
        ListTag list = tag.getList(FLAVORS_KEY, Tag.TAG_COMPOUND);
        list.add(flavor.save());
        tag.put(FLAVORS_KEY, list);
        writeTag(stack, tag);
        return true;
    }

    public static void setOriginalItem(ItemStack stack, Item item) {
        if (item == Items.AIR) {
            return;
        }
        ResourceLocation id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item);
        CompoundTag tag = copyTag(stack);
        tag.putString(ORIGINAL_ITEM_KEY, id.toString());
        writeTag(stack, tag);
    }

    public static Item getOriginalItem(ItemStack stack) {
        CompoundTag tag = copyTag(stack);
        if (!tag.contains(ORIGINAL_ITEM_KEY, Tag.TAG_STRING)) {
            return Items.AIR;
        }
        ResourceLocation id = ResourceLocation.tryParse(tag.getString(ORIGINAL_ITEM_KEY));
        if (id == null) {
            return Items.AIR;
        }
        return net.minecraft.core.registries.BuiltInRegistries.ITEM.get(id);
    }

    // ---------- 名称与提示 ----------

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
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
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
            tooltip.add(Component.translatable("tooltip.boiling-cauldron.bobo_chicken.flavors", names));
        }
    }

    // ---------- 食用 ----------

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
        FoodProperties food = original.components().get(DataComponents.FOOD);
        if (food == null) {
            return;
        }
        for (FoodProperties.PossibleEffect entry : food.effects()) {
            MobEffectInstance effect = entry.effect();
            if (effect != null && level.random.nextFloat() < entry.probability()) {
                user.addEffect(new MobEffectInstance(effect));
            }
        }
    }

    private static void applyFlavorEffect(LivingEntity user, MobEffectInstance template) {
        MobEffectInstance current = user.getEffect(template.getEffect());
        if (current == null
            || (current.getDuration() < template.getDuration()
                && current.getAmplifier() <= template.getAmplifier())) {
            user.addEffect(new MobEffectInstance(template));
        }
    }
}

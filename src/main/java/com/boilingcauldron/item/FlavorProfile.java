/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：口味档案——完全由数据包定义（id / 显示名 / 排序 / 药水效果）。
 * 说明：取代原先的 BoboFlavor 枚举，使数据包可以新增任意口味而无需改代码。
 * 更新：2026-11-21
 */
package com.boilingcauldron.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public record FlavorProfile(String id, Component name, int sort, int color, List<MobEffectInstance> effects) {

    private static final String KEY_ID = "Id";
    private static final String KEY_NAME = "Name";
    private static final String KEY_SORT = "Sort";
    private static final String KEY_COLOR = "Color";
    private static final String KEY_EFFECTS = "Effects";
    private static final String KEY_EFFECT = "Effect";
    private static final String KEY_DURATION = "Duration";
    private static final String KEY_AMPLIFIER = "Amplifier";

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString(KEY_ID, this.id);
        tag.putString(KEY_NAME, Component.Serializer.toJson(this.name));
        tag.putInt(KEY_SORT, this.sort);
        tag.putInt(KEY_COLOR, this.color);
        ListTag list = new ListTag();
        for (MobEffectInstance instance : this.effects) {
            CompoundTag effectTag = new CompoundTag();
            ResourceLocation effectId = ForgeRegistries.MOB_EFFECTS.getKey(instance.getEffect());
            if (effectId == null) {
                continue;
            }
            effectTag.putString(KEY_EFFECT, effectId.toString());
            effectTag.putInt(KEY_DURATION, instance.getDuration());
            effectTag.putInt(KEY_AMPLIFIER, instance.getAmplifier());
            list.add(effectTag);
        }
        tag.put(KEY_EFFECTS, list);
        return tag;
    }

    public static FlavorProfile load(CompoundTag tag) {
        String id = tag.getString(KEY_ID);
        Component name = tag.contains(KEY_NAME, Tag.TAG_STRING)
            ? Component.Serializer.fromJson(tag.getString(KEY_NAME))
            : Component.literal(id);
        int sort = tag.getInt(KEY_SORT);
        int color = tag.contains(KEY_COLOR) ? tag.getInt(KEY_COLOR) : 0x404040;

        List<MobEffectInstance> effects = new ArrayList<>();
        ListTag list = tag.getList(KEY_EFFECTS, Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag effectTag = list.getCompound(i);
            ResourceLocation effectId = ResourceLocation.tryParse(effectTag.getString(KEY_EFFECT));
            if (effectId == null) {
                continue;
            }
            MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(effectId);
            if (effect == null) {
                continue;
            }
            effects.add(new MobEffectInstance(effect,
                effectTag.getInt(KEY_DURATION), effectTag.getInt(KEY_AMPLIFIER)));
        }
        return new FlavorProfile(id, name, sort, color, List.copyOf(effects));
    }

    public boolean sameAs(FlavorProfile other) {
        return this.id.equals(other.id);
    }
}

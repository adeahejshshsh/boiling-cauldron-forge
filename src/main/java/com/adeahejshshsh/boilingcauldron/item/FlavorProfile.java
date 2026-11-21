package com.adeahejshshsh.boilingcauldron.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.ArrayList;
import java.util.List;

/**
 * 口味档案：完全由数据包定义（id / 显示名 / 排序 / 颜色 / 药水效果）。
 *
 * <p>1.21 起以 {@code DataComponents.CUSTOM_DATA} 的形式随物品保存。
 */
public record FlavorProfile(String id, Component name, int sort, int color, List<MobEffectInstance> effects) {

    public static final Codec<FlavorProfile> CODEC = RecordCodecBuilder.create(inst -> inst.group(
        Codec.STRING.fieldOf("id").forGetter(FlavorProfile::id),
        ComponentSerialization.CODEC.fieldOf("name").forGetter(FlavorProfile::name),
        Codec.INT.optionalFieldOf("sort", 0).forGetter(FlavorProfile::sort),
        Codec.INT.optionalFieldOf("color", 0x404040).forGetter(FlavorProfile::color),
        MobEffectInstance.CODEC.listOf().optionalFieldOf("effects", List.of()).forGetter(FlavorProfile::effects)
    ).apply(inst, FlavorProfile::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FlavorProfile> STREAM_CODEC =
        new StreamCodec<>() {
            @Override
            public FlavorProfile decode(RegistryFriendlyByteBuf buf) {
                CompoundTag tag = buf.readNbt();
                return FlavorProfile.load(tag == null ? new CompoundTag() : tag);
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buf, FlavorProfile profile) {
                buf.writeNbt(profile.save());
            }
        };

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
        tag.putString(KEY_NAME, Component.Serializer.toJson(this.name, net.minecraft.core.RegistryAccess.EMPTY));
        tag.putInt(KEY_SORT, this.sort);
        tag.putInt(KEY_COLOR, this.color);

        ListTag list = new ListTag();
        for (MobEffectInstance instance : this.effects) {
            CompoundTag effectTag = new CompoundTag();
            ResourceLocation effectId = instance.getEffect().unwrapKey()
                .map(key -> key.location())
                .orElse(null);
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
            ? Component.Serializer.fromJson(tag.getString(KEY_NAME), net.minecraft.core.RegistryAccess.EMPTY)
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
            Holder<MobEffect> effect = BuiltInRegistries.MOB_EFFECT
                .getHolder(effectId).orElse(null);
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

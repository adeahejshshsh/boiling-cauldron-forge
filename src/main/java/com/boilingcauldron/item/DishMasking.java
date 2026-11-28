/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：成品原料隐藏标记的 NBT 读写工具。
 * 移植：Fabric → Forge 1.20.1
 * 更新：2026-11-28 上游 1.0.1 新增
 */
package com.boilingcauldron.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public final class DishMasking {

    public static final String MASKED_KEY = "Masked";

    public static boolean isMasked(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.getBoolean(MASKED_KEY);
    }

    public static ItemStack createMaskedCopy(ItemStack stack) {
        ItemStack result = stack.copy();
        result.setCount(1);
        result.getOrCreateTag().putBoolean(MASKED_KEY, true);
        return result;
    }

    private DishMasking() {
    }
}

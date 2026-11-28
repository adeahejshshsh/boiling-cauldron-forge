/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：成品原料隐藏标记的组件读写工具。
 * 移植：Forge 1.20.1 → NeoForge 1.21.1
 * 更新：2026-11-28 上游 1.0.1 新增
 */
package com.adeahejshshsh.boilingcauldron.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public final class DishMasking {

    public static final String MASKED_KEY = "Masked";

    private static CompoundTag copyTag(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data == null ? new CompoundTag() : data.copyTag();
    }

    public static boolean isMasked(ItemStack stack) {
        return copyTag(stack).getBoolean(MASKED_KEY);
    }

    public static ItemStack createMaskedCopy(ItemStack stack) {
        ItemStack result = stack.copy();
        result.setCount(1);
        CompoundTag tag = copyTag(result);
        tag.putBoolean(MASKED_KEY, true);
        result.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        return result;
    }

    private DishMasking() {
    }
}

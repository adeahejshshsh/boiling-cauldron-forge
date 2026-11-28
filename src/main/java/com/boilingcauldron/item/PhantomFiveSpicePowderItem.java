/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：幻色五香粉物品，提示其隐藏成品原料的用途。
 * 移植：Fabric → Forge 1.20.1
 * 更新：2026-11-28 上游 1.0.1 新增
 */
package com.boilingcauldron.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PhantomFiveSpicePowderItem extends Item {

    public PhantomFiveSpicePowderItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.boiling-cauldron.phantom_five_spice_powder")
            .withStyle(ChatFormatting.DARK_PURPLE));
    }
}

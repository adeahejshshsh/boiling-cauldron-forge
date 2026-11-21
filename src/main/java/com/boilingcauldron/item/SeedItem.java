/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：带独立翻译键的种子 BlockItem。
 * 移植：Fabric → Forge 1.20.1
 */
package com.boilingcauldron.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class SeedItem extends BlockItem {
    private final String translationKey;

    public SeedItem(Block block, Item.Properties properties, String translationKey) {
        super(block, properties);
        this.translationKey = translationKey;
    }

    @Override
    public String getDescriptionId() {
        return this.translationKey;
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        return this.translationKey;
    }
}

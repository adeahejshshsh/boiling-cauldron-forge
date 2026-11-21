package com.adeahejshshsh.boilingcauldron.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

/** 带独立翻译键的种子物品。 */
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

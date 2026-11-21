/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：物品标签生成。
 * 更新：2026-11-21
 */
package com.boilingcauldron.datagen;

import com.boilingcauldron.BoilingCauldron;
import com.boilingcauldron.item.ModItems;
import com.boilingcauldron.tag.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider.TagLookup;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends ItemTagsProvider {

    public ModItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup,
                               CompletableFuture<TagLookup<Block>> blockTags,
                               ExistingFileHelper helper) {
        super(output, lookup, blockTags, BoilingCauldron.NAMESPACE, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModTags.MUSHROOMS).add(Items.RED_MUSHROOM, Items.BROWN_MUSHROOM);
        tag(ModTags.BOBO_CHICKEN).add(ModItems.BOBO_CHICKEN.get());
        tag(ModTags.BOBO_BASE).add(
            ModItems.MUSHROOM_BASE.get(),
            ModItems.MALA_BASE.get(),
            ModItems.RATTAN_PEPPER_BASE.get());
    }
}

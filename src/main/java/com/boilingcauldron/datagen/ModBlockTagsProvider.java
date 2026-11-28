/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：方块标签生成（麻辣烫锅热源；其余仅为 ItemTagsProvider 提供查询来源）。
 * 更新：2026-11-21
 */
package com.boilingcauldron.datagen;

import com.boilingcauldron.BoilingCauldron;
import com.boilingcauldron.tag.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends BlockTagsProvider {

    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup,
                                @Nullable ExistingFileHelper helper) {
        super(output, lookup, BoilingCauldron.NAMESPACE, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // 麻辣烫锅正下方的基础热源；点燃的熔炉由代码额外检测
        tag(ModTags.MALATANG_HEAT_SOURCES).add(
            Blocks.CAMPFIRE, Blocks.SOUL_CAMPFIRE, Blocks.FIRE, Blocks.SOUL_FIRE, Blocks.MAGMA_BLOCK);
    }
}

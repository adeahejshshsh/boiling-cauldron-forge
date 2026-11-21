/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：方块标签生成（当前无自定义方块标签，仅为 ItemTagsProvider 提供查询来源）。
 * 更新：2026-11-21
 */
package com.boilingcauldron.datagen;

import com.boilingcauldron.BoilingCauldron;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
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
        // 暂无自定义方块标签
    }
}

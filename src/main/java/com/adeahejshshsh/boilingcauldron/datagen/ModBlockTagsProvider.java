package com.adeahejshshsh.boilingcauldron.datagen;

import com.adeahejshshsh.boilingcauldron.BoilingCauldron;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/** 方块标签（当前为空，为物品标签提供查询源）。 */
public class ModBlockTagsProvider extends BlockTagsProvider {

    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup,
                                @Nullable ExistingFileHelper helper) {
        super(output, lookup, BoilingCauldron.MOD_ID, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
    }
}

package com.adeahejshshsh.boilingcauldron.datagen;

import com.adeahejshshsh.boilingcauldron.BoilingCauldron;
import com.adeahejshshsh.boilingcauldron.tag.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/** 方块标签：麻辣烫锅热源；其余仅为物品标签提供查询源。 */
public class ModBlockTagsProvider extends BlockTagsProvider {

    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup,
                                @Nullable ExistingFileHelper helper) {
        super(output, lookup, BoilingCauldron.MOD_ID, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // 麻辣烫锅正下方的基础热源；点燃的熔炉由代码额外检测
        tag(ModTags.MALATANG_HEAT_SOURCES).add(
            Blocks.CAMPFIRE, Blocks.SOUL_CAMPFIRE, Blocks.FIRE, Blocks.SOUL_FIRE, Blocks.MAGMA_BLOCK);
    }
}

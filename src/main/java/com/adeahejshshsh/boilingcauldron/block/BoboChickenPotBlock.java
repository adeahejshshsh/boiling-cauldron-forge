/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：钵钵鸡锅方块，复用通用调味锅交互，只提供钵钵鸡专属方块实体与 GUI。
 * 移植：Forge 1.20.1 → NeoForge 1.21.1
 * 更新：2026-11-28 上游 1.0.1：改为继承 AbstractSeasoningPotBlock
 */
package com.adeahejshshsh.boilingcauldron.block;

import com.adeahejshshsh.boilingcauldron.block.entity.AbstractSeasoningPotBlockEntity;
import com.adeahejshshsh.boilingcauldron.block.entity.BoboChickenPotBlockEntity;
import com.adeahejshshsh.boilingcauldron.screen.BoboChickenPotMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BoboChickenPotBlock extends AbstractSeasoningPotBlock {

    public BoboChickenPotBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BoboChickenPotBlockEntity(pos, state);
    }

    @Override
    protected Component getDisplayName() {
        return Component.translatable("block.boiling-cauldron.bobo_chicken_pot");
    }

    @Override
    protected AbstractContainerMenu createPotMenu(int syncId, Inventory playerInventory,
                                                  AbstractSeasoningPotBlockEntity pot) {
        return new BoboChickenPotMenu(syncId, playerInventory, (BoboChickenPotBlockEntity) pot);
    }
}

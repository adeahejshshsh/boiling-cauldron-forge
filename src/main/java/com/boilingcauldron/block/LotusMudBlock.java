/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：藏藕泥块，处理挖掘掉落和铲子右键取藕。
 * 移植：Fabric → Forge 1.20.1
 */
package com.boilingcauldron.block;

import com.boilingcauldron.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class LotusMudBlock extends Block {

    public LotusMudBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.is(ItemTags.SHOVELS)) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (!level.setBlock(pos, Blocks.MUD.defaultBlockState(), Block.UPDATE_ALL)) {
            return InteractionResult.PASS;
        }

        Block.popResource(level, pos, new ItemStack(ModItems.LOTUS_ROOT.get(), 3));
        level.playSound(null, pos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0F, 1.0F);

        if (!player.getAbilities().instabuild) {
            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
        }
        return InteractionResult.SUCCESS;
    }
}

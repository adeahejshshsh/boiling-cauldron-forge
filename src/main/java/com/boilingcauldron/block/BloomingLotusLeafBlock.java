/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：盛花荷叶方块，实现生长、骨粉扩散和下方方块转化。
 * 移植：Fabric → Forge 1.20.1
 */
package com.boilingcauldron.block;

import com.boilingcauldron.block.entity.BloomingLotusLeafBlockEntity;
import com.boilingcauldron.block.entity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.WaterlilyBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class BloomingLotusLeafBlock extends WaterlilyBlock implements EntityBlock {

    public static final IntegerProperty GROWTH = IntegerProperty.create("growth", 0, 4);

    public BloomingLotusLeafBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(GROWTH, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(GROWTH);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BloomingLotusLeafBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.is(Items.BONE_MEAL)) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (!(level.getBlockEntity(pos) instanceof BloomingLotusLeafBlockEntity blockEntity)) {
            return InteractionResult.PASS;
        }

        boolean spreadSuccess = blockEntity.useBoneMeal((ServerLevel) level, level.random);
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        if (spreadSuccess) {
            BoneMealItem.addGrowthParticles(level, pos, 15);
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) {
            return null;
        }
        return type == ModBlockEntities.BLOOMING_LOTUS_LEAF.get()
            ? (BlockEntityTicker<T>) (BlockEntityTicker<BloomingLotusLeafBlockEntity>)
                BloomingLotusLeafBlockEntity::tick
            : null;
    }
}

/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：竹根殖块方块，维护 bamboo_count 状态并调度产物生成。
 * 移植：Fabric → Forge 1.20.1
 */
package com.boilingcauldron.block;

import com.boilingcauldron.block.entity.BambooRootBlockEntity;
import com.boilingcauldron.block.entity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.Nullable;

public class BambooRootBlock extends BaseEntityBlock {

    public static final int MAX_COUNT = 5;
    public static final IntegerProperty BAMBOO_COUNT = IntegerProperty.create("bamboo_count", 0, MAX_COUNT);

    public BambooRootBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BAMBOO_COUNT);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BambooRootBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null
            : createTickerHelper(type, ModBlockEntities.BAMBOO_ROOT.get(), BambooRootBlockEntity::tick);
    }
}

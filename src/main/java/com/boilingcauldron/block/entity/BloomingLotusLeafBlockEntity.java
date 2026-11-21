/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：盛花荷叶 BlockEntity，负责生长/转化计时、扩散和 NBT。
 * 移植：Fabric → Forge 1.20.1
 */
package com.boilingcauldron.block.entity;

import com.boilingcauldron.block.BloomingLotusLeafBlock;
import com.boilingcauldron.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class BloomingLotusLeafBlockEntity extends BlockEntity {

    private static final int GROWTH_INTERVAL = 1200;
    private static final int CONVERSION_INTERVAL = 600;
    private static final int MAX_GROWTH = 4;
    private static final int MIN_SCAN_DEPTH = 2;
    private static final int MAX_SCAN_DEPTH = 4;
    private static final int DIRTY_INTERVAL = 20;
    private static final int DATA_VERSION = 1;

    private int growth;
    private int growthTicks;
    private int conversionTicks;

    public BloomingLotusLeafBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BLOOMING_LOTUS_LEAF.get(), pos, state);
    }

    @SuppressWarnings("unused")
    public static void tick(Level level, BlockPos pos, BlockState state, BloomingLotusLeafBlockEntity blockEntity) {
        if (level.isClientSide) {
            return;
        }
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        blockEntity.syncGrowthState(level, pos);

        if (blockEntity.growth >= MAX_GROWTH) {
            blockEntity.growthTicks = 0;
            blockEntity.conversionTicks++;
            if (blockEntity.conversionTicks >= CONVERSION_INTERVAL) {
                blockEntity.conversionTicks = 0;
                blockEntity.tryConvert(serverLevel, pos);
            }
        } else {
            blockEntity.conversionTicks = 0;
            blockEntity.growthTicks++;
            if (blockEntity.growthTicks >= GROWTH_INTERVAL) {
                blockEntity.growthTicks = 0;
                blockEntity.growth++;
                blockEntity.setChanged();
                blockEntity.syncGrowthState(level, pos);
                if (blockEntity.growth >= MAX_GROWTH) {
                    blockEntity.conversionTicks = 0;
                }
            }
        }

        if (blockEntity.growthTicks % DIRTY_INTERVAL == 0 || blockEntity.conversionTicks % DIRTY_INTERVAL == 0) {
            blockEntity.setChanged();
        }
    }

    public boolean useBoneMeal(ServerLevel level, RandomSource random) {
        if (growth < MAX_GROWTH) {
            growth++;
            if (growth >= MAX_GROWTH) {
                growthTicks = 0;
                conversionTicks = 0;
            }
            setChanged();
            syncGrowthState(level, worldPosition);
            return false;
        }

        if (trySpread(level, random, worldPosition)) {
            growth = 0;
            growthTicks = 0;
            conversionTicks = 0;
            setChanged();
            syncGrowthState(level, worldPosition);
            return true;
        }

        setChanged();
        return false;
    }

    private void tryConvert(ServerLevel level, BlockPos pos) {
        BlockPos targetPos = findConversionTarget(level, pos);
        if (targetPos == null) {
            setChanged();
            return;
        }

        if (!level.setBlock(targetPos, ModBlocks.LOTUS_MUD.get().defaultBlockState(), Block.UPDATE_ALL)) {
            setChanged();
            return;
        }

        growth = 0;
        growthTicks = 0;
        conversionTicks = 0;
        setChanged();
        syncGrowthState(level, pos);
        BoneMealItem.addGrowthParticles(level, pos, 15);
    }

    private boolean trySpread(ServerLevel level, RandomSource random, BlockPos pos) {
        List<BlockPos> candidates = new ArrayList<>();

        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                if (dx == 0 && dz == 0) {
                    continue;
                }
                BlockPos targetPos = pos.offset(dx, 0, dz);
                if (canSpreadTo(level, targetPos)) {
                    candidates.add(targetPos);
                }
            }
        }

        if (candidates.isEmpty()) {
            return false;
        }

        BlockPos targetPos = candidates.get(random.nextInt(candidates.size()));
        return level.setBlock(targetPos, ModBlocks.BLOOMING_LOTUS_LEAF.get().defaultBlockState(), Block.UPDATE_ALL);
    }

    private static boolean canSpreadTo(Level level, BlockPos targetPos) {
        if (isChunkUnloaded(level, targetPos)) {
            return false;
        }
        if (!level.getBlockState(targetPos).isAir()) {
            return false;
        }

        BlockPos supportPos = targetPos.below();
        BlockState supportState = level.getBlockState(supportPos);
        if (!supportState.is(Blocks.WATER)) {
            return false;
        }
        if (!level.getFluidState(supportPos).isSource()) {
            return false;
        }

        return ModBlocks.BLOOMING_LOTUS_LEAF.get().defaultBlockState().canSurvive(level, targetPos);
    }

    private static BlockPos findConversionTarget(Level level, BlockPos pos) {
        for (int dy = MIN_SCAN_DEPTH; dy <= MAX_SCAN_DEPTH; dy++) {
            BlockPos checkPos = pos.below(dy);
            if (isChunkUnloaded(level, checkPos)) {
                return null;
            }

            BlockState state = level.getBlockState(checkPos);
            if (isPassThrough(state)) {
                continue;
            }
            if (isConvertible(state)) {
                return checkPos;
            }
            return null;
        }
        return null;
    }

    private static boolean isChunkUnloaded(Level level, BlockPos pos) {
        return !level.hasChunk(pos.getX() >> 4, pos.getZ() >> 4);
    }

    private static boolean isPassThrough(BlockState state) {
        if (state.is(Blocks.WATER) || state.is(Blocks.BUBBLE_COLUMN)) {
            return true;
        }
        if (state.is(Blocks.KELP) || state.is(Blocks.KELP_PLANT)
            || state.is(Blocks.SEAGRASS) || state.is(Blocks.TALL_SEAGRASS)) {
            return true;
        }
        if (state.is(Blocks.SEA_PICKLE) || state.is(Blocks.LILY_PAD)) {
            return true;
        }
        return state.is(BlockTags.CORALS)
            || state.is(BlockTags.CORAL_BLOCKS)
            || state.is(BlockTags.WALL_CORALS);
    }

    private static boolean isConvertible(BlockState state) {
        return state.is(BlockTags.DIRT)
            || state.is(BlockTags.SAND)
            || state.is(Blocks.GRAVEL);
    }

    private void syncGrowthState(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!state.is(ModBlocks.BLOOMING_LOTUS_LEAF.get())) {
            return;
        }
        if (state.getValue(BloomingLotusLeafBlock.GROWTH) != growth) {
            level.setBlock(pos, state.setValue(BloomingLotusLeafBlock.GROWTH, growth), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("LotusDataVersion", DATA_VERSION);
        tag.putInt("Growth", growth);
        tag.putInt("GrowthTicks", growthTicks);
        tag.putInt("ConversionTicks", conversionTicks);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        if (tag.getInt("LotusDataVersion") >= DATA_VERSION) {
            growth = Mth.clamp(tag.getInt("Growth"), 0, MAX_GROWTH);
            growthTicks = Mth.clamp(tag.getInt("GrowthTicks"), 0, GROWTH_INTERVAL);
            conversionTicks = Mth.clamp(tag.getInt("ConversionTicks"), 0, CONVERSION_INTERVAL);

            if (growth >= MAX_GROWTH) {
                growthTicks = 0;
            } else {
                conversionTicks = 0;
            }
        } else {
            growth = 0;
            growthTicks = 0;
            conversionTicks = 0;
        }

        setChanged();
    }
}

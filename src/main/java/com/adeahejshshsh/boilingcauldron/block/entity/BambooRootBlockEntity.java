package com.adeahejshshsh.boilingcauldron.block.entity;

import com.adeahejshshsh.boilingcauldron.block.BambooRootBlock;
import com.adeahejshshsh.boilingcauldron.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

/** 竹根殖块：定时生成冬笋作物或竹荪菌落，最多 5 个存活产物。 */
public class BambooRootBlockEntity extends BlockEntity {

    public static final int MAX_BAMBOO_COUNT = 5;
    private static final int CHECK_INTERVAL = 1200;
    private static final float TRIGGER_CHANCE = 0.55F;
    private static final float CROP_CHANCE = 0.55F;
    private static final float BAMBOO_BONUS_PER_BLOCK = 0.01F;
    private static final float MAX_BAMBOO_BONUS = 0.20F;

    private int ticks;
    private final List<BlockPos> spawnedPositions = new ArrayList<>();

    public BambooRootBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BAMBOO_ROOT.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BambooRootBlockEntity blockEntity) {
        if (level.isClientSide) {
            return;
        }
        blockEntity.pruneSpawned(level);
        blockEntity.syncCount(level, pos);

        blockEntity.ticks++;
        if (blockEntity.ticks < CHECK_INTERVAL) {
            return;
        }
        blockEntity.ticks = 0;
        blockEntity.setChanged();
        blockEntity.tryReproduce(level, pos);
    }

    private void tryReproduce(Level level, BlockPos pos) {
        int alive = spawnedPositions.size();
        if (alive >= MAX_BAMBOO_COUNT) {
            return;
        }
        int spawned = 0;
        RandomSource random = level.random;
        float successChance = TRIGGER_CHANCE
            + Math.min(countNearbyBamboo(level, pos) * BAMBOO_BONUS_PER_BLOCK, MAX_BAMBOO_BONUS);

        for (int attempt = 0; attempt < 2; attempt++) {
            if (alive + spawned >= MAX_BAMBOO_COUNT) {
                break;
            }
            if (random.nextFloat() >= successChance) {
                continue;
            }
            BlockPos target = pickSpawnPosition(level, pos);
            if (target == null) {
                continue;
            }
            BlockState product = random.nextFloat() < CROP_CHANCE
                ? ModBlocks.BAMBOO_SHOOT_CROP.get().defaultBlockState()
                : ModBlocks.BAMBOO_FUNGUS_COLONY.get().defaultBlockState();
            if (!product.canSurvive(level, target)) {
                continue;
            }
            level.setBlock(target, product, 3);
            spawnedPositions.add(target);
            spawned++;
        }
        if (spawned > 0) {
            syncCount(level, pos);
            setChanged();
        }
    }

    private void pruneSpawned(Level level) {
        boolean removed = spawnedPositions.removeIf(p -> {
            if (isChunkUnloaded(level, p)) {
                return false;
            }
            BlockState state = level.getBlockState(p);
            return !state.is(ModBlocks.BAMBOO_SHOOT_CROP.get())
                && !state.is(ModBlocks.BAMBOO_FUNGUS_COLONY.get());
        });
        if (removed) {
            setChanged();
        }
    }

    private void syncCount(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!state.is(ModBlocks.BAMBOO_ROOT.get())) {
            return;
        }
        int clamped = Math.min(spawnedPositions.size(), MAX_BAMBOO_COUNT);
        if (state.getValue(BambooRootBlock.BAMBOO_COUNT) != clamped) {
            level.setBlock(pos, state.setValue(BambooRootBlock.BAMBOO_COUNT, clamped), 3);
        }
    }

    private BlockPos pickSpawnPosition(Level level, BlockPos pos) {
        List<BlockPos> candidates = new ArrayList<>();
        BlockPos scanRow = pos.above();
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                BlockPos floorPos = scanRow.offset(dx, 0, dz);
                if (isChunkUnloaded(level, floorPos)) {
                    continue;
                }
                BlockState floor = level.getBlockState(floorPos);
                if (!isSoil(floor)) {
                    continue;
                }
                BlockPos target = floorPos.above();
                if (!level.getBlockState(target).isAir()) {
                    continue;
                }
                candidates.add(target);
            }
        }
        if (candidates.isEmpty()) {
            return null;
        }
        return candidates.get(level.random.nextInt(candidates.size()));
    }

    private static boolean isSoil(BlockState state) {
        return state.is(BlockTags.DIRT) || state.is(Blocks.FARMLAND);
    }

    private static boolean isChunkUnloaded(Level level, BlockPos pos) {
        return !level.hasChunk(pos.getX() >> 4, pos.getZ() >> 4);
    }

    private int countNearbyBamboo(Level level, BlockPos pos) {
        int count = 0;
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                for (int dy = 0; dy < 8; dy++) {
                    BlockPos checkPos = pos.offset(dx, dy, dz);
                    if (isChunkUnloaded(level, checkPos)) {
                        continue;
                    }
                    if (level.getBlockState(checkPos).is(Blocks.BAMBOO)) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Ticks", ticks);
        long[] positions = new long[spawnedPositions.size()];
        for (int i = 0; i < positions.length; i++) {
            positions[i] = spawnedPositions.get(i).asLong();
        }
        tag.putLongArray("Spawned", positions);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        ticks = tag.getInt("Ticks");
        spawnedPositions.clear();
        for (long position : tag.getLongArray("Spawned")) {
            spawnedPositions.add(BlockPos.of(position));
        }
    }
}

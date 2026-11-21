/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：竹根殖块 BlockEntity，负责计时、产物生成和 NBT 持久化。
 * 移植：Fabric → Forge 1.20.1
 */
package com.boilingcauldron.block.entity;

import com.boilingcauldron.block.BambooRootBlock;
import com.boilingcauldron.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class BambooRootBlockEntity extends BlockEntity {

    public static final int MAX_BAMBOO_COUNT = 5;
    private static final int CHECK_INTERVAL = 1200; // 1 分钟
    private static final float TRIGGER_CHANCE = 0.55F; // 每次尝试基础成功率 55%
    private static final float CROP_CHANCE = 0.55F; // 55% 竹笋作物，45% 竹荪菌落
    private static final float BAMBOO_BONUS_PER_BLOCK = 0.01F; // 检测范围内每格原版竹子：成功率 +1%
    private static final float MAX_BAMBOO_BONUS = 0.20F; // 竹子加成至多 +20%

    private int ticks;
    // 已生成产物的位置，产物被破坏/采收后移除，计数回落并恢复繁殖
    private final List<BlockPos> spawnedPositions = new ArrayList<>();

    public BambooRootBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BAMBOO_ROOT.get(), pos, state);
    }

    @SuppressWarnings("unused")
    public static void tick(Level level, BlockPos pos, BlockState state, BambooRootBlockEntity blockEntity) {
        if (level.isClientSide) {
            return;
        }

        // 每 tick 清理已消失的产物并同步计数（实时回落）
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
            return; // 存活产物数量达到上限，停止
        }

        int spawned = 0;
        RandomSource random = level.random;
        // 5×5×8 范围内每格原版竹子 +1%，至多 +20%
        float successChance = TRIGGER_CHANCE
            + Math.min(countNearbyBamboo(level, pos) * BAMBOO_BONUS_PER_BLOCK, MAX_BAMBOO_BONUS);

        for (int attempt = 0; attempt < 2; attempt++) {
            if (alive + spawned >= MAX_BAMBOO_COUNT) {
                break;
            }
            if (random.nextFloat() >= successChance) {
                continue; // 本次尝试未成功
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
        boolean removed = spawnedPositions.removeIf(spawnedPos -> {
            if (isChunkUnloaded(level, spawnedPos)) {
                return false; // 未加载时保留，避免误清
            }

            BlockState state = level.getBlockState(spawnedPos);
            // 被采收/破坏/替换，或竹笋被施肥长成竹子：移出清单，计数 -1；原版竹子不计入
            return !state.is(ModBlocks.BAMBOO_SHOOT_CROP.get())
                && !state.is(ModBlocks.BAMBOO_FUNGUS_COLONY.get());
        });
        if (removed) {
            setChanged();
        }
    }

    // 将方块状态 bamboo_count 同步为当前存活产物数量
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
        BlockPos scanRow = pos.above(); // 上方 1 格

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

                BlockPos target = floorPos.above(); // 该方块上方一格
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

    // 统计 5×5×8（水平 ±2，根块所在层向上 8 格）内的原版竹子数量
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
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("Ticks", ticks);
        long[] positions = new long[spawnedPositions.size()];
        for (int i = 0; i < positions.length; i++) {
            positions[i] = spawnedPositions.get(i).asLong();
        }
        tag.putLongArray("Spawned", positions);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        ticks = tag.getInt("Ticks");
        spawnedPositions.clear();
        for (long position : tag.getLongArray("Spawned")) {
            spawnedPositions.add(BlockPos.of(position));
        }
    }
}

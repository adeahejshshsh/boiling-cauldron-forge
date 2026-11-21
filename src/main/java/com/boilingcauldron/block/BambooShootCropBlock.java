/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：冬笋作物方块，实现 6 阶段生长、骨粉转化和成熟掉落。
 * 移植：Fabric → Forge 1.20.1
 */
package com.boilingcauldron.block;

import com.boilingcauldron.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class BambooShootCropBlock extends CropBlock {

    public static final int MAX_AGE = 5;
    public static final IntegerProperty AGE = BlockStateProperties.AGE_5;

    public BambooShootCropBlock(Properties properties) {
        super(properties);
    }

    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }

    @Override
    public IntegerProperty getAgeProperty() {
        return AGE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return ModItems.WINTER_BAMBOO_SHOOTS.get();
    }

    @Override
    protected boolean mayPlaceOn(BlockState floor, BlockGetter level, BlockPos pos) {
        // 与原版竹子相同的地基，保证骨粉转化后竹子不会掉落
        return floor.is(BlockTags.BAMBOO_PLANTABLE_ON);
    }

    // 不受光照限制，仅要求合适的地基
    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return this.mayPlaceOn(level.getBlockState(pos.below()), level, pos);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return state.getValue(AGE) < MAX_AGE;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (random.nextInt(2) == 0) {
            int age = state.getValue(AGE);
            if (age < MAX_AGE) {
                level.setBlock(pos, state.setValue(AGE, age + 1), 3);
            }
        }
    }

    // 骨粉：未成熟无效，仅成熟时可用
    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient) {
        return this.isMaxAge(state);
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return this.isMaxAge(state);
    }

    // 成熟期用骨粉：变为原版竹子，并按原版竹子骨粉逻辑生长 1-2 节
    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        BlockState bambooState = Blocks.BAMBOO.defaultBlockState();
        level.setBlock(pos, bambooState, 3);
        if (Blocks.BAMBOO instanceof BonemealableBlock bamboo) {
            bamboo.performBonemeal(level, random, pos, bambooState);
        }
    }
}

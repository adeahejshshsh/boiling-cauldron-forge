/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：四种基础作物共用的 4 阶段作物方块实现。
 * 移植：Fabric → Forge 1.20.1
 */
package com.boilingcauldron.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class ModCropBlocks extends CropBlock {

    public static final int MAX_AGE = 3;
    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
    private ItemLike seedsItem;

    public ModCropBlocks(Properties properties) {
        super(properties);
    }

    public void setSeedsItem(ItemLike seedsItem) {
        this.seedsItem = seedsItem;
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
    public int getAge(BlockState state) {
        return state.getValue(this.getAgeProperty());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return this.seedsItem;
    }

    @Override
    protected boolean mayPlaceOn(BlockState floor, BlockGetter level, BlockPos pos) {
        return floor.is(Blocks.FARMLAND);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return state.getValue(AGE) < MAX_AGE;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.getRawBrightness(pos.above(), 0) >= 9 && random.nextInt(2) == 0) {
            int age = state.getValue(AGE);
            if (age < MAX_AGE) {
                level.setBlock(pos, state.setValue(AGE, age + 1), 3);
            }
        }
    }
}

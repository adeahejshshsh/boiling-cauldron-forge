/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：花椒作物方块，实现 5 阶段生长与右键采摘。
 * 移植：Fabric → Forge 1.20.1
 */
package com.boilingcauldron.block;

import com.boilingcauldron.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SichuanPepperCropBlock extends CropBlock {

    public static final int MAX_AGE = 4;
    public static final IntegerProperty AGE = BlockStateProperties.AGE_4;

    private ItemLike seedsItem;

    public SichuanPepperCropBlock(Properties properties) {
        super(properties);
    }

    public void setSeedsItem(ItemLike seedsItem) {
        this.seedsItem = seedsItem;
    }

    private static final VoxelShape[] AGE_TO_SHAPE = new VoxelShape[]{
        Block.box(3.0, 0.0, 3.0, 13.0, 8.0, 13.0),
        Block.box(2.0, 0.0, 2.0, 14.0, 12.0, 14.0),
        Block.box(2.0, 0.0, 2.0, 14.0, 14.0, 14.0),
        Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0),
        Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)
    };

    @Override
    public IntegerProperty getAgeProperty() {
        return AGE;
    }

    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return this.seedsItem;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return AGE_TO_SHAPE[state.getValue(AGE)];
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        return below.is(Blocks.FARMLAND) || below.is(Blocks.DIRT)
            || below.is(Blocks.GRASS_BLOCK) || below.is(Blocks.SAND);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (!state.canSurvive(level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        int age = state.getValue(AGE);
        boolean mature = age == MAX_AGE;

        if (mature) {
            // 右键采集：掉落 1-2 个花椒，回退到第二阶段
            if (!level.isClientSide) {
                popResource(level, pos, new ItemStack(ModItems.SICHUAN_PEPPER.get(),
                    level.random.nextIntBetweenInclusive(1, 2)));
                level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES,
                    SoundSource.BLOCKS, 1.0F, 1.0F);

                BlockState newState = state.setValue(AGE, 1);
                level.setBlock(pos, newState, 3);
                level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, newState));
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return super.use(state, level, pos, player, hand, hit);
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

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }
}

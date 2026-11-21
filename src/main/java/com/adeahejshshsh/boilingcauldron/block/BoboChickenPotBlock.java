package com.adeahejshshsh.boilingcauldron.block;

import com.adeahejshshsh.boilingcauldron.block.entity.BoboChickenPotBlockEntity;
import com.adeahejshshsh.boilingcauldron.block.entity.ModBlockEntities;
import com.adeahejshshsh.boilingcauldron.recipe.BoboSoakRecipe;
import com.adeahejshshsh.boilingcauldron.recipe.ModRecipes;
import com.adeahejshshsh.boilingcauldron.screen.BoboChickenPotMenu;
import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * 钵钵鸡锅。
 *
 * <p>当前实现加水与冲洗；GUI 与底料放入仍在移植中。
 */
public class BoboChickenPotBlock extends BaseEntityBlock {

    private static final VoxelShape BODY_SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 6.0, 14.0);
    private static final VoxelShape RIM_SHAPE = Block.box(1.0, 6.0, 1.0, 15.0, 7.0, 15.0);
    private static final VoxelShape SHAPE = Shapes.or(BODY_SHAPE, RIM_SHAPE);
    public static final MapCodec<BoboChickenPotBlock> CODEC = simpleCodec(BoboChickenPotBlock::new);

    public BoboChickenPotBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BoboChickenPotBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) {
            return null;
        }
        return createTickerHelper(type, ModBlockEntities.BOBO_CHICKEN_POT.get(), BoboChickenPotBlockEntity::tick);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }
        if (!(level.getBlockEntity(pos) instanceof BoboChickenPotBlockEntity pot)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (stack.is(Items.WATER_BUCKET)) {
            boolean hadBase = pot.hasWater() && pot.getBaseUses() > 0;
            if (pot.useWaterBucket()) {
                if (!player.getAbilities().instabuild) {
                    player.setItemInHand(hand, new ItemStack(Items.BUCKET));
                }
                level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                hint(player, hadBase
                    ? "message.boiling-cauldron.pot.flushed"
                    : "message.boiling-cauldron.pot.added_water");
                return ItemInteractionResult.SUCCESS;
            }
            if (!pot.isEmpty()) {
                hint(player, "message.boiling-cauldron.pot.not_empty");
            } else if (pot.hasWater()) {
                hint(player, "message.boiling-cauldron.pot.has_water");
            }
        }
        // 底料：从数据包配方读取口味/次数/时长/产物
        Optional<BoboSoakRecipe> lookup = level.getRecipeManager()
            .getRecipeFor(ModRecipes.BOBO_SOAK_TYPE.get(), new BoboSoakRecipe.Input(stack), level)
            .map(holder -> holder.value());
        if (lookup.isPresent()) {
            BoboSoakRecipe recipe = lookup.get();
            if (pot.tryAddBase(recipe.getFlavor(), recipe.getUses(), recipe.getSoakTime(), recipe.getResult())) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0F, 1.0F);
                hint(player, "message.boiling-cauldron.pot.added_base",
                    recipe.getFlavor().name(), recipe.getUses());
                return ItemInteractionResult.SUCCESS;
            }
            if (!pot.hasWater()) {
                hint(player, "message.boiling-cauldron.pot.need_water");
            } else if (pot.getBaseUses() > 0) {
                hint(player, "message.boiling-cauldron.pot.has_base", pot.getBaseUses());
            } else if (!pot.isEmpty()) {
                hint(player, "message.boiling-cauldron.pot.not_empty");
            }
            return ItemInteractionResult.SUCCESS;
        }

        // 打开 GUI
        if (pot.getBaseFlavor() == null && pot.isEmpty()) {
            hint(player, "message.boiling-cauldron.pot.hint_start");
        }
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(new SimpleMenuProvider(
                (id, inv, p) -> new BoboChickenPotMenu(id, inv, pot),
                Component.translatable("block.boiling-cauldron.bobo_chicken_pot")),
                buf -> buf.writeBlockPos(pos));
        }
        return ItemInteractionResult.SUCCESS;
    }

    private static void hint(Player player, String translationKey, Object... args) {
        player.displayClientMessage(
            Component.translatable(translationKey, args).withStyle(ChatFormatting.YELLOW), true);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof BoboChickenPotBlockEntity pot) {
                Containers.dropContents(level, pos, pot);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}

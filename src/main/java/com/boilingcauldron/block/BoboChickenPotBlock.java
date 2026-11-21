/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：钵钵鸡锅方块，处理加水、加底料、打开 GUI、形状和破坏掉落。
 * 移植：Fabric → Forge 1.20.1
 * 更新：2026-11-21 补充操作提示（底料/加水失败时给出 actionbar 原因）
 */
package com.boilingcauldron.block;

import com.boilingcauldron.block.entity.BoboChickenPotBlockEntity;
import com.boilingcauldron.block.entity.ModBlockEntities;
import com.boilingcauldron.recipe.BoboSoakRecipe;
import com.boilingcauldron.recipe.ModRecipes;
import com.boilingcauldron.tag.ModTags;
import com.boilingcauldron.screen.BoboChickenPotMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
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
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class BoboChickenPotBlock extends BaseEntityBlock {

    private static final VoxelShape BODY_SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 6.0, 14.0);
    private static final VoxelShape RIM_SHAPE = Block.box(1.0, 6.0, 1.0, 15.0, 7.0, 15.0);
    private static final VoxelShape SHAPE = Shapes.or(BODY_SHAPE, RIM_SHAPE);

    public BoboChickenPotBlock(Properties properties) {
        super(properties);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
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
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (!(level.getBlockEntity(pos) instanceof BoboChickenPotBlockEntity pot)) {
            return InteractionResult.PASS;
        }

        // ---------- 水桶：加水 / 冲洗 ----------
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
                return InteractionResult.SUCCESS;
            }
            // 失败：说明原因，然后仍然打开 GUI，避免玩家被挡住
            if (!pot.isEmpty()) {
                hint(player, "message.boiling-cauldron.pot.not_empty");
            } else if (pot.hasWater()) {
                hint(player, "message.boiling-cauldron.pot.has_water");
            }
        }

        // ---------- 底料 ----------
        // 口味、可用次数、浸泡时长与产物全部来自数据包配方 boiling-cauldron:bobo_soak
        Optional<BoboSoakRecipe> recipeLookup = level.getRecipeManager()
            .getRecipeFor(ModRecipes.BOBO_SOAK_TYPE.get(), new SimpleContainer(stack), level);
        if (recipeLookup.isPresent()) {
            BoboSoakRecipe recipe = recipeLookup.get();
            if (pot.tryAddBase(recipe)) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0F, 1.0F);
                hint(player, "message.boiling-cauldron.pot.added_base",
                    recipe.getFlavor().name(), recipe.getUses());
                return InteractionResult.SUCCESS;
            }
            // 失败：给出具体原因
            if (!pot.hasWater()) {
                hint(player, "message.boiling-cauldron.pot.need_water");
            } else if (pot.getBaseUses() > 0) {
                hint(player, "message.boiling-cauldron.pot.has_base", pot.getBaseUses());
            } else if (!pot.isEmpty()) {
                hint(player, "message.boiling-cauldron.pot.not_empty");
            }
            return InteractionResult.SUCCESS;
        }

        // 是底料物品但数据包里没有对应配方：明确报错，方便数据包作者排查
        if (stack.is(ModTags.BOBO_BASE)) {
            hint(player, "message.boiling-cauldron.pot.no_recipe");
            return InteractionResult.SUCCESS;
        }

        // ---------- 打开 GUI ----------
        if (pot.getBaseFlavor() == null && pot.isEmpty()) {
            hint(player, "message.boiling-cauldron.pot.hint_start");
        }
        if (player instanceof ServerPlayer serverPlayer) {
            NetworkHooks.openScreen(serverPlayer,
                new SimpleMenuProvider(
                    (syncId, inventory, p) -> new BoboChickenPotMenu(syncId, inventory, pot),
                    Component.translatable("block.boiling-cauldron.bobo_chicken_pot")),
                buf -> buf.writeBlockPos(pos));
        }
        return InteractionResult.SUCCESS;
    }

    /** 在物品栏上方（actionbar）提示玩家，避免交互失败时毫无反馈 */
    private static void hint(Player player, String translationKey, Object... args) {
        player.displayClientMessage(
            Component.translatable(translationKey, args).withStyle(ChatFormatting.YELLOW), true);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof BoboChickenPotBlockEntity pot) {
                Containers.dropContents(level, pos, pot);
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}

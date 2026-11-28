/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：调味锅通用方块，处理形状、加水、加底料、打开 GUI 与破坏掉落。
 * 移植：Forge 1.20.1 → NeoForge 1.21.1
 * 更新：2026-11-28 上游 1.0.1：抽出通用基类，供钵钵鸡锅与麻辣烫锅共用
 */
package com.adeahejshshsh.boilingcauldron.block;

import com.adeahejshshsh.boilingcauldron.block.entity.AbstractSeasoningPotBlockEntity;
import com.adeahejshshsh.boilingcauldron.item.FlavorProfile;
import com.adeahejshshsh.boilingcauldron.recipe.BoboSoakRecipe;
import com.adeahejshshsh.boilingcauldron.recipe.ModRecipes;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
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

public abstract class AbstractSeasoningPotBlock extends BaseEntityBlock {

    private static final VoxelShape BODY_SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 6.0, 14.0);
    private static final VoxelShape RIM_SHAPE = Block.box(1.0, 6.0, 1.0, 15.0, 7.0, 15.0);
    private static final VoxelShape SHAPE = Shapes.or(BODY_SHAPE, RIM_SHAPE);

    protected AbstractSeasoningPotBlock(Properties properties) {
        super(properties);
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
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos,
                                           CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                 BlockEntityType<T> type) {
        if (level.isClientSide) {
            return null;
        }
        return (tickLevel, tickPos, tickState, blockEntity) -> {
            if (blockEntity instanceof AbstractSeasoningPotBlockEntity pot) {
                AbstractSeasoningPotBlockEntity.tick(tickLevel, tickPos, tickState, pot);
            }
        };
    }

    /** 空手右键：直接打开界面。 */
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                               BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (!(level.getBlockEntity(pos) instanceof AbstractSeasoningPotBlockEntity pot)) {
            return InteractionResult.PASS;
        }
        if (pot.getBaseFlavor() == null && pot.isEmpty()) {
            hint(player, "message.boiling-cauldron.pot.hint_start");
        }
        openPotScreen(player, pot);
        return InteractionResult.SUCCESS;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }
        if (!(level.getBlockEntity(pos) instanceof AbstractSeasoningPotBlockEntity pot)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        // 潜行时直接开界面，方便放入 / 取出食材
        if (player.isShiftKeyDown()) {
            openPotScreen(player, pot);
            return ItemInteractionResult.SUCCESS;
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
                return ItemInteractionResult.SUCCESS;
            }
            if (!pot.isEmpty()) {
                hint(player, "message.boiling-cauldron.pot.not_empty");
            } else if (pot.hasWater()) {
                hint(player, "message.boiling-cauldron.pot.has_water");
            }
        }

        // ---------- 底料：口味与产出全部来自数据包配方 ----------
        Optional<BoboSoakRecipe> lookup = level.getRecipeManager()
            .getRecipeFor(ModRecipes.BOBO_SOAK_TYPE.get(), new BoboSoakRecipe.Input(stack), level)
            .map(holder -> holder.value());
        if (lookup.isPresent()) {
            BoboSoakRecipe recipe = lookup.get();
            if (pot.tryAddBase(recipe.getFlavor(), recipe.getUses(), recipe.getSoakTime())) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0F, 1.0F);
                hint(player, "message.boiling-cauldron.pot.added_base",
                    recipe.getFlavor().name(), pot.getBaseUses());
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

        // ---------- 子类专属交互（碗装盛取等） ----------
        ItemInteractionResult special = handleUse(state, level, pos, player, hand, hit, pot, stack);
        if (special != ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION) {
            return special;
        }

        // ---------- 打开 GUI ----------
        if (pot.getBaseFlavor() == null && pot.isEmpty()) {
            hint(player, "message.boiling-cauldron.pot.hint_start");
        }
        openPotScreen(player, pot);
        return ItemInteractionResult.SUCCESS;
    }

    /** 子类在通用流程之后接管交互；返回 PASS_TO_DEFAULT 表示不处理。 */
    protected ItemInteractionResult handleUse(BlockState state, Level level, BlockPos pos, Player player,
                                              InteractionHand hand, BlockHitResult hit,
                                              AbstractSeasoningPotBlockEntity pot, ItemStack stack) {
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    protected void openPotScreen(Player player, AbstractSeasoningPotBlockEntity pot) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        BlockPos pos = pot.getBlockPos();
        serverPlayer.openMenu(new SimpleMenuProvider(
                (syncId, inventory, p) -> createPotMenu(syncId, inventory, pot),
                getDisplayName()),
            buf -> buf.writeBlockPos(pos));
    }

    /** 在物品栏上方（actionbar）提示玩家，避免交互失败时毫无反馈。 */
    protected static void hint(Player player, String translationKey, Object... args) {
        player.displayClientMessage(
            Component.translatable(translationKey, args).withStyle(ChatFormatting.YELLOW), true);
    }

    protected abstract Component getDisplayName();

    protected abstract AbstractContainerMenu createPotMenu(int syncId, Inventory playerInventory,
                                                          AbstractSeasoningPotBlockEntity pot);

    /** 快捷方法：从配方管理器解析某个物品对应的口味档案。 */
    @Nullable
    protected static FlavorProfile resolveFlavor(Level level, ItemStack stack) {
        return level.getRecipeManager()
            .getRecipeFor(ModRecipes.BOBO_SOAK_TYPE.get(), new BoboSoakRecipe.Input(stack), level)
            .map(holder -> holder.value().getFlavor())
            .orElse(null);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState,
                            boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof AbstractSeasoningPotBlockEntity pot) {
                Containers.dropContents(level, pos, pot);
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity instanceof AbstractSeasoningPotBlockEntity pot
            ? AbstractContainerMenu.getRedstoneSignalFromContainer(pot)
            : 0;
    }
}

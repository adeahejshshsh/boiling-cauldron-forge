/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：麻辣烫锅方块，复用通用调味锅交互，处理碗装盛取与食材直投。
 * 移植：Forge 1.20.1 → NeoForge 1.21.1
 * 更新：2026-11-28 上游 1.0.1 新增
 */
package com.adeahejshshsh.boilingcauldron.block;

import com.adeahejshshsh.boilingcauldron.block.entity.AbstractSeasoningPotBlockEntity;
import com.adeahejshshsh.boilingcauldron.block.entity.MalatangPotBlockEntity;
import com.adeahejshshsh.boilingcauldron.item.BoboChickenItem;
import com.adeahejshshsh.boilingcauldron.item.FlavorProfile;
import com.adeahejshshsh.boilingcauldron.item.MalatangBowlItem;
import com.adeahejshshsh.boilingcauldron.screen.MalatangPotMenu;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class MalatangPotBlock extends AbstractSeasoningPotBlock {

    public static final MapCodec<MalatangPotBlock> CODEC = simpleCodec(MalatangPotBlock::new);

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    public MalatangPotBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MalatangPotBlockEntity(pos, state);
    }

    @Override
    protected Component getDisplayName() {
        return Component.translatable("block.boiling-cauldron.malatang_pot");
    }

    @Override
    protected AbstractContainerMenu createPotMenu(int syncId, Inventory playerInventory,
                                                  AbstractSeasoningPotBlockEntity pot) {
        return new MalatangPotMenu(syncId, playerInventory, (MalatangPotBlockEntity) pot);
    }

    @Override
    protected ItemInteractionResult handleUse(BlockState state, Level level, BlockPos pos, Player player,
                                              InteractionHand hand, BlockHitResult hit,
                                              AbstractSeasoningPotBlockEntity abstractPot, ItemStack stack) {
        if (!(abstractPot instanceof MalatangPotBlockEntity pot)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        // ---------- 已装食材的碗：继续装 ----------
        if (stack.getItem() instanceof MalatangBowlItem) {
            if (MalatangBowlItem.isFull(stack)) {
                hint(player, "message.boiling-cauldron.malatang_pot.bowl_full");
                return ItemInteractionResult.SUCCESS;
            }
            if (!pot.hasCompletedItem()) {
                hint(player, "message.boiling-cauldron.malatang_pot.no_completed");
                return ItemInteractionResult.SUCCESS;
            }
            FlavorProfile baseFlavor = pot.getBaseFlavor();
            if (!MalatangBowlItem.matchesBase(stack, baseFlavor)) {
                hint(player, "message.boiling-cauldron.malatang_pot.base_mismatch");
                return ItemInteractionResult.SUCCESS;
            }

            int slot = pot.findFirstCompletedSlot();
            if (slot < 0) {
                hint(player, "message.boiling-cauldron.malatang_pot.no_completed");
                return ItemInteractionResult.SUCCESS;
            }
            ItemStack ingredient = pot.getItem(slot).copy();
            if (!MalatangBowlItem.addIngredient(stack, ingredient)) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
            pot.removeCompletedItem(slot);
            player.getInventory().setChanged();
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.containerMenu.broadcastChanges();
            }
            level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0F, 1.0F);
            return ItemInteractionResult.SUCCESS;
        }

        // ---------- 空碗：起一份新的麻辣烫 ----------
        if (stack.is(Items.BOWL)) {
            if (!pot.hasCompletedItem()) {
                hint(player, "message.boiling-cauldron.malatang_pot.no_completed");
                return ItemInteractionResult.SUCCESS;
            }
            FlavorProfile baseFlavor = pot.getBaseFlavor();
            if (baseFlavor == null) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
            int slot = pot.findFirstCompletedSlot();
            if (slot < 0) {
                hint(player, "message.boiling-cauldron.malatang_pot.no_completed");
                return ItemInteractionResult.SUCCESS;
            }
            ItemStack ingredient = pot.getItem(slot).copy();
            ItemStack bowl = MalatangBowlItem.create(baseFlavor, ingredient);
            if (bowl.isEmpty()) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }

            pot.removeCompletedItem(slot);
            stack.shrink(1);
            player.getInventory().setChanged();
            if (!player.getInventory().add(bowl)) {
                player.drop(bowl, false);
            }
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.containerMenu.broadcastChanges();
            }
            level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            return ItemInteractionResult.SUCCESS;
        }

        // 钵钵鸡成品不能下麻辣烫锅
        if (stack.getItem() instanceof BoboChickenItem) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        // ---------- 生食材直接下锅 ----------
        if (stack.has(DataComponents.FOOD) && pot.insertIngredient(stack, true)) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.containerMenu.broadcastChanges();
            }
            level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0F, 1.0F);
            return ItemInteractionResult.SUCCESS;
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}

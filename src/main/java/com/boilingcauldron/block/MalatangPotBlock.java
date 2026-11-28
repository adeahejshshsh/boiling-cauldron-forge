/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：麻辣烫锅方块，复用通用调味锅交互，处理碗装盛取与食材直投。
 * 移植：Fabric → Forge 1.20.1
 * 更新：2026-11-28 上游 1.0.1 新增
 */
package com.boilingcauldron.block;

import com.boilingcauldron.block.entity.AbstractSeasoningPotBlockEntity;
import com.boilingcauldron.block.entity.MalatangPotBlockEntity;
import com.boilingcauldron.item.BoboChickenItem;
import com.boilingcauldron.item.FlavorProfile;
import com.boilingcauldron.item.MalatangBowlItem;
import com.boilingcauldron.screen.MalatangPotMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class MalatangPotBlock extends AbstractSeasoningPotBlock {

    public MalatangPotBlock(Properties properties) {
        super(properties);
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
    protected InteractionResult handleUse(BlockState state, Level level, BlockPos pos, Player player,
                                          InteractionHand hand, BlockHitResult hit,
                                          AbstractSeasoningPotBlockEntity abstractPot, ItemStack stack) {
        if (!(abstractPot instanceof MalatangPotBlockEntity pot)) {
            return InteractionResult.PASS;
        }

        // ---------- 已装食材的碗：继续装 ----------
        if (stack.getItem() instanceof MalatangBowlItem) {
            if (MalatangBowlItem.isFull(stack)) {
                hint(player, "message.boiling-cauldron.malatang_pot.bowl_full");
                return InteractionResult.SUCCESS;
            }
            if (!pot.hasCompletedItem()) {
                hint(player, "message.boiling-cauldron.malatang_pot.no_completed");
                return InteractionResult.SUCCESS;
            }
            FlavorProfile baseFlavor = pot.getBaseFlavor();
            if (!MalatangBowlItem.matchesBase(stack, baseFlavor)) {
                hint(player, "message.boiling-cauldron.malatang_pot.base_mismatch");
                return InteractionResult.SUCCESS;
            }

            int slot = pot.findFirstCompletedSlot();
            if (slot < 0) {
                hint(player, "message.boiling-cauldron.malatang_pot.no_completed");
                return InteractionResult.SUCCESS;
            }
            ItemStack ingredient = pot.getItem(slot).copy();
            if (!MalatangBowlItem.addIngredient(stack, ingredient)) {
                return InteractionResult.PASS;
            }
            pot.removeCompletedItem(slot);
            player.getInventory().setChanged();
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.containerMenu.broadcastChanges();
            }
            level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0F, 1.0F);
            return InteractionResult.SUCCESS;
        }

        // ---------- 空碗：起一份新的麻辣烫 ----------
        if (stack.is(Items.BOWL)) {
            if (!pot.hasCompletedItem()) {
                hint(player, "message.boiling-cauldron.malatang_pot.no_completed");
                return InteractionResult.SUCCESS;
            }
            FlavorProfile baseFlavor = pot.getBaseFlavor();
            if (baseFlavor == null) {
                return InteractionResult.PASS;
            }
            int slot = pot.findFirstCompletedSlot();
            if (slot < 0) {
                hint(player, "message.boiling-cauldron.malatang_pot.no_completed");
                return InteractionResult.SUCCESS;
            }
            ItemStack ingredient = pot.getItem(slot).copy();
            ItemStack bowl = MalatangBowlItem.create(baseFlavor, ingredient);
            if (bowl.isEmpty()) {
                return InteractionResult.PASS;
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
            return InteractionResult.SUCCESS;
        }

        // 钵钵鸡成品不能下麻辣烫锅
        if (stack.getItem() instanceof BoboChickenItem) {
            return InteractionResult.PASS;
        }

        // ---------- 生食材直接下锅 ----------
        if (stack.isEdible() && pot.insertIngredient(stack, true)) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.containerMenu.broadcastChanges();
            }
            level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0F, 1.0F);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}

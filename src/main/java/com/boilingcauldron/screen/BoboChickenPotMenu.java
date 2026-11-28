/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：钵钵鸡锅容器逻辑，复用通用调味锅槽位，只处理钵钵鸡搬运扣次规则。
 * 移植：Fabric → Forge 1.20.1
 * 更新：2026-11-28 上游 1.0.1：改为继承 AbstractSeasoningPotMenu
 */
package com.boilingcauldron.screen;

import com.boilingcauldron.block.entity.BoboChickenPotBlockEntity;
import com.boilingcauldron.item.BoboChickenItem;
import com.boilingcauldron.item.MalatangBowlItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class BoboChickenPotMenu extends AbstractSeasoningPotMenu<BoboChickenPotBlockEntity> {

    public BoboChickenPotMenu(int syncId, Inventory playerInventory, BoboChickenPotBlockEntity blockEntity) {
        super(ModMenus.BOBO_CHICKEN_POT.get(), syncId, playerInventory, blockEntity, blockEntity.getBlockPos());
    }

    public BoboChickenPotMenu(int syncId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(syncId, playerInventory, buf.readBlockPos());
    }

    public BoboChickenPotMenu(int syncId, Inventory playerInventory, BlockPos pos) {
        this(syncId, playerInventory, pos, findClientData(playerInventory, pos,
            BoboChickenPotBlockEntity.class, BoboChickenPotBlockEntity.SLOT_COUNT));
    }

    private BoboChickenPotMenu(int syncId, Inventory playerInventory, BlockPos pos,
                               ClientData<BoboChickenPotBlockEntity> data) {
        super(ModMenus.BOBO_CHICKEN_POT.get(), syncId, playerInventory, data.container(), pos, data.blockEntity());
    }

    /** 钵钵鸡在锅内搬运时仍需扣底料（相当于再泡一次）。 */
    @Override
    protected boolean shouldChargeWhenMoving(ItemStack stack) {
        return stack.getItem() instanceof BoboChickenItem;
    }

    @Override
    protected boolean canInsertOnClient(ItemStack stack) {
        return super.canInsertOnClient(stack) && !(stack.getItem() instanceof MalatangBowlItem);
    }
}

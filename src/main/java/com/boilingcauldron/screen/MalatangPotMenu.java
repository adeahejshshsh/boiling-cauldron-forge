/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：麻辣烫锅容器逻辑，复用通用调味锅槽位，提供热源状态查询。
 * 移植：Fabric → Forge 1.20.1
 * 更新：2026-11-28 上游 1.0.1 新增
 */
package com.boilingcauldron.screen;

import com.boilingcauldron.block.entity.MalatangPotBlockEntity;
import com.boilingcauldron.item.BoboChickenItem;
import com.boilingcauldron.item.MalatangBowlItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class MalatangPotMenu extends AbstractSeasoningPotMenu<MalatangPotBlockEntity> {

    public MalatangPotMenu(int syncId, Inventory playerInventory, MalatangPotBlockEntity blockEntity) {
        super(ModMenus.MALATANG_POT.get(), syncId, playerInventory, blockEntity, blockEntity.getBlockPos());
    }

    public MalatangPotMenu(int syncId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(syncId, playerInventory, buf.readBlockPos());
    }

    public MalatangPotMenu(int syncId, Inventory playerInventory, BlockPos pos) {
        this(syncId, playerInventory, pos, findClientData(playerInventory, pos,
            MalatangPotBlockEntity.class, MalatangPotBlockEntity.SLOT_COUNT));
    }

    private MalatangPotMenu(int syncId, Inventory playerInventory, BlockPos pos,
                            ClientData<MalatangPotBlockEntity> data) {
        super(ModMenus.MALATANG_POT.get(), syncId, playerInventory, data.container(), pos, data.blockEntity());
    }

    public boolean isHeated() {
        return this.blockEntity != null && this.blockEntity.isHeated();
    }

    @Override
    protected boolean canInsertOnClient(ItemStack stack) {
        return super.canInsertOnClient(stack)
            && !(stack.getItem() instanceof BoboChickenItem)
            && !(stack.getItem() instanceof MalatangBowlItem);
    }
}

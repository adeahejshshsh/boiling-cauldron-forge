/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：调味锅通用容器逻辑，管理 4×4 槽位、物品移动与底料扣次。
 * 移植：Fabric → Forge 1.20.1
 * 更新：2026-11-28 上游 1.0.1：抽出通用基类，供钵钵鸡锅与麻辣烫锅共用
 */
package com.boilingcauldron.screen;

import com.boilingcauldron.block.entity.AbstractSeasoningPotBlockEntity;
import com.boilingcauldron.item.FlavorProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractSeasoningPotMenu<T extends AbstractSeasoningPotBlockEntity>
    extends AbstractContainerMenu {

    public static final int SLOTS_PER_ROW = 4;
    public static final int POT_SLOT_START_X = 53;
    public static final int POT_SLOT_START_Y = 35;
    public static final int PLAYER_INVENTORY_X = 8;
    public static final int PLAYER_INVENTORY_Y = 120;
    public static final int HOTBAR_Y = 178;

    protected final Container container;
    protected final BlockPos pos;
    @Nullable
    protected final T blockEntity;
    private boolean lastTakenFromPot;
    private boolean movingWithinPot;

    protected AbstractSeasoningPotMenu(MenuType<?> type, int syncId, Inventory playerInventory,
                                       T blockEntity, BlockPos pos) {
        this(type, syncId, playerInventory, blockEntity, pos, blockEntity);
    }

    protected AbstractSeasoningPotMenu(MenuType<?> type, int syncId, Inventory playerInventory,
                                       Container container, BlockPos pos, @Nullable T blockEntity) {
        super(type, syncId);
        this.container = container;
        this.pos = pos;
        this.blockEntity = blockEntity;
        container.startOpen(playerInventory.player);

        int slotCount = container.getContainerSize();
        int rows = (slotCount + SLOTS_PER_ROW - 1) / SLOTS_PER_ROW;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < SLOTS_PER_ROW; col++) {
                int index = col + row * SLOTS_PER_ROW;
                if (index >= slotCount) {
                    break;
                }
                this.addSlot(new SeasoningPotSlot(container, index,
                    POT_SLOT_START_X + col * 18, POT_SLOT_START_Y + row * 18));
            }
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9,
                    PLAYER_INVENTORY_X + col * 18, PLAYER_INVENTORY_Y + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, PLAYER_INVENTORY_X + col * 18, HOTBAR_Y));
        }
    }

    /** 客户端侧拿不到方块实体时，用空容器占位，避免除零 / 空指针。 */
    protected static <T extends AbstractSeasoningPotBlockEntity> ClientData<T> findClientData(
            Inventory playerInventory, BlockPos pos, Class<T> blockEntityClass, int fallbackSlotCount) {
        BlockEntity blockEntity = playerInventory.player.level().getBlockEntity(pos);
        if (blockEntityClass.isInstance(blockEntity)) {
            T typed = blockEntityClass.cast(blockEntity);
            return new ClientData<>(typed, typed);
        }
        return new ClientData<>(new SimpleContainer(fallbackSlotCount), null);
    }

    @Override
    public void clicked(int slotIndex, int button, ClickType actionType, Player player) {
        boolean clickedPot = slotIndex >= 0 && slotIndex < this.container.getContainerSize();
        boolean clickedPotHadItem = clickedPot && this.slots.get(slotIndex).hasItem();
        ItemStack cursorBefore = this.getCarried().copy();
        boolean movedFromPot = this.lastTakenFromPot;
        this.lastTakenFromPot = false;
        this.movingWithinPot = movedFromPot && clickedPot && actionType == ClickType.PICKUP;

        try {
            super.clicked(slotIndex, button, actionType, player);
        } finally {
            this.movingWithinPot = false;
        }

        if (actionType == ClickType.PICKUP && clickedPot && cursorBefore.isEmpty()
            && clickedPotHadItem && !this.slots.get(slotIndex).hasItem()) {
            this.lastTakenFromPot = true;
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();
        ItemStack result = stack.copy();
        int potSlotCount = this.container.getContainerSize();

        if (index < potSlotCount) {
            if (!this.moveItemStackTo(stack, potSlotCount, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            boolean inserted = false;
            for (int potIndex = 0; potIndex < potSlotCount && !stack.isEmpty(); potIndex++) {
                Slot potSlot = this.slots.get(potIndex);
                if (!potSlot.hasItem() && potSlot.mayPlace(stack)) {
                    potSlot.set(stack.split(1));
                    potSlot.setChanged();
                    inserted = true;
                }
            }
            if (!inserted) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return result;
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.blockEntity != null) {
            return this.blockEntity.stillValid(player);
        }
        return player.distanceToSqr(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5) <= 64.0;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.container.stopOpen(player);
        if (this.blockEntity != null && this.blockEntity.getLevel() != null
            && !this.blockEntity.getLevel().isClientSide) {
            this.blockEntity.clearExhaustedBaseIfEmpty();
        }
    }

    @Nullable
    public T getBlockEntity() {
        return this.blockEntity;
    }

    public boolean isMovingWithinPot() {
        return this.movingWithinPot;
    }

    public int getProgress(int slot) {
        return this.blockEntity == null ? -1 : this.blockEntity.getProgress(slot);
    }

    public boolean hasWater() {
        return this.blockEntity != null && this.blockEntity.hasWater();
    }

    public int getBaseUses() {
        return this.blockEntity == null ? 0 : this.blockEntity.getBaseUses();
    }

    public int getEffectiveCookingTicks() {
        return this.blockEntity == null ? 1 : this.blockEntity.getEffectiveCookingTicks();
    }

    @Nullable
    public FlavorProfile getBaseFlavor() {
        return this.blockEntity == null ? null : this.blockEntity.getBaseFlavor();
    }

    /** 锅之间搬运物品时是否要扣一次底料（钵钵鸡需要，麻辣烫不需要）。 */
    protected boolean shouldChargeWhenMoving(ItemStack stack) {
        return false;
    }

    /** 客户端侧（没有方块实体）判断某个物品是否允许放入。 */
    protected boolean canInsertOnClient(ItemStack stack) {
        return stack.isEdible();
    }

    protected record ClientData<T extends AbstractSeasoningPotBlockEntity>(Container container,
                                                                           @Nullable T blockEntity) {
    }

    private class SeasoningPotSlot extends Slot {

        private SeasoningPotSlot(Container container, int index, int x, int y) {
            super(container, index, x, y);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            T pot = AbstractSeasoningPotMenu.this.blockEntity;
            if (pot == null) {
                return AbstractSeasoningPotMenu.this.canInsertOnClient(stack);
            }
            return pot.canAcceptItem(stack, !AbstractSeasoningPotMenu.this.isMovingWithinPot());
        }

        @Override
        public void set(ItemStack stack) {
            ItemStack before = this.getItem().copy();
            super.set(stack);

            T pot = AbstractSeasoningPotMenu.this.blockEntity;
            if (pot == null || pot.getLevel() == null || pot.getLevel().isClientSide) {
                return;
            }

            boolean beforeEmpty = before.isEmpty();
            boolean afterEmpty = stack.isEmpty();
            if (!beforeEmpty && afterEmpty) {
                pot.onItemRemoved(this.index);
            } else if (beforeEmpty && !afterEmpty) {
                pot.startCooking(this.index, this.shouldCharge(stack));
            } else if (!beforeEmpty && !ItemStack.matches(before, stack)) {
                pot.onItemRemoved(this.index);
                pot.startCooking(this.index, this.shouldCharge(stack));
            }
        }

        private boolean shouldCharge(ItemStack stack) {
            return !AbstractSeasoningPotMenu.this.isMovingWithinPot()
                || AbstractSeasoningPotMenu.this.shouldChargeWhenMoving(stack);
        }

        @Override
        public ItemStack remove(int amount) {
            ItemStack result = super.remove(amount);
            T pot = AbstractSeasoningPotMenu.this.blockEntity;
            if (!result.isEmpty() && pot != null && pot.getLevel() != null && !pot.getLevel().isClientSide) {
                pot.onItemRemoved(this.index);
            }
            return result;
        }
    }
}

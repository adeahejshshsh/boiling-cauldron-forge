/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：钵钵鸡锅容器逻辑，管理槽位、物品移动和底料扣次。
 * 移植：Fabric → Forge 1.20.1（ScreenHandler → AbstractContainerMenu）
 */
package com.boilingcauldron.screen;

import com.boilingcauldron.block.entity.BoboChickenPotBlockEntity;
import com.boilingcauldron.item.BoboChickenItem;
import com.boilingcauldron.item.FlavorProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public class BoboChickenPotMenu extends AbstractContainerMenu {
    public static final int POT_SLOT_START_X = 53;
    public static final int POT_SLOT_START_Y = 35;
    public static final int PLAYER_INVENTORY_X = 8;
    public static final int PLAYER_INVENTORY_Y = 120;
    public static final int HOTBAR_Y = 178;

    private final Container inventory;
    private final BlockPos pos;
    @Nullable
    private final BoboChickenPotBlockEntity blockEntity;
    private boolean lastTakenFromPot;
    private boolean movingWithinPot;

    public BoboChickenPotMenu(int syncId, Inventory playerInventory, BoboChickenPotBlockEntity blockEntity) {
        this(syncId, playerInventory, blockEntity, blockEntity.getBlockPos(), blockEntity);
    }

    public BoboChickenPotMenu(int syncId, Inventory playerInventory, BlockPos pos) {
        this(syncId, playerInventory, clientInventory(playerInventory, pos), pos,
            clientBlockEntity(playerInventory, pos));
    }

    private BoboChickenPotMenu(int syncId, Inventory playerInventory, Container inventory, BlockPos pos,
                               @Nullable BoboChickenPotBlockEntity blockEntity) {
        super(ModMenus.BOBO_CHICKEN_POT.get(), syncId);
        checkContainerSize(inventory, BoboChickenPotBlockEntity.SLOT_COUNT);
        this.inventory = inventory;
        this.pos = pos;
        this.blockEntity = blockEntity;
        inventory.startOpen(playerInventory.player);

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                int index = col + row * BoboChickenPotBlockEntity.SLOTS_PER_ROW;
                this.addSlot(new BoboChickenSlot(this, inventory, index,
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

    private static Container clientInventory(Inventory playerInventory, BlockPos pos) {
        BlockEntity blockEntity = playerInventory.player.level().getBlockEntity(pos);
        return blockEntity instanceof BoboChickenPotBlockEntity pot
            ? pot
            : new SimpleContainer(BoboChickenPotBlockEntity.SLOT_COUNT);
    }

    @Nullable
    private static BoboChickenPotBlockEntity clientBlockEntity(Inventory playerInventory, BlockPos pos) {
        BlockEntity blockEntity = playerInventory.player.level().getBlockEntity(pos);
        return blockEntity instanceof BoboChickenPotBlockEntity pot ? pot : null;
    }

    @Override
    public void clicked(int slotIndex, int button, ClickType actionType, Player player) {
        boolean clickedPot = slotIndex >= 0 && slotIndex < BoboChickenPotBlockEntity.SLOT_COUNT;
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

        if (index < BoboChickenPotBlockEntity.SLOT_COUNT) {
            if (!this.moveItemStackTo(stack, BoboChickenPotBlockEntity.SLOT_COUNT, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            boolean inserted = false;
            for (int potIndex = 0; potIndex < BoboChickenPotBlockEntity.SLOT_COUNT && !stack.isEmpty(); potIndex++) {
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
        this.inventory.stopOpen(player);
        if (this.blockEntity != null && this.blockEntity.getLevel() != null
            && !this.blockEntity.getLevel().isClientSide) {
            this.blockEntity.clearExhaustedBaseIfEmpty();
        }
    }

    @Nullable
    public BoboChickenPotBlockEntity getBlockEntity() {
        return this.blockEntity;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
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

    public int getSoakTicks() {
        return this.blockEntity == null
            ? BoboChickenPotBlockEntity.DEFAULT_SOAK_TICKS
            : this.blockEntity.getSoakTicks();
    }

    @Nullable
    public FlavorProfile getBaseFlavor() {
        return this.blockEntity == null ? null : this.blockEntity.getBaseFlavor();
    }

    private static class BoboChickenSlot extends Slot {
        private final BoboChickenPotMenu handler;

        private BoboChickenSlot(BoboChickenPotMenu handler, Container container, int index, int x, int y) {
            super(container, index, x, y);
            this.handler = handler;
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            BoboChickenPotBlockEntity blockEntity = this.handler.getBlockEntity();
            if (blockEntity == null) {
                return stack.isEdible();
            }
            return blockEntity.canAcceptFood(stack, !this.handler.isMovingWithinPot());
        }

        @Override
        public void set(ItemStack stack) {
            ItemStack before = this.getItem().copy();
            super.set(stack);

            BoboChickenPotBlockEntity blockEntity = this.handler.getBlockEntity();
            if (blockEntity == null || blockEntity.getLevel() == null || blockEntity.getLevel().isClientSide) {
                return;
            }

            boolean beforeEmpty = before.isEmpty();
            boolean afterEmpty = stack.isEmpty();
            if (!beforeEmpty && afterEmpty) {
                blockEntity.onFoodRemoved(this.index);
            } else if (beforeEmpty && !afterEmpty) {
                blockEntity.startSoak(this.index, this.shouldCharge(stack));
            } else if (!beforeEmpty && !ItemStack.matches(before, stack)) {
                blockEntity.onFoodRemoved(this.index);
                blockEntity.startSoak(this.index, this.shouldCharge(stack));
            }
        }

        private boolean shouldCharge(ItemStack stack) {
            return !this.handler.isMovingWithinPot() || stack.getItem() instanceof BoboChickenItem;
        }

        @Override
        public ItemStack remove(int amount) {
            ItemStack result = super.remove(amount);
            BoboChickenPotBlockEntity blockEntity = this.handler.getBlockEntity();
            if (!result.isEmpty() && blockEntity != null && blockEntity.getLevel() != null
                && !blockEntity.getLevel().isClientSide) {
                blockEntity.onFoodRemoved(this.index);
            }
            return result;
        }
    }
}

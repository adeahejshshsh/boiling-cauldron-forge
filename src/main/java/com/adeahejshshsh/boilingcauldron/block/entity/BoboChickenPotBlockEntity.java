package com.adeahejshshsh.boilingcauldron.block.entity;

import com.adeahejshshsh.boilingcauldron.item.BoboChickenItem;
import com.adeahejshshsh.boilingcauldron.item.FlavorProfile;
import com.adeahejshshsh.boilingcauldron.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/** 钵钵鸡锅：16 格冷泡容器。 */
public class BoboChickenPotBlockEntity extends BlockEntity implements WorldlyContainer {

    public static final int SLOT_COUNT = 16;
    public static final int SLOTS_PER_ROW = 4;
    public static final int DEFAULT_SOAK_TICKS = 20 * 60;
    public static final int DEFAULT_BASE_USES = 64;
    private static final int SYNC_INTERVAL = 10;

    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
    private final int[] progress = new int[SLOT_COUNT];
    private boolean hasWater;
    @Nullable
    private FlavorProfile baseFlavor;
    private int baseUses;
    private int syncTicks;
    private int soakTicks = DEFAULT_SOAK_TICKS;
    private ItemStack resultItem = ItemStack.EMPTY;

    public BoboChickenPotBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BOBO_CHICKEN_POT.get(), pos, state);
        Arrays.fill(progress, -1);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BoboChickenPotBlockEntity blockEntity) {
        if (level.isClientSide) {
            return;
        }
        boolean changed = false;
        boolean active = false;
        for (int slot = 0; slot < SLOT_COUNT; slot++) {
            if (blockEntity.progress[slot] < 0) {
                continue;
            }
            if (blockEntity.items.get(slot).isEmpty()) {
                blockEntity.progress[slot] = -1;
                changed = true;
                continue;
            }
            if (blockEntity.progress[slot] >= blockEntity.soakTicks) {
                if (!(blockEntity.items.get(slot).getItem() instanceof BoboChickenItem)) {
                    blockEntity.finishSlot(slot);
                    changed = true;
                }
                continue;
            }
            blockEntity.progress[slot]++;
            if (blockEntity.progress[slot] >= blockEntity.soakTicks) {
                blockEntity.finishSlot(slot);
                changed = true;
            } else {
                active = true;
            }
        }
        blockEntity.syncTicks++;
        if (changed || (active && blockEntity.syncTicks >= SYNC_INTERVAL)) {
            blockEntity.syncTicks = 0;
            blockEntity.sync();
        }
    }

    public boolean useWaterBucket() {
        if (hasWater) {
            if (baseUses > 0 && isEmpty()) {
                baseFlavor = null;
                baseUses = 0;
                sync();
                return true;
            }
            return false;
        }
        if (!isEmpty()) {
            return false;
        }
        hasWater = true;
        baseFlavor = null;
        baseUses = 0;
        sync();
        return true;
    }

    public boolean tryAddBase(FlavorProfile flavor, int uses, int soak, ItemStack result) {
        if (!hasWater || baseUses > 0 || !isEmpty()) {
            return false;
        }
        baseFlavor = flavor;
        baseUses = uses;
        soakTicks = soak;
        resultItem = result.copy();
        sync();
        return true;
    }

    public boolean canAcceptFood(ItemStack stack, boolean requireCharge) {
        if (baseFlavor == null || stack.isEmpty() || !stack.has(net.minecraft.core.component.DataComponents.FOOD)) {
            return false;
        }
        if (stack.getItem() instanceof BoboChickenItem) {
            if (!BoboChickenItem.canAddFlavor(stack, baseFlavor) || baseUses <= 0) {
                return false;
            }
        }
        return !requireCharge || baseUses > 0;
    }

    public boolean startSoak(int slot, boolean charge) {
        if (slot < 0 || slot >= SLOT_COUNT || baseFlavor == null) {
            return false;
        }
        ItemStack stack = getItem(slot);
        if (stack.isEmpty() || !stack.has(net.minecraft.core.component.DataComponents.FOOD)) {
            return false;
        }
        if (stack.getItem() instanceof BoboChickenItem) {
            if (!BoboChickenItem.canAddFlavor(stack, baseFlavor) || !charge) {
                return false;
            }
        }
        if (charge) {
            if (baseUses <= 0) {
                return false;
            }
            baseUses--;
            if (baseUses == 0) {
                hasWater = false;
            }
        }
        progress[slot] = 0;
        sync();
        return true;
    }

    public void onFoodRemoved(int slot) {
        if (slot < 0 || slot >= SLOT_COUNT) {
            return;
        }
        progress[slot] = -1;
        sync();
    }

    public void clearExhaustedBaseIfEmpty() {
        if (baseFlavor != null && !hasWater && baseUses <= 0 && isEmpty()) {
            baseFlavor = null;
            sync();
        }
    }

    public int getProgress(int slot) {
        return (slot < 0 || slot >= SLOT_COUNT) ? -1 : progress[slot];
    }

    public boolean hasWater() {
        return hasWater;
    }

    public int getBaseUses() {
        return baseUses;
    }

    public int getSoakTicks() {
        return soakTicks;
    }

    @Nullable
    public FlavorProfile getBaseFlavor() {
        return baseFlavor;
    }

    private void finishSlot(int slot) {
        ItemStack original = items.get(slot);
        if (original.isEmpty() || baseFlavor == null) {
            progress[slot] = -1;
            return;
        }
        ItemStack result = resultItem.isEmpty()
            ? new ItemStack(ModItems.BOBO_CHICKEN.get())
            : resultItem.copy();
        if (original.getItem() instanceof BoboChickenItem) {
            CompoundTag originalTag = BoboChickenItem.copyTagPublic(original);
            result.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.of(originalTag));
        } else {
            BoboChickenItem.setOriginalItem(result, original.getItem());
        }
        if (!BoboChickenItem.addFlavor(result, baseFlavor)) {
            progress[slot] = -1;
            return;
        }
        items.set(slot, result);
        progress[slot] = soakTicks;
        setChanged();
    }

    private void sync() {
        setChanged();
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    // ---------------- Container ----------------

    @Override
    public int getContainerSize() {
        return SLOT_COUNT;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        if (slot < 0 || slot >= SLOT_COUNT || amount <= 0) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = items.get(slot);
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack result = stack.split(amount);
        setChanged();
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return removeItem(slot, 1);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot < 0 || slot >= SLOT_COUNT) {
            return;
        }
        if (stack.getCount() > 1) {
            stack.setCount(1);
        }
        items.set(slot, stack);
        setChanged();
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        items.clear();
        Arrays.fill(progress, -1);
        hasWater = false;
        baseFlavor = null;
        baseUses = 0;
        soakTicks = DEFAULT_SOAK_TICKS;
        resultItem = ItemStack.EMPTY;
        setChanged();
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return new int[0];
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction) {
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return false;
    }

    // ---------------- NBT ----------------

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, items, registries);
        tag.putIntArray("Progress", progress);
        tag.putBoolean("HasWater", hasWater);
        tag.putInt("BaseUses", baseUses);
        tag.putInt("SoakTicks", soakTicks);
        if (!resultItem.isEmpty()) {
            tag.put("ResultItem", resultItem.save(registries));
        }
        if (baseFlavor != null) {
            tag.put("BaseFlavor", baseFlavor.save());
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        ContainerHelper.loadAllItems(tag, items, registries);
        Arrays.fill(progress, -1);
        soakTicks = tag.contains("SoakTicks") ? Math.max(1, tag.getInt("SoakTicks")) : DEFAULT_SOAK_TICKS;
        int[] savedProgress = tag.getIntArray("Progress");
        for (int i = 0; i < Math.min(savedProgress.length, SLOT_COUNT); i++) {
            if (!items.get(i).isEmpty()) {
                progress[i] = Math.max(-1, Math.min(savedProgress[i], soakTicks));
            }
        }
        hasWater = tag.getBoolean("HasWater");
        baseUses = Math.max(0, tag.getInt("BaseUses"));
        resultItem = tag.contains("ResultItem") ? ItemStack.parse(registries, tag.getCompound("ResultItem")).orElse(ItemStack.EMPTY) : ItemStack.EMPTY;
        baseFlavor = tag.contains("BaseFlavor", Tag.TAG_COMPOUND) ? FlavorProfile.load(tag.getCompound("BaseFlavor")) : null;
        if (baseFlavor == null) {
            baseUses = 0;
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveCustomOnly(registries);
    }
}

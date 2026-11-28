/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：调味锅通用 BlockEntity，管理水、底料、槽位、进度、同步与 NBT。
 * 移植：Fabric → Forge 1.20.1
 * 更新：2026-11-28 上游 1.0.1：抽出通用基类，供钵钵鸡锅与麻辣烫锅共用
 */
package com.boilingcauldron.block.entity;

import com.boilingcauldron.item.FlavorProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * 调味锅通用方块实体。
 *
 * <p>两类锅（钵钵鸡锅、麻辣烫锅）共享水/底料/槽位/进度这一套机制，
 * 差异只在「煮多久」「底料能用几次」「什么能吃」「做完怎么处理」。
 */
public abstract class AbstractSeasoningPotBlockEntity extends BlockEntity implements WorldlyContainer {

    private static final String PROGRESS_KEY = "Progress";
    private static final String HAS_WATER_KEY = "HasWater";
    private static final String BASE_USES_KEY = "BaseUses";
    private static final String BASE_FLAVOR_KEY = "BaseFlavor";
    private static final String COOKING_TICKS_KEY = "CookingTicks";
    private static final int SYNC_INTERVAL = 10;

    protected final NonNullList<ItemStack> items;
    protected final int[] progress;
    private boolean hasWater;
    @Nullable
    private FlavorProfile baseFlavor;
    private int baseUses;
    /** 实际生效的烹饪/浸泡时长；未指定时回落到 {@link #getCookingTicks()}。 */
    private int cookingTicks;
    private int syncTicks;

    protected AbstractSeasoningPotBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int slotCount) {
        super(type, pos, state);
        this.items = NonNullList.withSize(slotCount, ItemStack.EMPTY);
        this.progress = new int[slotCount];
        this.cookingTicks = getCookingTicks();
        Arrays.fill(this.progress, -1);
    }

    // ---------------- 子类规则 ----------------

    /** 默认烹饪/浸泡时长（tick）。 */
    public abstract int getCookingTicks();

    /** 一份底料能提供的总次数。 */
    public abstract int getBaseCapacity();

    /** 该物品能否放入锅中进行处理。 */
    public abstract boolean canAcceptItem(ItemStack stack, boolean charge);

    /** 该物品开始处理时是否必须扣一次底料。 */
    protected boolean requiresChargeToStart(ItemStack stack) {
        return false;
    }

    /**
     * 是否允许数据包配方覆盖时长与次数。
     *
     * <p>钵钵鸡锅沿用配方里的 soak_time / uses（默认 60 秒、64 次）；
     * 麻辣烫锅固定 20 秒，不接受覆盖。
     */
    protected boolean usesRecipeTiming() {
        return true;
    }

    /** 是否需要下方热源。 */
    protected boolean requiresHeatSource() {
        return false;
    }

    protected boolean hasHeatSource(Level level, BlockPos pos, BlockState state) {
        return true;
    }

    /** 某个槽位处理完成时调用。 */
    protected abstract void finishSlot(int slot);

    /** 已完成但需要重新处理的槽位（例如钵钵鸡可以继续加口味）。 */
    protected boolean shouldFinishCompletedSlot(int slot) {
        return false;
    }

    // ---------------- 主循环 ----------------

    public static void tick(Level level, BlockPos pos, BlockState state, AbstractSeasoningPotBlockEntity pot) {
        if (level.isClientSide) {
            return;
        }

        boolean canCook = !pot.requiresHeatSource() || pot.hasHeatSource(level, pos, state);
        boolean changed = false;
        boolean active = false;
        int ticks = pot.getEffectiveCookingTicks();

        for (int slot = 0; slot < pot.items.size(); slot++) {
            if (pot.progress[slot] < 0) {
                continue;
            }
            if (pot.items.get(slot).isEmpty()) {
                pot.progress[slot] = -1;
                changed = true;
                continue;
            }

            if (pot.progress[slot] >= ticks) {
                if (pot.shouldFinishCompletedSlot(slot)) {
                    pot.finishSlot(slot);
                    changed = true;
                }
                continue;
            }

            if (!canCook) {
                continue;
            }

            pot.progress[slot]++;
            if (pot.progress[slot] >= ticks) {
                pot.finishSlot(slot);
                changed = true;
            } else {
                active = true;
            }
        }

        pot.syncTicks++;
        if (changed || (active && pot.syncTicks >= SYNC_INTERVAL)) {
            pot.syncTicks = 0;
            pot.sync();
        }
    }

    public int getEffectiveCookingTicks() {
        return this.cookingTicks > 0 ? this.cookingTicks : getCookingTicks();
    }

    public void setEffectiveCookingTicks(int ticks) {
        this.cookingTicks = ticks > 0 ? ticks : getCookingTicks();
    }

    // ---------------- 水与底料 ----------------

    /** 用水桶加水；锅里已有底料且为空锅时，视为冲洗底料。 */
    public boolean useWaterBucket() {
        if (this.hasWater) {
            if (this.baseUses > 0 && isEmpty()) {
                this.baseFlavor = null;
                this.baseUses = 0;
                sync();
                return true;
            }
            return false;
        }

        if (!isEmpty()) {
            return false;
        }

        this.hasWater = true;
        this.baseFlavor = null;
        this.baseUses = 0;
        sync();
        return true;
    }

    /**
     * 放入底料。
     *
     * @param flavor      口味档案
     * @param recipeUses  数据包指定的次数；≤0 表示用锅的默认值
     * @param recipeTicks 数据包指定的时长；≤0 表示用锅的默认值
     */
    public boolean tryAddBase(FlavorProfile flavor, int recipeUses, int recipeTicks) {
        if (!this.hasWater || this.baseUses > 0 || !isEmpty()) {
            return false;
        }

        this.baseFlavor = flavor;
        boolean useRecipe = usesRecipeTiming();
        this.baseUses = useRecipe && recipeUses > 0 ? recipeUses : getBaseCapacity();
        this.cookingTicks = useRecipe && recipeTicks > 0 ? recipeTicks : getCookingTicks();
        sync();
        return true;
    }

    /** 开始处理某个槽位；charge 为 true 时扣一次底料。 */
    public boolean startCooking(int slot, boolean charge) {
        if (slot < 0 || slot >= this.items.size() || this.baseFlavor == null) {
            return false;
        }

        ItemStack stack = getItem(slot);
        if (stack.isEmpty() || !canAcceptItem(stack, charge)) {
            return false;
        }
        if (!charge && requiresChargeToStart(stack)) {
            return false;
        }
        if (charge) {
            if (this.baseUses <= 0) {
                return false;
            }
            this.baseUses--;
            if (this.baseUses == 0) {
                this.hasWater = false;
            }
        }

        this.progress[slot] = 0;
        sync();
        return true;
    }

    public void onItemRemoved(int slot) {
        if (slot < 0 || slot >= this.items.size()) {
            return;
        }
        this.progress[slot] = -1;
        sync();
    }

    public void clearExhaustedBaseIfEmpty() {
        if (this.baseFlavor != null && !this.hasWater && this.baseUses <= 0 && isEmpty()) {
            this.baseFlavor = null;
            sync();
        }
    }

    // ---------------- 查询 ----------------

    public int getProgress(int slot) {
        if (slot < 0 || slot >= this.items.size()) {
            return -1;
        }
        return this.progress[slot];
    }

    public int getSlotCount() {
        return this.items.size();
    }

    public boolean hasWater() {
        return this.hasWater;
    }

    public int getBaseUses() {
        return this.baseUses;
    }

    @Nullable
    public FlavorProfile getBaseFlavor() {
        return this.baseFlavor;
    }

    protected void sync() {
        setChanged();
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    // ---------------- Container ----------------

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        if (slot < 0 || slot >= this.items.size() || amount <= 0) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = this.items.get(slot);
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
        if (slot < 0 || slot >= this.items.size()) {
            return;
        }
        if (stack.getCount() > 1) {
            stack.setCount(1);
        }
        this.items.set(slot, stack);
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
        this.items.clear();
        Arrays.fill(this.progress, -1);
        this.hasWater = false;
        this.baseFlavor = null;
        this.baseUses = 0;
        this.cookingTicks = getCookingTicks();
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
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, this.items);
        tag.putIntArray(PROGRESS_KEY, this.progress);
        tag.putBoolean(HAS_WATER_KEY, this.hasWater);
        tag.putInt(BASE_USES_KEY, this.baseUses);
        tag.putInt(COOKING_TICKS_KEY, this.cookingTicks);
        if (this.baseFlavor != null) {
            tag.put(BASE_FLAVOR_KEY, this.baseFlavor.save());
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        ContainerHelper.loadAllItems(tag, this.items);
        Arrays.fill(this.progress, -1);

        this.cookingTicks = tag.contains(COOKING_TICKS_KEY)
            ? Math.max(1, tag.getInt(COOKING_TICKS_KEY))
            : getCookingTicks();

        int[] savedProgress = tag.getIntArray(PROGRESS_KEY);
        for (int i = 0; i < Math.min(savedProgress.length, this.items.size()); i++) {
            if (!this.items.get(i).isEmpty()) {
                this.progress[i] = Mth.clamp(savedProgress[i], -1, getEffectiveCookingTicks());
            }
        }

        this.hasWater = tag.getBoolean(HAS_WATER_KEY);
        this.baseUses = Mth.clamp(tag.getInt(BASE_USES_KEY), 0, Math.max(getBaseCapacity(), this.baseUses));
        this.baseFlavor = tag.contains(BASE_FLAVOR_KEY, Tag.TAG_COMPOUND)
            ? FlavorProfile.load(tag.getCompound(BASE_FLAVOR_KEY))
            : null;
        if (this.baseFlavor == null) {
            this.baseUses = 0;
        }
        if (this.level != null && !this.level.isClientSide
            && !this.hasWater && this.baseUses <= 0 && isEmpty()) {
            this.baseFlavor = null;
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }
}

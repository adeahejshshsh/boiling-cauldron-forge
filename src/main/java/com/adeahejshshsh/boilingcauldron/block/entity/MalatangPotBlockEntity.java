/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：麻辣烫锅 BlockEntity，实现热源检测、20 秒煮制与碗装食材提取。
 * 移植：Forge 1.20.1 → NeoForge 1.21.1
 * 更新：2026-11-28 上游 1.0.1 新增
 */
package com.adeahejshshsh.boilingcauldron.block.entity;

import com.adeahejshshsh.boilingcauldron.item.BoboChickenItem;
import com.adeahejshshsh.boilingcauldron.item.MalatangBowlItem;
import com.adeahejshshsh.boilingcauldron.tag.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;

public class MalatangPotBlockEntity extends AbstractSeasoningPotBlockEntity {

    public static final int SLOT_COUNT = 16;
    public static final int SLOTS_PER_ROW = 4;
    public static final int COOKING_TICKS = 20 * 20;
    public static final int BASE_USES = 64;

    public MalatangPotBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MALATANG_POT.get(), pos, state, SLOT_COUNT);
    }

    @Override
    public int getCookingTicks() {
        return COOKING_TICKS;
    }

    @Override
    public int getBaseCapacity() {
        return BASE_USES;
    }

    @Override
    public boolean canAcceptItem(ItemStack stack, boolean charge) {
        if (getBaseFlavor() == null || stack.isEmpty() || !stack.has(DataComponents.FOOD)) {
            return false;
        }
        if (stack.getItem() instanceof BoboChickenItem || stack.getItem() instanceof MalatangBowlItem) {
            return false;
        }
        return !charge || getBaseUses() > 0;
    }

    /** 麻辣烫锅固定 20 秒、64 次，不接受数据包覆盖。 */
    @Override
    protected boolean usesRecipeTiming() {
        return false;
    }

    /** 从手上直接塞入一份食材并开始煮；锅满则失败。 */
    public boolean insertIngredient(ItemStack stack, boolean charge) {
        if (!canAcceptItem(stack, charge)) {
            return false;
        }

        int slot = -1;
        for (int i = 0; i < this.items.size(); i++) {
            if (this.items.get(i).isEmpty()) {
                slot = i;
                break;
            }
        }
        if (slot < 0) {
            return false;
        }

        ItemStack single = stack.copy();
        single.setCount(1);
        this.items.set(slot, single);
        if (!startCooking(slot, charge)) {
            this.items.set(slot, ItemStack.EMPTY);
            sync();
            return false;
        }
        return true;
    }

    public boolean hasCompletedItem() {
        return findFirstCompletedSlot() >= 0;
    }

    public int findFirstCompletedSlot() {
        int ticks = getEffectiveCookingTicks();
        for (int i = 0; i < this.items.size(); i++) {
            if (!this.items.get(i).isEmpty() && this.progress[i] >= ticks) {
                return i;
            }
        }
        return -1;
    }

    public ItemStack removeCompletedItem(int slot) {
        if (slot < 0 || slot >= this.items.size()) {
            return ItemStack.EMPTY;
        }
        ItemStack result = this.items.get(slot).copy();
        if (result.isEmpty()) {
            return ItemStack.EMPTY;
        }
        this.items.set(slot, ItemStack.EMPTY);
        this.progress[slot] = -1;
        sync();
        return result;
    }

    @Override
    protected boolean requiresHeatSource() {
        return true;
    }

    @Override
    protected boolean hasHeatSource(Level level, BlockPos pos, BlockState state) {
        BlockState below = level.getBlockState(pos.below());
        if (below.is(ModTags.MALATANG_HEAT_SOURCES)) {
            // 营火需要是点燃状态；火/岩浆块等没有 LIT 属性，直接算热源
            return !below.hasProperty(CampfireBlock.LIT) || below.getValue(CampfireBlock.LIT);
        }
        if (below.getBlock() instanceof AbstractFurnaceBlock) {
            return below.hasProperty(AbstractFurnaceBlock.LIT) && below.getValue(AbstractFurnaceBlock.LIT);
        }
        return false;
    }

    public boolean isHeated() {
        return this.level != null && hasHeatSource(this.level, this.worldPosition, getBlockState());
    }

    @Override
    protected void finishSlot(int slot) {
        // 煮好后留在锅内，等待用碗盛取
    }
}

/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：钵钵鸡锅 BlockEntity，复用通用调味锅逻辑，只实现钵钵鸡专属规则。
 * 移植：Fabric → Forge 1.20.1
 * 更新：2026-11-28 上游 1.0.1：改为继承 AbstractSeasoningPotBlockEntity
 */
package com.boilingcauldron.block.entity;

import com.boilingcauldron.item.BoboChickenItem;
import com.boilingcauldron.item.MalatangBowlItem;
import com.boilingcauldron.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class BoboChickenPotBlockEntity extends AbstractSeasoningPotBlockEntity {

    public static final int SLOT_COUNT = 16;
    public static final int SLOTS_PER_ROW = 4;
    public static final int SOAK_TICKS = 20 * 60;
    public static final int BASE_USES = 64;

    public BoboChickenPotBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BOBO_CHICKEN_POT.get(), pos, state, SLOT_COUNT);
    }

    @Override
    public int getCookingTicks() {
        return SOAK_TICKS;
    }

    @Override
    public int getBaseCapacity() {
        return BASE_USES;
    }

    @Override
    public boolean canAcceptItem(ItemStack stack, boolean charge) {
        if (getBaseFlavor() == null || stack.isEmpty() || !stack.isEdible()) {
            return false;
        }
        if (stack.getItem() instanceof MalatangBowlItem) {
            return false;
        }
        if (stack.getItem() instanceof BoboChickenItem) {
            if (!BoboChickenItem.canAddFlavor(stack, getBaseFlavor()) || getBaseUses() <= 0) {
                return false;
            }
        }
        return !charge || getBaseUses() > 0;
    }

    /** 钵钵鸡成品再泡一次要额外扣底料；普通食材由放入时统一扣。 */
    @Override
    protected boolean requiresChargeToStart(ItemStack stack) {
        return stack.getItem() instanceof BoboChickenItem;
    }

    @Override
    protected void finishSlot(int slot) {
        ItemStack original = this.items.get(slot);
        if (original.isEmpty() || getBaseFlavor() == null) {
            this.progress[slot] = -1;
            return;
        }

        ItemStack result = new ItemStack(ModItems.BOBO_CHICKEN.get());
        if (original.getItem() instanceof BoboChickenItem) {
            // 已是钵钵鸡：保留原有口味，再叠加新口味
            if (original.getTag() != null) {
                result.setTag(original.getTag().copy());
            }
        } else {
            BoboChickenItem.setOriginalItem(result, original.getItem());
        }

        if (!BoboChickenItem.addFlavor(result, getBaseFlavor())) {
            this.progress[slot] = -1;
            return;
        }

        this.items.set(slot, result);
        this.progress[slot] = getEffectiveCookingTicks();
        setChanged();
    }

    /** 已完成的钵钵鸡可以继续泡，其它产物停在完成态等玩家取出。 */
    @Override
    protected boolean shouldFinishCompletedSlot(int slot) {
        return !(this.items.get(slot).getItem() instanceof BoboChickenItem);
    }
}

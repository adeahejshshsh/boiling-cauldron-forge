/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：钵钵鸡锅 GUI 屏幕，绘制背景、状态文字和进度条。
 * 移植：Fabric → Forge 1.20.1（HandledScreen → AbstractContainerScreen）
 */
package com.boilingcauldron.screen;

import com.boilingcauldron.block.entity.BoboChickenPotBlockEntity;
import com.boilingcauldron.item.FlavorProfile;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BoboChickenPotScreen extends AbstractContainerScreen<BoboChickenPotMenu> {
    private static final ResourceLocation TEXTURE =
        new ResourceLocation("boiling-cauldron", "textures/gui/container/bobo_chicken_pot.png");
    private static final int PROGRESS_COLOR = 0xFF55FF55;
    private static final int TEXT_COLOR = 0x404040;
    private static final int WATER_COLOR = 0x3F76E4;

    public BoboChickenPotScreen(BoboChickenPotMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 202;
        this.titleLabelY = 6;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = 108;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);

        int textX = 8;
        int textY = 22;
        FlavorProfile baseFlavor = this.menu.getBaseFlavor();
        int baseUses = this.menu.getBaseUses();
        if (baseFlavor != null && baseUses > 0) {
            Component baseName = baseFlavor.name();
            Component remaining = Component.translatable(
                "gui.boiling-cauldron.bobo_chicken_pot.remaining", baseUses);
            guiGraphics.drawString(this.font, baseName, textX, textY, baseFlavor.color(), false);
            guiGraphics.drawString(this.font, remaining,
                textX + this.font.width(baseName), textY, TEXT_COLOR, false);
        } else if (this.menu.hasWater()) {
            guiGraphics.drawString(this.font,
                Component.translatable("gui.boiling-cauldron.bobo_chicken_pot.water"),
                textX, textY, WATER_COLOR, false);
        } else {
            guiGraphics.drawString(this.font,
                Component.translatable("gui.boiling-cauldron.bobo_chicken_pot.empty"),
                textX, textY, TEXT_COLOR, false);
        }

        for (int slot = 0; slot < BoboChickenPotBlockEntity.SLOT_COUNT; slot++) {
            int progress = this.menu.getProgress(slot);
            if (progress < 0 || !this.menu.getSlot(slot).hasItem()) {
                continue;
            }

            int col = slot % 4;
            int row = slot / 4;
            int slotX = BoboChickenPotMenu.POT_SLOT_START_X + col * 18;
            int slotY = BoboChickenPotMenu.POT_SLOT_START_Y + row * 18;
            int soakTicks = Math.max(1, this.menu.getSoakTicks());
            int width = Math.min(16, progress * 16 / soakTicks);
            if (width > 0) {
                guiGraphics.fill(slotX, slotY + 16, slotX + width, slotY + 18, PROGRESS_COLOR);
            }
        }
    }

}

/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：麻辣烫锅 GUI，绘制背景、底料 / 水状态、热源状态与进度条。
 * 移植：Forge 1.20.1 → NeoForge 1.21.1
 * 更新：2026-11-28 上游 1.0.1 新增
 */
package com.adeahejshshsh.boilingcauldron.screen;

import com.adeahejshshsh.boilingcauldron.block.entity.MalatangPotBlockEntity;
import com.adeahejshshsh.boilingcauldron.item.FlavorProfile;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MalatangPotScreen extends AbstractContainerScreen<MalatangPotMenu> {

    private static final ResourceLocation TEXTURE =
        ResourceLocation.fromNamespaceAndPath("boiling-cauldron", "textures/gui/container/malatang_pot.png");
    private static final int PROGRESS_COLOR = 0xFFFFAA33;
    private static final int TEXT_COLOR = 0x404040;
    private static final int WATER_COLOR = 0x3F76E4;
    private static final int HEATED_COLOR = 0x3F8A3F;
    private static final int UNHEATED_COLOR = 0xE03A2F;
    private static final int STATUS_Y = 14;
    private static final int HEAT_Y = 24;

    public MalatangPotScreen(MalatangPotMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 202;
        this.titleLabelY = 4;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = 108;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        // AbstractContainerScreen 不会自动渲染 tooltip，必须由子类显式调用
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);

        int textX = 8;
        FlavorProfile baseFlavor = this.menu.getBaseFlavor();
        int baseUses = this.menu.getBaseUses();
        if (baseFlavor != null && baseUses > 0) {
            Component baseName = baseFlavor.name();
            Component remaining = Component.translatable(
                "gui.boiling-cauldron.malatang_pot.remaining", baseUses);
            guiGraphics.drawString(this.font, baseName, textX, STATUS_Y, baseFlavor.color(), false);
            guiGraphics.drawString(this.font, remaining,
                textX + this.font.width(baseName), STATUS_Y, TEXT_COLOR, false);
        } else if (this.menu.hasWater()) {
            guiGraphics.drawString(this.font,
                Component.translatable("gui.boiling-cauldron.malatang_pot.water"),
                textX, STATUS_Y, WATER_COLOR, false);
        } else {
            guiGraphics.drawString(this.font,
                Component.translatable("gui.boiling-cauldron.malatang_pot.empty"),
                textX, STATUS_Y, TEXT_COLOR, false);
        }

        boolean heated = this.menu.isHeated();
        guiGraphics.drawString(this.font,
            Component.translatable(heated
                ? "gui.boiling-cauldron.malatang_pot.heated"
                : "gui.boiling-cauldron.malatang_pot.unheated"),
            textX, HEAT_Y, heated ? HEATED_COLOR : UNHEATED_COLOR, false);

        int ticks = Math.max(1, this.menu.getEffectiveCookingTicks());
        for (int slot = 0; slot < MalatangPotBlockEntity.SLOT_COUNT; slot++) {
            int progress = this.menu.getProgress(slot);
            if (progress < 0 || !this.menu.getSlot(slot).hasItem()) {
                continue;
            }

            int col = slot % AbstractSeasoningPotMenu.SLOTS_PER_ROW;
            int row = slot / AbstractSeasoningPotMenu.SLOTS_PER_ROW;
            int slotX = AbstractSeasoningPotMenu.POT_SLOT_START_X + col * 18;
            int slotY = AbstractSeasoningPotMenu.POT_SLOT_START_Y + row * 18;
            int width = Math.min(16, progress * 16 / ticks);
            if (width > 0) {
                guiGraphics.fill(slotX, slotY + 16, slotX + width, slotY + 18, PROGRESS_COLOR);
            }
        }
    }
}

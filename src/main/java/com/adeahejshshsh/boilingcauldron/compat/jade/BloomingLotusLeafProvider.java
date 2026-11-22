package com.adeahejshshsh.boilingcauldron.compat.jade;

import com.adeahejshshsh.boilingcauldron.BoilingCauldron;
import com.adeahejshshsh.boilingcauldron.block.BloomingLotusLeafBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

/** 盛花荷叶：显示生长阶段，成熟时提示正在转化下方方块。 */
public enum BloomingLotusLeafProvider implements IBlockComponentProvider {

    INSTANCE;

    private static final ResourceLocation UID = BoilingCauldron.id("jade/blooming_lotus_leaf");

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        BlockState state = accessor.getBlockState();
        if (!state.hasProperty(BloomingLotusLeafBlock.GROWTH)) {
            return;
        }
        int growth = state.getValue(BloomingLotusLeafBlock.GROWTH);
        int max = 0;
        for (int value : BloomingLotusLeafBlock.GROWTH.getPossibleValues()) {
            max = Math.max(max, value);
        }
        boolean mature = growth >= max;
        tooltip.add(Component.translatable("jade.boiling-cauldron.blooming_lotus_leaf.growth",
                growth, max)
            .withStyle(mature ? ChatFormatting.GREEN : ChatFormatting.GRAY));
        if (mature) {
            tooltip.add(Component.translatable("jade.boiling-cauldron.blooming_lotus_leaf.mature")
                .withStyle(ChatFormatting.DARK_GREEN));
        }
    }
}

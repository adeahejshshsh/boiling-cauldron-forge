package com.adeahejshshsh.boilingcauldron.compat.jade;

import com.adeahejshshsh.boilingcauldron.BoilingCauldron;
import com.adeahejshshsh.boilingcauldron.block.BambooRootBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

/** 竹根殖块：显示当前存活产物数量（来自方块状态，无需服务端同步）。 */
public enum BambooRootProvider implements IBlockComponentProvider {

    INSTANCE;

    private static final ResourceLocation UID = BoilingCauldron.id("jade/bamboo_root");

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        BlockState state = accessor.getBlockState();
        if (!state.hasProperty(BambooRootBlock.BAMBOO_COUNT)) {
            return;
        }
        int count = state.getValue(BambooRootBlock.BAMBOO_COUNT);
        boolean full = count >= BambooRootBlock.MAX_COUNT;
        tooltip.add(Component.translatable("jade.boiling-cauldron.bamboo_root.count",
                count, BambooRootBlock.MAX_COUNT)
            .withStyle(full ? ChatFormatting.RED : ChatFormatting.GRAY));
    }
}

package com.adeahejshshsh.boilingcauldron.compat.jade;

import com.adeahejshshsh.boilingcauldron.BoilingCauldron;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

/** 钵钵鸡锅：把服务端下发的 NBT 渲染成提示行。 */
public enum BoboChickenPotProvider implements IBlockComponentProvider {

    INSTANCE;

    private static final ResourceLocation UID = BoilingCauldron.id("jade/bobo_chicken_pot");

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();
        if (data == null || data.isEmpty()) {
            return;
        }

        boolean hasWater = data.getBoolean(BoboChickenPotDataProvider.KEY_WATER);
        tooltip.add(Component.translatable("jade.boiling-cauldron.bobo_chicken_pot.water",
                Component.translatable(hasWater
                    ? "jade.boiling-cauldron.bobo_chicken_pot.water.filled"
                    : "jade.boiling-cauldron.bobo_chicken_pot.water.empty"))
            .withStyle(hasWater ? ChatFormatting.AQUA : ChatFormatting.DARK_GRAY));

        if (data.contains(BoboChickenPotDataProvider.KEY_BASE_NAME)) {
            Component baseName = ComponentSerialization.CODEC
                .parse(NbtOps.INSTANCE, data.get(BoboChickenPotDataProvider.KEY_BASE_NAME))
                .result()
                .orElse(Component.empty());
            tooltip.add(Component.translatable("jade.boiling-cauldron.bobo_chicken_pot.base", baseName)
                .withStyle(ChatFormatting.GOLD));
        }

        int uses = data.getInt(BoboChickenPotDataProvider.KEY_BASE_USES);
        if (uses > 0) {
            tooltip.add(Component.translatable("jade.boiling-cauldron.bobo_chicken_pot.uses", uses)
                .withStyle(ChatFormatting.YELLOW));
        }

        int soaking = data.getInt(BoboChickenPotDataProvider.KEY_SOAKING);
        if (soaking > 0) {
            tooltip.add(Component.translatable("jade.boiling-cauldron.bobo_chicken_pot.soaking", soaking)
                .withStyle(ChatFormatting.GREEN));
        }
    }
}

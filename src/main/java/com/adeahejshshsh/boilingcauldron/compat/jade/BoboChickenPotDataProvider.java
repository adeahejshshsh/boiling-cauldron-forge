package com.adeahejshshsh.boilingcauldron.compat.jade;

import com.adeahejshshsh.boilingcauldron.BoilingCauldron;
import com.adeahejshshsh.boilingcauldron.block.entity.BoboChickenPotBlockEntity;
import com.adeahejshshsh.boilingcauldron.item.FlavorProfile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

/**
 * 钵钵鸡锅的服务端数据提供者。
 *
 * <p>水量、底料、剩余次数、浸泡格数都只存在于方块实体，客户端拿不到，
 * 因此必须由服务端写进 NBT 再随 Jade 的数据包下发。
 */
public enum BoboChickenPotDataProvider implements IServerDataProvider<BlockAccessor> {

    INSTANCE;

    public static final String KEY_WATER = "HasWater";
    public static final String KEY_BASE_NAME = "BaseName";
    public static final String KEY_BASE_USES = "BaseUses";
    public static final String KEY_SOAKING = "Soaking";

    private static final ResourceLocation UID = BoilingCauldron.id("jade/bobo_chicken_pot_data");

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public void appendServerData(CompoundTag tag, BlockAccessor accessor) {
        if (!(accessor.getBlockEntity() instanceof BoboChickenPotBlockEntity pot)) {
            return;
        }
        tag.putBoolean(KEY_WATER, pot.hasWater());
        tag.putInt(KEY_BASE_USES, pot.getBaseUses());

        FlavorProfile base = pot.getBaseFlavor();
        if (base != null) {
            // 底料名是数据包自定义的 Component，只能整体序列化下发
            ComponentSerialization.CODEC.encodeStart(NbtOps.INSTANCE, base.name())
                .result()
                .ifPresent(encoded -> tag.put(KEY_BASE_NAME, encoded));
        }

        int soaking = 0;
        for (int slot = 0; slot < BoboChickenPotBlockEntity.SLOT_COUNT; slot++) {
            if (pot.getProgress(slot) >= 0) {
                soaking++;
            }
        }
        tag.putInt(KEY_SOAKING, soaking);
    }
}

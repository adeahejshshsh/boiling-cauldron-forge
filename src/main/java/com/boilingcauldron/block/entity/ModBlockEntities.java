/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：注册模组 BlockEntity 类型。
 * 移植：Fabric → Forge 1.20.1
 */
package com.boilingcauldron.block.entity;

import com.boilingcauldron.BoilingCauldron;
import com.boilingcauldron.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
        DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, BoilingCauldron.NAMESPACE);

    public static final RegistryObject<BlockEntityType<BambooRootBlockEntity>> BAMBOO_ROOT =
        BLOCK_ENTITIES.register("bamboo_root", () -> BlockEntityType.Builder
            .of(BambooRootBlockEntity::new, ModBlocks.BAMBOO_ROOT.get())
            .build(null));

    public static final RegistryObject<BlockEntityType<BloomingLotusLeafBlockEntity>> BLOOMING_LOTUS_LEAF =
        BLOCK_ENTITIES.register("blooming_lotus_leaf", () -> BlockEntityType.Builder
            .of(BloomingLotusLeafBlockEntity::new, ModBlocks.BLOOMING_LOTUS_LEAF.get())
            .build(null));

    public static final RegistryObject<BlockEntityType<BoboChickenPotBlockEntity>> BOBO_CHICKEN_POT =
        BLOCK_ENTITIES.register("bobo_chicken_pot", () -> BlockEntityType.Builder
            .of(BoboChickenPotBlockEntity::new, ModBlocks.BOBO_CHICKEN_POT.get())
            .build(null));

    public static void register(IEventBus modBus) {
        BLOCK_ENTITIES.register(modBus);
    }
}

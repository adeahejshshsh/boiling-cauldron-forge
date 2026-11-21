package com.adeahejshshsh.boilingcauldron.block.entity;

import com.adeahejshshsh.boilingcauldron.BoilingCauldron;
import com.adeahejshshsh.boilingcauldron.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
        DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, BoilingCauldron.NAMESPACE);

    public static final Supplier<BlockEntityType<BambooRootBlockEntity>> BAMBOO_ROOT =
        BLOCK_ENTITIES.register("bamboo_root", () -> BlockEntityType.Builder
            .of(BambooRootBlockEntity::new, ModBlocks.BAMBOO_ROOT.get())
            .build(null));

    public static final Supplier<BlockEntityType<BloomingLotusLeafBlockEntity>> BLOOMING_LOTUS_LEAF =
        BLOCK_ENTITIES.register("blooming_lotus_leaf", () -> BlockEntityType.Builder
            .of(BloomingLotusLeafBlockEntity::new, ModBlocks.BLOOMING_LOTUS_LEAF.get())
            .build(null));

    public static final Supplier<BlockEntityType<BoboChickenPotBlockEntity>> BOBO_CHICKEN_POT =
        BLOCK_ENTITIES.register("bobo_chicken_pot", () -> BlockEntityType.Builder
            .of(BoboChickenPotBlockEntity::new, ModBlocks.BOBO_CHICKEN_POT.get())
            .build(null));

    public static void register(IEventBus modBus) {
        BLOCK_ENTITIES.register(modBus);
    }
}

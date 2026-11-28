package com.adeahejshshsh.boilingcauldron.item;

import com.adeahejshshsh.boilingcauldron.BoilingCauldron;
import com.adeahejshshsh.boilingcauldron.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/** 创造模式物品栏。 */
public class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> TABS =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BoilingCauldron.NAMESPACE);

    public static final Supplier<CreativeModeTab> BOILING_CAULDRON = TABS.register("boiling_cauldron_group",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemgroup.boiling_cauldron"))
            .icon(() -> new ItemStack(ModItems.GONG_CAI.get()))
            .displayItems((parameters, output) -> {
                output.accept(ModItems.GINGER.get());
                output.accept(ModItems.GINGER_SEEDS.get());
                output.accept(ModItems.GARLIC.get());
                output.accept(ModItems.GARLIC_SEEDS.get());
                output.accept(ModItems.GONG_CAI.get());
                output.accept(ModItems.GONG_CAI_SEEDS.get());
                output.accept(ModItems.WINTER_BAMBOO_SHOOTS.get());
                output.accept(ModItems.BAMBOO_FUNGUS.get());
                output.accept(ModItems.MUSHROOM_BASE.get());
                output.accept(ModItems.MALA_BASE.get());
                output.accept(ModItems.RATTAN_PEPPER_BASE.get());
                output.accept(ModBlocks.BAMBOO_FUNGUS_COLONY.get());
                output.accept(ModBlocks.BAMBOO_ROOT.get());
                output.accept(ModItems.LOTUS_ROOT.get());
                output.accept(ModBlocks.BLOOMING_LOTUS_LEAF.get());
                output.accept(ModBlocks.LOTUS_MUD.get());
                output.accept(ModItems.SICHUAN_PEPPER.get());
                output.accept(ModItems.SICHUAN_PEPPER_SEEDS.get());
                output.accept(ModBlocks.BOBO_CHICKEN_POT.get());
                output.accept(ModItems.BOBO_CHICKEN.get());
                output.accept(ModBlocks.MALATANG_POT.get());
                output.accept(ModItems.MALATANG_BOWL.get());
                output.accept(ModItems.PHANTOM_FIVE_SPICE_POWDER.get());
                output.accept(ModBlocks.LUSH_GRASS.get());
            })
            .build());

    public static void register(IEventBus modBus) {
        TABS.register(modBus);
    }
}

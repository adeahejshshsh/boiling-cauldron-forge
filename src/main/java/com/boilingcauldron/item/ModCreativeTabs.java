/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：注册创造模式物品栏分组。
 * 移植：Fabric → Forge 1.20.1
 */
package com.boilingcauldron.item;

import com.boilingcauldron.BoilingCauldron;
import com.boilingcauldron.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> TABS =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BoilingCauldron.NAMESPACE);

    public static final RegistryObject<CreativeModeTab> BOILING_CAULDRON = TABS.register("boiling_cauldron_group",
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

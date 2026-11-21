package com.adeahejshshsh.boilingcauldron.screen;

import com.adeahejshshsh.boilingcauldron.BoilingCauldron;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModMenus {

    public static final DeferredRegister<MenuType<?>> MENUS =
        DeferredRegister.create(Registries.MENU, BoilingCauldron.NAMESPACE);

    public static final Supplier<MenuType<BoboChickenPotMenu>> BOBO_CHICKEN_POT =
        MENUS.register("bobo_chicken_pot", () -> IMenuTypeExtension.create(
            (windowId, inventory, data) -> new BoboChickenPotMenu(windowId, inventory, data)));

    public static void register(IEventBus modBus) {
        MENUS.register(modBus);
    }
}

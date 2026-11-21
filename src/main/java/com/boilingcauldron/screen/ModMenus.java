/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：注册菜单（MenuType）。
 * 移植：Fabric → Forge 1.20.1（ExtendedScreenHandlerType → IForgeMenuType）
 */
package com.boilingcauldron.screen;

import com.boilingcauldron.BoilingCauldron;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenus {

    public static final DeferredRegister<MenuType<?>> MENUS =
        DeferredRegister.create(ForgeRegistries.MENU_TYPES, BoilingCauldron.NAMESPACE);

    public static final RegistryObject<MenuType<BoboChickenPotMenu>> BOBO_CHICKEN_POT =
        MENUS.register("bobo_chicken_pot", () -> IForgeMenuType.create(
            (windowId, inventory, data) -> new BoboChickenPotMenu(windowId, inventory, data.readBlockPos())));

    public static void register(IEventBus modBus) {
        MENUS.register(modBus);
    }
}

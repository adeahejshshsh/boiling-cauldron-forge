package com.adeahejshshsh.boilingcauldron;

import com.adeahejshshsh.boilingcauldron.screen.BoboChickenPotScreen;
import com.adeahejshshsh.boilingcauldron.screen.ModMenus;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

/** 客户端入口：注册 GUI 屏幕。 */
public final class BoilingCauldronClient {

    private BoilingCauldronClient() {
    }

    @EventBusSubscriber(modid = BoilingCauldron.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static final class ClientEvents {

        private ClientEvents() {
        }

        @SubscribeEvent
        public static void onRegisterScreens(RegisterMenuScreensEvent event) {
            event.register(ModMenus.BOBO_CHICKEN_POT.get(), BoboChickenPotScreen::new);
        }

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // 预留：客户端初始化
        }
    }
}

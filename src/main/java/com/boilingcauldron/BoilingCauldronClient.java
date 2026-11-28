/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：客户端入口，注册钵钵鸡锅与麻辣烫锅 GUI 屏幕。
 * 移植：Fabric → Forge 1.20.1（ClientModInitializer → FMLClientSetupEvent）
 * 说明：方块渲染层改用模型 JSON 的 render_type 字段（Forge 官方推荐做法）。
 */
package com.boilingcauldron;

import com.boilingcauldron.screen.BoboChickenPotScreen;
import com.boilingcauldron.screen.MalatangPotScreen;
import com.boilingcauldron.screen.ModMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class BoilingCauldronClient {

    @Mod.EventBusSubscriber(modid = BoilingCauldron.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                MenuScreens.register(ModMenus.BOBO_CHICKEN_POT.get(), BoboChickenPotScreen::new);
                MenuScreens.register(ModMenus.MALATANG_POT.get(), MalatangPotScreen::new);
            });
        }
    }
}

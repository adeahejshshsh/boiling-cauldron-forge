/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：注册流浪商人的种子交易。
 * 移植：Fabric → Forge 1.20.1（TradeOfferHelper → WandererTradesEvent）
 */
package com.boilingcauldron.trade;

import com.boilingcauldron.BoilingCauldron;
import com.boilingcauldron.item.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.BasicItemListing;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Supplier;

/**
 * 为流浪商人添加种子交易，作为村庄箱子之外的保底获取途径。
 */
@Mod.EventBusSubscriber(modid = BoilingCauldron.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ModTradeOffers {

    private static final int MAX_USES = 2;
    private static final int MERCHANT_EXPERIENCE = 1;
    private static final float PRICE_MULTIPLIER = 0.05F;

    private ModTradeOffers() {
    }

    @SubscribeEvent
    public static void onWandererTrades(WandererTradesEvent event) {
        addSeedOffer(event, ModItems.GINGER_SEEDS);
        addSeedOffer(event, ModItems.GARLIC_SEEDS);
        addSeedOffer(event, ModItems.GONG_CAI_SEEDS);
        addSeedOffer(event, ModItems.SICHUAN_PEPPER_SEEDS);
    }

    private static void addSeedOffer(WandererTradesEvent event, Supplier<Item> seed) {
        event.getGenericTrades().add(new BasicItemListing(
            new ItemStack(Items.EMERALD, 1),
            new ItemStack(seed.get(), 1),
            MAX_USES,
            MERCHANT_EXPERIENCE,
            PRICE_MULTIPLIER
        ));
    }
}

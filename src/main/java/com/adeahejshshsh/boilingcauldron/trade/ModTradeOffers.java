package com.adeahejshshsh.boilingcauldron.trade;

import com.adeahejshshsh.boilingcauldron.BoilingCauldron;
import com.adeahejshshsh.boilingcauldron.item.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.BasicItemListing;
import net.neoforged.neoforge.event.village.WandererTradesEvent;

import java.util.function.Supplier;

/** 流浪商人种子交易。 */
@EventBusSubscriber(modid = BoilingCauldron.MOD_ID)
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

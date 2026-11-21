package com.adeahejshshsh.boilingcauldron.item;

import com.adeahejshshsh.boilingcauldron.BoilingCauldron;
import com.adeahejshshsh.boilingcauldron.block.ModBlocks;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** 物品注册（NeoForge 1.21.1）。 */
public class ModItems {

    public static final DeferredRegister.Items ITEMS =
        DeferredRegister.createItems(BoilingCauldron.NAMESPACE);

    // 食材
    public static final DeferredItem<Item> GINGER = ITEMS.registerSimpleItem("ginger",
        new Item.Properties().food(ModFoods.GINGER));
    public static final DeferredItem<Item> GARLIC = ITEMS.registerSimpleItem("garlic",
        new Item.Properties().food(ModFoods.GARLIC));
    public static final DeferredItem<Item> GONG_CAI = ITEMS.registerSimpleItem("gong_cai",
        new Item.Properties().food(ModFoods.GONG_CAI));
    public static final DeferredItem<Item> WINTER_BAMBOO_SHOOTS = ITEMS.registerSimpleItem("winter_bamboo_shoots",
        new Item.Properties().food(ModFoods.WINTER_BAMBOO_SHOOTS));
    public static final DeferredItem<Item> LOTUS_ROOT = ITEMS.registerSimpleItem("lotus_root",
        new Item.Properties().food(ModFoods.LOTUS_ROOT));
    public static final DeferredItem<Item> SICHUAN_PEPPER = ITEMS.registerSimpleItem("sichuan_pepper",
        new Item.Properties().food(ModFoods.SICHUAN_PEPPER));
    public static final DeferredItem<Item> BAMBOO_FUNGUS = ITEMS.registerSimpleItem("bamboo_fungus",
        new Item.Properties().food(ModFoods.BAMBOO_FUNGUS));

    // 底料
    public static final DeferredItem<Item> MUSHROOM_BASE = ITEMS.registerSimpleItem("mushroom_base");
    public static final DeferredItem<Item> MALA_BASE = ITEMS.registerSimpleItem("mala_base");
    public static final DeferredItem<Item> RATTAN_PEPPER_BASE = ITEMS.registerSimpleItem("rattan_pepper_base");

    // 成品
    public static final DeferredItem<Item> BOBO_CHICKEN = ITEMS.registerItem("bobo_chicken",
        BoboChickenItem::new, new Item.Properties().food(ModFoods.BOBO_CHICKEN));

    // 种子
    public static final DeferredItem<Item> GINGER_SEEDS = ITEMS.registerItem("ginger_seeds",
        props -> new SeedItem(ModBlocks.GINGER_CROP.get(), props, "item.boiling-cauldron.ginger_seeds"),
        new Item.Properties());
    public static final DeferredItem<Item> GARLIC_SEEDS = ITEMS.registerItem("garlic_seeds",
        props -> new SeedItem(ModBlocks.GARLIC_CROP.get(), props, "item.boiling-cauldron.garlic_seeds"),
        new Item.Properties());
    public static final DeferredItem<Item> GONG_CAI_SEEDS = ITEMS.registerItem("gong_cai_seeds",
        props -> new SeedItem(ModBlocks.GONG_CAI_CROP.get(), props, "item.boiling-cauldron.gong_cai_seeds"),
        new Item.Properties());
    public static final DeferredItem<Item> SICHUAN_PEPPER_SEEDS = ITEMS.registerItem("sichuan_pepper_seeds",
        props -> new SeedItem(ModBlocks.SICHUAN_PEPPER_CROP.get(), props, "item.boiling-cauldron.sichuan_pepper_seeds"),
        new Item.Properties());

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
    }
}

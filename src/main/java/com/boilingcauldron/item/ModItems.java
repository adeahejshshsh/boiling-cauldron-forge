/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：注册模组物品、种子和成品。
 * 移植：Fabric → Forge 1.20.1
 */
package com.boilingcauldron.item;

import com.boilingcauldron.BoilingCauldron;
import com.boilingcauldron.block.ModBlocks;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
        DeferredRegister.create(ForgeRegistries.ITEMS, BoilingCauldron.NAMESPACE);

    // 食物物品
    public static final RegistryObject<Item> GINGER = ITEMS.register("ginger",
        () -> new Item(new Item.Properties().food(ModFoods.GINGER)));
    public static final RegistryObject<Item> GARLIC = ITEMS.register("garlic",
        () -> new Item(new Item.Properties().food(ModFoods.GARLIC)));
    public static final RegistryObject<Item> GONG_CAI = ITEMS.register("gong_cai",
        () -> new Item(new Item.Properties().food(ModFoods.GONG_CAI)));
    public static final RegistryObject<Item> WINTER_BAMBOO_SHOOTS = ITEMS.register("winter_bamboo_shoots",
        () -> new Item(new Item.Properties().food(ModFoods.WINTER_BAMBOO_SHOOTS)));
    public static final RegistryObject<Item> LOTUS_ROOT = ITEMS.register("lotus_root",
        () -> new Item(new Item.Properties().food(ModFoods.LOTUS_ROOT)));
    public static final RegistryObject<Item> SICHUAN_PEPPER = ITEMS.register("sichuan_pepper",
        () -> new Item(new Item.Properties().food(ModFoods.SICHUAN_PEPPER)));
    public static final RegistryObject<Item> BAMBOO_FUNGUS = ITEMS.register("bamboo_fungus",
        () -> new Item(new Item.Properties().food(ModFoods.BAMBOO_FUNGUS)));

    // 底料物品（不可食用）
    public static final RegistryObject<Item> MUSHROOM_BASE = ITEMS.register("mushroom_base",
        () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> MALA_BASE = ITEMS.register("mala_base",
        () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RATTAN_PEPPER_BASE = ITEMS.register("rattan_pepper_base",
        () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> BOBO_CHICKEN = ITEMS.register("bobo_chicken",
        () -> new BoboChickenItem(new Item.Properties().food(ModFoods.BOBO_CHICKEN)));

    // 种子物品（SeedItem 继承 BlockItem，指定自定义翻译键避免冲突）
    public static final RegistryObject<Item> GINGER_SEEDS = ITEMS.register("ginger_seeds",
        () -> new SeedItem(ModBlocks.GINGER_CROP.get(), new Item.Properties(),
            "item.boiling-cauldron.ginger_seeds"));
    public static final RegistryObject<Item> GARLIC_SEEDS = ITEMS.register("garlic_seeds",
        () -> new SeedItem(ModBlocks.GARLIC_CROP.get(), new Item.Properties(),
            "item.boiling-cauldron.garlic_seeds"));
    public static final RegistryObject<Item> GONG_CAI_SEEDS = ITEMS.register("gong_cai_seeds",
        () -> new SeedItem(ModBlocks.GONG_CAI_CROP.get(), new Item.Properties(),
            "item.boiling-cauldron.gong_cai_seeds"));
    public static final RegistryObject<Item> SICHUAN_PEPPER_SEEDS = ITEMS.register("sichuan_pepper_seeds",
        () -> new SeedItem(ModBlocks.SICHUAN_PEPPER_CROP.get(), new Item.Properties(),
            "item.boiling-cauldron.sichuan_pepper_seeds"));

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
    }
}

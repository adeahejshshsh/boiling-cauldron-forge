/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：注册模组方块及其方块物品。
 * 移植：Fabric → Forge 1.20.1（DeferredRegister）
 */
package com.boilingcauldron.block;

import com.boilingcauldron.BoilingCauldron;
import com.boilingcauldron.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PlaceOnWaterBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS =
        DeferredRegister.create(ForgeRegistries.BLOCKS, BoilingCauldron.NAMESPACE);

    /** 方块对应的 BlockItem 也由本类注册 */
    public static final DeferredRegister<Item> BLOCK_ITEMS =
        DeferredRegister.create(ForgeRegistries.ITEMS, BoilingCauldron.NAMESPACE);

    /** 普通方块：同时注册 BlockItem */
    private static RegistryObject<Block> register(String id, Supplier<Block> blockSupplier) {
        return register(id, blockSupplier, BlockItem::new);
    }

    /** 自定义 BlockItem 工厂（如盛花荷叶需要水面放置逻辑） */
    private static RegistryObject<Block> register(String id, Supplier<Block> blockSupplier,
                                                  BiFunction<Block, Item.Properties, Item> itemFactory) {
        RegistryObject<Block> block = BLOCKS.register(id, blockSupplier);
        BLOCK_ITEMS.register(id, () -> itemFactory.apply(block.get(), new Item.Properties()));
        return block;
    }

    /** 作物方块：不注册 BlockItem，玩家只用种子种植 */
    private static RegistryObject<Block> registerCrop(String id, Supplier<Block> blockSupplier) {
        return BLOCKS.register(id, blockSupplier);
    }

    /** 作物方块通用属性 */
    private static BlockBehaviour.Properties cropProperties() {
        return BlockBehaviour.Properties.of()
            .noCollission()
            .randomTicks()
            .instabreak()
            .pushReaction(PushReaction.DESTROY)
            .noOcclusion();
    }

    public static final RegistryObject<Block> GINGER_CROP = registerCrop(
        "ginger_crop", () -> new ModCropBlocks(cropProperties()));

    public static final RegistryObject<Block> GARLIC_CROP = registerCrop(
        "garlic_crop", () -> new ModCropBlocks(cropProperties()));

    public static final RegistryObject<Block> GONG_CAI_CROP = registerCrop(
        "gong_cai_crop", () -> new ModCropBlocks(cropProperties()));

    public static final RegistryObject<Block> SICHUAN_PEPPER_CROP = registerCrop(
        "sichuan_pepper_crop", () -> new SichuanPepperCropBlock(cropProperties()));

    public static final RegistryObject<Block> BAMBOO_SHOOT_CROP = registerCrop(
        "bamboo_shoot_crop", () -> new BambooShootCropBlock(cropProperties()));

    public static final RegistryObject<Block> BAMBOO_ROOT = register(
        "bamboo_root", () -> new BambooRootBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.DIRT)
            .strength(0.5F)
            .sound(SoundType.GRAVEL)));

    public static final RegistryObject<Block> BAMBOO_FUNGUS_COLONY = register(
        "bamboo_fungus_colony", () -> new BambooFungusColonyBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.TERRACOTTA_WHITE)
            .instabreak()
            .noCollission()
            .noOcclusion()
            .sound(SoundType.FUNGUS)
            .pushReaction(PushReaction.DESTROY)));

    /** 盛花荷叶：仿原版荷叶，只能贴水放置 */
    public static final RegistryObject<Block> BLOOMING_LOTUS_LEAF = register(
        "blooming_lotus_leaf",
        () -> new BloomingLotusLeafBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.PLANT)
            .strength(0.0F)
            .sound(SoundType.LILY_PAD)
            .noOcclusion()),
        PlaceOnWaterBlockItem::new);

    /** 藏藕泥块 */
    public static final RegistryObject<Block> LOTUS_MUD = register(
        "lotus_mud", () -> new LotusMudBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.DIRT)
            .strength(0.5F)
            .sound(SoundType.GRAVEL)));

    /** 繁茂草丛 */
    public static final RegistryObject<Block> LUSH_GRASS = register(
        "lush_grass", () -> new LushGrassBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.PLANT)
            .instabreak()
            .noCollission()
            .noOcclusion()
            .replaceable()
            .offsetType(BlockBehaviour.OffsetType.XZ)
            .sound(SoundType.GRASS)
            .pushReaction(PushReaction.DESTROY)));

    /** 钵钵鸡锅：16 格冷泡容器 */
    public static final RegistryObject<Block> BOBO_CHICKEN_POT = register(
        "bobo_chicken_pot", () -> new BoboChickenPotBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.NETHER)
            .strength(1.5F)
            .sound(SoundType.DECORATED_POT)
            .requiresCorrectToolForDrops()
            .noOcclusion()));

    public static void register(IEventBus modBus) {
        BLOCKS.register(modBus);
        BLOCK_ITEMS.register(modBus);
    }

    /** 注册完成后回填作物方块的种子引用（在 FMLCommonSetupEvent 中调用） */
    public static void setCropSeeds() {
        ((ModCropBlocks) GINGER_CROP.get()).setSeedsItem(ModItems.GINGER_SEEDS.get());
        ((ModCropBlocks) GARLIC_CROP.get()).setSeedsItem(ModItems.GARLIC_SEEDS.get());
        ((ModCropBlocks) GONG_CAI_CROP.get()).setSeedsItem(ModItems.GONG_CAI_SEEDS.get());
        ((SichuanPepperCropBlock) SICHUAN_PEPPER_CROP.get()).setSeedsItem(ModItems.SICHUAN_PEPPER_SEEDS.get());
    }
}

package com.adeahejshshsh.boilingcauldron.block;

import com.adeahejshshsh.boilingcauldron.BoilingCauldron;
import com.adeahejshshsh.boilingcauldron.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * 方块注册（NeoForge 1.21.1）。
 *
 * <p>本阶段包含作物与植物类方块；带方块实体的方块（竹根殖块、盛花荷叶、钵钵鸡锅）
 * 在后续阶段移植。
 */
public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS =
        DeferredRegister.createBlocks(BoilingCauldron.NAMESPACE);

    public static final DeferredRegister.Items BLOCK_ITEMS =
        DeferredRegister.createItems(BoilingCauldron.NAMESPACE);

    private static DeferredBlock<Block> register(String id, Supplier<Block> supplier) {
        DeferredBlock<Block> block = BLOCKS.register(id, supplier);
        BLOCK_ITEMS.register(id, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }

    private static DeferredBlock<Block> registerCrop(String id, Supplier<Block> supplier) {
        return BLOCKS.register(id, supplier);
    }

    private static BlockBehaviour.Properties cropProperties() {
        return BlockBehaviour.Properties.of()
            .noCollission()
            .randomTicks()
            .instabreak()
            .pushReaction(PushReaction.DESTROY)
            .noOcclusion();
    }

    public static final DeferredBlock<Block> GINGER_CROP =
        registerCrop("ginger_crop", () -> new ModCropBlocks(cropProperties()));

    public static final DeferredBlock<Block> GARLIC_CROP =
        registerCrop("garlic_crop", () -> new ModCropBlocks(cropProperties()));

    public static final DeferredBlock<Block> GONG_CAI_CROP =
        registerCrop("gong_cai_crop", () -> new ModCropBlocks(cropProperties()));

    public static final DeferredBlock<Block> SICHUAN_PEPPER_CROP =
        registerCrop("sichuan_pepper_crop", () -> new SichuanPepperCropBlock(cropProperties()));

    public static final DeferredBlock<Block> BAMBOO_SHOOT_CROP =
        registerCrop("bamboo_shoot_crop", () -> new BambooShootCropBlock(cropProperties()));

    public static final DeferredBlock<Block> BAMBOO_FUNGUS_COLONY =
        register("bamboo_fungus_colony", () -> new BambooFungusColonyBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.TERRACOTTA_WHITE)
            .instabreak()
            .noCollission()
            .noOcclusion()
            .sound(SoundType.FUNGUS)
            .pushReaction(PushReaction.DESTROY)));

    public static final DeferredBlock<Block> LOTUS_MUD =
        register("lotus_mud", () -> new LotusMudBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.DIRT)
            .strength(0.5F)
            .sound(SoundType.GRAVEL)));

    public static final DeferredBlock<Block> LUSH_GRASS =
        register("lush_grass", () -> new LushGrassBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.PLANT)
            .instabreak()
            .noCollission()
            .noOcclusion()
            .replaceable()
            .offsetType(BlockBehaviour.OffsetType.XZ)
            .sound(SoundType.GRASS)
            .pushReaction(PushReaction.DESTROY)));

    public static final DeferredBlock<Block> BAMBOO_ROOT =
        register("bamboo_root", () -> new BambooRootBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.DIRT)
            .strength(0.5F)
            .sound(SoundType.GRAVEL)));

    public static final DeferredBlock<Block> BLOOMING_LOTUS_LEAF =
        register("blooming_lotus_leaf", () -> new BloomingLotusLeafBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.PLANT)
            .strength(0.0F)
            .sound(SoundType.LILY_PAD)
            .noOcclusion()));

    public static final DeferredBlock<Block> BOBO_CHICKEN_POT =
        register("bobo_chicken_pot", () -> new BoboChickenPotBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.NETHER)
            .strength(1.5F)
            .sound(SoundType.DECORATED_POT)
            .requiresCorrectToolForDrops()
            .noOcclusion()));

    public static void register(IEventBus modBus) {
        BLOCKS.register(modBus);
        BLOCK_ITEMS.register(modBus);
    }

    /** 注册完成后回填作物方块的种子引用 */
    public static void setCropSeeds() {
        ((ModCropBlocks) GINGER_CROP.get()).setSeedsItem(ModItems.GINGER_SEEDS.get());
        ((ModCropBlocks) GARLIC_CROP.get()).setSeedsItem(ModItems.GARLIC_SEEDS.get());
        ((ModCropBlocks) GONG_CAI_CROP.get()).setSeedsItem(ModItems.GONG_CAI_SEEDS.get());
        ((SichuanPepperCropBlock) SICHUAN_PEPPER_CROP.get()).setSeedsItem(ModItems.SICHUAN_PEPPER_SEEDS.get());
    }
}

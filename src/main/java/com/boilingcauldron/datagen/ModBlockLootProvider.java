/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：方块掉落表生成。
 * 更新：2026-11-21
 */
package com.boilingcauldron.datagen;

import com.boilingcauldron.block.BambooShootCropBlock;
import com.boilingcauldron.block.ModBlocks;
import com.boilingcauldron.block.ModCropBlocks;
import com.boilingcauldron.block.SichuanPepperCropBlock;
import com.boilingcauldron.item.ModItems;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.tags.ItemTags;

import java.util.List;
import java.util.Set;

public class ModBlockLootProvider extends BlockLootSubProvider {

    public ModBlockLootProvider() {
        super(Set.of(), FeatureFlags.VANILLA_SET);
    }

    @Override
    protected void generate() {
        // 四作物：成熟掉产物 + 1~3 种子；未成熟只掉 1 种子
        addCrop(ModBlocks.GINGER_CROP.get(), ModItems.GINGER.get(), ModItems.GINGER_SEEDS.get());
        addCrop(ModBlocks.GARLIC_CROP.get(), ModItems.GARLIC.get(), ModItems.GARLIC_SEEDS.get());
        addCrop(ModBlocks.GONG_CAI_CROP.get(), ModItems.GONG_CAI.get(), ModItems.GONG_CAI_SEEDS.get());

        // 花椒：5 阶段，独立属性
        addSichuanPepper();
        // 冬笋：仅成熟掉落
        addBambooShoot();
        // 竹荪菌落：剪刀掉自身，否则 2~3 竹荪
        addBambooFungusColony();
        // 藏藕泥块：精准采集/锄头掉自身，否则 1~4 莲藕
        addLotusMud();
        // 繁茂草丛：剪刀掉自身，否则 35% 掉随机种子
        addLushGrass();

        dropSelf(ModBlocks.BLOOMING_LOTUS_LEAF.get());
        dropSelf(ModBlocks.BOBO_CHICKEN_POT.get());
        dropSelf(ModBlocks.BAMBOO_ROOT.get());
    }

    private void addCrop(Block crop, Item product, Item seeds) {
        LootItemCondition.Builder mature = LootItemBlockStatePropertyCondition.hasBlockStateProperties(crop)
            .setProperties(StatePropertiesPredicate.Builder.properties()
                .hasProperty(ModCropBlocks.AGE, ModCropBlocks.MAX_AGE));
        this.add(crop, this.createCropDrops(crop, product, seeds, mature));
    }

    private void addSichuanPepper() {
        Block crop = ModBlocks.SICHUAN_PEPPER_CROP.get();
        LootItemCondition.Builder mature = LootItemBlockStatePropertyCondition.hasBlockStateProperties(crop)
            .setProperties(StatePropertiesPredicate.Builder.properties()
                .hasProperty(SichuanPepperCropBlock.AGE, SichuanPepperCropBlock.MAX_AGE));
        LootItemCondition.Builder notMature = mature.invert();

        this.add(crop, LootTable.lootTable()
            .withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .when(mature)
                .add(LootItem.lootTableItem(ModItems.SICHUAN_PEPPER.get())
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                .add(LootItem.lootTableItem(ModItems.SICHUAN_PEPPER_SEEDS.get())
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))))
            .withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .when(notMature)
                .add(LootItem.lootTableItem(ModItems.SICHUAN_PEPPER_SEEDS.get())
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 1))))));
    }

    private void addBambooShoot() {
        Block crop = ModBlocks.BAMBOO_SHOOT_CROP.get();
        LootItemCondition.Builder mature = LootItemBlockStatePropertyCondition.hasBlockStateProperties(crop)
            .setProperties(StatePropertiesPredicate.Builder.properties()
                .hasProperty(BambooShootCropBlock.AGE, BambooShootCropBlock.MAX_AGE));
        this.add(crop, LootTable.lootTable()
            .withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .when(mature)
                .add(LootItem.lootTableItem(ModItems.WINTER_BAMBOO_SHOOTS.get()))));
    }

    private void addBambooFungusColony() {
        Block colony = ModBlocks.BAMBOO_FUNGUS_COLONY.get();
        LootItemCondition.Builder shears = MatchTool.toolMatches(
            ItemPredicate.Builder.item().of(Items.SHEARS));
        this.add(colony, LootTable.lootTable()
            .withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .when(shears.invert())
                .add(LootItem.lootTableItem(ModItems.BAMBOO_FUNGUS.get())
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 3)))))
            .withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .when(shears)
                .add(LootItem.lootTableItem(colony))));
    }

    private void addLotusMud() {
        Block mud = ModBlocks.LOTUS_MUD.get();
        LootItemCondition.Builder silkTouch = net.minecraft.world.level.storage.loot.predicates.AnyOfCondition.anyOf(
            MatchTool.toolMatches(ItemPredicate.Builder.item()
                .hasEnchantment(new net.minecraft.advancements.critereon.EnchantmentPredicate(
                    Enchantments.SILK_TOUCH,
                    net.minecraft.advancements.critereon.MinMaxBounds.Ints.atLeast(1)))),
            MatchTool.toolMatches(ItemPredicate.Builder.item().of(ItemTags.HOES)));
        this.add(mud, LootTable.lootTable()
            .withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .when(silkTouch.invert())
                .add(LootItem.lootTableItem(ModItems.LOTUS_ROOT.get())
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4)))))
            .withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .when(silkTouch)
                .add(LootItem.lootTableItem(mud))));
    }

    private void addLushGrass() {
        Block grass = ModBlocks.LUSH_GRASS.get();
        LootItemCondition.Builder shears = MatchTool.toolMatches(
            ItemPredicate.Builder.item().of(Items.SHEARS));
        this.add(grass, LootTable.lootTable()
            .withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .when(LootItemRandomChanceCondition.randomChance(0.35F))
                .when(shears.invert())
                .add(LootItem.lootTableItem(ModItems.GINGER_SEEDS.get()).setWeight(25))
                .add(LootItem.lootTableItem(ModItems.GARLIC_SEEDS.get()).setWeight(25))
                .add(LootItem.lootTableItem(ModItems.GONG_CAI_SEEDS.get()).setWeight(25))
                .add(LootItem.lootTableItem(ModItems.SICHUAN_PEPPER_SEEDS.get()).setWeight(25)))
            .withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .when(shears)
                .add(LootItem.lootTableItem(grass))));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return List.of(
            ModBlocks.GINGER_CROP.get(),
            ModBlocks.GARLIC_CROP.get(),
            ModBlocks.GONG_CAI_CROP.get(),
            ModBlocks.SICHUAN_PEPPER_CROP.get(),
            ModBlocks.BAMBOO_SHOOT_CROP.get(),
            ModBlocks.BAMBOO_FUNGUS_COLONY.get(),
            ModBlocks.BAMBOO_ROOT.get(),
            ModBlocks.BLOOMING_LOTUS_LEAF.get(),
            ModBlocks.LOTUS_MUD.get(),
            ModBlocks.LUSH_GRASS.get(),
            ModBlocks.BOBO_CHICKEN_POT.get());
    }
}

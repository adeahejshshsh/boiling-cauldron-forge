/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：向村庄箱子注入种子战利品。
 * 移植：Fabric → Forge 1.20.1（LootTableEvents.MODIFY → LootTableLoadEvent）
 */
package com.boilingcauldron.loot;

import com.boilingcauldron.BoilingCauldron;
import com.boilingcauldron.item.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.Set;

/**
 * 向村庄箱子追加种子奖池，解决四种作物在生存模式中没有初始来源的问题。
 */
@Mod.EventBusSubscriber(modid = BoilingCauldron.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ModLootTableModifiers {

    private static final Set<ResourceLocation> VILLAGE_HOUSE_CHESTS = Set.of(
        villageChest("village_plains_house"),
        villageChest("village_desert_house"),
        villageChest("village_savanna_house"),
        villageChest("village_snowy_house"),
        villageChest("village_taiga_house")
    );

    private static final float SEASONING_PACK_CHANCE = 0.08F;

    private ModLootTableModifiers() {
    }

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        ResourceLocation name = event.getName();
        LootTable table = event.getTable();
        if (table == null) {
            return;
        }

        SeedDrop[] drops = THEME_DROPS.get(name);
        if (drops != null) {
            for (SeedDrop drop : drops) {
                table.addPool(createSeedPool(drop).build());
            }
        }

        if (VILLAGE_HOUSE_CHESTS.contains(name)) {
            table.addPool(createSeasoningPackPool().build());
        }
    }

    private static final Map<ResourceLocation, SeedDrop[]> THEME_DROPS = Map.of(
        villageChest("village_plains_house"), new SeedDrop[]{
            new SeedDrop(ModItems.GARLIC_SEEDS, 0.15F, 1, 2),
            new SeedDrop(ModItems.GINGER_SEEDS, 0.08F, 1, 1)
        },
        villageChest("village_savanna_house"), new SeedDrop[]{
            new SeedDrop(ModItems.GINGER_SEEDS, 0.15F, 1, 2),
            new SeedDrop(ModItems.SICHUAN_PEPPER_SEEDS, 0.10F, 1, 1)
        },
        villageChest("village_taiga_house"), new SeedDrop[]{
            new SeedDrop(ModItems.GARLIC_SEEDS, 0.15F, 1, 2),
            new SeedDrop(ModItems.GONG_CAI_SEEDS, 0.08F, 1, 1)
        },
        villageChest("village_desert_house"), new SeedDrop[]{
            new SeedDrop(ModItems.SICHUAN_PEPPER_SEEDS, 0.15F, 1, 2)
        },
        villageChest("village_snowy_house"), new SeedDrop[]{
            new SeedDrop(ModItems.GARLIC_SEEDS, 0.10F, 1, 2)
        },
        villageChest("village_butcher"), new SeedDrop[]{
            new SeedDrop(ModItems.GINGER_SEEDS, 0.10F, 1, 1),
            new SeedDrop(ModItems.GARLIC_SEEDS, 0.10F, 1, 1)
        },
        villageChest("village_fisher"), new SeedDrop[]{
            new SeedDrop(ModItems.GONG_CAI_SEEDS, 0.15F, 1, 2)
        }
    );

    private static LootPool.Builder createSeedPool(SeedDrop drop) {
        return LootPool.lootPool()
            .setRolls(ConstantValue.exactly(1))
            .add(LootItem.lootTableItem(drop.item().get())
                .apply(SetItemCountFunction.setCount(
                    UniformGenerator.between(drop.minCount(), drop.maxCount()))))
            .when(LootItemRandomChanceCondition.randomChance(drop.chance()));
    }

    /**
     * 所有村庄民居共用的“调料包”奖池，随机产出一种种子。
     */
    private static LootPool.Builder createSeasoningPackPool() {
        return LootPool.lootPool()
            .setRolls(ConstantValue.exactly(1))
            .when(LootItemRandomChanceCondition.randomChance(SEASONING_PACK_CHANCE))
            .add(LootItem.lootTableItem(ModItems.GARLIC_SEEDS.get()).setWeight(30))
            .add(LootItem.lootTableItem(ModItems.GINGER_SEEDS.get()).setWeight(30))
            .add(LootItem.lootTableItem(ModItems.GONG_CAI_SEEDS.get()).setWeight(20))
            .add(LootItem.lootTableItem(ModItems.SICHUAN_PEPPER_SEEDS.get()).setWeight(20));
    }

    private static ResourceLocation villageChest(String path) {
        return new ResourceLocation("minecraft", "chests/village/" + path);
    }

    private record SeedDrop(java.util.function.Supplier<Item> item, float chance, int minCount, int maxCount) {
    }
}

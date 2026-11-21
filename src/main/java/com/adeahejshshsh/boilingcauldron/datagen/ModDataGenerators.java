package com.adeahejshshsh.boilingcauldron.datagen;

import com.adeahejshshsh.boilingcauldron.BoilingCauldron;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/** 数据生成入口：./gradlew runData */
@EventBusSubscriber(modid = BoilingCauldron.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class ModDataGenerators {

    private ModDataGenerators() {
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper helper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookup = event.getLookupProvider();

        if (event.includeClient()) {
            generator.addProvider(true, new ModBlockStateProvider(output, helper));
            generator.addProvider(true, new ModItemModelProvider(output, helper));
            generator.addProvider(true, new ModLanguageProvider(output, "zh_cn"));
            generator.addProvider(true, new ModLanguageProvider(output, "en_us"));
        }
        if (event.includeServer()) {
            generator.addProvider(true, new LootTableProvider(output, Set.of(), List.of(
                new LootTableProvider.SubProviderEntry(ModBlockLootProvider::new, LootContextParamSets.BLOCK)),
                lookup));
            generator.addProvider(true, new ModRecipeProvider(output, lookup));
            ModBlockTagsProvider blockTags = new ModBlockTagsProvider(output, lookup, helper);
            generator.addProvider(true, blockTags);
            generator.addProvider(true, new ModItemTagsProvider(output, lookup, blockTags.contentsGetter(), helper));
        }
    }
}

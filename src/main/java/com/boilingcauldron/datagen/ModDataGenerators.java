/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：Forge 数据生成入口（GatherDataEvent）。
 * 运行：./gradlew runData
 * 输出：src/generated/resources
 * 更新：2026-11-21
 */
package com.boilingcauldron.datagen;

import com.boilingcauldron.BoilingCauldron;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = BoilingCauldron.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
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
                new LootTableProvider.SubProviderEntry(ModBlockLootProvider::new, LootContextParamSets.BLOCK))));
            generator.addProvider(true, new ModRecipeProvider(output));
            ModBlockTagsProvider blockTags = new ModBlockTagsProvider(output, lookup, helper);
            generator.addProvider(true, blockTags);
            generator.addProvider(true, new ModItemTagsProvider(output, lookup, blockTags.contentsGetter(), helper));
        }
    }
}

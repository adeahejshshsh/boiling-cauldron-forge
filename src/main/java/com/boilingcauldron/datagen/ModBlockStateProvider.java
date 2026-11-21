/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：方块状态与方块模型生成。
 * 说明：盛花荷叶的 5 个平面模型与钵钵鸡锅的 Blockbench 模型为手写资源，
 *       这里只生成它们的 blockstate 并引用已有模型文件。
 * 更新：2026-11-21
 */
package com.boilingcauldron.datagen;

import com.boilingcauldron.BoilingCauldron;
import com.boilingcauldron.block.BambooRootBlock;
import com.boilingcauldron.block.BambooShootCropBlock;
import com.boilingcauldron.block.BloomingLotusLeafBlock;
import com.boilingcauldron.block.ModBlocks;
import com.boilingcauldron.block.SichuanPepperCropBlock;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModBlockStateProvider extends BlockStateProvider {

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper helper) {
        super(output, BoilingCauldron.NAMESPACE, helper);
    }

    @Override
    protected void registerStatesAndModels() {
        // 四作物共用 4 阶段，模型 parent = block/crop
        cropWithAge("ginger_crop", ModBlocks.GINGER_CROP.get(),
            com.boilingcauldron.block.ModCropBlocks.AGE, 3);
        cropWithAge("garlic_crop", ModBlocks.GARLIC_CROP.get(),
            com.boilingcauldron.block.ModCropBlocks.AGE, 3);
        cropWithAge("gong_cai_crop", ModBlocks.GONG_CAI_CROP.get(),
            com.boilingcauldron.block.ModCropBlocks.AGE, 3);
        cropWithAge("sichuan_pepper_crop", ModBlocks.SICHUAN_PEPPER_CROP.get(),
            SichuanPepperCropBlock.AGE, 4);

        // 冬笋：6 阶段十字模型
        crossWithAge("bamboo_shoot_crop", ModBlocks.BAMBOO_SHOOT_CROP.get(),
            BambooShootCropBlock.AGE, 5);

        // 十字植物
        crossBlock("bamboo_fungus_colony", ModBlocks.BAMBOO_FUNGUS_COLONY.get());
        crossBlock("lush_grass", ModBlocks.LUSH_GRASS.get());

        // 竹根殖块：cube_all，bamboo_count 0~5 共用同一模型
        ModelFile bambooRoot = models()
            .withExistingParent("bamboo_root", mcLoc("block/cube_all"))
            .texture("all", modLoc("block/bamboo_root"));
        VariantBlockStateBuilder rootBuilder = getVariantBuilder(ModBlocks.BAMBOO_ROOT.get());
        for (int count = 0; count <= BambooRootBlock.MAX_COUNT; count++) {
            rootBuilder.partialState()
                .with(BambooRootBlock.BAMBOO_COUNT, count)
                .modelForState().modelFile(bambooRoot).addModel();
        }

        // 藏藕泥块
        simpleBlock(ModBlocks.LOTUS_MUD.get(), models()
            .withExistingParent("lotus_mud", mcLoc("block/cube_all"))
            .texture("all", modLoc("block/lotus_mud")));

        // 盛花荷叶：模型手写，仅生成 blockstate
        VariantBlockStateBuilder leafBuilder = getVariantBuilder(ModBlocks.BLOOMING_LOTUS_LEAF.get());
        for (int growth = 0; growth <= 4; growth++) {
            ResourceLocation modelId = modLoc("block/blooming_lotus_leaf_stage" + growth);
            leafBuilder.partialState()
                .with(BloomingLotusLeafBlock.GROWTH, growth)
                .modelForState().modelFile(models().getExistingFile(modelId)).addModel();
        }

        // 钵钵鸡锅：Blockbench 模型手写，仅生成 blockstate
        simpleBlock(ModBlocks.BOBO_CHICKEN_POT.get(),
            models().getExistingFile(modLoc("block/bobo_chicken_pot")));
    }

    private void cropWithAge(String name, Block block, IntegerProperty age, int maxAge) {
        VariantBlockStateBuilder builder = getVariantBuilder(block);
        for (int value = 0; value <= maxAge; value++) {
            String modelName = name + "_stage" + value;
            ModelFile model = models()
                .withExistingParent(modelName, mcLoc("block/crop"))
                .texture("crop", modLoc("block/" + modelName))
                .renderType("cutout");
            builder.partialState().with(age, value)
                .modelForState().modelFile(model).addModel();
        }
    }

    private void crossWithAge(String name, Block block, IntegerProperty age, int maxAge) {
        VariantBlockStateBuilder builder = getVariantBuilder(block);
        for (int value = 0; value <= maxAge; value++) {
            String modelName = name + "_stage" + value;
            ModelFile model = models()
                .withExistingParent(modelName, mcLoc("block/cross"))
                .texture("cross", modLoc("block/" + modelName))
                .renderType("cutout");
            builder.partialState().with(age, value)
                .modelForState().modelFile(model).addModel();
        }
    }

    private void crossBlock(String name, Block block) {
        ModelFile model = models()
            .withExistingParent(name, mcLoc("block/cross"))
            .texture("cross", modLoc("block/" + name))
            .renderType("cutout");
        simpleBlock(block, model);
    }
}

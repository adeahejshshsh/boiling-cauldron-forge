package com.adeahejshshsh.boilingcauldron.datagen;

import com.adeahejshshsh.boilingcauldron.BoilingCauldron;
import com.adeahejshshsh.boilingcauldron.block.BambooRootBlock;
import com.adeahejshshsh.boilingcauldron.block.BambooShootCropBlock;
import com.adeahejshshsh.boilingcauldron.block.BloomingLotusLeafBlock;
import com.adeahejshshsh.boilingcauldron.block.ModBlocks;
import com.adeahejshshsh.boilingcauldron.block.ModCropBlocks;
import com.adeahejshshsh.boilingcauldron.block.SichuanPepperCropBlock;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/** 方块状态与方块模型生成。 */
public class ModBlockStateProvider extends BlockStateProvider {

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper helper) {
        super(output, BoilingCauldron.NAMESPACE, helper);
    }

    @Override
    protected void registerStatesAndModels() {
        cropWithAge("ginger_crop", ModBlocks.GINGER_CROP.get(), ModCropBlocks.AGE, 3);
        cropWithAge("garlic_crop", ModBlocks.GARLIC_CROP.get(), ModCropBlocks.AGE, 3);
        cropWithAge("gong_cai_crop", ModBlocks.GONG_CAI_CROP.get(), ModCropBlocks.AGE, 3);
        cropWithAge("sichuan_pepper_crop", ModBlocks.SICHUAN_PEPPER_CROP.get(),
            SichuanPepperCropBlock.AGE, 4);
        crossWithAge("bamboo_shoot_crop", ModBlocks.BAMBOO_SHOOT_CROP.get(),
            BambooShootCropBlock.AGE, 5);

        crossBlock("bamboo_fungus_colony", ModBlocks.BAMBOO_FUNGUS_COLONY.get());
        crossBlock("lush_grass", ModBlocks.LUSH_GRASS.get());

        ModelFile bambooRoot = models()
            .withExistingParent("bamboo_root", mcLoc("block/cube_all"))
            .texture("all", modLoc("block/bamboo_root"));
        VariantBlockStateBuilder rootBuilder = getVariantBuilder(ModBlocks.BAMBOO_ROOT.get());
        for (int count = 0; count <= BambooRootBlock.MAX_COUNT; count++) {
            rootBuilder.partialState()
                .with(BambooRootBlock.BAMBOO_COUNT, count)
                .modelForState().modelFile(bambooRoot).addModel();
        }

        simpleBlock(ModBlocks.LOTUS_MUD.get(), models()
            .withExistingParent("lotus_mud", mcLoc("block/cube_all"))
            .texture("all", modLoc("block/lotus_mud")));

        VariantBlockStateBuilder leafBuilder = getVariantBuilder(ModBlocks.BLOOMING_LOTUS_LEAF.get());
        for (int growth = 0; growth <= 4; growth++) {
            ResourceLocation modelId = modLoc("block/blooming_lotus_leaf_stage" + growth);
            leafBuilder.partialState()
                .with(BloomingLotusLeafBlock.GROWTH, growth)
                .modelForState().modelFile(models().getExistingFile(modelId)).addModel();
        }

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

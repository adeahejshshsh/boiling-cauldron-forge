package com.adeahejshshsh.boilingcauldron.datagen;

import com.adeahejshshsh.boilingcauldron.BoilingCauldron;
import com.adeahejshshsh.boilingcauldron.block.ModBlocks;
import com.adeahejshshsh.boilingcauldron.item.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/** 物品模型生成。 */
public class ModItemModelProvider extends ItemModelProvider {

    public ModItemModelProvider(PackOutput output, ExistingFileHelper helper) {
        super(output, BoilingCauldron.NAMESPACE, helper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.GINGER.get());
        basicItem(ModItems.GARLIC.get());
        basicItem(ModItems.GONG_CAI.get());
        basicItem(ModItems.WINTER_BAMBOO_SHOOTS.get());
        basicItem(ModItems.LOTUS_ROOT.get());
        basicItem(ModItems.SICHUAN_PEPPER.get());
        basicItem(ModItems.BAMBOO_FUNGUS.get());

        basicItem(ModItems.MUSHROOM_BASE.get());
        basicItem(ModItems.MALA_BASE.get());
        basicItem(ModItems.RATTAN_PEPPER_BASE.get());
        basicItem(ModItems.BOBO_CHICKEN.get());
        basicItem(ModItems.MALATANG_BOWL.get());
        basicItem(ModItems.PHANTOM_FIVE_SPICE_POWDER.get());

        basicItem(ModItems.GINGER_SEEDS.get());
        basicItem(ModItems.GARLIC_SEEDS.get());
        basicItem(ModItems.GONG_CAI_SEEDS.get());
        basicItem(ModItems.SICHUAN_PEPPER_SEEDS.get());

        withExistingParent("bamboo_root", modLoc("block/bamboo_root"));
        withExistingParent("lotus_mud", modLoc("block/lotus_mud"));
        withExistingParent("bobo_chicken_pot", modLoc("block/bobo_chicken_pot"));

        itemFromBlockTexture("bamboo_fungus_colony");
        itemFromBlockTexture("lush_grass");
        itemFromBlockTexture("blooming_lotus_leaf_stage0", "blooming_lotus_leaf");
    }

    private void itemFromBlockTexture(String blockTextureName) {
        itemFromBlockTexture(blockTextureName, blockTextureName);
    }

    private void itemFromBlockTexture(String blockTextureName, String itemModelName) {
        getBuilder(itemModelName)
            .parent(getExistingFile(mcLoc("item/generated")))
            .texture("layer0", modLoc("block/" + blockTextureName));
    }
}

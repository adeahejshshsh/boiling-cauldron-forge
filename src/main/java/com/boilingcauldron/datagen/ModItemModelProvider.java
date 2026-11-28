/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：物品模型生成。
 * 更新：2026-11-21
 */
package com.boilingcauldron.datagen;

import com.boilingcauldron.BoilingCauldron;
import com.boilingcauldron.block.ModBlocks;
import com.boilingcauldron.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {

    public ModItemModelProvider(PackOutput output, ExistingFileHelper helper) {
        super(output, BoilingCauldron.NAMESPACE, helper);
    }

    @Override
    protected void registerModels() {
        // 食材
        basicItem(ModItems.GINGER.get());
        basicItem(ModItems.GARLIC.get());
        basicItem(ModItems.GONG_CAI.get());
        basicItem(ModItems.WINTER_BAMBOO_SHOOTS.get());
        basicItem(ModItems.LOTUS_ROOT.get());
        basicItem(ModItems.SICHUAN_PEPPER.get());
        basicItem(ModItems.BAMBOO_FUNGUS.get());

        // 底料
        basicItem(ModItems.MUSHROOM_BASE.get());
        basicItem(ModItems.MALA_BASE.get());
        basicItem(ModItems.RATTAN_PEPPER_BASE.get());

        // 成品
        basicItem(ModItems.BOBO_CHICKEN.get());
        basicItem(ModItems.MALATANG_BOWL.get());
        basicItem(ModItems.PHANTOM_FIVE_SPICE_POWDER.get());

        // 种子（贴图为 item/xxx_seeds）
        basicItem(ModItems.GINGER_SEEDS.get());
        basicItem(ModItems.GARLIC_SEEDS.get());
        basicItem(ModItems.GONG_CAI_SEEDS.get());
        basicItem(ModItems.SICHUAN_PEPPER_SEEDS.get());

        // 方块物品：直接引用方块模型
        withExistingParent("bamboo_root", modLoc("block/bamboo_root"));
        withExistingParent("lotus_mud", modLoc("block/lotus_mud"));
        withExistingParent("bobo_chicken_pot", modLoc("block/bobo_chicken_pot"));
        withExistingParent("malatang_pot", modLoc("block/malatang_pot"));

        // 十字植物：物品贴图取方块贴图（与手写资源一致：item/generated + 方块纹理）
        itemFromBlockTexture("bamboo_fungus_colony");
        itemFromBlockTexture("lush_grass");

        // 盛花荷叶物品用 stage0 平面贴图
        itemFromBlockTexture("blooming_lotus_leaf_stage0", "blooming_lotus_leaf");
    }

    /** 用方块纹理做物品贴图，与原手写资源保持一致 */
    private void itemFromBlockTexture(String blockTextureName) {
        itemFromBlockTexture(blockTextureName, blockTextureName);
    }

    private void itemFromBlockTexture(String blockTextureName, String itemModelName) {
        getBuilder(itemModelName)
            .parent(getExistingFile(mcLoc("item/generated")))
            .texture("layer0", modLoc("block/" + blockTextureName));
    }
}

package com.boilingcauldron.compat.jade;

import com.boilingcauldron.block.BambooRootBlock;
import com.boilingcauldron.block.BloomingLotusLeafBlock;
import com.boilingcauldron.block.BoboChickenPotBlock;
import com.boilingcauldron.block.entity.BoboChickenPotBlockEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

/**
 * Jade 插件入口。
 *
 * <p>{@code @WailaPlugin} 的 value 表示「加载本插件所需的前置 modId」，
 * 留空即表示插件与本模组同体 —— 这里正是这种情况。
 */
@WailaPlugin
public class BoilingCauldronJadePlugin implements IWailaPlugin {

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(
            BoboChickenPotDataProvider.INSTANCE, BoboChickenPotBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(
            BambooRootProvider.INSTANCE, BambooRootBlock.class);
        registration.registerBlockComponent(
            BloomingLotusLeafProvider.INSTANCE, BloomingLotusLeafBlock.class);
        registration.registerBlockComponent(
            BoboChickenPotProvider.INSTANCE, BoboChickenPotBlock.class);
    }
}

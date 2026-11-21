package com.adeahejshshsh.boilingcauldron;

import com.adeahejshshsh.boilingcauldron.block.ModBlocks;
import com.adeahejshshsh.boilingcauldron.block.entity.ModBlockEntities;
import com.adeahejshshsh.boilingcauldron.item.ModCreativeTabs;
import com.adeahejshshsh.boilingcauldron.item.ModItems;
import com.adeahejshshsh.boilingcauldron.recipe.ModRecipes;
import com.adeahejshshsh.boilingcauldron.screen.ModMenus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.ComposterBlock;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** NeoForge 1.21.1 入口。 */
@Mod(BoilingCauldron.MOD_ID)
public class BoilingCauldron {

    /** NeoForge 模组 ID：只允许 [a-z][a-z0-9_]{1,63}，不能含连字符。 */
    public static final String MOD_ID = "boilingcauldron";

    /** 资源命名空间，沿用原 mod id。 */
    public static final String NAMESPACE = "boiling-cauldron";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public BoilingCauldron(IEventBus modBus) {
        ModBlocks.register(modBus);
        ModItems.register(modBus);
        ModBlockEntities.register(modBus);
        ModMenus.register(modBus);
        ModCreativeTabs.register(modBus);
        ModRecipes.register(modBus);

        modBus.addListener(this::commonSetup);

        LOGGER.info("沸鼎烟火（Boiling Cauldron）NeoForge 版初始化完成");
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(ModBlocks::setCropSeeds);
        event.enqueueWork(BoilingCauldron::registerCompostables);
    }

    /**
     * 堆肥注册：原版 {@code ComposterBlock.COMPOSTABLES} 为 public static final，
     * 键是 Item，必须用 {@code asItem()}。
     */
    private static void registerCompostables() {
        ComposterBlock.COMPOSTABLES.put(ModBlocks.BAMBOO_FUNGUS_COLONY.get().asItem(), 0.65F);
        ComposterBlock.COMPOSTABLES.put(ModBlocks.LUSH_GRASS.get().asItem(), 0.30F);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(NAMESPACE, path);
    }
}

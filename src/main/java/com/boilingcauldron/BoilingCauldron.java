/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：模组主入口，初始化注册方块、物品、方块实体、菜单与创造栏。
 * 移植：Fabric → Forge 1.20.1（ModInitializer → @Mod）
 */
package com.boilingcauldron;

import com.boilingcauldron.block.ModBlocks;
import com.boilingcauldron.block.entity.ModBlockEntities;
import com.boilingcauldron.item.ModCreativeTabs;
import com.boilingcauldron.item.ModItems;
import com.boilingcauldron.recipe.ModRecipes;
import com.boilingcauldron.screen.ModMenus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(BoilingCauldron.MOD_ID)
public class BoilingCauldron {
    /**
     * Forge 模组 ID。Forge 校验规则为 ^[a-z][a-z0-9_]{1,63}$ —— **不允许连字符**，
     * 不匹配会在加载阶段抛 InvalidModFileException 直接崩溃。
     */
    public static final String MOD_ID = "boilingcauldron";

    /** 资源命名空间，沿用原 mod id（Forge 命名空间允许连字符）。 */
    public static final String NAMESPACE = "boiling-cauldron";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public BoilingCauldron() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.register(modBus);        // 先注册方块
        ModItems.register(modBus);         // 再注册物品（种子依赖作物方块）
        ModBlockEntities.register(modBus); // 方块实体类型
        ModMenus.register(modBus);         // 钵钵鸡锅 GUI
        ModRecipes.register(modBus);       // 自定义配方类型 bobo_soak（数据包驱动）
        ModCreativeTabs.register(modBus);  // 创造模式物品栏

        modBus.addListener(this::commonSetup);

        LOGGER.info("沸鼎烟火（Boiling Cauldron）Forge 版初始化完成");
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // 注册完成后再回填种子引用，避免静态初始化顺序问题
        event.enqueueWork(ModBlocks::setCropSeeds);
        // 堆肥：Forge 1.20.1 没有专用 API，直接写入原版 ComposterBlock 的可堆肥表
        event.enqueueWork(BoilingCauldron::registerCompostables);
    }

    /**
     * 注册堆肥。
     * <p>原版 {@code ComposterBlock.COMPOSTABLES} 是 {@code public static final Object2FloatMap<ItemLike>}，
     * 键为 <b>Item</b>（实际查表用 {@code stack.getItem()}），所以必须用 {@code Block.asItem()}。
     */
    private static void registerCompostables() {
        ComposterBlock.COMPOSTABLES.put(ModBlocks.BAMBOO_FUNGUS_COLONY.get().asItem(), 0.65F);
        ComposterBlock.COMPOSTABLES.put(ModBlocks.LUSH_GRASS.get().asItem(), 0.30F);
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(NAMESPACE, path);
    }
}

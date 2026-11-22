/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：语言文件生成（zh_cn / en_us）。
 * 更新：2026-11-21
 */
package com.adeahejshshsh.boilingcauldron.datagen;

import com.adeahejshshsh.boilingcauldron.BoilingCauldron;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class ModLanguageProvider extends LanguageProvider {

    private final String locale;

    public ModLanguageProvider(PackOutput output, String locale) {
        super(output, BoilingCauldron.NAMESPACE, locale);
        this.locale = locale;
    }

    @Override
    protected void addTranslations() {
        if ("zh_cn".equals(this.locale)) {
            addZhCn();
        } else {
            addEnUs();
        }
    }

    private void addZhCn() {
        add("block.boiling-cauldron.bamboo_fungus_colony", "竹荪菌落");
        add("block.boiling-cauldron.bamboo_root", "竹根殖块");
        add("block.boiling-cauldron.bamboo_shoot_crop", "冬笋作物");
        add("block.boiling-cauldron.blooming_lotus_leaf", "盛花荷叶");
        add("block.boiling-cauldron.bobo_chicken_pot", "钵钵鸡锅");
        add("block.boiling-cauldron.garlic_crop", "大蒜作物");
        add("block.boiling-cauldron.ginger_crop", "生姜作物");
        add("block.boiling-cauldron.gong_cai_crop", "贡菜作物");
        add("block.boiling-cauldron.lotus_mud", "藏藕泥块");
        add("block.boiling-cauldron.lush_grass", "繁茂草丛");
        add("block.boiling-cauldron.sichuan_pepper_crop", "花椒作物");
        add("gui.boiling-cauldron.bobo_chicken_pot.empty", "空锅");
        add("gui.boiling-cauldron.bobo_chicken_pot.remaining", "· 剩余 %s 次");
        add("gui.boiling-cauldron.bobo_chicken_pot.water", "清水");
        add("item.boiling-cauldron.bamboo_fungus", "竹荪");
        add("item.boiling-cauldron.bobo_chicken", "钵钵鸡");
        add("item.boiling-cauldron.bobo_chicken.three_flavors", "三味钵钵鸡");
        add("item.boiling-cauldron.garlic", "大蒜");
        add("item.boiling-cauldron.garlic_seeds", "大蒜种子");
        add("item.boiling-cauldron.ginger", "生姜");
        add("item.boiling-cauldron.ginger_seeds", "生姜种子");
        add("item.boiling-cauldron.gong_cai", "贡菜");
        add("item.boiling-cauldron.gong_cai_seeds", "贡菜种子");
        add("item.boiling-cauldron.lotus_root", "莲藕");
        add("item.boiling-cauldron.mala_base", "麻辣底料");
        add("item.boiling-cauldron.mushroom_base", "菌菇底料");
        add("item.boiling-cauldron.rattan_pepper_base", "藤椒底料");
        add("item.boiling-cauldron.sichuan_pepper", "花椒");
        add("item.boiling-cauldron.sichuan_pepper_seeds", "花椒种子");
        add("item.boiling-cauldron.winter_bamboo_shoots", "冬笋");
        add("itemgroup.boiling_cauldron", "沸鼎烟火");
        add("tooltip.boiling-cauldron.bobo_chicken.flavor.mala", "辣辣");
        add("tooltip.boiling-cauldron.bobo_chicken.flavor.mushroom", "菌香");
        add("tooltip.boiling-cauldron.bobo_chicken.flavor.rattan_pepper", "藤椒");
        add("tooltip.boiling-cauldron.bobo_chicken.flavors", "口味：%s");
        add("tooltip.boiling-cauldron.bobo_chicken.original", "原料：%s");
        add("tooltip.boiling-cauldron.bobo_chicken.separator", "、");
        add("message.boiling-cauldron.pot.added_water", "已加水，请放入底料");
        add("message.boiling-cauldron.pot.added_base", "已加入%s，可浸泡 %s 次");
        add("message.boiling-cauldron.pot.flushed", "已冲洗底料，水保留");
        add("message.boiling-cauldron.pot.need_water", "需要先用水桶加水");
        add("message.boiling-cauldron.pot.has_base", "锅里已有底料（剩余 %s 次）");
        add("message.boiling-cauldron.pot.has_water", "锅里已有水");
        add("message.boiling-cauldron.pot.not_empty", "锅里有食物，请先取空");
        add("message.boiling-cauldron.pot.hint_start", "空锅：先用水桶加水，再放入底料");
        add("jei.boiling-cauldron.bobo_soak", "钵钵鸡浸泡");
        add("jei.boiling-cauldron.bobo_soak.time", "浸泡 %s 秒");
        add("jei.boiling-cauldron.bobo_soak.info", "把底料放入钵钵鸡锅可提供 %s 次浸泡额度；每格食物浸泡完成后变成钵钵鸡。");
        add("message.boiling-cauldron.pot.no_recipe", "这个底料没有对应配方（请检查数据包）");
        add("jei.boiling-cauldron.bobo_soak.uses", "可用 %s 次");
        add("item.boiling-cauldron.bobo_chicken.suffix", "的钵钵鸡");
        add("item.boiling-cauldron.bobo_chicken.joiner", "");
        add("jei.boiling-cauldron.info.bamboo_fungus", "由竹荪菌落掉落（2–3 个）。\n竹荪菌落由竹根殖块在附近土壤上生成。");
        add("jei.boiling-cauldron.info.bamboo_fungus_colony", "由竹根殖块在附近土壤上生成。\n用剪刀采集可获得方块本身；否则掉落 2–3 个竹荪。\n可用于堆肥（0.65）。");
        add("jei.boiling-cauldron.info.bamboo_root", "不会自然生成，由 8 个竹子加 1 个泥土合成。\n放置后会周期性地在附近 5×5 的土壤上生成冬笋作物或竹荪菌落，最多同时存在 5 个。\n附近竹子越多成功率越高（每根 +1%，上限 +20%）。");
        add("jei.boiling-cauldron.info.blooming_lotus_leaf", "自然生成于沼泽与红树林沼泽的水面上。\n生长到第 4 阶段（成熟）后，会周期性地把下方的泥土、沙子或砂砾转化为藏藕泥块。\n对成熟的荷叶使用骨粉，可让它向相邻水面扩散。");
        add("jei.boiling-cauldron.info.lotus_mud", "不会自然生成。\n由成熟的盛花荷叶把下方的泥土、沙子或砂砾转化而来。\n手持锹右击可获得 3 个莲藕；直接挖掘掉落 1–4 个。");
        add("jei.boiling-cauldron.info.lotus_root", "从藏藕泥块获得：手持锹右击得 3 个，直接挖掘得 1–4 个。\n藏藕泥块由成熟的盛花荷叶转化下方方块形成。");
        add("jei.boiling-cauldron.info.lush_grass", "自然生成于以下群系的地表：平原、向日葵平原、森林、繁花森林、白桦森林、针叶林、热带草原、草甸、沼泽、红树林沼泽。\n每 4 个区块最多尝试一次。\n用剪刀采集可获得方块本身；其它方式破坏时有 35% 概率掉落随机的本模组作物种子。");
        add("jei.boiling-cauldron.info.seeds", "获取途径：\n· 繁茂草丛：35% 概率掉落随机种子\n· 村庄箱子：不同村庄类型给不同种子，另有 8% 概率出现调料包\n· 流浪商人：1 个绿宝石换 1 个种子");
        add("jei.boiling-cauldron.info.winter_bamboo_shoots", "由竹根殖块生成的冬笋作物成熟后获得。\n对成熟的冬笋作物使用骨粉，会把它转化为原版竹子。");
        add("jade.boiling-cauldron.bamboo_root.count", "存活产物：%s / %s");
        add("jade.boiling-cauldron.blooming_lotus_leaf.growth", "生长阶段：%s / %s");
        add("jade.boiling-cauldron.blooming_lotus_leaf.mature", "已成熟，正在转化下方方块");
        add("jade.boiling-cauldron.bobo_chicken_pot.water", "水：%s");
        add("jade.boiling-cauldron.bobo_chicken_pot.water.filled", "已注入");
        add("jade.boiling-cauldron.bobo_chicken_pot.water.empty", "未注入");
        add("jade.boiling-cauldron.bobo_chicken_pot.base", "底料：%s");
        add("jade.boiling-cauldron.bobo_chicken_pot.uses", "剩余次数：%s");
        add("jade.boiling-cauldron.bobo_chicken_pot.soaking", "浸泡中：%s 格");
    }

    private void addEnUs() {
        add("block.boiling-cauldron.bamboo_fungus_colony", "Bamboo Fungus Colony");
        add("block.boiling-cauldron.bamboo_root", "Bamboo Root");
        add("block.boiling-cauldron.bamboo_shoot_crop", "Winter Bamboo Shoot Crop");
        add("block.boiling-cauldron.blooming_lotus_leaf", "Blooming Lotus Leaf");
        add("block.boiling-cauldron.bobo_chicken_pot", "Bobo Chicken Pot");
        add("block.boiling-cauldron.garlic_crop", "Garlic Crop");
        add("block.boiling-cauldron.ginger_crop", "Ginger Crop");
        add("block.boiling-cauldron.gong_cai_crop", "Gong Cai Crop");
        add("block.boiling-cauldron.lotus_mud", "Lotus Mud");
        add("block.boiling-cauldron.lush_grass", "Lush Grass");
        add("block.boiling-cauldron.sichuan_pepper_crop", "Sichuan Pepper Crop");
        add("gui.boiling-cauldron.bobo_chicken_pot.empty", "Empty");
        add("gui.boiling-cauldron.bobo_chicken_pot.remaining", "· %s uses left");
        add("gui.boiling-cauldron.bobo_chicken_pot.water", "Water");
        add("item.boiling-cauldron.bamboo_fungus", "Bamboo Fungus");
        add("item.boiling-cauldron.bobo_chicken", "Bobo Chicken");
        add("item.boiling-cauldron.bobo_chicken.three_flavors", "Three-Flavor Bobo Chicken");
        add("item.boiling-cauldron.garlic", "Garlic");
        add("item.boiling-cauldron.garlic_seeds", "Garlic Seeds");
        add("item.boiling-cauldron.ginger", "Ginger");
        add("item.boiling-cauldron.ginger_seeds", "Ginger Seeds");
        add("item.boiling-cauldron.gong_cai", "Gong Cai");
        add("item.boiling-cauldron.gong_cai_seeds", "Gong Cai Seeds");
        add("item.boiling-cauldron.lotus_root", "Lotus Root");
        add("item.boiling-cauldron.mala_base", "Mala Base");
        add("item.boiling-cauldron.mushroom_base", "Mushroom Base");
        add("item.boiling-cauldron.rattan_pepper_base", "Rattan Pepper Base");
        add("item.boiling-cauldron.sichuan_pepper", "Sichuan Pepper");
        add("item.boiling-cauldron.sichuan_pepper_seeds", "Sichuan Pepper Seeds");
        add("item.boiling-cauldron.winter_bamboo_shoots", "Winter Bamboo Shoots");
        add("itemgroup.boiling_cauldron", "Boiling Cauldron");
        add("tooltip.boiling-cauldron.bobo_chicken.flavor.mala", "Mala");
        add("tooltip.boiling-cauldron.bobo_chicken.flavor.mushroom", "Mushroom");
        add("tooltip.boiling-cauldron.bobo_chicken.flavor.rattan_pepper", "Rattan Pepper");
        add("tooltip.boiling-cauldron.bobo_chicken.flavors", "Flavors: %s");
        add("tooltip.boiling-cauldron.bobo_chicken.original", "Original: %s");
        add("tooltip.boiling-cauldron.bobo_chicken.separator", ", ");
        add("message.boiling-cauldron.pot.added_water", "Water added. Now add a soup base.");
        add("message.boiling-cauldron.pot.added_base", "Added %s (%s soaks available).");
        add("message.boiling-cauldron.pot.flushed", "Base rinsed out. Water kept.");
        add("message.boiling-cauldron.pot.need_water", "Fill the pot with water first.");
        add("message.boiling-cauldron.pot.has_base", "This pot already has a base (%s left).");
        add("message.boiling-cauldron.pot.has_water", "The pot already contains water.");
        add("message.boiling-cauldron.pot.not_empty", "Remove the food from the pot first.");
        add("message.boiling-cauldron.pot.hint_start", "Empty pot: fill with water, then add a soup base.");
        add("jei.boiling-cauldron.bobo_soak", "Bobo Chicken Soaking");
        add("jei.boiling-cauldron.bobo_soak.time", "Soak %ss");
        add("jei.boiling-cauldron.bobo_soak.info", "Put a soup base into the Bobo Chicken Pot to gain %s soaks. Each food becomes Bobo Chicken after soaking.");
        add("message.boiling-cauldron.pot.no_recipe", "No recipe found for this base (check your datapack).");
        add("jei.boiling-cauldron.bobo_soak.uses", "%s uses");
        add("item.boiling-cauldron.bobo_chicken.suffix", " Bobo Chicken");
        add("item.boiling-cauldron.bobo_chicken.joiner", "-");
        add("jei.boiling-cauldron.info.bamboo_fungus", "Dropped by Bamboo Fungus Colonies (2-3).\nColonies are spawned by the Bamboo Root on nearby soil.");
        add("jei.boiling-cauldron.info.bamboo_fungus_colony", "Spawned by the Bamboo Root on nearby soil.\nHarvest with shears to obtain the block itself; otherwise it drops 2-3 Bamboo Fungus.\nCompostable (0.65).");
        add("jei.boiling-cauldron.info.bamboo_root", "Does not generate naturally; crafted from 8 bamboo plus 1 dirt.\nOnce placed, it periodically spawns Winter Bamboo Shoot crops or Bamboo Fungus Colonies on soil within a 5x5 area, up to 5 alive at once.\nMore bamboo nearby raises the success chance (+1% each, capped at +20%).");
        add("jei.boiling-cauldron.info.blooming_lotus_leaf", "Generates naturally on the water surface of swamps and mangrove swamps.\nOnce it reaches growth stage 4 (mature), it periodically converts dirt, sand or gravel below into Lotus Mud.\nUsing bone meal on a mature leaf spreads it to adjacent water.");
        add("jei.boiling-cauldron.info.lotus_mud", "Does not generate naturally.\nCreated when a mature Blooming Lotus Leaf converts dirt, sand or gravel below it.\nRight-click with a shovel for 3 Lotus Roots; mining drops 1-4.");
        add("jei.boiling-cauldron.info.lotus_root", "Obtained from Lotus Mud: right-click with a shovel for 3, or mine for 1-4.\nLotus Mud is formed when a mature Blooming Lotus Leaf converts the block below.");
        add("jei.boiling-cauldron.info.lush_grass", "Generates naturally on the surface of: plains, sunflower plains, forest, flower forest, birch forest, taiga, savanna, meadow, swamp, mangrove swamp.\nAt most one attempt per 4 chunks.\nHarvest with shears to obtain the block itself; breaking it any other way has a 35% chance to drop a random crop seed from this mod.");
        add("jei.boiling-cauldron.info.seeds", "Obtained from:\n- Lush Grass: 35% chance to drop a random seed\n- Village chests: different seeds per village type, plus an 8% seasoning pack pool\n- Wandering Trader: 1 emerald for 1 seed");
        add("jei.boiling-cauldron.info.winter_bamboo_shoots", "Harvested from mature Winter Bamboo Shoot crops spawned by the Bamboo Root.\nUsing bone meal on a mature crop turns it into vanilla bamboo.");
        add("jade.boiling-cauldron.bamboo_root.count", "Alive offspring: %s / %s");
        add("jade.boiling-cauldron.blooming_lotus_leaf.growth", "Growth: %s / %s");
        add("jade.boiling-cauldron.blooming_lotus_leaf.mature", "Mature - converting the block below");
        add("jade.boiling-cauldron.bobo_chicken_pot.water", "Water: %s");
        add("jade.boiling-cauldron.bobo_chicken_pot.water.filled", "filled");
        add("jade.boiling-cauldron.bobo_chicken_pot.water.empty", "empty");
        add("jade.boiling-cauldron.bobo_chicken_pot.base", "Base: %s");
        add("jade.boiling-cauldron.bobo_chicken_pot.uses", "Uses left: %s");
        add("jade.boiling-cauldron.bobo_chicken_pot.soaking", "Soaking: %s slot(s)");
    }
}

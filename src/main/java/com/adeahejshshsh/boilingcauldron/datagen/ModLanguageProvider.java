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

package com.adeahejshshsh.boilingcauldron.item;

import net.minecraft.world.food.FoodProperties;

public class ModFoods {
    private static FoodProperties simple(int hunger, float saturation) {
        return new FoodProperties.Builder().nutrition(hunger).saturationModifier(saturation).build();
    }

    public static final FoodProperties GINGER = simple(1, 0.25F);
    public static final FoodProperties GARLIC = simple(1, 0.25F);
    public static final FoodProperties GONG_CAI = simple(2, 0.25F);
    public static final FoodProperties WINTER_BAMBOO_SHOOTS = simple(2, 0.25F);
    public static final FoodProperties LOTUS_ROOT = simple(2, 0.25F);
    public static final FoodProperties SICHUAN_PEPPER = simple(2, 0.25F);
    public static final FoodProperties BAMBOO_FUNGUS = simple(1, 0.25F);
    public static final FoodProperties BOBO_CHICKEN = simple(8, 0.55F);
    /** 麻辣烫成品：数值全部写在数据组件里，基础食物属性为 0 */
    public static final FoodProperties MALATANG_BOWL =
        new FoodProperties.Builder().nutrition(0).saturationModifier(0.0F).alwaysEdible().build();
}

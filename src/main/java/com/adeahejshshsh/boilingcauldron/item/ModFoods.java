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
}

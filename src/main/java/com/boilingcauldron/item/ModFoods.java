/*
 * 沸鼎烟火（Boiling Cauldron）
 * 文件用途：定义食材和成品的食物属性。
 * 移植：Fabric → Forge 1.20.1
 */
package com.boilingcauldron.item;

import net.minecraft.world.food.FoodProperties;

public class ModFoods {
    public static final FoodProperties GINGER = new FoodProperties.Builder().nutrition(1).saturationMod(0.25F).build();
    public static final FoodProperties GARLIC = new FoodProperties.Builder().nutrition(1).saturationMod(0.25F).build();
    public static final FoodProperties GONG_CAI = new FoodProperties.Builder().nutrition(2).saturationMod(0.25F).build();
    public static final FoodProperties WINTER_BAMBOO_SHOOTS = new FoodProperties.Builder().nutrition(2).saturationMod(0.25F).build();
    public static final FoodProperties LOTUS_ROOT = new FoodProperties.Builder().nutrition(2).saturationMod(0.25F).build();
    public static final FoodProperties SICHUAN_PEPPER = new FoodProperties.Builder().nutrition(2).saturationMod(0.25F).build();
    public static final FoodProperties BAMBOO_FUNGUS = new FoodProperties.Builder().nutrition(1).saturationMod(0.25F).build();
    public static final FoodProperties BOBO_CHICKEN = new FoodProperties.Builder().nutrition(8).saturationMod(0.55F).build();
}

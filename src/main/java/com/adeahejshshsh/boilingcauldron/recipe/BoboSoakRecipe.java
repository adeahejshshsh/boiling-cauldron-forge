package com.adeahejshshsh.boilingcauldron.recipe;

import com.adeahejshshsh.boilingcauldron.item.FlavorProfile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

/**
 * 自定义配方「钵钵鸡浸泡」：底料 → 成品，附带口味、次数、浸泡时长。
 *
 * <p>1.21 起配方改为 Codec 驱动，不再有 {@code ResourceLocation id}。
 */
public class BoboSoakRecipe implements Recipe<BoboSoakRecipe.Input> {

    /** 单输入容器：只放一个底料。 */
    public record Input(ItemStack base) implements RecipeInput {
        @Override
        public ItemStack getItem(int index) {
            return index == 0 ? this.base : ItemStack.EMPTY;
        }

        @Override
        public int size() {
            return 1;
        }
    }

    public static final MapCodec<BoboSoakRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
        Ingredient.CODEC_NONEMPTY.fieldOf("base").forGetter(BoboSoakRecipe::getBase),
        FlavorProfile.CODEC.fieldOf("flavor").forGetter(BoboSoakRecipe::getFlavor),
        ItemStack.CODEC.fieldOf("result").forGetter(BoboSoakRecipe::getResult),
        Codec.INT.optionalFieldOf("soak_time", 20 * 60).forGetter(BoboSoakRecipe::getSoakTime),
        Codec.INT.optionalFieldOf("uses", 64).forGetter(BoboSoakRecipe::getUses)
    ).apply(inst, BoboSoakRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BoboSoakRecipe> STREAM_CODEC =
        StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, BoboSoakRecipe::getBase,
            FlavorProfile.STREAM_CODEC, BoboSoakRecipe::getFlavor,
            ItemStack.STREAM_CODEC, BoboSoakRecipe::getResult,
            ByteBufCodecs.VAR_INT, BoboSoakRecipe::getSoakTime,
            ByteBufCodecs.VAR_INT, BoboSoakRecipe::getUses,
            BoboSoakRecipe::new);

    private final Ingredient base;
    private final FlavorProfile flavor;
    private final ItemStack result;
    private final int soakTime;
    private final int uses;

    public BoboSoakRecipe(Ingredient base, FlavorProfile flavor, ItemStack result, int soakTime, int uses) {
        this.base = base;
        this.flavor = flavor;
        this.result = result;
        this.soakTime = soakTime;
        this.uses = uses;
    }

    public Ingredient getBase() {
        return this.base;
    }

    public FlavorProfile getFlavor() {
        return this.flavor;
    }

    public ItemStack getResult() {
        return this.result;
    }

    public int getSoakTime() {
        return this.soakTime;
    }

    public int getUses() {
        return this.uses;
    }

    public boolean matchesBase(ItemStack stack) {
        return this.base.test(stack);
    }

    @Override
    public boolean matches(Input input, Level level) {
        return this.base.test(input.base());
    }

    @Override
    public ItemStack assemble(Input input, HolderLookup.Provider registries) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return this.result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, this.base);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.BOBO_SOAK_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.BOBO_SOAK_TYPE.get();
    }
}

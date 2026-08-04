package net.blay09.mods.replikaentropie.fabric.datagen;

import net.blay09.mods.replikaentropie.ReplikaEntropie;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.recipe.FragmentAcceleratorRecipe;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModFragmentAcceleratorRecipeProvider extends FabricRecipeProvider {
    private static final float REDSTONE_SPEED_MULTIPLIER = 1.1f;
    private static final float SOUL_SPEED_MULTIPLIER = 1.1f;
    private static final float GLOW_INK_SPEED_MULTIPLIER = 1.1f;
    private static final float AMETHYST_SPEED_MULTIPLIER = 1.1f;

    private static final List<Item> EXOTIC_PLANTS = List.of(
            Items.TORCHFLOWER,
            Items.WITHER_ROSE,
            Items.CRIMSON_ROOTS,
            Items.WARPED_ROOTS,
            Items.NETHER_SPROUTS,
            Items.WEEPING_VINES,
            Items.TWISTING_VINES,
            Items.NETHER_WART,
            Items.CHORUS_FLOWER,
            Items.CHORUS_PLANT,
            Items.GLOW_LICHEN,
            Items.TORCHFLOWER_SEEDS,
            Items.PITCHER_POD,
            Items.PITCHER_PLANT,
            Items.CRIMSON_FUNGUS,
            Items.WARPED_FUNGUS
    );

    private static final List<Item> MUSIC_DISCS = List.of(
            Items.MUSIC_DISC_13,
            Items.MUSIC_DISC_CAT,
            Items.MUSIC_DISC_BLOCKS,
            Items.MUSIC_DISC_CHIRP,
            Items.MUSIC_DISC_FAR,
            Items.MUSIC_DISC_MALL,
            Items.MUSIC_DISC_MELLOHI,
            Items.MUSIC_DISC_STAL,
            Items.MUSIC_DISC_STRAD,
            Items.MUSIC_DISC_WARD,
            Items.MUSIC_DISC_11,
            Items.MUSIC_DISC_WAIT,
            Items.MUSIC_DISC_OTHERSIDE,
            Items.MUSIC_DISC_RELIC,
            Items.MUSIC_DISC_5,
            Items.MUSIC_DISC_PIGSTEP
    );

    public ModFragmentAcceleratorRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        return new RecipeProvider(registries, output) {
            @Override
            public void buildRecipes() {
                EXOTIC_PLANTS.forEach(exoticPlant -> fragmentAcceleratorRecipe(exoticPlant, 1.2f).save(output));

                fragmentAcceleratorRecipe(ModItems.fragments.asItem(), 1.1f).save(output);

                fragmentAcceleratorRecipe(Items.SOUL_TORCH, SOUL_SPEED_MULTIPLIER).save(output);
                fragmentAcceleratorRecipe(Items.REDSTONE_TORCH, REDSTONE_SPEED_MULTIPLIER).save(output);
                fragmentAcceleratorRecipe(Items.SOUL_LANTERN, SOUL_SPEED_MULTIPLIER).save(output);
                fragmentAcceleratorRecipe(Items.END_ROD, 1.1f).save(output);
                fragmentAcceleratorRecipe(Items.GLOW_ITEM_FRAME, GLOW_INK_SPEED_MULTIPLIER).save(output);
                fragmentAcceleratorRecipe(Items.WITHER_SKELETON_SKULL, 2f).save(output);
                fragmentAcceleratorRecipe(Items.DRAGON_HEAD, 2f).save(output);
                fragmentAcceleratorRecipe(Items.DRAGON_EGG, 2f).save(output);
                fragmentAcceleratorRecipe(Items.ENDER_EYE, 2f).save(output);
                fragmentAcceleratorRecipe(Items.REDSTONE, REDSTONE_SPEED_MULTIPLIER).save(output);
                fragmentAcceleratorRecipe(Items.REPEATER, REDSTONE_SPEED_MULTIPLIER + 0.2f).save(output);
                fragmentAcceleratorRecipe(Items.COMPARATOR, REDSTONE_SPEED_MULTIPLIER + 0.2f).save(output);
                fragmentAcceleratorRecipe(Items.FIRE_CHARGE, 1.1f).save(output);
                fragmentAcceleratorRecipe(Items.COMPASS, REDSTONE_SPEED_MULTIPLIER).save(output);
                fragmentAcceleratorRecipe(Items.RECOVERY_COMPASS, REDSTONE_SPEED_MULTIPLIER + 0.8f).save(output);
                fragmentAcceleratorRecipe(Items.CLOCK, REDSTONE_SPEED_MULTIPLIER).save(output);
                fragmentAcceleratorRecipe(Items.SPYGLASS, AMETHYST_SPEED_MULTIPLIER).save(output);
                fragmentAcceleratorRecipe(Items.ENDER_PEARL, 1.1f).save(output);
                fragmentAcceleratorRecipe(Items.ELYTRA, 2f).save(output);
                fragmentAcceleratorRecipe(Items.WARPED_FUNGUS_ON_A_STICK, 1.1f).save(output);
                fragmentAcceleratorRecipe(Items.POWERED_RAIL, REDSTONE_SPEED_MULTIPLIER).save(output);
                fragmentAcceleratorRecipe(Items.DETECTOR_RAIL, REDSTONE_SPEED_MULTIPLIER).save(output);
                fragmentAcceleratorRecipe(Items.ACTIVATOR_RAIL, REDSTONE_SPEED_MULTIPLIER).save(output);
                fragmentAcceleratorRecipe(Items.TNT_MINECART, 1.1f).save(output);
                MUSIC_DISCS.forEach(musicDisc -> fragmentAcceleratorRecipe(musicDisc, 1.1f).save(output));
                fragmentAcceleratorRecipe(Items.TOTEM_OF_UNDYING, 2f).save(output);
                fragmentAcceleratorRecipe(Items.SPECTRAL_ARROW, 1.1f).save(output);
                fragmentAcceleratorRecipe(Items.TIPPED_ARROW, 1.1f).save(output);
                fragmentAcceleratorRecipe(Items.GLOW_BERRIES, 1.1f).save(output);
                fragmentAcceleratorRecipe(Items.CHORUS_FRUIT, 1.1f).save(output);
                fragmentAcceleratorRecipe(Items.POISONOUS_POTATO, 1.1f).save(output);
                fragmentAcceleratorRecipe(Items.PUFFERFISH, 1.1f).save(output);
                fragmentAcceleratorRecipe(Items.AMETHYST_SHARD, AMETHYST_SPEED_MULTIPLIER).save(output);
                fragmentAcceleratorRecipe(Items.GLOW_INK_SAC, 1.1f).save(output);
                fragmentAcceleratorRecipe(Items.HEART_OF_THE_SEA, 2f).save(output);
                fragmentAcceleratorRecipe(Items.BLAZE_ROD, 1.2f).save(output);
                fragmentAcceleratorRecipe(Items.NETHER_STAR, 2f).save(output);
                fragmentAcceleratorRecipe(Items.DISC_FRAGMENT_5, 1.1f).save(output);
                fragmentAcceleratorRecipe(Items.GLOWSTONE_DUST, 1.1f).save(output);
                fragmentAcceleratorRecipe(Items.GUNPOWDER, 1.1f).save(output);
                fragmentAcceleratorRecipe(Items.DRAGON_BREATH, 2f).save(output);
                fragmentAcceleratorRecipe(Items.BLAZE_POWDER, 1.1f).save(output);
                fragmentAcceleratorRecipe(Items.MAGMA_CREAM, 1.1f).save(output);
                fragmentAcceleratorRecipe(Items.GHAST_TEAR, 1.1f).save(output);
                fragmentAcceleratorRecipe(Items.EXPERIENCE_BOTTLE, 1.1f).save(output);
                fragmentAcceleratorRecipe(Items.ENCHANTED_BOOK, 1.1f).save(output);
            }
        };
    }

    private FragmentAcceleratorRecipeBuilder fragmentAcceleratorRecipe(Item ingredient, float speedMultiplier) {
        return new FragmentAcceleratorRecipeBuilder(Ingredient.of(ingredient), speedMultiplier);
    }

    @Override
    public String getName() {
        return ReplikaEntropie.MOD_ID + " Fragment Accelerator Recipes";
    }

    public record FragmentAcceleratorRecipeBuilder(Ingredient ingredient, float speedMultiplier) {
        public void save(RecipeOutput output) {
            final var id = ReplikaEntropie.id("fragment_accelerator/" + ingredientPath(ingredient));
            output.accept(ResourceKey.create(Registries.RECIPE, id), new FragmentAcceleratorRecipe(ingredient, speedMultiplier), null);
        }

        private static String ingredientPath(Ingredient ingredient) {
            return ingredient.items()
                    .findFirst()
                    .map(Holder::value)
                    .map(BuiltInRegistries.ITEM::getKey)
                    .map(Identifier::getPath)
                    .orElseThrow();
        }
    }
}

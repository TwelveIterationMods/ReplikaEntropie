package net.blay09.mods.replikaentropie.fabric.datagen;

import net.blay09.mods.replikaentropie.ReplikaEntropie;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.recipe.RecyclerRecipe;
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
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class ModRecycleRecipeProvider extends FabricRecipeProvider {
    public ModRecycleRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    private record RockSet(float cobblestoneScrap, Item stairs, Item slab, Item wall) {
    }

    private static final float RENEWABLE_STONE_SCRAP = 0.01f;
    private static final float STONE_SCRAP = 0.1f;
    private static final float EXOTIC_STONE_SCRAP = 0.2f;
    private static final float COAL_SCRAP = 0.2f;

    private static final float PLANK_BIOMASS = 0.1f;
    private static final float CHARCOAL_BIOMASS = 0.1f;
    private static final float STICK_BIOMASS = PLANK_BIOMASS * 0.5f;
    private static final float EXOTIC_PLANK_BIOMASS = 0.2f;

    private static final float WOOL_BIOMASS = 0.1f;

    private static final float REDSTONE_FRAGMENTS = 0.1f;

    private static final List<RockSet> STONES = List.of(
            new RockSet(RENEWABLE_STONE_SCRAP, Items.COBBLESTONE_STAIRS, Items.COBBLESTONE_SLAB, Items.COBBLESTONE_WALL),
            new RockSet(STONE_SCRAP, Items.MOSSY_COBBLESTONE_STAIRS, Items.MOSSY_COBBLESTONE_SLAB, Items.MOSSY_COBBLESTONE_WALL),
            new RockSet(STONE_SCRAP, Items.STONE_STAIRS, Items.STONE_SLAB, null),
            new RockSet(STONE_SCRAP, Items.STONE_BRICK_STAIRS, Items.STONE_BRICK_SLAB, Items.STONE_BRICK_SLAB),
            new RockSet(STONE_SCRAP, Items.MOSSY_STONE_BRICK_STAIRS, Items.MOSSY_STONE_BRICK_SLAB, Items.MOSSY_STONE_BRICK_SLAB),
            new RockSet(STONE_SCRAP, Items.GRANITE_STAIRS, Items.GRANITE_SLAB, Items.GRANITE_WALL),
            new RockSet(STONE_SCRAP, Items.POLISHED_GRANITE_STAIRS, Items.POLISHED_GRANITE_SLAB, null),
            new RockSet(STONE_SCRAP, Items.DIORITE_STAIRS, Items.DIORITE_SLAB, Items.DIORITE_WALL),
            new RockSet(STONE_SCRAP, Items.POLISHED_DIORITE_STAIRS, Items.POLISHED_DIORITE_SLAB, null),
            new RockSet(STONE_SCRAP, Items.ANDESITE_STAIRS, Items.ANDESITE_SLAB, Items.ANDESITE_WALL),
            new RockSet(STONE_SCRAP, Items.POLISHED_ANDESITE_STAIRS, Items.POLISHED_ANDESITE_SLAB, null),
            new RockSet(STONE_SCRAP, Items.COBBLED_DEEPSLATE_STAIRS, Items.COBBLED_DEEPSLATE_SLAB, Items.COBBLED_DEEPSLATE_WALL),
            new RockSet(STONE_SCRAP, Items.POLISHED_DEEPSLATE_STAIRS, Items.POLISHED_DEEPSLATE_SLAB, null),
            new RockSet(STONE_SCRAP, Items.DEEPSLATE_BRICK_STAIRS, Items.DEEPSLATE_BRICK_SLAB, Items.DEEPSLATE_BRICK_SLAB),
            new RockSet(STONE_SCRAP, Items.DEEPSLATE_TILE_STAIRS, Items.DEEPSLATE_TILE_SLAB, Items.DEEPSLATE_TILE_SLAB),
            new RockSet(STONE_SCRAP, Items.BRICK_STAIRS, Items.BRICK_SLAB, Items.BRICK_SLAB),
            new RockSet(STONE_SCRAP, Items.MUD_BRICK_STAIRS, Items.MUD_BRICK_SLAB, Items.MUD_BRICK_SLAB),
            new RockSet(STONE_SCRAP, Items.SANDSTONE_STAIRS, Items.SANDSTONE_SLAB, Items.SANDSTONE_WALL),
            new RockSet(STONE_SCRAP, Items.RED_SANDSTONE_STAIRS, Items.RED_SANDSTONE_SLAB, Items.RED_SANDSTONE_WALL),
            new RockSet(STONE_SCRAP, Items.SMOOTH_SANDSTONE_STAIRS, Items.SMOOTH_SANDSTONE_SLAB, null),
            new RockSet(STONE_SCRAP, Items.SMOOTH_RED_SANDSTONE_STAIRS, Items.SMOOTH_RED_SANDSTONE_SLAB, null),
            new RockSet(EXOTIC_STONE_SCRAP, Items.QUARTZ_STAIRS, Items.QUARTZ_SLAB, null),
            new RockSet(EXOTIC_STONE_SCRAP, Items.SMOOTH_QUARTZ_STAIRS, Items.SMOOTH_QUARTZ_SLAB, null),
            new RockSet(EXOTIC_STONE_SCRAP, Items.PRISMARINE_STAIRS, Items.PRISMARINE_SLAB, Items.PRISMARINE_WALL),
            new RockSet(EXOTIC_STONE_SCRAP, Items.PRISMARINE_BRICK_STAIRS, Items.PRISMARINE_BRICK_SLAB, Items.PRISMARINE_BRICK_SLAB),
            new RockSet(EXOTIC_STONE_SCRAP, Items.DARK_PRISMARINE_STAIRS, Items.DARK_PRISMARINE_SLAB, null),
            new RockSet(EXOTIC_STONE_SCRAP, Items.NETHER_BRICK_STAIRS, Items.NETHER_BRICK_SLAB, Items.NETHER_BRICK_SLAB),
            new RockSet(EXOTIC_STONE_SCRAP, Items.RED_NETHER_BRICK_STAIRS, Items.RED_NETHER_BRICK_SLAB, Items.RED_NETHER_BRICK_SLAB),
            new RockSet(EXOTIC_STONE_SCRAP, Items.BLACKSTONE_STAIRS, Items.BLACKSTONE_SLAB, Items.BLACKSTONE_WALL),
            new RockSet(EXOTIC_STONE_SCRAP, Items.POLISHED_BLACKSTONE_STAIRS, Items.POLISHED_BLACKSTONE_SLAB, Items.POLISHED_BLACKSTONE_WALL),
            new RockSet(EXOTIC_STONE_SCRAP, Items.POLISHED_BLACKSTONE_BRICK_STAIRS, Items.POLISHED_BLACKSTONE_BRICK_SLAB, Items.POLISHED_BLACKSTONE_BRICK_SLAB),
            new RockSet(EXOTIC_STONE_SCRAP, Items.END_STONE_BRICK_STAIRS, Items.END_STONE_BRICK_SLAB, Items.END_STONE_BRICK_WALL)
    );

    private record WoodSet(float plankBiomass, Item fence, Item fenceGate, Item door, Item trapDoor, Item pressurePlate,
                           Item button) {
    }

    private static final List<WoodSet> WOODS = List.of(
            new WoodSet(PLANK_BIOMASS, Items.OAK_FENCE, Items.OAK_FENCE_GATE, Items.OAK_DOOR, Items.OAK_TRAPDOOR, Items.OAK_PRESSURE_PLATE, Items.OAK_BUTTON),
            new WoodSet(PLANK_BIOMASS, Items.SPRUCE_FENCE, Items.SPRUCE_FENCE_GATE, Items.SPRUCE_DOOR, Items.SPRUCE_TRAPDOOR, Items.SPRUCE_PRESSURE_PLATE, Items.SPRUCE_BUTTON),
            new WoodSet(PLANK_BIOMASS, Items.BIRCH_FENCE, Items.BIRCH_FENCE_GATE, Items.BIRCH_DOOR, Items.BIRCH_TRAPDOOR, Items.BIRCH_PRESSURE_PLATE, Items.BIRCH_BUTTON),
            new WoodSet(PLANK_BIOMASS, Items.JUNGLE_FENCE, Items.JUNGLE_FENCE_GATE, Items.JUNGLE_DOOR, Items.JUNGLE_TRAPDOOR, Items.JUNGLE_PRESSURE_PLATE, Items.JUNGLE_BUTTON),
            new WoodSet(PLANK_BIOMASS, Items.ACACIA_FENCE, Items.ACACIA_FENCE_GATE, Items.ACACIA_DOOR, Items.ACACIA_TRAPDOOR, Items.ACACIA_PRESSURE_PLATE, Items.ACACIA_BUTTON),
            new WoodSet(PLANK_BIOMASS, Items.DARK_OAK_FENCE, Items.DARK_OAK_FENCE_GATE, Items.DARK_OAK_DOOR, Items.DARK_OAK_TRAPDOOR, Items.DARK_OAK_PRESSURE_PLATE, Items.DARK_OAK_BUTTON),
            new WoodSet(EXOTIC_PLANK_BIOMASS, Items.CRIMSON_FENCE, Items.CRIMSON_FENCE_GATE, Items.CRIMSON_DOOR, Items.CRIMSON_TRAPDOOR, Items.CRIMSON_PRESSURE_PLATE, Items.CRIMSON_BUTTON),
            new WoodSet(EXOTIC_PLANK_BIOMASS, Items.WARPED_FENCE, Items.WARPED_FENCE_GATE, Items.WARPED_DOOR, Items.WARPED_TRAPDOOR, Items.WARPED_PRESSURE_PLATE, Items.WARPED_BUTTON),
            new WoodSet(PLANK_BIOMASS, Items.MANGROVE_FENCE, Items.MANGROVE_FENCE_GATE, Items.MANGROVE_DOOR, Items.MANGROVE_TRAPDOOR, Items.MANGROVE_PRESSURE_PLATE, Items.MANGROVE_BUTTON),
            new WoodSet(PLANK_BIOMASS, Items.BAMBOO_FENCE, Items.BAMBOO_FENCE_GATE, Items.BAMBOO_DOOR, Items.BAMBOO_TRAPDOOR, Items.BAMBOO_PRESSURE_PLATE, Items.BAMBOO_BUTTON),
            new WoodSet(PLANK_BIOMASS, Items.CHERRY_FENCE, Items.CHERRY_FENCE_GATE, Items.CHERRY_DOOR, Items.CHERRY_TRAPDOOR, Items.CHERRY_PRESSURE_PLATE, Items.CHERRY_BUTTON)
    );

    private static final List<Item> CARPETS = Items.CARPET.asList();
    private static final List<Item> BEDS = Items.BED.asList();
    private static final List<Item> CANDLES = Stream.concat(Stream.of(Items.CANDLE), Items.DYED_CANDLE.asList().stream()).toList();
    private static final List<Item> BANNERS = Items.BANNER.asList();

    private static final List<Item> SAPLINGS = List.of(
            Items.OAK_SAPLING,
            Items.SPRUCE_SAPLING,
            Items.BIRCH_SAPLING,
            Items.JUNGLE_SAPLING,
            Items.ACACIA_SAPLING,
            Items.DARK_OAK_SAPLING,
            Items.CHERRY_SAPLING,
            Items.MANGROVE_PROPAGULE
    );

    private static final List<Item> PLANTS = List.of(
            Items.SHORT_GRASS,
            Items.FERN,
            Items.DEAD_BUSH,
            Items.VINE,
            Items.SUGAR_CANE,
            Items.TALL_GRASS,
            Items.LARGE_FERN,
            Items.BIG_DRIPLEAF,
            Items.SMALL_DRIPLEAF,
            Items.CACTUS,
            Items.HANGING_ROOTS,
            Items.LILY_PAD,
            Items.WHEAT_SEEDS,
            Items.COCOA_BEANS,
            Items.PUMPKIN_SEEDS,
            Items.MELON_SEEDS,
            Items.BEETROOT_SEEDS,
            Items.BROWN_MUSHROOM,
            Items.RED_MUSHROOM
    );

    private static final List<Item> OCEAN_PLANTS = List.of(
            Items.SEAGRASS,
            Items.SEA_PICKLE,
            Items.KELP,
            Items.TUBE_CORAL,
            Items.BRAIN_CORAL,
            Items.BUBBLE_CORAL,
            Items.FIRE_CORAL,
            Items.HORN_CORAL,
            Items.DEAD_TUBE_CORAL,
            Items.DEAD_BRAIN_CORAL,
            Items.DEAD_BUBBLE_CORAL,
            Items.DEAD_FIRE_CORAL,
            Items.DEAD_HORN_CORAL,
            Items.DEAD_TUBE_CORAL_FAN,
            Items.DEAD_BRAIN_CORAL_FAN,
            Items.DEAD_BUBBLE_CORAL_FAN,
            Items.DEAD_FIRE_CORAL_FAN,
            Items.DEAD_HORN_CORAL_FAN,
            Items.TUBE_CORAL_FAN,
            Items.BRAIN_CORAL_FAN,
            Items.BUBBLE_CORAL_FAN,
            Items.FIRE_CORAL_FAN,
            Items.HORN_CORAL_FAN
    );

    private static final List<Item> FLOWERS = List.of(
            Items.DANDELION,
            Items.POPPY,
            Items.BLUE_ORCHID,
            Items.ALLIUM,
            Items.AZURE_BLUET,
            Items.RED_TULIP,
            Items.ORANGE_TULIP,
            Items.WHITE_TULIP,
            Items.PINK_TULIP,
            Items.OXEYE_DAISY,
            Items.CORNFLOWER,
            Items.LILY_OF_THE_VALLEY,
            Items.PINK_PETALS,
            Items.SPORE_BLOSSOM,
            Items.SUNFLOWER,
            Items.LILAC,
            Items.ROSE_BUSH,
            Items.PEONY
    );

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

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        return new RecipeProvider(registries, output) {
            @Override
            public void buildRecipes() {
        // Building Blocks / Scrap
        // for (final var stone : STONES) {
        //     final var cobblestoneScrap = 0.01f;
        //     recycleRecipe(stone.stairs, (cobblestoneScrap * 6) / 4f, 0f, 0f).save(output);
        //     recycleRecipe(stone.slab, (cobblestoneScrap * 3) / 6f, 0f, 0f).save(output);
        //     if (stone.wall != null) {
        //         recycleRecipe(stone.wall, (cobblestoneScrap * 6) / 6f, 0f, 0f).save(output);
        //     }
        // }

        final var IRON_INGOT_SCRAP = 0.1f;
        final var IRON_NUGGET_SCRAP = IRON_INGOT_SCRAP / 9f;
        recycleRecipe(Items.IRON_BARS, (IRON_INGOT_SCRAP * 6) / 16f, 0f, 0f).save(output);
        recycleRecipe(Items.IRON_DOOR, (IRON_INGOT_SCRAP * 6) / 3f, 0f, 0f).save(output);
        recycleRecipe(Items.IRON_TRAPDOOR, IRON_INGOT_SCRAP * 4, 0f, 0f).save(output);
        recycleRecipe(Items.IRON_CHAIN, IRON_INGOT_SCRAP + IRON_NUGGET_SCRAP * 2, 0f, 0f).save(output);
        recycleRecipe(Items.HEAVY_WEIGHTED_PRESSURE_PLATE, IRON_INGOT_SCRAP * 2, 0f, 0f).save(output);

        final var GOLD_INGOT_SCRAP = 0.1f;
        final var GOLD_NUGGET_SCRAP = GOLD_INGOT_SCRAP / 9f;
        recycleRecipe(Items.LIGHT_WEIGHTED_PRESSURE_PLATE, GOLD_INGOT_SCRAP * 2, 0f, 0f).save(output);

        recycleRecipe(Items.SEA_LANTERN, 9f, 0f, 0f).save(output);

        // Building Blocks / Biomass
        for (final var wood : WOODS) {
            final var plankBiomass = wood.plankBiomass;
            final var stickBiomass = plankBiomass * 0.5f;
            // recycleRecipe(wood.fence, 0f, (plankBiomass * 4 + stickBiomass * 2) / 3f, 0f).save(output);
            // recycleRecipe(wood.fenceGate, 0f, plankBiomass * 2 + stickBiomass * 4, 0f).save(output);
            // recycleRecipe(wood.door, 0f, (plankBiomass * 6) / 3f, 0f).save(output);
            // recycleRecipe(wood.trapDoor, 0f, (plankBiomass * 6) / 2f, 0f).save(output);
            // recycleRecipe(wood.pressurePlate, 0f, plankBiomass * 2, 0f).save(output);
            // recycleRecipe(wood.button, 0f, plankBiomass, 0f).save(output);
        }

        // Colored Blocks / Biomass
        CARPETS.forEach(bed -> recycleRecipe(bed, 0f, WOOL_BIOMASS * 2, 0f).save(output));
        BEDS.forEach(bed -> recycleRecipe(bed, 0f, (PLANK_BIOMASS * 3 + WOOL_BIOMASS * 3), 0f).save(output));
        BANNERS.forEach(bed -> recycleRecipe(bed, 0f, (STICK_BIOMASS + WOOL_BIOMASS * 6), 0f).save(output));

        final var stringBiomass = 0.1f;
        final var honeyBiomass = 0.1f;
        CANDLES.forEach(candle -> recycleRecipe(candle, 0f, stringBiomass + honeyBiomass, 0f).save(output));

        // Natural Blocks
        SAPLINGS.forEach(sapling -> recycleRecipe(sapling, 0f, 0.1f, 0f).save(output));
        PLANTS.forEach(plant -> recycleRecipe(plant, 0f, 0.1f, 0f).save(output));
        FLOWERS.forEach(flowers -> recycleRecipe(flowers, 0f, 0.1f, 0f).save(output));
        EXOTIC_PLANTS.forEach(exoticPlant -> recycleRecipe(exoticPlant, 0f, 0.2f, 0.2f).save(output));
        OCEAN_PLANTS.forEach(oceanPlant -> recycleRecipe(oceanPlant, 0f, 0.2f, 0f).save(output));
        recycleRecipe(Items.TURTLE_EGG, 0f, 1f, 0f).save(output);
        recycleRecipe(Items.SNIFFER_EGG, 0f, 2f, 0f).save(output);

        // Functional Blocks
        final var SOUL_FRAGMENTS = 0.1f;
        recycleRecipe(Items.TORCH, COAL_SCRAP / 4f, STICK_BIOMASS / 4f, 0f).save(output);
        recycleRecipe(Items.SOUL_TORCH, COAL_SCRAP / 4f, STICK_BIOMASS / 4f, SOUL_FRAGMENTS).save(output);
        recycleRecipe(Items.REDSTONE_TORCH, 0f, STICK_BIOMASS / 4f, REDSTONE_FRAGMENTS).save(output);
        recycleRecipe(Items.LANTERN, COAL_SCRAP / 4f + IRON_NUGGET_SCRAP * 9, STICK_BIOMASS / 4f, 0f).save(output);
        recycleRecipe(Items.SOUL_LANTERN, COAL_SCRAP / 4f + IRON_NUGGET_SCRAP * 9, STICK_BIOMASS / 4f, SOUL_FRAGMENTS).save(output);
        recycleRecipe(Items.END_ROD, 0f, 0.2f, 0.1f).save(output);
        recycleRecipe(Items.BELL, 12f, 0f, 0f).save(output);
        recycleRecipe(Items.CONDUIT, 0f, 12f, 0f).save(output);
        final var COPPER_INGOT_SCRAP = 0.05f;
        recycleRecipe(Items.LIGHTNING_ROD.weathering().unaffected(), COPPER_INGOT_SCRAP * 3, 0f, 0f).save(output);
        final var clayScrap = 0.1f;
        recycleRecipe(Items.FLOWER_POT, clayScrap * 3, 0f, 0f).save(output);
        recycleRecipe(Items.LADDER, 0f, STICK_BIOMASS * 7, 0f).save(output);
        recycleRecipe(Items.ARMOR_STAND, 0f, STICK_BIOMASS * 6, 0f).save(output);
        recycleRecipe(Items.ITEM_FRAME, 0f, STICK_BIOMASS * 6, 0f).save(output);
        final var LEATHER_BIOMASS = 0.1f;
        final var GLOW_INK_BIOMASS = 0.1f;
        final var GLOW_INK_FRAGMENTS = 0.1f;
        recycleRecipe(Items.GLOW_ITEM_FRAME, 0f, STICK_BIOMASS * 8 + LEATHER_BIOMASS + GLOW_INK_BIOMASS, GLOW_INK_FRAGMENTS).save(output);
        recycleRecipe(Items.PAINTING, 0f, STICK_BIOMASS * 8 + WOOL_BIOMASS, 0f).save(output);
        recycleRecipe(Items.SKELETON_SKULL, 0f, 1f, 0f).save(output);
        recycleRecipe(Items.WITHER_SKELETON_SKULL, 0f, 1f, 1f).save(output);
        recycleRecipe(Items.ZOMBIE_HEAD, 0f, 1f, 0f).save(output);
        recycleRecipe(Items.PLAYER_HEAD, 0f, 1f, 0f).save(output);
        recycleRecipe(Items.CREEPER_HEAD, 0f, 1f, 0f).save(output);
        recycleRecipe(Items.PIGLIN_HEAD, 0f, 1f, 0f).save(output);
        recycleRecipe(Items.DRAGON_HEAD, 0f, 1f, 1f).save(output);
        recycleRecipe(Items.DRAGON_EGG, 0f, 1f, 1f).save(output);
        recycleRecipe(Items.ENDER_EYE, 0f, 1.1f, 1f).save(output);

        // Redstone Blocks
        final var QUARTZ_SCRAP = 0.1f;
        recycleRecipe(Items.REDSTONE, 0f, 0f, REDSTONE_FRAGMENTS).save(output);
        recycleRecipe(Items.REPEATER, 0f, STICK_BIOMASS * 2, REDSTONE_FRAGMENTS * 3).save(output);
        recycleRecipe(Items.COMPARATOR, QUARTZ_SCRAP, STICK_BIOMASS * 3, REDSTONE_FRAGMENTS * 3).save(output);
        recycleRecipe(Items.TRIPWIRE_HOOK, IRON_INGOT_SCRAP, PLANK_BIOMASS + STICK_BIOMASS, 0f).save(output);
        final var STRING_BIOMASS = 0.1f;
        recycleRecipe(Items.STRING, 0f, STRING_BIOMASS, 0f).save(output);

        // Tools & Utilities
        recycleRecipe(Items.WOODEN_SHOVEL, 0f, STICK_BIOMASS * 2 + PLANK_BIOMASS, 0f).save(output);
        recycleRecipe(Items.WOODEN_PICKAXE, 0f, STICK_BIOMASS * 2 + PLANK_BIOMASS * 3, 0f).save(output);
        recycleRecipe(Items.WOODEN_AXE, 0f, STICK_BIOMASS * 2 + PLANK_BIOMASS * 3, 0f).save(output);
        recycleRecipe(Items.WOODEN_HOE, 0f, STICK_BIOMASS * 2 + PLANK_BIOMASS * 2, 0f).save(output);
        recycleRecipe(Items.STONE_SHOVEL, 0f, STICK_BIOMASS * 2, 0f).save(output);
        recycleRecipe(Items.STONE_PICKAXE, 0f, STICK_BIOMASS * 2, 0f).save(output);
        recycleRecipe(Items.STONE_AXE, 0f, STICK_BIOMASS * 2, 0f).save(output);
        recycleRecipe(Items.STONE_HOE, 0f, STICK_BIOMASS * 2, 0f).save(output);
        recycleRecipe(Items.IRON_SHOVEL, IRON_INGOT_SCRAP, STICK_BIOMASS * 2, 0f).save(output);
        recycleRecipe(Items.IRON_PICKAXE, IRON_INGOT_SCRAP * 3, STICK_BIOMASS * 2, 0f).save(output);
        recycleRecipe(Items.IRON_AXE, IRON_INGOT_SCRAP * 3, STICK_BIOMASS * 2, 0f).save(output);
        recycleRecipe(Items.IRON_HOE, IRON_INGOT_SCRAP * 2, STICK_BIOMASS * 2, 0f).save(output);
        recycleRecipe(Items.GOLDEN_SHOVEL, GOLD_INGOT_SCRAP, STICK_BIOMASS * 2, 0f).save(output);
        recycleRecipe(Items.GOLDEN_PICKAXE, GOLD_INGOT_SCRAP * 3, STICK_BIOMASS * 2, 0f).save(output);
        recycleRecipe(Items.GOLDEN_AXE, GOLD_INGOT_SCRAP * 3, STICK_BIOMASS * 2, 0f).save(output);
        recycleRecipe(Items.GOLDEN_HOE, GOLD_INGOT_SCRAP * 2, STICK_BIOMASS * 2, 0f).save(output);
        final var DIAMOND_SCRAP = 1f;
        recycleRecipe(Items.DIAMOND_SHOVEL, DIAMOND_SCRAP, STICK_BIOMASS * 2, 0f).save(output);
        recycleRecipe(Items.DIAMOND_PICKAXE, DIAMOND_SCRAP * 3, STICK_BIOMASS * 2, 0f).save(output);
        recycleRecipe(Items.DIAMOND_AXE, DIAMOND_SCRAP * 3, STICK_BIOMASS * 2, 0f).save(output);
        recycleRecipe(Items.DIAMOND_HOE, DIAMOND_SCRAP * 2, STICK_BIOMASS * 2, 0f).save(output);
        final var NETHERITE_SCRAP = 0.1f;
        final var NETHERITE_INGOT_SCRAP = NETHERITE_SCRAP * 4 + GOLD_INGOT_SCRAP * 4;
        recycleRecipe(Items.NETHERITE_SHOVEL, NETHERITE_INGOT_SCRAP, STICK_BIOMASS * 2, 0f).save(output);
        recycleRecipe(Items.NETHERITE_PICKAXE, NETHERITE_INGOT_SCRAP * 3, STICK_BIOMASS * 2, 0f).save(output);
        recycleRecipe(Items.NETHERITE_AXE, NETHERITE_INGOT_SCRAP * 3, STICK_BIOMASS * 2, 0f).save(output);
        recycleRecipe(Items.NETHERITE_HOE, NETHERITE_INGOT_SCRAP * 2, STICK_BIOMASS * 2, 0f).save(output);
        recycleRecipe(Items.BUCKET, IRON_INGOT_SCRAP * 3, 0f, 0f).save(output);
        recycleRecipe(Items.FISHING_ROD, 0f, STICK_BIOMASS * 3 + STRING_BIOMASS * 2, 0f).save(output);
        final var FLINT_SCRAP = 0.1f;
        recycleRecipe(Items.FLINT_AND_STEEL, IRON_INGOT_SCRAP + FLINT_SCRAP, 0f, 0f).save(output);
        recycleRecipe(Items.FIRE_CHARGE, COAL_SCRAP, 0.1f, 0.1f).save(output);
        recycleRecipe(Items.BONE_MEAL, 0f, 0.1f, 0f).save(output);
        recycleRecipe(Items.SHEARS, IRON_INGOT_SCRAP, 0f, 0f).save(output);
        recycleRecipe(Items.BRUSH, COPPER_INGOT_SCRAP, STICK_BIOMASS + 0.1f, 0f).save(output);
        recycleRecipe(Items.NAME_TAG, 1f, 0f, 0f).save(output);
        recycleRecipe(Items.LEAD, 0f, STRING_BIOMASS * 4 + 0.2f, 0f).save(output);
        recycleRecipe(Items.COMPASS, IRON_INGOT_SCRAP * 4, 0f, REDSTONE_FRAGMENTS).save(output);
        recycleRecipe(Items.RECOVERY_COMPASS, IRON_INGOT_SCRAP * 4, 0f, REDSTONE_FRAGMENTS + 0.8f).save(output);
        recycleRecipe(Items.CLOCK, GOLD_INGOT_SCRAP * 4, 0f, REDSTONE_FRAGMENTS).save(output);
        final var AMETHYST_FRAGMENTS = 0.1f;
        recycleRecipe(Items.SPYGLASS, COPPER_INGOT_SCRAP * 2, 0f, AMETHYST_FRAGMENTS).save(output);
        recycleRecipe(Items.ENDER_PEARL, 0f, 0f, 0.1f).save(output);
        recycleRecipe(Items.ELYTRA, 1f, 1f, 1f).save(output);
        recycleRecipe(Items.CARROT_ON_A_STICK, 0f, STRING_BIOMASS * 2 + 0.1f, 0f).save(output);
        recycleRecipe(Items.WARPED_FUNGUS_ON_A_STICK, 0f, STRING_BIOMASS * 2 + 0.1f, 0.1f).save(output);
        recycleRecipe(Items.RAIL, IRON_INGOT_SCRAP * 6, 0f, 0f).save(output);
        recycleRecipe(Items.POWERED_RAIL, GOLD_INGOT_SCRAP * 6, 0f, REDSTONE_FRAGMENTS).save(output);
        recycleRecipe(Items.DETECTOR_RAIL, IRON_INGOT_SCRAP * 6, 0f, REDSTONE_FRAGMENTS).save(output);
        recycleRecipe(Items.ACTIVATOR_RAIL, IRON_INGOT_SCRAP * 6, 0f, REDSTONE_FRAGMENTS).save(output);
        recycleRecipe(Items.MINECART, IRON_INGOT_SCRAP * 5, 0f, 0f).save(output);
        recycleRecipe(Items.HOPPER_MINECART, IRON_INGOT_SCRAP * 5 + IRON_INGOT_SCRAP * 5, 0f, 0f).save(output);
        recycleRecipe(Items.TNT_MINECART, IRON_INGOT_SCRAP * 5, 0f, 0.1f).save(output);
        recycleRecipe(Items.GOAT_HORN, 0f, 1f, 0f).save(output);
        for (final var musicDisc : MUSIC_DISCS) {
            recycleRecipe(musicDisc, 1f, 0f, 0.1f).save(output);
        }

        // Combat
        recycleRecipe(Items.WOODEN_SWORD, 0f, STICK_BIOMASS + PLANK_BIOMASS + 2, 0f).save(output);
        recycleRecipe(Items.STONE_SWORD, 0f, STICK_BIOMASS, 0f).save(output);
        recycleRecipe(Items.IRON_SWORD, IRON_INGOT_SCRAP * 2, STICK_BIOMASS, 0f).save(output);
        recycleRecipe(Items.GOLDEN_SWORD, GOLD_INGOT_SCRAP * 2, STICK_BIOMASS, 0f).save(output);
        recycleRecipe(Items.DIAMOND_SWORD, DIAMOND_SCRAP * 2, STICK_BIOMASS, 0f).save(output);
        recycleRecipe(Items.NETHERITE_SWORD, NETHERITE_INGOT_SCRAP * 2, STICK_BIOMASS, 0f).save(output);
        recycleRecipe(Items.TRIDENT, 1f, 0f, 0f).save(output);
        recycleRecipe(Items.SHIELD, IRON_INGOT_SCRAP, PLANK_BIOMASS * 6, 0f).save(output);
        recycleRecipe(Items.LEATHER_HELMET, 0f, LEATHER_BIOMASS * 5, 0f).save(output);
        recycleRecipe(Items.LEATHER_CHESTPLATE, 0f, LEATHER_BIOMASS * 8, 0f).save(output);
        recycleRecipe(Items.LEATHER_LEGGINGS, 0f, LEATHER_BIOMASS * 7, 0f).save(output);
        recycleRecipe(Items.LEATHER_BOOTS, 0f, LEATHER_BIOMASS * 4, 0f).save(output);
        recycleRecipe(Items.CHAINMAIL_HELMET, IRON_INGOT_SCRAP * 5, 0f, 0f).save(output);
        recycleRecipe(Items.CHAINMAIL_CHESTPLATE, IRON_INGOT_SCRAP * 8, 0f, 0f).save(output);
        recycleRecipe(Items.CHAINMAIL_LEGGINGS, IRON_INGOT_SCRAP * 7, 0f, 0f).save(output);
        recycleRecipe(Items.CHAINMAIL_BOOTS, IRON_INGOT_SCRAP * 4, 0f, 0f).save(output);
        recycleRecipe(Items.IRON_HELMET, IRON_INGOT_SCRAP * 5, 0f, 0f).save(output);
        recycleRecipe(Items.IRON_CHESTPLATE, IRON_INGOT_SCRAP * 8, 0f, 0f).save(output);
        recycleRecipe(Items.IRON_LEGGINGS, IRON_INGOT_SCRAP * 7, 0f, 0f).save(output);
        recycleRecipe(Items.IRON_BOOTS, IRON_INGOT_SCRAP * 4, 0f, 0f).save(output);
        recycleRecipe(Items.GOLDEN_HELMET, GOLD_INGOT_SCRAP * 5, 0f, 0f).save(output);
        recycleRecipe(Items.GOLDEN_CHESTPLATE, GOLD_INGOT_SCRAP * 8, 0f, 0f).save(output);
        recycleRecipe(Items.GOLDEN_LEGGINGS, GOLD_INGOT_SCRAP * 7, 0f, 0f).save(output);
        recycleRecipe(Items.GOLDEN_BOOTS, GOLD_INGOT_SCRAP * 4, 0f, 0f).save(output);
        recycleRecipe(Items.DIAMOND_HELMET, DIAMOND_SCRAP * 5, 0f, 0f).save(output);
        recycleRecipe(Items.DIAMOND_CHESTPLATE, DIAMOND_SCRAP * 8, 0f, 0f).save(output);
        recycleRecipe(Items.DIAMOND_LEGGINGS, DIAMOND_SCRAP * 7, 0f, 0f).save(output);
        recycleRecipe(Items.DIAMOND_BOOTS, DIAMOND_SCRAP * 4, 0f, 0f).save(output);
        recycleRecipe(Items.NETHERITE_HELMET, NETHERITE_INGOT_SCRAP * 5, 0f, 0f).save(output);
        recycleRecipe(Items.NETHERITE_CHESTPLATE, NETHERITE_INGOT_SCRAP * 8, 0f, 0f).save(output);
        recycleRecipe(Items.NETHERITE_LEGGINGS, NETHERITE_INGOT_SCRAP * 7, 0f, 0f).save(output);
        recycleRecipe(Items.NETHERITE_BOOTS, NETHERITE_INGOT_SCRAP * 4, 0f, 0f).save(output);
        recycleRecipe(Items.TURTLE_HELMET, 0f, 0.5f, 0f).save(output);
        recycleRecipe(Items.LEATHER_HORSE_ARMOR, 0f, LEATHER_BIOMASS * 7, 0f).save(output);
        recycleRecipe(Items.IRON_HORSE_ARMOR, 0f, IRON_INGOT_SCRAP * 7, 0f).save(output);
        recycleRecipe(Items.GOLDEN_HORSE_ARMOR, 0f, GOLD_INGOT_SCRAP * 7, 0f).save(output);
        recycleRecipe(Items.DIAMOND_HORSE_ARMOR, 0f, DIAMOND_SCRAP * 7, 0f).save(output);
        recycleRecipe(Items.TOTEM_OF_UNDYING, 1f, 0f, 1f).save(output);
        recycleRecipe(Items.BOW, 0f, STICK_BIOMASS * 3 + STRING_BIOMASS * 3, 0f).save(output);
        recycleRecipe(Items.CROSSBOW, IRON_INGOT_SCRAP * 2, PLANK_BIOMASS + STICK_BIOMASS * 4 + STRING_BIOMASS * 3, 0f).save(output);
        recycleRecipe(Items.ARROW, FLINT_SCRAP, STICK_BIOMASS + 0.1f, 0f).save(output);
        recycleRecipe(Items.SPECTRAL_ARROW, FLINT_SCRAP, STICK_BIOMASS + 0.1f, 0.1f).save(output);
        recycleRecipe(Items.TIPPED_ARROW, FLINT_SCRAP, STICK_BIOMASS + 0.1f, 0.1f).save(output);

        // Food & Drinks
        final var FRUIT_BIOMASS = 0.1f;
        final var MEAT_BIOMASS = 0.2f;
        final var FISH_BIOMASS = 0.3f;
        recycleRecipe(Items.APPLE, 0f, FRUIT_BIOMASS, 0f).save(output);
        recycleRecipe(Items.GOLDEN_APPLE, GOLD_INGOT_SCRAP * 8, 0.1f, 0f).save(output);
        recycleRecipe(Items.ENCHANTED_GOLDEN_APPLE, GOLD_INGOT_SCRAP * 8, 0.1f, 0f).save(output);
        recycleRecipe(Items.MELON_SLICE, 0f, FRUIT_BIOMASS, 0f).save(output);
        recycleRecipe(Items.SWEET_BERRIES, 0f, FRUIT_BIOMASS, 0f).save(output);
        recycleRecipe(Items.GLOW_BERRIES, 0f, FRUIT_BIOMASS, 0.1f).save(output);
        recycleRecipe(Items.CHORUS_FRUIT, 0f, FRUIT_BIOMASS, 0.1f).save(output);
        recycleRecipe(Items.CARROT, 0f, FRUIT_BIOMASS, 0f).save(output);
        recycleRecipe(Items.GOLDEN_CARROT, GOLD_NUGGET_SCRAP * 8, FRUIT_BIOMASS, 0f).save(output);
        recycleRecipe(Items.POTATO, 0f, FRUIT_BIOMASS, 0f).save(output);
        recycleRecipe(Items.POISONOUS_POTATO, 0f, FRUIT_BIOMASS, 0.1f).save(output);
        recycleRecipe(Items.BEETROOT, 0f, FRUIT_BIOMASS, 0f).save(output);
        recycleRecipe(Items.DRIED_KELP, 0f, FRUIT_BIOMASS, 0f).save(output);
        recycleRecipe(Items.BEEF, 0f, MEAT_BIOMASS, 0f).save(output);
        recycleRecipe(Items.COOKED_BEEF, 0f, MEAT_BIOMASS, 0f).save(output);
        recycleRecipe(Items.PORKCHOP, 0f, MEAT_BIOMASS, 0f).save(output);
        recycleRecipe(Items.COOKED_PORKCHOP, 0f, MEAT_BIOMASS, 0f).save(output);
        recycleRecipe(Items.MUTTON, 0f, MEAT_BIOMASS, 0f).save(output);
        recycleRecipe(Items.COOKED_MUTTON, 0f, MEAT_BIOMASS, 0f).save(output);
        recycleRecipe(Items.CHICKEN, 0f, MEAT_BIOMASS, 0f).save(output);
        recycleRecipe(Items.COOKED_CHICKEN, 0f, MEAT_BIOMASS, 0f).save(output);
        recycleRecipe(Items.RABBIT, 0f, MEAT_BIOMASS, 0f).save(output);
        recycleRecipe(Items.COOKED_RABBIT, 0f, MEAT_BIOMASS, 0f).save(output);
        recycleRecipe(Items.SALMON, 0f, FISH_BIOMASS, 0f).save(output);
        recycleRecipe(Items.COOKED_SALMON, 0f, FISH_BIOMASS, 0f).save(output);
        recycleRecipe(Items.COD, 0f, FISH_BIOMASS, 0f).save(output);
        recycleRecipe(Items.COOKED_COD, 0f, FISH_BIOMASS, 0f).save(output);
        recycleRecipe(Items.TROPICAL_FISH, 0f, FISH_BIOMASS, 0f).save(output);
        recycleRecipe(Items.PUFFERFISH, 0f, FISH_BIOMASS, 0.1f).save(output);
        recycleRecipe(Items.BREAD, 0f, FRUIT_BIOMASS * 3, 0f).save(output);
        recycleRecipe(Items.COOKIE, 0f, FRUIT_BIOMASS * 3, 0f).save(output);
        recycleRecipe(Items.CAKE, 0f, FRUIT_BIOMASS * 4, 0f).save(output);
        recycleRecipe(Items.PUMPKIN_PIE, 0f, FRUIT_BIOMASS * 2, 0f).save(output);
        recycleRecipe(Items.ROTTEN_FLESH, 0f, MEAT_BIOMASS, 0f).save(output);
        recycleRecipe(Items.SPIDER_EYE, 0f, MEAT_BIOMASS, 0f).save(output);
        recycleRecipe(Items.MUSHROOM_STEM, 0f, FRUIT_BIOMASS * 2, 0f).save(output);
        recycleRecipe(Items.BEETROOT_SOUP, 0f, FRUIT_BIOMASS * 6, 0f).save(output);
        recycleRecipe(Items.RABBIT_STEW, 0f, FRUIT_BIOMASS * 3 + MEAT_BIOMASS, 0f).save(output);
        recycleRecipe(Items.SUSPICIOUS_STEW, 0f, FRUIT_BIOMASS * 3, 0f).save(output);

        // Ingredients
        recycleRecipe(Items.CHARCOAL, 0f, CHARCOAL_BIOMASS, 0f).save(output);
        final var EMERALD_SCRAP = 1f;
        recycleRecipe(Items.EMERALD, EMERALD_SCRAP, 0f, 0f).save(output);
        final var LAPIS_SCRAP = 0.1f;
        recycleRecipe(Items.LAPIS_LAZULI, LAPIS_SCRAP, 0f, 0f).save(output);
        recycleRecipe(Items.DIAMOND, DIAMOND_SCRAP, 0f, 0f).save(output);
        recycleRecipe(Items.QUARTZ, QUARTZ_SCRAP, 0f, 0f).save(output);
        recycleRecipe(Items.AMETHYST_SHARD, 0f, 0f, AMETHYST_FRAGMENTS).save(output);
        recycleRecipe(Items.IRON_NUGGET, IRON_NUGGET_SCRAP, 0f, 0f).save(output);
        recycleRecipe(Items.GOLD_NUGGET, GOLD_NUGGET_SCRAP, 0f, 0f).save(output);
        recycleRecipe(Items.IRON_INGOT, IRON_INGOT_SCRAP, 0f, 0f).save(output);
        recycleRecipe(Items.COPPER_INGOT, COPPER_INGOT_SCRAP, 0f, 0f).save(output);
        recycleRecipe(Items.GOLD_INGOT, GOLD_INGOT_SCRAP, 0f, 0f).save(output);
        recycleRecipe(Items.NETHERITE_SCRAP, 0.1f, 0f, 0f).save(output);
        recycleRecipe(Items.NETHERITE_INGOT, NETHERITE_INGOT_SCRAP, 0f, 0f).save(output);
        recycleRecipe(Items.STICK, 0f, STICK_BIOMASS, 0f).save(output);
        recycleRecipe(Items.FLINT, FLINT_SCRAP, 0f, 0f).save(output);
        recycleRecipe(Items.WHEAT, 0f, 0.1f, 0f).save(output);
        recycleRecipe(Items.BONE, 0f, 0.1f, 0f).save(output);
        recycleRecipe(Items.FEATHER, 0f, 0.1f, 0f).save(output);
        recycleRecipe(Items.EGG, 0f, 0.1f, 0f).save(output);
        recycleRecipe(Items.LEATHER, 0f, LEATHER_BIOMASS, 0f).save(output);
        recycleRecipe(Items.RABBIT_HIDE, 0f, LEATHER_BIOMASS, 0f).save(output);
        recycleRecipe(Items.HONEYCOMB, 0f, 0.1f, 0f).save(output);
        recycleRecipe(Items.INK_SAC, 0f, 0.1f, 0f).save(output);
        recycleRecipe(Items.GLOW_INK_SAC, 0f, 0.1f, 0.1f).save(output);
        recycleRecipe(Items.TURTLE_SCUTE, 0f, 0.1f, 0f).save(output);
        recycleRecipe(Items.SLIME_BALL, 0f, 0.1f, 0f).save(output);
        recycleRecipe(Items.CLAY, 0.1f, 0f, 0f).save(output);
        recycleRecipe(Items.PRISMARINE_SHARD, 0.1f, 0f, 0f).save(output);
        recycleRecipe(Items.PRISMARINE_CRYSTALS, 0.1f, 0f, 0f).save(output);
        recycleRecipe(Items.NAUTILUS_SHELL, 0f, 0.1f, 0f).save(output);
        recycleRecipe(Items.HEART_OF_THE_SEA, 0f, 1f, 1f).save(output);
        recycleRecipe(Items.BLAZE_ROD, 0f, 0f, 0.2f).save(output);
        recycleRecipe(Items.NETHER_STAR, 0f, 0f, 1f).save(output);
        recycleRecipe(Items.DISC_FRAGMENT_5, 0.1f, 0f, 0.1f).save(output);
        recycleRecipe(Items.BOWL, 0f, PLANK_BIOMASS * 3 / 4f, 0f).save(output);
        recycleRecipe(Items.BRICK, 0.1f, 0f, 0f).save(output);
        recycleRecipe(Items.NETHER_BRICK, 0.1f, 0f, 0f).save(output);
        recycleRecipe(Items.PAPER, 0f, 0.1f, 0f).save(output);
        recycleRecipe(Items.BOOK, 0f, LEATHER_BIOMASS + 0.3f, 0f).save(output);
        recycleRecipe(Items.GLASS_BOTTLE, 0.1f, 0f, 0f).save(output);
        recycleRecipe(Items.GLOWSTONE_DUST, 0f, 0f, 0.1f).save(output);
        recycleRecipe(Items.GUNPOWDER, 0f, 0f, 0.1f).save(output);
        recycleRecipe(Items.DRAGON_BREATH, 0.1f, 0f, 1f).save(output);
        recycleRecipe(Items.FERMENTED_SPIDER_EYE, 0f, MEAT_BIOMASS, 0f).save(output);
        recycleRecipe(Items.BLAZE_POWDER, 0f, MEAT_BIOMASS, 0.1f).save(output);
        recycleRecipe(Items.GLISTERING_MELON_SLICE, GOLD_NUGGET_SCRAP * 8, FRUIT_BIOMASS, 0f).save(output);
        recycleRecipe(Items.MAGMA_CREAM, 0.1f, 0.1f, 0.1f).save(output);
        recycleRecipe(Items.GHAST_TEAR, 0f, 0.1f, 0.1f).save(output);
        recycleRecipe(Items.PHANTOM_MEMBRANE, 0f, MEAT_BIOMASS, 0f).save(output);
        recycleRecipe(Items.EXPERIENCE_BOTTLE, 0.1f, 0f, 0.1f).save(output);
        recycleRecipe(Items.ENCHANTED_BOOK, 0f, LEATHER_BIOMASS + 0.3f, 0.1f).save(output);
            }
        };
    }

    private RecycleRecipeBuilder recycleRecipe(Item item, float scrap, float biomass, float fragments) {
        return new RecycleRecipeBuilder(Ingredient.of(item), scrap, biomass, fragments);
    }

    @Override
    public String getName() {
        return ReplikaEntropie.MOD_ID + " Recycle Recipes";
    }

    public record RecycleRecipeBuilder(Ingredient ingredient, float scrap, float biomass, float fragments) {
        public void save(RecipeOutput output) {
            final var id = ReplikaEntropie.id("recycle/" + ingredientPath(ingredient));
            output.accept(ResourceKey.create(Registries.RECIPE, id), new RecyclerRecipe(ingredient, buildOutputs()), null);
        }

        private List<ItemStackTemplate> buildOutputs() {
            final var outputs = new ArrayList<ItemStackTemplate>(3);
            addOutput(outputs, ModItems.scrap.asItem(), scrap);
            addOutput(outputs, ModItems.biomass.asItem(), biomass);
            addOutput(outputs, ModItems.fragments.asItem(), fragments);
            return outputs;
        }

        private static void addOutput(List<ItemStackTemplate> outputs, Item item, float amount) {
            final int wholeOutput = toWholeOutput(amount);
            if (wholeOutput > 0) {
                outputs.add(new ItemStackTemplate(item, wholeOutput));
            }
        }

        private static int toWholeOutput(float amount) {
            return amount > 0f ? (int) Math.ceil(amount) : 0;
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

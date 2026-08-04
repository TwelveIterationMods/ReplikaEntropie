package net.blay09.mods.replikaentropie.compat.recipeviewers;

import net.blay09.mods.balm.platform.compatibility.recipeviewer.RecipeViewerInfoProvider;
import net.blay09.mods.balm.platform.compatibility.recipeviewer.RecipeViewerRegistrar;
import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.blay09.mods.replikaentropie.component.AssemblyTicket;
import net.blay09.mods.replikaentropie.component.ModDataComponents;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.recipe.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Optional;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class ReplikaEntropieRecipeViewerProvider implements RecipeViewerInfoProvider {
    private static final Identifier RECYCLER_TEXTURE = id("textures/gui/jei/recycler.png");
    private static final Identifier ASSEMBLER_TEXTURE = id("textures/gui/jei/assembler.png");
    private static final Identifier BIOMASS_INCUBATOR_TEXTURE = id("textures/gui/jei/biomass_incubator.png");
    private static final Identifier FABRICATOR_TEXTURE = id("textures/gui/jei/fabricator.png");
    private static final Identifier RESEARCH_TEXTURE = id("textures/gui/jei/research.png");
    private static final Identifier LAVASCRAP_TEXTURE = id("textures/gui/jei/lavascrap.png");
    private static final Identifier COBBLESCRAP_TEXTURE = id("textures/gui/jei/cobblescrap.png");
    private static final Identifier FRAGMENT_ACCELERATOR_TEXTURE = id("textures/gui/jei/fragment_accelerator.png");
    private static final Identifier FRAGMENTAL_HEATER_TEXTURE = id("textures/gui/jei/fragmental_heater.png");
    private static final Identifier BIOMASS_HARVESTER_TEXTURE = id("textures/gui/jei/biomass_harvester.png");

    @Override
    public void initialize(RecipeViewerRegistrar registrar) {
        registrar.registerIngredientInfo(ModItems.assemblyTicket, Component.translatable("jei.replikaentropie.assembly_ticket.info"));
        registrar.registerIngredientInfo(ModItems.data, Component.translatable("jei.replikaentropie.data.info"));
        registrar.registerIngredientInfo(ModItems.damagedChipset, Component.translatable("jei.replikaentropie.damaged_chipset.info"));
        registrar.registerIngredientInfo(ModBlocks.fragmentalWaste, Component.translatable("jei.replikaentropie.fragmental_waste.info"));
        registrar.registerIngredientInfo(ModItems.metalDetector, Component.translatable("jei.replikaentropie.metal_detector.info"));

        registerRecyclerRecipes(registrar);
        registerAssemblerRecipes(registrar);
        registerBiomassIncubatorRecipes(registrar);
        registerFabricatorRecipes(registrar);
        registerResearchRecipes(registrar);
        registerFragmentAcceleratorRecipes(registrar);
        registerFragmentalHeaterRecipes(registrar);
        registerLavascrapRecipe(registrar);
        registerCobblescrapRecipe(registrar);
        registerBiomassHarvesterRecipe(registrar);
    }

    private static void registerRecyclerRecipes(RecipeViewerRegistrar registrar) {
        registrar.registerRecipeType(id("recycler"), RecyclerRecipe.class)
                .withSyncedRecipes(ModRecipes.recycler)
                .withCraftingStation(ModBlocks.recycler)
                .buildDisplay(display -> display
                        .title(Component.translatable(id("recycler").toLanguageKey("jei")))
                        .icon(ModBlocks.recycler)
                        .size(82, 88)
                        .background(RECYCLER_TEXTURE)
                        .slots((recipe, slots) -> {
                            slots.inputSlot(1, 36).add(recipe.ingredient());
                            final var outputs = recipe.outputs();
                            for (int i = 0; i < outputs.size(); i++) {
                                var output = outputs.get(i);
                                slots.outputSlot(61, 5 + i * 31).add(output);
                            }
                        }));
    }

    private static void registerAssemblerRecipes(RecipeViewerRegistrar registrar) {
        registrar.registerRecipeType(id("assembler"), AssemblerRecipe.class)
                .withSyncedRecipes(ModRecipes.assembler)
                .withCraftingStation(ModBlocks.assembler)
                .buildDisplay(display -> display
                        .title(Component.translatable(id("assembler").toLanguageKey("jei")))
                        .icon(ModBlocks.assembler)
                        .size(115, 77)
                        .background(ASSEMBLER_TEXTURE)
                        .slots((recipe, slots) -> {
                            slots.craftingStationSlot(1, 13).add(createAssemblyTicket(recipe.result().create()));

                            for (int i = 0; i < Math.min(5, recipe.ingredients().size()); i++) {
                                final var countedIngredient = recipe.ingredients().get(i);
                                final var slot = slots.inputSlot(26 + i * 18, 42);
                                for (final var itemHolder : countedIngredient.ingredient().items().toList()) {
                                    final var itemStack = new ItemStack(itemHolder);
                                    itemStack.setCount(Math.max(1, countedIngredient.count()));
                                    slot.add(itemStack);
                                }
                            }
                            for (int i = 5; i < recipe.ingredients().size(); i++) {
                                final var countedIngredient = recipe.ingredients().get(i);
                                final var slot = slots.inputSlot(34 + i * 18, 60);
                                for (final var itemHolder : countedIngredient.ingredient().items().toList()) {
                                    final var itemStack = new ItemStack(itemHolder);
                                    itemStack.setCount(Math.max(1, countedIngredient.count()));
                                    slot.add(itemStack);
                                }
                            }

                            slots.outputSlot(62, 13).add(recipe.result());
                        }));
    }

    private static void registerBiomassIncubatorRecipes(RecipeViewerRegistrar registrar) {
        registrar.registerRecipeType(id("biomass_incubator"), BiomassIncubatorRecipe.class)
                .withSyncedRecipes(ModRecipes.biomassIncubator)
                .withCraftingStation(ModBlocks.biomassIncubator)
                .buildDisplay(display -> display
                        .title(Component.translatable(id("biomass_incubator").toLanguageKey("jei")))
                        .icon(ModBlocks.biomassIncubator)
                        .size(88, 75)
                        .background(BIOMASS_INCUBATOR_TEXTURE)
                        .slots((recipe, slots) -> {
                            slots.craftingStationSlot(1, 58).add(Items.WATER_BUCKET);
                            slots.renderOnlySlot(36, 40).add(recipe.soil());
                            slots.inputSlot(36, 15).add(recipe.seed());
                            slots.outputSlot(71, 4).add(recipe.result());
                        }));
    }

    private static void registerFabricatorRecipes(RecipeViewerRegistrar registrar) {
        registrar.registerRecipeType(id("fabricator"), FabricatorRecipe.class)
                .withSyncedRecipes(ModRecipes.fabricator)
                .withCraftingStation(ModBlocks.fabricator)
                .buildDisplay(display -> display
                        .title(Component.translatable(id("fabricator").toLanguageKey("jei")))
                        .icon(ModBlocks.fabricator)
                        .size(68, 80)
                        .background(FABRICATOR_TEXTURE)
                        .slots((recipe, slots) -> {
                            if (recipe.scrap() > 0) {
                                slots.inputSlot(1, 1).add(ModItems.scrap.createStack(recipe.scrap()));
                            }
                            if (recipe.biomass() > 0) {
                                slots.inputSlot(26, 1).add(ModItems.biomass.createStack(recipe.biomass()));
                            }
                            if (recipe.fragments() > 0) {
                                slots.inputSlot(51, 1).add(ModItems.fragments.createStack(recipe.fragments()));
                            }

                            slots.outputSlot(26, 59).add(recipe.result());
                        }));
    }

    private static void registerResearchRecipes(RecipeViewerRegistrar registrar) {
        registrar.registerRecipeType(id("research"), ResearchRecipe.class)
                .withSyncedRecipes(ModRecipes.research)
                .withCraftingStation(ModItems.skyScraper)
                .buildDisplay(display -> display
                        .title(Component.translatable(id("research").toLanguageKey("jei")))
                        .icon(ModItems.skyScraper)
                        .size(104, 76)
                        .background(RESEARCH_TEXTURE)
                        .slots((recipe, slots) -> {
                            if (recipe.dataCost() > 0) {
                                slots.inputSlot(6, 52).add(ModItems.data.createStack(recipe.dataCost()));
                            }
                            if (recipe.scrapCost() > 0) {
                                slots.inputSlot(32, 52).add(ModItems.scrap.createStack(recipe.scrapCost()));
                            }
                            if (recipe.biomassCost() > 0) {
                                slots.inputSlot(57, 52).add(ModItems.biomass.createStack(recipe.biomassCost()));
                            }
                            if (recipe.fragmentsCost() > 0) {
                                slots.inputSlot(82, 52).add(ModItems.fragments.createStack(recipe.fragmentsCost()));
                            }

                            final var icon = recipe.icon().create();
                            if (recipe.type() == ResearchRecipe.Type.ASSEMBLER) {
                                slots.outputSlot(44, 14).add(createAssemblyTicket(icon));
                                slots.renderOnlySlot(73, 14).add(icon);
                            } else if (recipe.type() == ResearchRecipe.Type.LORE) {
                                slots.renderOnlySlot(44, 14).add(icon);
                            } else {
                                slots.outputSlot(44, 14).add(icon);
                            }
                        }));
    }

    private static void registerFragmentAcceleratorRecipes(RecipeViewerRegistrar registrar) {
        registrar.registerRecipeType(id("fragment_accelerator"), FragmentAcceleratorRecipe.class)
                .withSyncedRecipes(ModRecipes.fragmentAccelerator)
                .withCraftingStation(ModBlocks.fragmentAccelerator)
                .buildDisplay(display -> display
                        .title(Component.translatable(id("fragment_accelerator").toLanguageKey("jei")))
                        .icon(ModBlocks.fragmentAccelerator)
                        .size(107, 76)
                        .background(FRAGMENT_ACCELERATOR_TEXTURE)
                        .slots((recipe, slots) -> {
                            slots.inputSlot(30, 1).add(recipe.ingredient());
                            slots.inputSlot(90, 30).add(recipe.ingredient());
                            slots.inputSlot(30, 59).add(recipe.ingredient());
                            slots.outputSlot(30, 30).add(ModItems.fragments.createStack());
                            slots.outputSlot(61, 30).add(ModBlocks.wasteBarrel);
                        }));
    }

    private static void registerFragmentalHeaterRecipes(RecipeViewerRegistrar registrar) {
        registrar.registerRecipeType(id("fragmental_heater"), FragmentalHeaterRecipe.class)
                .withSyncedRecipes(ModRecipes.fragmentalHeater)
                .withCraftingStation(ModBlocks.fragmentalHeater)
                .buildDisplay(display -> display
                        .title(Component.translatable(id("fragmental_heater").toLanguageKey("jei")))
                        .icon(ModBlocks.fragmentalHeater)
                        .size(129, 87)
                        .background(FRAGMENTAL_HEATER_TEXTURE)
                        .slots((recipe, slots) -> {
                            for (int row = 0; row < 3; row++) {
                                for (int column = 0; column < 4; column++) {
                                    if((column + row * 3) % 2 == 0) {
                                        slots.inputSlot(1 + column * 37, 1 + row * 25).add(recipe.ingredient());
                                    }
                                }
                            }
                        }));
    }

    private static void registerLavascrapRecipe(RecipeViewerRegistrar registrar) {
        registrar.registerCustomRecipeType(id("lavascrap"), LavascrapRecipe.class)
                .withRecipe(new LavascrapRecipe())
                .withCraftingStation(ModBlocks.lavascrap)
                .buildDisplay(display -> display
                        .title(Component.translatable(id("lavascrap").toLanguageKey("jei")))
                        .icon(ModBlocks.lavascrap)
                        .size(144, 75)
                        .background(LAVASCRAP_TEXTURE)
                        .slots((_, slots) -> {
                            slots.craftingStationSlot(1, 58).add(Items.WATER_BUCKET);
                            slots.craftingStationSlot(127, 58).add(Items.LAVA_BUCKET);
                            slots.outputSlot(64, 1).add(Items.OBSIDIAN);
                        }));
    }

    private static void registerCobblescrapRecipe(RecipeViewerRegistrar registrar) {
        registrar.registerCustomRecipeType(id("cobblescrap"), CobblescrapRecipe.class)
                .withRecipe(new CobblescrapRecipe())
                .withCraftingStation(ModBlocks.cobblescrap)
                .buildDisplay(display -> display
                        .title(Component.translatable(id("cobblescrap").toLanguageKey("jei")))
                        .icon(ModBlocks.cobblescrap)
                        .size(144, 50)
                        .background(COBBLESCRAP_TEXTURE)
                        .slots((_, slots) -> slots.outputSlot(66, 20).add(Items.COBBLESTONE)));
    }

    private static void registerBiomassHarvesterRecipe(RecipeViewerRegistrar registrar) {
        registrar.registerCustomRecipeType(id("biomass_harvester"), BiomassHarvesterRecipe.class)
                .withRecipe(new BiomassHarvesterRecipe())
                .withCraftingStation(ModBlocks.biomassHarvester)
                .buildDisplay(display -> display
                        .title(Component.translatable(id("biomass_harvester").toLanguageKey("jei")))
                        .icon(ModBlocks.biomassHarvester)
                        .size(76, 76)
                        .background(BIOMASS_HARVESTER_TEXTURE)
                        .slots((_, slots) -> {
                            final var toolSlots = List.of(slots.inputSlot(30, 1),
                                    slots.inputSlot(1, 30),
                                    slots.inputSlot(60, 30),
                                    slots.inputSlot(30, 59));
                            for (final var toolSlot : toolSlots) {
                                for (final var toolItem : BuiltInRegistries.ITEM.getTagOrEmpty(ItemTags.SWORDS)) {
                                    toolSlot.add(toolItem.value());
                                }
                                for (final var toolItem : BuiltInRegistries.ITEM.getTagOrEmpty(ItemTags.HOES)) {
                                    toolSlot.add(toolItem.value());
                                }
                                toolSlot.add(Items.SHEARS);
                            }
                        }));
    }

    private static ItemStack createAssemblyTicket(ItemStack result) {
        final var ticket = ModItems.assemblyTicket.createStack();
        ticket.set(DataComponents.CUSTOM_NAME, Component.translatable("item.replikaentropie.assembly_ticket.tooltip.title", result.getHoverName()));
        ticket.set(ModDataComponents.assemblyTicket(), new AssemblyTicket(Optional.empty(), 0, true));
        return ticket;
    }

    public record LavascrapRecipe() {
    }

    public record CobblescrapRecipe() {
    }

    public record BiomassHarvesterRecipe() {
    }
}

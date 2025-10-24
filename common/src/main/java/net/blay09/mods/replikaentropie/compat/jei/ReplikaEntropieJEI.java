package net.blay09.mods.replikaentropie.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.blay09.mods.replikaentropie.ReplikaEntropie;
import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.recipe.ModRecipes;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Objects;

@JeiPlugin
public class ReplikaEntropieJEI implements IModPlugin {

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        final var recipeManager = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();

        registration.addIngredientInfo(ModItems.assemblyTicket, Component.translatable("jei.replikaentropie.assembly_ticket.info"));
        registration.addIngredientInfo(ModItems.data, Component.translatable("jei.replikaentropie.data.info"));
        registration.addIngredientInfo(ModItems.damagedChipset, Component.translatable("jei.replikaentropie.damaged_chipset.info"));
        registration.addIngredientInfo(ModBlocks.fragmentalWaste, Component.translatable("jei.replikaentropie.fragmental_waste.info"));

        registration.addRecipes(JeiRecyclerCategory.TYPE, recipeManager.getAllRecipesFor(ModRecipes.recyclerType));
        registration.addRecipes(JeiDefragmentizerCategory.TYPE, recipeManager.getAllRecipesFor(ModRecipes.recyclerType).stream()
                .filter(it -> it.fragments() > 0).toList());
        registration.addRecipes(JeiFragmentAcceleratorCategory.TYPE, recipeManager.getAllRecipesFor(ModRecipes.recyclerType).stream()
                .filter(it -> it.fragments() > 0).toList());
        registration.addRecipes(JeiAssemblerCategory.TYPE, recipeManager.getAllRecipesFor(ModRecipes.assemblerType));
        registration.addRecipes(JeiBiomassIncubatorCategory.TYPE, recipeManager.getAllRecipesFor(ModRecipes.biomassIncubatorType));
        registration.addRecipes(JeiFabricatorCategory.TYPE, recipeManager.getAllRecipesFor(ModRecipes.fabricatorType));
        registration.addRecipes(JeiResearchCategory.TYPE, recipeManager.getAllRecipesFor(ModRecipes.researchType));
        registration.addRecipes(JeiWorldEaterCategory.TYPE, List.of(new JeiWorldEaterCategory.JeiWorldEaterRecipe()));
        registration.addRecipes(JeiLavascrapCategory.TYPE, List.of(new JeiLavascrapCategory.JeiLavascrapRecipe()));
        registration.addRecipes(JeiCobblescrapCategory.TYPE, List.of(new JeiCobblescrapCategory.JeiCobblescrapRecipe()));
        registration.addRecipes(JeiBiomassHarvesterCategory.TYPE, List.of(new JeiBiomassHarvesterCategory.JeiBiomassHarvesterRecipe()));
        registration.addRecipes(JeiReplikaWorkbenchCategory.TYPE, List.of(
                new JeiReplikaWorkbenchCategory.JeiReplikaWorkbenchRecipe(
                        new ItemStack(ModItems.replikaHelmetFrame), new ItemStack(ModItems.replikaHelmet)
                ),
                new JeiReplikaWorkbenchCategory.JeiReplikaWorkbenchRecipe(
                        new ItemStack(ModItems.replikaChestplateFrame), new ItemStack(ModItems.replikaChestplate)
                ),
                new JeiReplikaWorkbenchCategory.JeiReplikaWorkbenchRecipe(
                        new ItemStack(ModItems.replikaLeggingsFrame), new ItemStack(ModItems.replikaLeggings)
                ),
                new JeiReplikaWorkbenchCategory.JeiReplikaWorkbenchRecipe(
                        new ItemStack(ModItems.replikaBootsFrame), new ItemStack(ModItems.replikaBoots)
                )
        ));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.recycler), JeiRecyclerCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.assembler), JeiAssemblerCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.biomassIncubator), JeiBiomassIncubatorCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.fabricator), JeiFabricatorCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.worldEater), JeiWorldEaterCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModItems.skyScraper), JeiResearchCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.replikaWorkbench), JeiReplikaWorkbenchCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.lavascrap), JeiLavascrapCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.cobblescrap), JeiCobblescrapCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.defragmentizer), JeiDefragmentizerCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.fragmentAccelerator), JeiFragmentAcceleratorCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.biomassHarvester), JeiBiomassHarvesterCategory.TYPE);
    }

    @Override
    public ResourceLocation getPluginUid() {
        return ReplikaEntropie.id("jei");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
                new JeiRecyclerCategory(registration.getJeiHelpers().getGuiHelper()),
                new JeiAssemblerCategory(registration.getJeiHelpers().getGuiHelper()),
                new JeiBiomassIncubatorCategory(registration.getJeiHelpers().getGuiHelper()),
                new JeiFabricatorCategory(registration.getJeiHelpers().getGuiHelper()),
                new JeiWorldEaterCategory(registration.getJeiHelpers().getGuiHelper()),
                new JeiResearchCategory(registration.getJeiHelpers().getGuiHelper()),
                new JeiReplikaWorkbenchCategory(registration.getJeiHelpers().getGuiHelper()),
                new JeiLavascrapCategory(registration.getJeiHelpers().getGuiHelper()),
                new JeiCobblescrapCategory(registration.getJeiHelpers().getGuiHelper()),
                new JeiDefragmentizerCategory(registration.getJeiHelpers().getGuiHelper()),
                new JeiFragmentAcceleratorCategory(registration.getJeiHelpers().getGuiHelper()),
                new JeiBiomassHarvesterCategory(registration.getJeiHelpers().getGuiHelper())
        );
    }
}

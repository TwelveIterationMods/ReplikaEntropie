package net.blay09.mods.replikaentropie.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.*;
import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.blay09.mods.replikaentropie.item.ModItems;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        blockStateModelGenerator.createNonTemplateHorizontalBlock(ModBlocks.replikaWorkbench);
        blockStateModelGenerator.createNonTemplateHorizontalBlock(ModBlocks.entropicDataMiner);
        blockStateModelGenerator.createNonTemplateHorizontalBlock(ModBlocks.recycler);
        blockStateModelGenerator.createNonTemplateHorizontalBlock(ModBlocks.fabricator);
        blockStateModelGenerator.createNonTemplateHorizontalBlock(ModBlocks.assembler);
        blockStateModelGenerator.createNonTemplateModelBlock(ModBlocks.fragmentalWaste);
        blockStateModelGenerator.createNonTemplateHorizontalBlock(ModBlocks.biomassIncubator);
        blockStateModelGenerator.createNonTemplateHorizontalBlock(ModBlocks.biomassHarvester);
        blockStateModelGenerator.createNonTemplateHorizontalBlock(ModBlocks.cobblescrap);
        blockStateModelGenerator.createNonTemplateHorizontalBlock(ModBlocks.lavascrap);
        blockStateModelGenerator.createNonTemplateHorizontalBlock(ModBlocks.worldEater);
        blockStateModelGenerator.createNonTemplateModelBlock(ModBlocks.fragmentAccelerator);
        blockStateModelGenerator.createNonTemplateModelBlock(ModBlocks.defragmentizer);
        blockStateModelGenerator.createNonTemplateHorizontalBlock(ModBlocks.chaosEngine);
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
        itemModelGenerator.generateFlatItem(ModItems.damagedChipset, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.handheldAnalyzer, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.skyScraper, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.data, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.scrap, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.biomass, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.fragments, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.chipset, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.biosteel, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.assemblyTicket, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.biosteelHelmet, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.biosteelChestplate, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.biosteelLeggings, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.biosteelBoots, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.hazmatLining, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.hazmatHelmet, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.hazmatChestplate, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.hazmatLeggings, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.hazmatBoots, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.replikaSkin, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.replikaHelmetFrame, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.replikaChestplateFrame, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.replikaLeggingsFrame, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.replikaBootsFrame, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.replikaHelmet, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.replikaChestplate, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.replikaLeggings, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.replikaBoots, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.nightVisionGoggles, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.brightVisionGoggles, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.graviliftHarness, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.semisonicSpeeders, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.magphasers, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.slowphasers, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.stompers, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.springBoots, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.nullphaser, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.oreVacuum, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.burstDrill, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.biomash, ModelTemplates.FLAT_ITEM);
    }

}

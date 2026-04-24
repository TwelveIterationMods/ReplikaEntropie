package net.blay09.mods.replikaentropie.fabric.datagen;

import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.color.item.Constant;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;

public class ModModelProvider extends FabricModelProvider {

    public ModModelProvider(FabricPackOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators generators) {
        generators.createNonTemplateModelBlock(ModBlocks.digSpot.asBlock());
        generators.createNonTemplateHorizontalBlock(ModBlocks.replikaWorkbench.value());
        generators.createNonTemplateHorizontalBlock(ModBlocks.entropicDataMiner.value());
        generators.createNonTemplateHorizontalBlock(ModBlocks.recycler.value());
        generators.createNonTemplateHorizontalBlock(ModBlocks.fabricator.value());
        generators.createNonTemplateHorizontalBlock(ModBlocks.assembler.value());
        generators.createNonTemplateModelBlock(ModBlocks.wasteBarrel.value());
        generators.createNonTemplateModelBlock(ModBlocks.fragmentalWaste.value());
        generators.createNonTemplateHorizontalBlock(ModBlocks.biomassIncubator.value());
        generators.createNonTemplateHorizontalBlock(ModBlocks.biomassHarvester.value());
        generators.createNonTemplateHorizontalBlock(ModBlocks.cobblescrap.value());
        generators.createNonTemplateHorizontalBlock(ModBlocks.lavascrap.value());
        generators.createNonTemplateHorizontalBlock(ModBlocks.worldEater.value());
        generators.createNonTemplateModelBlock(ModBlocks.fragmentAccelerator.value());
        generators.createNonTemplateModelBlock(ModBlocks.fragmentalGenerator.value());
        generators.createNonTemplateHorizontalBlock(ModBlocks.chaosEngine.value());

        generators.registerSimpleItemModel(ModBlocks.replikaWorkbench.value(), ModelLocationUtils.getModelLocation(ModBlocks.replikaWorkbench.value()));
        generators.registerSimpleItemModel(ModBlocks.entropicDataMiner.value(), ModelLocationUtils.getModelLocation(ModBlocks.entropicDataMiner.value()));
        generators.registerSimpleItemModel(ModBlocks.recycler.value(), ModelLocationUtils.getModelLocation(ModBlocks.recycler.value()));
        generators.registerSimpleItemModel(ModBlocks.fabricator.value(), ModelLocationUtils.getModelLocation(ModBlocks.fabricator.value()));
        generators.registerSimpleItemModel(ModBlocks.assembler.value(), ModelLocationUtils.getModelLocation(ModBlocks.assembler.value()));
        generators.registerSimpleItemModel(ModBlocks.wasteBarrel.value(), ModelLocationUtils.getModelLocation(ModBlocks.wasteBarrel.value()));
        generators.registerSimpleItemModel(ModBlocks.fragmentalWaste.value(), ModelLocationUtils.getModelLocation(ModBlocks.fragmentalWaste.value()));
        generators.registerSimpleItemModel(ModBlocks.biomassIncubator.value(), ModelLocationUtils.getModelLocation(ModBlocks.biomassIncubator.value()));
        generators.registerSimpleItemModel(ModBlocks.biomassHarvester.value(), ModelLocationUtils.getModelLocation(ModBlocks.biomassHarvester.value()));
        generators.registerSimpleTintedItemModel(ModBlocks.cobblescrap.value(), ModelLocationUtils.getModelLocation(ModBlocks.cobblescrap.value()), new Constant(0xFF3F76E4));
        generators.registerSimpleTintedItemModel(ModBlocks.lavascrap.value(), ModelLocationUtils.getModelLocation(ModBlocks.lavascrap.value()), new Constant(0xFF3F76E4));
        generators.registerSimpleItemModel(ModBlocks.worldEater.value(), ModelLocationUtils.getModelLocation(ModBlocks.worldEater.value()));
        generators.registerSimpleItemModel(ModBlocks.fragmentAccelerator.value(), ModelLocationUtils.getModelLocation(ModBlocks.fragmentAccelerator.value()));
        generators.registerSimpleItemModel(ModBlocks.fragmentalGenerator.value(), ModelLocationUtils.getModelLocation(ModBlocks.fragmentalGenerator.value()));
        generators.registerSimpleItemModel(ModBlocks.chaosEngine.value(), ModelLocationUtils.getModelLocation(ModBlocks.chaosEngine.value()));
    }

    @Override
    public void generateItemModels(ItemModelGenerators generators) {
        generators.generateFlatItem(ModItems.damagedChipset.asItem(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.makeshiftPSU.asItem(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.data.asItem(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.scrap.asItem(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.biomass.asItem(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.fragments.asItem(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.chipset.asItem(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.biosteel.asItem(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.assemblyTicket.asItem(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.biomash.asItem(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.hazmatLining.asItem(), ModelTemplates.FLAT_ITEM);

        generators.generateFlatItem(ModItems.handheldAnalyzer.asItem(), ModelTemplates.FLAT_HANDHELD_ITEM);
        generators.generateFlatItem(ModItems.skyScraper.asItem(), ModelTemplates.FLAT_HANDHELD_ITEM);
        generators.generateFlatItem(ModItems.nullphaser.asItem(), ModelTemplates.FLAT_HANDHELD_ITEM);
        generators.generateFlatItem(ModItems.oreVacuum.asItem(), ModelTemplates.FLAT_HANDHELD_ITEM);

        generators.declareCustomModelItem(ModItems.metalDetector.asItem());

        generators.generateFlatItem(ModItems.biosteelHelmet.asItem(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.biosteelChestplate.asItem(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.biosteelLeggings.asItem(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.biosteelBoots.asItem(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.hazmatHelmet.asItem(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.hazmatChestplate.asItem(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.hazmatLeggings.asItem(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.hazmatBoots.asItem(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.scubaHelmet.asItem(), ModelTemplates.FLAT_ITEM);

        generators.generateFlatItem(ModItems.nightVisionGoggles.asItem(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.brightVisionGoggles.asItem(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.graviliftHarness.asItem(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.semisonicSpeeders.asItem(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.magphasers.asItem(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.slowphasers.asItem(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.stompers.asItem(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.springBoots.asItem(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.automaticHackTool.asItem(), ModelTemplates.FLAT_ITEM);
    }
}

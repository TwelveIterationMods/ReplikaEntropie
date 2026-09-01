package net.blay09.mods.replikaentropie.fabric.datagen;

import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.color.item.Constant;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import static net.minecraft.client.data.models.BlockModelGenerators.*;

public class ModModelProvider extends FabricModelProvider {

    public ModModelProvider(FabricPackOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators generators) {
        generators.createNonTemplateModelBlock(ModBlocks.digSpot.asBlock());
        generators.createNonTemplateHorizontalBlock(ModBlocks.replikaWorkbench.asBlock());
        generators.createNonTemplateHorizontalBlock(ModBlocks.entropicDataMiner.asBlock());
        generators.createNonTemplateHorizontalBlock(ModBlocks.recycler.asBlock());
        generators.createNonTemplateHorizontalBlock(ModBlocks.fabricator.asBlock());
        generators.createNonTemplateHorizontalBlock(ModBlocks.assembler.asBlock());
        generators.createNonTemplateModelBlock(ModBlocks.wasteBarrel.asBlock());
        generators.createNonTemplateModelBlock(ModBlocks.fragmentalWaste.asBlock());
        generators.createNonTemplateHorizontalBlock(ModBlocks.biomassIncubator.asBlock());
        generators.createNonTemplateHorizontalBlock(ModBlocks.biomassHarvester.asBlock());
        generators.createNonTemplateHorizontalBlock(ModBlocks.cobblescrap.asBlock());
        generators.createNonTemplateHorizontalBlock(ModBlocks.lavascrap.asBlock());
        generators.createNonTemplateHorizontalBlock(ModBlocks.worldEater.asBlock());
        generators.createNonTemplateModelBlock(ModBlocks.fragmentAccelerator.asBlock());
        generators.createNonTemplateModelBlock(ModBlocks.fragmentalHeater.asBlock());
        generators.createNonTemplateHorizontalBlock(ModBlocks.chaosEngine.asBlock());
        generators.createNonTemplateHorizontalBlock(ModBlocks.waterSink.asBlock());
        generators.createNonTemplateHorizontalBlock(ModBlocks.lavaSink.asBlock());
        generators.createNonTemplateModelBlock(ModBlocks.solarSink.asBlock());
        generators.createNonTemplateModelBlock(ModBlocks.fragmentedSun.asBlock());
        generators.createNonTemplateHorizontalBlock(ModBlocks.bluePrinter.asBlock());
        createCrane(generators, ModBlocks.crane.asBlock());

        createFunnel(generators, ModBlocks.funnel.asBlock());

        generators.registerSimpleItemModel(ModBlocks.replikaWorkbench.asBlock(), ModelLocationUtils.getModelLocation(ModBlocks.replikaWorkbench.asBlock()));
        generators.registerSimpleItemModel(ModBlocks.entropicDataMiner.asBlock(), ModelLocationUtils.getModelLocation(ModBlocks.entropicDataMiner.asBlock()));
        generators.registerSimpleItemModel(ModBlocks.recycler.asBlock(), ModelLocationUtils.getModelLocation(ModBlocks.recycler.asBlock()));
        generators.registerSimpleItemModel(ModBlocks.fabricator.asBlock(), ModelLocationUtils.getModelLocation(ModBlocks.fabricator.asBlock()));
        generators.registerSimpleItemModel(ModBlocks.assembler.asBlock(), ModelLocationUtils.getModelLocation(ModBlocks.assembler.asBlock()));
        generators.registerSimpleItemModel(ModBlocks.wasteBarrel.asBlock(), ModelLocationUtils.getModelLocation(ModBlocks.wasteBarrel.asBlock()));
        generators.registerSimpleItemModel(ModBlocks.fragmentalWaste.asBlock(), ModelLocationUtils.getModelLocation(ModBlocks.fragmentalWaste.asBlock()));
        generators.registerSimpleItemModel(ModBlocks.biomassHarvester.asBlock(), ModelLocationUtils.getModelLocation(ModBlocks.biomassHarvester.asBlock()));
        generators.registerSimpleTintedItemModel(ModBlocks.cobblescrap.asBlock(), ModelLocationUtils.getModelLocation(ModBlocks.cobblescrap.asBlock()), new Constant(0xFF3F76E4));
        generators.registerSimpleTintedItemModel(ModBlocks.lavascrap.asBlock(), ModelLocationUtils.getModelLocation(ModBlocks.lavascrap.asBlock()), new Constant(0xFF3F76E4));
        generators.registerSimpleItemModel(ModBlocks.worldEater.asBlock(), ModelLocationUtils.getModelLocation(ModBlocks.worldEater.asBlock()));
        generators.registerSimpleItemModel(ModBlocks.fragmentAccelerator.asBlock(), ModelLocationUtils.getModelLocation(ModBlocks.fragmentAccelerator.asBlock()));
        generators.registerSimpleItemModel(ModBlocks.fragmentalHeater.asBlock(), ModelLocationUtils.getModelLocation(ModBlocks.fragmentalHeater.asBlock()));
        generators.registerSimpleItemModel(ModBlocks.chaosEngine.asBlock(), ModelLocationUtils.getModelLocation(ModBlocks.chaosEngine.asBlock()));
        generators.registerSimpleTintedItemModel(ModBlocks.waterSink.asBlock(), ModelLocationUtils.getModelLocation(ModBlocks.waterSink.asBlock()), new Constant(0xFF3F76E4));
        generators.registerSimpleItemModel(ModBlocks.lavaSink.asBlock(), ModelLocationUtils.getModelLocation(ModBlocks.lavaSink.asBlock()));
        generators.registerSimpleItemModel(ModBlocks.solarSink.asBlock(), ModelLocationUtils.getModelLocation(ModBlocks.solarSink.asBlock()));
        generators.registerSimpleItemModel(ModBlocks.bluePrinter.asBlock(), ModelLocationUtils.getModelLocation(ModBlocks.bluePrinter.asBlock()));
        generators.registerSimpleItemModel(ModBlocks.crane.asBlock(), ModelLocationUtils.getModelLocation(ModBlocks.crane.asBlock(), "_bottom"));
    }

    private void createCrane(BlockModelGenerators generators, Block crane) {
        final var bottomModel = plainVariant(ModelLocationUtils.getModelLocation(crane, "_bottom"));
        final var topModel = plainVariant(ModelLocationUtils.getModelLocation(crane, "_top"));
        generators.blockStateOutput.accept(MultiVariantGenerator.dispatch(crane)
                .with(PropertyDispatch.initial(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.DOUBLE_BLOCK_HALF, BlockStateProperties.POWERED)
                        .select(Direction.NORTH, DoubleBlockHalf.LOWER, false, bottomModel)
                        .select(Direction.NORTH, DoubleBlockHalf.LOWER, true, bottomModel)
                        .select(Direction.EAST, DoubleBlockHalf.LOWER, false, bottomModel.with(Y_ROT_90))
                        .select(Direction.EAST, DoubleBlockHalf.LOWER, true, bottomModel.with(Y_ROT_90))
                        .select(Direction.SOUTH, DoubleBlockHalf.LOWER, false, bottomModel.with(Y_ROT_180))
                        .select(Direction.SOUTH, DoubleBlockHalf.LOWER, true, bottomModel.with(Y_ROT_180))
                        .select(Direction.WEST, DoubleBlockHalf.LOWER, false, bottomModel.with(Y_ROT_270))
                        .select(Direction.WEST, DoubleBlockHalf.LOWER, true, bottomModel.with(Y_ROT_270))
                        .select(Direction.NORTH, DoubleBlockHalf.UPPER, false, topModel)
                        .select(Direction.NORTH, DoubleBlockHalf.UPPER, true, topModel)
                        .select(Direction.EAST, DoubleBlockHalf.UPPER, false, topModel.with(Y_ROT_90))
                        .select(Direction.EAST, DoubleBlockHalf.UPPER, true, topModel.with(Y_ROT_90))
                        .select(Direction.SOUTH, DoubleBlockHalf.UPPER, false, topModel.with(Y_ROT_180))
                        .select(Direction.SOUTH, DoubleBlockHalf.UPPER, true, topModel.with(Y_ROT_180))
                        .select(Direction.WEST, DoubleBlockHalf.UPPER, false, topModel.with(Y_ROT_270))
                        .select(Direction.WEST, DoubleBlockHalf.UPPER, true, topModel.with(Y_ROT_270))));
    }

    private void createFunnel(BlockModelGenerators generators, Block funnel) {
        final var downBlock = plainVariant(ModelLocationUtils.getModelLocation(funnel));
        final var sideBlock = plainVariant(ModelLocationUtils.getModelLocation(funnel, "_side"));
        generators.registerSimpleFlatItemModel(funnel.asItem());
        generators.blockStateOutput.accept(MultiVariantGenerator.dispatch(funnel)
                .with(PropertyDispatch.initial(BlockStateProperties.FACING_HOPPER)
                        .select(Direction.DOWN, downBlock)
                        .select(Direction.NORTH, sideBlock)
                        .select(Direction.EAST, sideBlock.with(Y_ROT_90))
                        .select(Direction.SOUTH, sideBlock.with(Y_ROT_180))
                        .select(Direction.WEST, sideBlock.with(Y_ROT_270))));
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
        generators.generateFlatItem(ModItems.replikaAlloy.asItem(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.assemblyTicket.asItem(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.biomash.asItem(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.hazmatLining.asItem(), ModelTemplates.FLAT_ITEM);

        generators.generateFlatItem(ModItems.handheldAnalyzer.asItem(), ModelTemplates.FLAT_HANDHELD_ITEM);
        generators.generateFlatItem(ModItems.skyScraper.asItem(), ModelTemplates.FLAT_HANDHELD_ITEM);
        generators.generateFlatItem(ModItems.nullphaser.asItem(), ModelTemplates.FLAT_HANDHELD_ITEM);
        generators.generateFlatItem(ModItems.oreVacuum.asItem(), ModelTemplates.FLAT_HANDHELD_ITEM);

        generators.declareCustomModelItem(ModBlocks.biomassIncubator.asItem());
        generators.declareCustomModelItem(ModItems.metalDetector.asItem());

        generators.generateFlatItem(ModItems.biosteelHelmet.asItem(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.biosteelChestplate.asItem(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.biosteelLeggings.asItem(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.biosteelBoots.asItem(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.hazmatHelmet.asItem(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.hazmatChestplate.asItem(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.hazmatLeggings.asItem(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.hazmatBoots.asItem(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.replikaHelmet.asItem(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.replikaChestplate.asItem(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.replikaLeggings.asItem(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.replikaBoots.asItem(), ModelTemplates.FLAT_ITEM);

        generators.generateFlatItem(ModItems.nightVisionGoggles.asItem(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.brightVisionGoggles.asItem(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.graviliftEngine.asItem(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.semisonicSpeeders.asItem(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.magphasers.asItem(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.slowphasers.asItem(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.stompers.asItem(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.bouncers.asItem(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.automaticHackTool.asItem(), ModelTemplates.FLAT_ITEM);

        generators.generateFlatItem(ModItems.wasteBarrelMinecart.asItem(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.fragmentalWasteMinecart.asItem(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.biomassHarvesterMinecart.asItem(), ModelTemplates.FLAT_ITEM);

        generators.generateFlatItem(ModBlocks.fragmentedSun.asItem(), ModelTemplates.FLAT_ITEM);
    }
}

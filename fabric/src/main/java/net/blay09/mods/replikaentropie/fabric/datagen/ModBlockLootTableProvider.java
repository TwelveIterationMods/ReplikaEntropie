package net.blay09.mods.replikaentropie.fabric.datagen;

import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.concurrent.CompletableFuture;

public class ModBlockLootTableProvider extends FabricBlockLootSubProvider {
    protected ModBlockLootTableProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate() {
        dropSelf(ModBlocks.replikaWorkbench.asBlock());
        dropSelf(ModBlocks.entropicDataMiner.asBlock());
        dropSelf(ModBlocks.fabricator.asBlock());
        dropSelf(ModBlocks.assembler.asBlock());
        dropSelf(ModBlocks.wasteBarrel.asBlock());
        dropSelf(ModBlocks.fragmentalWaste.asBlock());
        dropSelf(ModBlocks.biomassIncubator.asBlock());
        dropSelf(ModBlocks.biomassHarvester.asBlock());
        dropSelf(ModBlocks.cobblescrap.asBlock());
        dropSelf(ModBlocks.lavascrap.asBlock());
        dropSelf(ModBlocks.worldEater.asBlock());
        dropSelf(ModBlocks.fragmentalHeater.asBlock());
        dropSelf(ModBlocks.fragmentAccelerator.asBlock());
        dropSelf(ModBlocks.chaosEngine.asBlock());
        dropSelf(ModBlocks.waterSink.asBlock());
        dropSelf(ModBlocks.lavaSink.asBlock());
        dropSelf(ModBlocks.solarSink.asBlock());
        dropSelf(ModBlocks.fragmentedSun.asBlock());
        dropSelf(ModBlocks.recycler.asBlock());
        dropSelf(ModBlocks.bluePrinter.asBlock());
        add(ModBlocks.crane.asBlock(), createSinglePropConditionTable(ModBlocks.crane.asBlock(), BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER));
    }
}

package net.blay09.mods.replikaentropie.fabric.datagen;

import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class ModBlockLootTableProvider extends FabricBlockLootSubProvider {
    protected ModBlockLootTableProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate() {
        dropSelf(ModBlocks.replikaWorkbench.value());
        dropSelf(ModBlocks.entropicDataMiner.value());
        dropSelf(ModBlocks.fabricator.value());
        dropSelf(ModBlocks.assembler.value());
        dropSelf(ModBlocks.wasteBarrel.value());
        dropSelf(ModBlocks.fragmentalWaste.value());
        dropSelf(ModBlocks.biomassIncubator.value());
        dropSelf(ModBlocks.biomassHarvester.value());
        dropSelf(ModBlocks.cobblescrap.value());
        dropSelf(ModBlocks.lavascrap.value());
        dropSelf(ModBlocks.worldEater.value());
        dropSelf(ModBlocks.fragmentalGenerator.value());
        dropSelf(ModBlocks.fragmentAccelerator.value());
        dropSelf(ModBlocks.chaosEngine.value());
        dropSelf(ModBlocks.waterSink.value());
        dropSelf(ModBlocks.lavaSink.value());
        dropSelf(ModBlocks.solarSink.value());
        dropSelf(ModBlocks.recycler.value());
        dropSelf(ModBlocks.bluePrinter.value());
    }
}

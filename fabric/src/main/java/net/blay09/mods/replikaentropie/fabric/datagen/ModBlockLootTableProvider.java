package net.blay09.mods.replikaentropie.fabric.datagen;

import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;

public class ModBlockLootTableProvider extends FabricBlockLootTableProvider {
    protected ModBlockLootTableProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {
        dropSelf(ModBlocks.replikaWorkbench);
        dropSelf(ModBlocks.entropicDataMiner);
        dropSelf(ModBlocks.fabricator);
        dropSelf(ModBlocks.assembler);
        dropSelf(ModBlocks.fragmentalWaste);
        dropSelf(ModBlocks.biomassIncubator);
        dropSelf(ModBlocks.biomassHarvester);
        dropSelf(ModBlocks.cobblescrap);
        dropSelf(ModBlocks.lavascrap);
        dropSelf(ModBlocks.worldEater);
        dropSelf(ModBlocks.defragmentizer);
        dropSelf(ModBlocks.fragmentAccelerator);
        dropSelf(ModBlocks.chaosEngine);
        dropSelf(ModBlocks.recycler);
    }
}

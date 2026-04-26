package net.blay09.mods.replikaentropie.fabric.datagen;

import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.blay09.mods.replikaentropie.tag.ModBlockTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagsProvider.BlockTagsProvider {
    public ModBlockTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        valueLookupBuilder(ModBlockTags.BLOCKS_NULLPHASE).add(Blocks.BEDROCK, Blocks.BARRIER, Blocks.OBSIDIAN);
        valueLookupBuilder(ModBlockTags.IMMUNE_TO_STOMPING).add(Blocks.BEDROCK, Blocks.BARRIER);
        valueLookupBuilder(ModBlockTags.IMMUNE_TO_WORLD_EATER).add(Blocks.BEDROCK, Blocks.BARRIER);
        valueLookupBuilder(ModBlockTags.SLASHED_BY_BIOMASS_HARVESTER).add(Blocks.SUGAR_CANE);

        valueLookupBuilder(BlockTags.MINEABLE_WITH_PICKAXE).add(
                ModBlocks.replikaWorkbench.value(),
                ModBlocks.entropicDataMiner.value(),
                ModBlocks.fabricator.value(),
                ModBlocks.assembler.value(),
                ModBlocks.fragmentalWaste.value(),
                ModBlocks.biomassIncubator.value(),
                ModBlocks.biomassHarvester.value(),
                ModBlocks.cobblescrap.value(),
                ModBlocks.lavascrap.value(),
                ModBlocks.worldEater.value(),
                ModBlocks.fragmentalGenerator.value(),
                ModBlocks.fragmentAccelerator.value(),
                ModBlocks.chaosEngine.value(),
                ModBlocks.waterSink.value(),
                ModBlocks.lavaSink.value(),
                ModBlocks.solarSink.value(),
                ModBlocks.recycler.value()
        );
    }
}

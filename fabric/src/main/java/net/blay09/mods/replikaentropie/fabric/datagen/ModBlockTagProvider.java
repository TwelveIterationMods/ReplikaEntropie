package net.blay09.mods.replikaentropie.fabric.datagen;

import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.blay09.mods.replikaentropie.tag.ModBlockTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.references.BlockItemIds;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagsProvider.BlockTagsProvider {
    public ModBlockTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        tag(ModBlockTags.BLOCKS_NULLPHASE).add(BlockItemIds.BEDROCK.block(), BlockItemIds.BARRIER.block(), BlockItemIds.OBSIDIAN.block());
        tag(ModBlockTags.IMMUNE_TO_STOMPING).add(BlockItemIds.BEDROCK.block(), BlockItemIds.BARRIER.block());
        tag(ModBlockTags.IMMUNE_TO_WORLD_EATER).add(BlockItemIds.BEDROCK.block(), BlockItemIds.BARRIER.block());
        tag(ModBlockTags.CRANE_RELOCATION_NOT_SUPPORTED).add(
                BlockItemIds.RAIL.block(),
                BlockItemIds.POWERED_RAIL.block(),
                BlockItemIds.DETECTOR_RAIL.block(),
                BlockItemIds.ACTIVATOR_RAIL.block()
        );
        getOrCreateRawBuilder(ModBlockTags.CRANE_RELOCATION_NOT_SUPPORTED).addOptionalTag(Identifier.fromNamespaceAndPath("c", "relocation_not_supported"));
        tag(ModBlockTags.SLASHED_BY_BIOMASS_HARVESTER).add(BlockItemIds.SUGAR_CANE.block());

        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
                ModBlocks.replikaWorkbench.asResourceKey(),
                ModBlocks.entropicDataMiner.asResourceKey(),
                ModBlocks.fabricator.asResourceKey(),
                ModBlocks.assembler.asResourceKey(),
                ModBlocks.fragmentalWaste.asResourceKey(),
                ModBlocks.biomassIncubator.asResourceKey(),
                ModBlocks.biomassHarvester.asResourceKey(),
                ModBlocks.cobblescrap.asResourceKey(),
                ModBlocks.lavascrap.asResourceKey(),
                ModBlocks.worldEater.asResourceKey(),
                ModBlocks.fragmentalHeater.asResourceKey(),
                ModBlocks.fragmentAccelerator.asResourceKey(),
                ModBlocks.chaosEngine.asResourceKey(),
                ModBlocks.waterSink.asResourceKey(),
                ModBlocks.lavaSink.asResourceKey(),
                ModBlocks.solarSink.asResourceKey(),
                ModBlocks.fragmentedSun.asResourceKey(),
                ModBlocks.recycler.asResourceKey(),
                ModBlocks.crane.asResourceKey()
        );
    }
}

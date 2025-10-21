package net.blay09.mods.replikaentropie.fabric.datagen;

import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.blay09.mods.replikaentropie.tag.ModBlockTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider<Block> {
    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.BLOCK, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        getOrCreateTagBuilder(ModBlockTags.BLOCKS_NULLPHASE).add(Blocks.BEDROCK, Blocks.BARRIER);
        getOrCreateTagBuilder(ModBlockTags.IMMUNE_TO_STOMPING).add(Blocks.BEDROCK, Blocks.BARRIER);
        getOrCreateTagBuilder(ModBlockTags.IMMUNE_TO_WORLD_EATER).add(Blocks.BEDROCK, Blocks.BARRIER);

        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_PICKAXE).add(
                ModBlocks.replikaWorkbench,
                ModBlocks.entropicDataMiner,
                ModBlocks.fabricator,
                ModBlocks.assembler,
                ModBlocks.fragmentalWaste,
                ModBlocks.biomassIncubator,
                ModBlocks.biomassHarvester,
                ModBlocks.cobblescrap,
                ModBlocks.lavascrap,
                ModBlocks.worldEater,
                ModBlocks.defragmentizer,
                ModBlocks.fragmentAccelerator,
                ModBlocks.chaosEngine,
                ModBlocks.recycler
        );
    }
}

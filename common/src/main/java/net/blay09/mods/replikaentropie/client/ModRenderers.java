package net.blay09.mods.replikaentropie.client;

import net.blay09.mods.balm.client.color.block.BalmBlockColorRegistrar;
import net.blay09.mods.balm.client.renderer.blockentity.BalmBlockEntityRendererRegistrar;
import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.blay09.mods.replikaentropie.block.entity.ModBlockEntities;
import net.blay09.mods.replikaentropie.client.renderer.*;
import net.minecraft.client.color.block.BlockTintSources;

import java.util.List;

public class ModRenderers {
    public static void initialize(BalmBlockColorRegistrar registrar) {
        registrar.register(List.of(BlockTintSources.water()), ModBlocks.cobblescrap, ModBlocks.lavascrap);
    }

    public static void initialize(BalmBlockEntityRendererRegistrar registrar) {
        registrar.register(ModBlockEntities.biomassHarvester, BiomassHarvesterRenderer::new);
        registrar.register(ModBlockEntities.biomassIncubator, BiomassIncubatorRenderer::new);
        registrar.register(ModBlockEntities.worldEater, WorldEaterRenderer::new);
        registrar.register(ModBlockEntities.fragmentalGenerator, FragmentalGeneratorRenderer::new);
        registrar.register(ModBlockEntities.chaosEngine, ChaosEngineRenderer::new);
        registrar.register(ModBlockEntities.fragmentAccelerator, FragmentAcceleratorRenderer::new);
    }
}


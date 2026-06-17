package net.blay09.mods.replikaentropie.client;

import net.blay09.mods.balm.client.color.block.BalmBlockColorRegistrar;
import net.blay09.mods.balm.client.renderer.blockentity.BalmBlockEntityRendererRegistrar;
import net.blay09.mods.balm.client.renderer.entity.BalmEntityRendererRegistrar;
import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.blay09.mods.replikaentropie.block.entity.ModBlockEntities;
import net.blay09.mods.replikaentropie.entity.ModEntities;
import net.blay09.mods.replikaentropie.client.renderer.*;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.client.renderer.entity.MinecartRenderer;

import java.util.List;

public class ModRenderers {
    public static void initialize(BalmBlockColorRegistrar registrar) {
        registrar.register(List.of(BlockTintSources.water()), ModBlocks.cobblescrap, ModBlocks.lavascrap, ModBlocks.waterSink);
    }

    public static void initialize(BalmBlockEntityRendererRegistrar registrar) {
        registrar.register(ModBlockEntities.biomassHarvester, BiomassHarvesterRenderer::new);
        registrar.register(ModBlockEntities.biomassIncubator, BiomassIncubatorRenderer::new);
        registrar.register(ModBlockEntities.worldEater, WorldEaterRenderer::new);
        registrar.register(ModBlockEntities.fragmentalHeater, FragmentalHeaterRenderer::new);
        registrar.register(ModBlockEntities.chaosEngine, ChaosEngineRenderer::new);
        registrar.register(ModBlockEntities.fragmentAccelerator, FragmentAcceleratorRenderer::new);
        registrar.register(ModBlockEntities.crane, CraneRenderer::new);
    }

    public static void initialize(BalmEntityRendererRegistrar registrar) {
        registrar.register(ModEntities.wasteBarrelMinecart, context -> new MinecartRenderer(context, ModelLayers.MINECART));
        registrar.register(ModEntities.fragmentalWasteMinecart, context -> new MinecartRenderer(context, ModelLayers.MINECART));
        registrar.register(ModEntities.biomassHarvesterMinecart, BiomassHarvesterMinecartRenderer::new);
    }
}

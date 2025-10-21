package net.blay09.mods.replikaentropie.client;

import net.blay09.mods.balm.api.client.rendering.BalmRenderers;
import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.blay09.mods.replikaentropie.block.entity.ModBlockEntities;
import net.blay09.mods.replikaentropie.client.renderer.BiomassHarvesterRenderer;
import net.blay09.mods.replikaentropie.client.renderer.BiomassIncubatorRenderer;
import net.blay09.mods.replikaentropie.client.renderer.WorldEaterRenderer;
import net.blay09.mods.replikaentropie.client.renderer.DefragmentizerRenderer;
import net.blay09.mods.replikaentropie.client.renderer.ChaosEngineRenderer;
import net.blay09.mods.replikaentropie.client.renderer.FragmentAcceleratorRenderer;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class ModRenderers {
    public static void initialize(BalmRenderers renderers) {
        renderers.setBlockRenderType(() -> ModBlocks.fabricator, RenderType.cutout());
        renderers.setBlockRenderType(() -> ModBlocks.assembler, RenderType.cutout());
        renderers.setBlockRenderType(() -> ModBlocks.biomassIncubator, RenderType.cutout());
        renderers.setBlockRenderType(() -> ModBlocks.cobblescrap, RenderType.cutout());
        renderers.setBlockRenderType(() -> ModBlocks.lavascrap, RenderType.cutout());
        renderers.setBlockRenderType(() -> ModBlocks.worldEater, RenderType.cutout());
        renderers.setBlockRenderType(() -> ModBlocks.recycler, RenderType.cutout());
        renderers.setBlockRenderType(() -> ModBlocks.defragmentizer, RenderType.cutout());
        renderers.setBlockRenderType(() -> ModBlocks.fragmentAccelerator, RenderType.cutout());
        renderers.registerBlockColorHandler(id("biomass_incubator"), (state, level, pos, index) -> level.getBlockTint(pos, BiomeColors.WATER_COLOR_RESOLVER), () -> new Block[]{ModBlocks.biomassIncubator});
        renderers.registerBlockColorHandler(id("cobblescrap"), (state, level, pos, index) -> level.getBlockTint(pos, BiomeColors.WATER_COLOR_RESOLVER), () -> new Block[]{ModBlocks.cobblescrap, ModBlocks.lavascrap});
        renderers.registerItemColorHandler((itemStack, index) -> 0xFF3F76E4, () -> new ItemLike[]{
                ModBlocks.cobblescrap,
                ModBlocks.lavascrap,
                ModBlocks.biomassIncubator
        });

        renderers.registerBlockEntityRenderer(id("biomass_harvester"), ModBlockEntities.biomassHarvester::get, BiomassHarvesterRenderer::new);
        renderers.registerBlockEntityRenderer(id("biomass_incubator"), ModBlockEntities.biomassIncubator::get, BiomassIncubatorRenderer::new);
        renderers.registerBlockEntityRenderer(id("world_eater"), ModBlockEntities.worldEater::get, WorldEaterRenderer::new);
        renderers.registerBlockEntityRenderer(id("defragmentizer"), ModBlockEntities.defragmentizer::get, DefragmentizerRenderer::new);
        renderers.registerBlockEntityRenderer(id("chaos_engine"), ModBlockEntities.chaosEngine::get, ChaosEngineRenderer::new);
        renderers.registerBlockEntityRenderer(id("fragment_accelerator"), ModBlockEntities.fragmentAccelerator::get, FragmentAcceleratorRenderer::new);
    }
}


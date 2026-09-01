package net.blay09.mods.replikaentropie.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.blay09.mods.replikaentropie.block.entity.BiomassIncubatorBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class BiomassIncubatorRenderer implements BlockEntityRenderer<BiomassIncubatorBlockEntity, BiomassIncubatorRenderer.State> {

    private final BlockModelResolver blockModelResolver;
    private final BlockDisplayContext blockDisplayContext = BlockDisplayContext.create();

    public BiomassIncubatorRenderer(BlockEntityRendererProvider.Context context) {
        blockModelResolver = context.blockModelResolver();
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(BiomassIncubatorBlockEntity blockEntity, State state, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, cameraPosition, breakProgress);


        final var soilContainer = blockEntity.getSoilContainer();
        final var soilStack = soilContainer.getItem(0);
        final var soilBlock = Block.byItem(soilStack.getItem());
        var soilState = soilBlock.defaultBlockState();
        if (soilState.is(Blocks.DIRT)) {
            soilState = Blocks.FARMLAND.defaultBlockState();
        }
        if (soilState.hasProperty(BlockStateProperties.MOISTURE) && !blockEntity.getFluidTank().isEmpty(0)) {
            soilState = soilState.setValue(BlockStateProperties.MOISTURE, 7);
        }
        blockModelResolver.update(state.soilBlock, soilState, blockDisplayContext);

        final var seedsContainer = blockEntity.getSeedsContainer();
        final var seedStack = seedsContainer.getItem(0);
        final var seedBlock = Block.byItem(seedStack.getItem());
        var seedState = seedBlock instanceof CropBlock cropBlock
                ? cropBlock.getStateForAge(Mth.floor(cropBlock.getMaxAge() * blockEntity.getGrowthProgress()))
                : seedBlock.defaultBlockState();
        state.seedBlock.clear();
        if (!seedState.isAir()) {
            blockModelResolver.update(state.seedBlock, seedState, blockDisplayContext);
        }
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0.5f, 2 / 16f, 0.5f);
        poseStack.scale(1f - 4 / 16f, 0.001f, 1f - 4 / 16f);
        poseStack.translate(-0.5f, 0f, -0.5f);
        state.soilBlock.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();

        if (!state.seedBlock.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.5f, 3 / 16f, 0.5f);
            poseStack.scale(1f - 4 / 16f, 1f - 4 / 16f, 1f - 4 / 16f);
            poseStack.translate(-0.5f, 0f, -0.5f);
            state.seedBlock.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }

    }

    public static class State extends BlockEntityRenderState {
        public final BlockModelRenderState soilBlock = new BlockModelRenderState();
        public final BlockModelRenderState seedBlock = new BlockModelRenderState();
    }
}

package net.blay09.mods.replikaentropie.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.blay09.mods.replikaentropie.block.entity.CraneBlockEntity;
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
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class CraneRenderer implements BlockEntityRenderer<CraneBlockEntity, CraneRenderer.State> {
    private final BlockModelResolver blockModelResolver;
    private final BlockDisplayContext blockDisplayContext = BlockDisplayContext.create();

    public CraneRenderer(BlockEntityRendererProvider.Context context) {
        blockModelResolver = context.blockModelResolver();
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(CraneBlockEntity blockEntity, State state, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, cameraPosition, breakProgress);
        state.block.clear();
        state.progress = blockEntity.getTransferProgress(partialTick);
        state.shouldRender = !blockEntity.getCarriedState().isAir();
        if (state.shouldRender) {
            final var sourceOffset = blockEntity.getSourcePos().subtract(blockEntity.getBlockPos());
            final var destinationOffset = blockEntity.getDestinationPos().subtract(blockEntity.getBlockPos());
            state.source = new Vec3(sourceOffset.getX(), sourceOffset.getY(), sourceOffset.getZ());
            state.destination = new Vec3(destinationOffset.getX(), destinationOffset.getY(), destinationOffset.getZ());
            blockModelResolver.update(state.block, blockEntity.getCarriedState(), blockDisplayContext);
        }
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (!state.shouldRender) {
            return;
        }

        final double eased = state.progress * state.progress * (3 - 2 * state.progress);
        final double x = state.source.x + (state.destination.x - state.source.x) * eased;
        final double z = state.source.z + (state.destination.z - state.source.z) * eased;
        final double y = Math.sin(state.progress * Math.PI) * 1.25;

        poseStack.pushPose();
        poseStack.translate(x, y, z);
        state.block.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }

    public static class State extends BlockEntityRenderState {
        public final BlockModelRenderState block = new BlockModelRenderState();
        public boolean shouldRender;
        public float progress;
        public Vec3 source = Vec3.ZERO;
        public Vec3 destination = Vec3.ZERO;
    }
}

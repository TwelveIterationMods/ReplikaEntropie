package net.blay09.mods.replikaentropie.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.blay09.mods.replikaentropie.block.entity.ChaosEngineBlockEntity;
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

public class ChaosEngineRenderer implements BlockEntityRenderer<ChaosEngineBlockEntity, ChaosEngineRenderer.State> {

    private final BlockModelResolver blockModelResolver;
    private final BlockDisplayContext blockDisplayContext = BlockDisplayContext.create();

    public ChaosEngineRenderer(BlockEntityRendererProvider.Context context) {
        blockModelResolver = context.blockModelResolver();
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(ChaosEngineBlockEntity blockEntity, State state, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, cameraPosition, breakProgress);
        state.block.clear();
        final var displayBlockState = blockEntity.getDisplayBlockState();
        if (displayBlockState == null) {
            return;
        }

        final var level = blockEntity.getLevel();
        state.time = level != null ? level.getGameTime() : 0;
        blockModelResolver.update(state.block, displayBlockState, blockDisplayContext);
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (state.block.isEmpty()) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.5f, 0.5f, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(state.time * 16));
        poseStack.translate(0f, Math.sin(state.time * 0.85f) * 0.05f, 0f);

        float scale = 0.5f;
        poseStack.scale(scale, scale, scale);
        poseStack.translate(-0.5f, -0.5f, -0.5f);

        state.block.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);

        poseStack.popPose();
    }

    public static class State extends BlockEntityRenderState {
        public final BlockModelRenderState block = new BlockModelRenderState();
        public long time;
    }
}

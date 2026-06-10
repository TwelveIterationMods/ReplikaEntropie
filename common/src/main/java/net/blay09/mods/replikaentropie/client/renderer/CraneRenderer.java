package net.blay09.mods.replikaentropie.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.blay09.mods.replikaentropie.block.entity.CraneBlockEntity;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class CraneRenderer implements BlockEntityRenderer<CraneBlockEntity, CraneRenderer.State> {
    public CraneRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(CraneBlockEntity blockEntity, State state, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, cameraPosition, breakProgress);
        state.block = null;
        final var carriedState = blockEntity.getCarriedState();
        if (!(blockEntity.getLevel() instanceof ClientLevel level) || carriedState.isAir()) {
            return;
        }

        final var sourcePos = blockEntity.getSourcePos();
        final var sourceOffset = sourcePos.subtract(blockEntity.getBlockPos());
        final var destinationOffset = blockEntity.getDestinationPos().subtract(blockEntity.getBlockPos());
        final float progress = blockEntity.getTransferProgress(partialTick);
        final double eased = progress * progress * (3 - 2 * progress);
        final double x = sourceOffset.getX() + (destinationOffset.getX() - sourceOffset.getX()) * eased;
        final double z = sourceOffset.getZ() + (destinationOffset.getZ() - sourceOffset.getZ()) * eased;
        final double y = Math.sin(progress * Math.PI) * 1.25;

        state.offset = new Vec3(x, y, z);
        final BlockPos renderPos = BlockPos.containing(Vec3.atLowerCornerOf(blockEntity.getBlockPos()).add(state.offset));
        state.block = new MovingBlockRenderState();
        state.block.randomSeedPos = sourcePos;
        state.block.blockPos = renderPos;
        state.block.blockState = carriedState;
        state.block.biome = level.getBiome(renderPos);
        state.block.cardinalLighting = level.cardinalLighting();
        state.block.lightEngine = level.getLightEngine();
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (state.block == null) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(state.offset);
        submitNodeCollector.submitMovingBlock(poseStack, state.block);
        poseStack.popPose();
    }

    public static class State extends BlockEntityRenderState {
        public @Nullable MovingBlockRenderState block;
        public Vec3 offset = Vec3.ZERO;
    }
}

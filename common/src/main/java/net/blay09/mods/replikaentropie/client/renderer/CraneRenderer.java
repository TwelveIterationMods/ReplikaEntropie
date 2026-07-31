package net.blay09.mods.replikaentropie.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.blay09.mods.replikaentropie.block.entity.CraneBlockEntity;
import net.blay09.mods.replikaentropie.client.ModBlockStateModels;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.jspecify.annotations.Nullable;

public class CraneRenderer implements BlockEntityRenderer<CraneBlockEntity, CraneRenderer.State> {
    private static final Matrix4fc IDENTITY = new Matrix4f();

    private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;

    public CraneRenderer(BlockEntityRendererProvider.Context context) {
        blockEntityRenderDispatcher = context.blockEntityRenderDispatcher();
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(CraneBlockEntity blockEntity, State state, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, cameraPosition, breakProgress);
        state.block = null;
        state.blockEntity = null;
        state.arm.clear();
        state.magnet.clear();

        final var blockState = blockEntity.getBlockState();
        state.facing = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);

        final var level = blockEntity.getLevel();
        state.armLightCoords = level != null ? LevelRenderer.getLightCoords(level, blockEntity.getBlockPos().above(2)) : state.lightCoords;
        state.magnetLightCoords = state.armLightCoords;

        final var seed = blockEntity.getBlockPos().asLong();
        final var craneArmModel = ModBlockStateModels.craneArm.asBlockStateModel();
        final var craneArmParts = state.arm.setupModel(IDENTITY, craneArmModel.hasMaterialFlag(BakedQuad.FLAG_TRANSLUCENT));
        craneArmModel.collectParts(state.arm.scratchRandomSource(seed), craneArmParts);

        final var craneMagnetModel = ModBlockStateModels.craneMagnet.asBlockStateModel();
        final var craneMagnetParts = state.magnet.setupModel(IDENTITY, craneMagnetModel.hasMaterialFlag(BakedQuad.FLAG_TRANSLUCENT));
        craneMagnetModel.collectParts(state.magnet.scratchRandomSource(seed), craneMagnetParts);

        final var carriedState = blockEntity.getCarriedState();
        if (!(level instanceof ClientLevel clientLevel) || carriedState.isAir()) {
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
        state.block.biome = clientLevel.getBiome(renderPos);
        state.block.cardinalLighting = clientLevel.cardinalLighting();
        state.block.lightEngine = clientLevel.getLightEngine();

        if (carriedState.getBlock() instanceof EntityBlock entityBlock) {
            final var carriedBlockEntity = entityBlock.newBlockEntity(renderPos, carriedState);
            if (carriedBlockEntity != null) {
                carriedBlockEntity.setLevel(clientLevel);
                state.blockEntity = blockEntityRenderDispatcher.tryExtractRenderState(carriedBlockEntity, partialTick, breakProgress);
            }
        }
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (!state.arm.isEmpty() || !state.magnet.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.5f, 2f - 4/16f + 1/64f, 0.5f);
            poseStack.mulPose(Axis.YP.rotationDegrees(-state.facing.toYRot()));
            poseStack.translate(-0.5f, 0f, 0f);
            state.arm.submit(poseStack, submitNodeCollector, state.armLightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.translate(0f, -1/16f, 0.5f - 1/16f);
            state.magnet.submit(poseStack, submitNodeCollector, state.magnetLightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }

        if (state.block == null) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(state.offset);
        submitNodeCollector.submitMovingBlock(poseStack, state.block);
        if (state.blockEntity != null) {
            blockEntityRenderDispatcher.submit(state.blockEntity, poseStack, submitNodeCollector, camera);
        }
        poseStack.popPose();
    }

    public static class State extends BlockEntityRenderState {
        public final BlockModelRenderState arm = new BlockModelRenderState();
        public final BlockModelRenderState magnet = new BlockModelRenderState();
        public @Nullable MovingBlockRenderState block;
        public @Nullable BlockEntityRenderState blockEntity;
        public Vec3 offset = Vec3.ZERO;
        public Direction facing = Direction.NORTH;
        public int armLightCoords;
        public int magnetLightCoords;
    }
}

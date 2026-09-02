package net.blay09.mods.replikaentropie.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.blay09.mods.replikaentropie.block.entity.CraneBlockEntity;
import net.blay09.mods.replikaentropie.client.ModBlockStateModels;
import net.minecraft.client.multiplayer.ClientLevel;
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
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.jspecify.annotations.Nullable;

public class CraneRenderer implements BlockEntityRenderer<CraneBlockEntity, CraneRenderer.State> {
    private static final Matrix4fc IDENTITY = new Matrix4f();
    private static final double LIFTED_BLOCK_Y = 11 / 16f;
    private static final float MAGNET_VIBRATION_DISTANCE = 1f / 48f;
    private static final float BLOCK_VIBRATION_DISTANCE = 1f / 32f;
    private static final int BOTH_VIBRATION_START = CraneBlockEntity.MAGNET_VIBRATION_TICKS;
    private static final int BLOCK_SNAP_START = BOTH_VIBRATION_START + CraneBlockEntity.BOTH_VIBRATION_TICKS;
    private static final int SNAP_PAUSE_START = BLOCK_SNAP_START + CraneBlockEntity.BLOCK_SNAP_TICKS;
    private static final int ARM_ROTATION_START = SNAP_PAUSE_START + CraneBlockEntity.SNAP_PAUSE_TICKS;
    private static final int MAGNET_BOUNCE_START = ARM_ROTATION_START + CraneBlockEntity.ARM_ROTATION_TICKS;
    private static final int BLOCK_DROP_START = MAGNET_BOUNCE_START;

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
        state.offset = Vec3.ZERO;
        state.magnetOffsetX = 0f;
        state.magnetOffsetZ = 0f;
        state.magnetScale = 1f;
        state.armRotationDegrees = 0f;

        final var blockState = blockEntity.getBlockState();
        state.facing = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);

        final var level = blockEntity.getLevel();
        state.armLightCoords = level != null ? LightCoordsUtil.getLightCoords(level, blockEntity.getBlockPos().above(2)) : state.lightCoords;
        state.magnetLightCoords = state.armLightCoords;

        final var seed = blockEntity.getBlockPos().asLong();
        final var craneArmModel = ModBlockStateModels.craneArm.asBlockStateModel();
        final var craneArmParts = state.arm.setupModel(IDENTITY, craneArmModel.hasMaterialFlag(BakedQuad.FLAG_TRANSLUCENT));
        craneArmModel.collectParts(state.arm.scratchRandomSource(seed), craneArmParts);

        final var craneMagnetModel = ModBlockStateModels.craneMagnet.asBlockStateModel();
        final var craneMagnetParts = state.magnet.setupModel(IDENTITY, craneMagnetModel.hasMaterialFlag(BakedQuad.FLAG_TRANSLUCENT));
        craneMagnetModel.collectParts(state.magnet.scratchRandomSource(seed), craneMagnetParts);

        final var carriedState = blockEntity.getCarriedState();
        if (blockEntity.isArmReturning()) {
            final float armReturnProgress = smoothProgress(blockEntity.getArmReturnTicks(partialTick), 0, CraneBlockEntity.ARM_RETURN_TICKS);
            state.armRotationDegrees = 90f * (1f - armReturnProgress);
        }

        if (!(level instanceof ClientLevel clientLevel) || carriedState.isAir()) {
            return;
        }

        final var sourcePos = blockEntity.getSourcePos();
        final var destinationPos = blockEntity.getDestinationPos();
        final var sourceOffset = sourcePos.subtract(blockEntity.getBlockPos());
        final var destinationOffset = destinationPos.subtract(blockEntity.getBlockPos());
        final var fromOffset = blockEntity.isReturningToSource() ? destinationOffset : sourceOffset;
        final var toOffset = blockEntity.isReturningToSource() ? sourceOffset : destinationOffset;
        final float animationTicks = blockEntity.getAnimationTicks(partialTick);
        final float rotateProgress = smoothProgress(animationTicks, ARM_ROTATION_START, CraneBlockEntity.ARM_ROTATION_TICKS);
        final float carriedRotationDegrees = blockEntity.isReturningToSource() ? 90f * (1f - rotateProgress) : 90f * rotateProgress;
        state.armRotationDegrees = carriedRotationDegrees;

        if (animationTicks < BLOCK_SNAP_START) {
            final float vibrationProgress = stageProgress(animationTicks, BOTH_VIBRATION_START, CraneBlockEntity.BOTH_VIBRATION_TICKS);
            state.magnetOffsetX = Mth.sin(animationTicks * 2.8f) * MAGNET_VIBRATION_DISTANCE;
            state.magnetOffsetZ = Mth.cos(animationTicks * 3.4f) * MAGNET_VIBRATION_DISTANCE;
            final double x = fromOffset.getX() + Mth.sin(animationTicks * 3.7f) * BLOCK_VIBRATION_DISTANCE * vibrationProgress;
            final double z = fromOffset.getZ() + Mth.cos(animationTicks * 3.1f) * BLOCK_VIBRATION_DISTANCE * vibrationProgress;
            state.offset = new Vec3(x, 0, z);
        } else if (animationTicks < SNAP_PAUSE_START) {
            final float snapProgress = easeOutCubic(stageProgress(animationTicks, BLOCK_SNAP_START, CraneBlockEntity.BLOCK_SNAP_TICKS));
            state.offset = new Vec3(fromOffset.getX(), LIFTED_BLOCK_Y * snapProgress, fromOffset.getZ());
        } else if (animationTicks < BLOCK_DROP_START) {
            state.offset = rotateHorizontalOffset(fromOffset, blockEntity.isReturningToSource() ? -rotateProgress : rotateProgress)
                    .add(0, LIFTED_BLOCK_Y, 0);
        } else {
            final float dropProgress = easeInCubic(stageProgress(animationTicks, BLOCK_DROP_START, CraneBlockEntity.BLOCK_DROP_TICKS));
            state.offset = new Vec3(toOffset.getX(), LIFTED_BLOCK_Y * (1f - dropProgress), toOffset.getZ());
        }

        final float bounceProgress = stageProgress(animationTicks, MAGNET_BOUNCE_START, CraneBlockEntity.MAGNET_BOUNCE_TICKS);
        if (bounceProgress > 0f && bounceProgress < 1f) {
            state.magnetScale = 1f - Mth.sin(bounceProgress * Mth.PI) * 0.12f;
        }

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
                state.blockEntity = blockEntityRenderDispatcher.tryExtractRenderState(carriedBlockEntity, partialTick, breakProgress, false);
            }
        }
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (!state.arm.isEmpty() || !state.magnet.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.5f, 2f - 4/16f + 1/64f, 0.5f);
            poseStack.rotateDegrees(Axis.YP, -state.facing.toYRot());
            poseStack.rotateDegrees(Axis.YP, -state.armRotationDegrees);
            poseStack.translate(-0.5f, 0f, 0f);
            state.arm.submit(poseStack, submitNodeCollector, state.armLightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.translate(state.magnetOffsetX, -1/16f, 0.5f - 1/16f + state.magnetOffsetZ);
            poseStack.translate(0.5f, 0.5f, 0.5f);
            poseStack.scale(state.magnetScale, state.magnetScale, state.magnetScale);
            poseStack.translate(-0.5f, -0.5f, -0.5f);
            state.magnet.submit(poseStack, submitNodeCollector, state.magnetLightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }

        if (state.block == null) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(state.offset);
        submitNodeCollector.submitMovingBlock(poseStack, state.block, 0);
        if (state.blockEntity != null) {
            blockEntityRenderDispatcher.submit(state.blockEntity, poseStack, submitNodeCollector, camera);
        }
        poseStack.popPose();
    }

    private static float stageProgress(float animationTicks, int startTick, int durationTicks) {
        return Mth.clamp((animationTicks - startTick) / durationTicks, 0f, 1f);
    }

    private static float smoothProgress(float animationTicks, int startTick, int durationTicks) {
        final float progress = stageProgress(animationTicks, startTick, durationTicks);
        return progress * progress * (3f - 2f * progress);
    }

    private static float easeOutCubic(float progress) {
        final float remaining = 1f - progress;
        return 1f - remaining * remaining * remaining;
    }

    private static float easeInCubic(float progress) {
        return progress * progress * progress;
    }

    private static Vec3 rotateHorizontalOffset(BlockPos offset, float progress) {
        final double radians = progress * Mth.HALF_PI;
        final double sin = Math.sin(radians);
        final double cos = Math.cos(radians);
        final double x = offset.getX() * cos - offset.getZ() * sin;
        final double z = offset.getX() * sin + offset.getZ() * cos;
        return new Vec3(x, 0, z);
    }

    public static class State extends BlockEntityRenderState {
        public final BlockModelRenderState arm = new BlockModelRenderState();
        public final BlockModelRenderState magnet = new BlockModelRenderState();
        public @Nullable MovingBlockRenderState block;
        public @Nullable BlockEntityRenderState blockEntity;
        public Vec3 offset = Vec3.ZERO;
        public Direction facing = Direction.NORTH;
        public float armRotationDegrees;
        public float magnetOffsetX;
        public float magnetOffsetZ;
        public float magnetScale = 1f;
        public int armLightCoords;
        public int magnetLightCoords;
    }
}

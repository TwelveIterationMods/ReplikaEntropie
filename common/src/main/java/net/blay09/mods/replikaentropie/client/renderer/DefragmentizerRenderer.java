package net.blay09.mods.replikaentropie.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.HashCommon;
import net.blay09.mods.replikaentropie.block.entity.DefragmentizerBlockEntity;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class DefragmentizerRenderer implements BlockEntityRenderer<DefragmentizerBlockEntity, DefragmentizerRenderer.State> {

    record AnimationStage(float lerpStartY, float lerpEndY, float wobbleAmplitude, float wobbleSpeed, float scale) {
        public float computeOffset(float time) {
            return lerpStartY + (lerpEndY - lerpStartY) * time;
        }

        public float computeWobbleX(float time) {
            return (float) Math.sin(Math.PI * wobbleSpeed * time) * wobbleAmplitude;
        }

        public float computeWobbleZ(float time) {
            return (float) Math.cos(Math.PI * wobbleSpeed * time) * wobbleAmplitude;
        }
    }

    private static final List<Pair<Float, AnimationStage>> STAGES = List.of(
            Pair.of(0f, new AnimationStage(14f / 16f, 19f / 32f, 0f, 0f, 0.5f)),
            Pair.of(0.20f, new AnimationStage(19f / 32f, 19f / 32f, 0.025f, 12f, 0.5f)),
            Pair.of(0.33f, new AnimationStage(18f / 32f, 11f / 32f, 0f, 0f, 0.4f)),
            Pair.of(0.50f, new AnimationStage(11f / 32f, 11f / 32f, 0.025f, 6f, 0.35f)),
            Pair.of(0.55f, new AnimationStage(11f / 32f, 11f / 32f, 0.03f, 24f, 0.3f)),
            Pair.of(0.60f, new AnimationStage(11f / 32f, 11f / 32f, 0.03f, 48f, 0.3f)),
            Pair.of(0.66f, new AnimationStage(10f / 32f, 0f, 0f, 0f, 0.28f)),
            Pair.of(1f, new AnimationStage(0f, 0f, 0f, 0f, 0.28f))
    );

    private final ItemModelResolver itemModelResolver;

    public DefragmentizerRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(DefragmentizerBlockEntity blockEntity, State state, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, cameraPosition, breakProgress);
        for (int i = 0; i < state.items.length; i++) {
            state.items[i] = null;
        }

        final var xs = new float[]{
                5f / 16f,
                5f / 16f,
                10f / 16f,
                10f / 16f
        };
        final var zs = new float[]{
                5f / 16f,
                10f / 16f,
                5f / 16f,
                10f / 16f
        };

        final var inputs = blockEntity.getInputContainer();

        final var shardStack = ModItems.fragments.createStack();
        final var seed = HashCommon.long2int(blockEntity.getBlockPos().asLong());

        for (int i = 0; i < inputs.getContainerSize(); i++) {
            final var inputStack = inputs.getItem(i);
            if (inputStack.isEmpty()) {
                continue;
            }

            final var baseX = xs[i];
            final var baseZ = zs[i];

            final var progress = blockEntity.getClientProcessingProgress(i, partialTick);

            var stageIndex = 0;
            for (int j = STAGES.size() - 2; j >= 0; j--) {
                final var stage = STAGES.get(j);
                if (progress >= stage.getFirst()) {
                    stageIndex = j;
                    break;
                }
            }

            final var timedStage = STAGES.get(stageIndex);
            final var startTime = timedStage.getFirst();
            final var endTime = STAGES.get(stageIndex + 1).getFirst();
            final var time = (progress - startTime) / (endTime - startTime);
            final var stage = timedStage.getSecond();
            final var y = stage.computeOffset(time);
            final var wobbleX = stage.computeWobbleX(time);
            final var wobbleZ = stage.computeWobbleZ(time);

            final var itemState = new ItemStackRenderState();
            itemModelResolver.updateForTopItem(itemState, progress < 0.66f ? inputStack : shardStack, ItemDisplayContext.GROUND, blockEntity.getLevel(), null, seed + i);
            state.items[i] = itemState;
            state.xs[i] = baseX + wobbleX;
            state.ys[i] = y;
            state.zs[i] = baseZ + wobbleZ;
            state.rotations[i] = blockEntity.getClientItemRotation(i);
            state.scales[i] = stage.scale;
        }
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        for (int i = 0; i < state.items.length; i++) {
            final var itemState = state.items[i];
            if (itemState == null) {
                continue;
            }

            poseStack.pushPose();
            poseStack.translate(state.xs[i], state.ys[i], state.zs[i]);
            poseStack.mulPose(Axis.XP.rotationDegrees(90f));
            poseStack.mulPose(Axis.ZP.rotationDegrees(state.rotations[i]));
            poseStack.scale(state.scales[i], state.scales[i], state.scales[i]);
            itemState.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }
    }

    public static class State extends BlockEntityRenderState {
        public final ItemStackRenderState[] items = new ItemStackRenderState[4];
        public final float[] xs = new float[4];
        public final float[] ys = new float[4];
        public final float[] zs = new float[4];
        public final float[] rotations = new float[4];
        public final float[] scales = new float[4];
    }
}

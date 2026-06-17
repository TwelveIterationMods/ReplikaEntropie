package net.blay09.mods.replikaentropie.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.HashCommon;
import net.blay09.mods.replikaentropie.block.entity.FragmentalGeneratorBlockEntity;
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

public class FragmentalGeneratorRenderer implements BlockEntityRenderer<FragmentalGeneratorBlockEntity, FragmentalGeneratorRenderer.State> {

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

    public FragmentalGeneratorRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(FragmentalGeneratorBlockEntity blockEntity, State state, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, cameraPosition, breakProgress);
        for (final var contentState : state.content) {
            contentState.item.clear();
        }

        final var xs = new float[]{
                4f / 16f,
                8f / 16f,
                12f / 16f,
                4f / 16f,
                8f / 16f,
                12f / 16f
        };
        final var zs = new float[]{
                4f / 16f,
                4f / 16f,
                4f / 16f,
                12f / 16f,
                12f / 16f,
                12f / 16f
        };

        final var inputs = blockEntity.getContainer();

        final var seed = HashCommon.long2int(blockEntity.getBlockPos().asLong());

        for (int i = 0; i < FragmentalGeneratorBlockEntity.INPUT_SLOT_COUNT; i++) {
            final var inputStack = inputs.getItem(FragmentalGeneratorBlockEntity.INPUT_SLOT_START + i);
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

            final var contentState = state.content.get(i);
            itemModelResolver.updateForTopItem(contentState.item, progress < 0.66f ? inputStack : ItemStack.EMPTY, ItemDisplayContext.GROUND, blockEntity.getLevel(), null, seed + i);
            contentState.x = baseX + wobbleX;
            contentState.y = y;
            contentState.z = baseZ + wobbleZ;
            contentState.rotation = blockEntity.getClientItemRotation(i);
            contentState.scale = stage.scale;
        }
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        for (final var contentState : state.content) {
            if (!contentState.item.isEmpty()) {
                poseStack.pushPose();
                poseStack.translate(contentState.x, contentState.y, contentState.z);
                poseStack.mulPose(Axis.XP.rotationDegrees(90f));
                poseStack.mulPose(Axis.ZP.rotationDegrees(contentState.rotation));
                poseStack.scale(contentState.scale, contentState.scale, contentState.scale);
                contentState.item.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
                poseStack.popPose();
            }
        }
    }

    public static class State extends BlockEntityRenderState {
        public final List<ContentRenderState> content = List.of(
                new ContentRenderState(),
                new ContentRenderState(),
                new ContentRenderState(),
                new ContentRenderState(),
                new ContentRenderState(),
                new ContentRenderState()
        );
    }

    public static class ContentRenderState {
        public final ItemStackRenderState item = new ItemStackRenderState();
        public float x;
        public float y;
        public float z;
        public float rotation;
        public float scale;
    }
}

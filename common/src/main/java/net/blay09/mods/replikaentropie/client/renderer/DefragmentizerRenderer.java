package net.blay09.mods.replikaentropie.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import net.blay09.mods.replikaentropie.block.entity.DefragmentizerBlockEntity;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class DefragmentizerRenderer implements BlockEntityRenderer<DefragmentizerBlockEntity> {

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

    private final ItemRenderer itemRenderer;

    public DefragmentizerRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(DefragmentizerBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
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

        final var shardStack = new ItemStack(ModItems.fragments);

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

            poseStack.pushPose();
            poseStack.translate(baseX + wobbleX, y, baseZ + wobbleZ);
            poseStack.mulPose(Axis.XP.rotationDegrees(90f));
            poseStack.mulPose(Axis.ZP.rotationDegrees(blockEntity.getClientItemRotation(i)));
            poseStack.scale(stage.scale, stage.scale, stage.scale);

            itemRenderer.renderStatic(
                    progress < 0.66f ? inputStack : shardStack,
                    ItemDisplayContext.GROUND,
                    packedLight,
                    packedOverlay,
                    poseStack,
                    buffer,
                    blockEntity.getLevel(),
                    0
            );

            poseStack.popPose();
        }
    }

}

package net.blay09.mods.replikaentropie.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.blay09.mods.replikaentropie.block.entity.FragmentAcceleratorBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;

public class FragmentAcceleratorRenderer implements BlockEntityRenderer<FragmentAcceleratorBlockEntity> {

    private final ItemRenderer itemRenderer;

    public FragmentAcceleratorRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(FragmentAcceleratorBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (!blockEntity.isClientSpinning()) {
            return;
        }

        final var inputs = blockEntity.getInputContainer();
        final var segments = inputs.getContainerSize();
        final var baseY = 7f / 16f;
        final var radius = 4f / 16f;
        final var scale = 0.375f;

        final var angle = blockEntity.getClientAngle();
        for (int i = 0; i < segments; i++) {
            final var itemStack = inputs.getItem(i);
            if (itemStack.isEmpty()) {
                continue;
            }

            final var slotAngle = angle + (360f / segments) * i;
            final var slotRad = Math.toRadians(slotAngle);
            final var x = 0.5f + (float) Math.cos(slotRad) * radius;
            final var z = 0.5f + (float) Math.sin(slotRad) * radius;

            poseStack.pushPose();
            poseStack.translate(x, baseY, z);
            poseStack.mulPose(Axis.YP.rotationDegrees(180f - slotAngle));
            poseStack.scale(scale, scale, scale);

            itemRenderer.renderStatic(
                    itemStack,
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

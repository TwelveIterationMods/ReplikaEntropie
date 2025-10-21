package net.blay09.mods.replikaentropie.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.blay09.mods.replikaentropie.block.entity.BiomassHarvesterBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;

public class BiomassHarvesterRenderer implements BlockEntityRenderer<BiomassHarvesterBlockEntity> {

    private final ItemRenderer itemRenderer;

    public BiomassHarvesterRenderer(BlockEntityRendererProvider.Context context) {
        itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(BiomassHarvesterBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(Axis.XP.rotationDegrees(90f));
        final var scale = 1.25f;
        poseStack.scale(scale, scale, scale);

        final var weaponsContainer = blockEntity.getWeaponsContainer();
        final var level = blockEntity.getLevel();
        final var time = ((level != null ? level.getGameTime() : 0) + partialTick) / 20f;

        final var state = blockEntity.getState();

        final var warningWobble = (float) Math.sin(time * Math.PI * 4f) * 0.02f;
        final var spinDegrees = blockEntity.getClientSpinDegrees(partialTick);

        for (int i = 0; i < weaponsContainer.getContainerSize(); i++) {
            final var itemStack = weaponsContainer.getItem(i);
            if (itemStack.isEmpty()) {
                continue;
            }

            poseStack.pushPose();
            poseStack.mulPose(Axis.ZP.rotationDegrees(i * 90f));
            poseStack.mulPose(Axis.ZP.rotationDegrees(spinDegrees));

            if (state == BiomassHarvesterBlockEntity.State.WARNING) {
                poseStack.translate(0.35f + warningWobble, 0.25f + warningWobble, 0f);
            } else {
                poseStack.translate(0.35f, 0.25f, 0f);
            }

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

        poseStack.popPose();
    }
}

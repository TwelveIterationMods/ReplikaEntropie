package net.blay09.mods.replikaentropie.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.blay09.mods.replikaentropie.block.entity.ChaosEngineBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class ChaosEngineRenderer implements BlockEntityRenderer<ChaosEngineBlockEntity> {

    private final BlockRenderDispatcher blockRenderer;

    public ChaosEngineRenderer(BlockEntityRendererProvider.Context context) {
        blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(ChaosEngineBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        final var displayBlockState = blockEntity.getDisplayBlockState();
        if (displayBlockState == null) {
            return;
        }

        final var level = blockEntity.getLevel();
        final var time = level != null ? level.getGameTime() : 0;

        poseStack.pushPose();
        poseStack.translate(0.5f, 0.5f, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(time * 16));
        poseStack.translate(0f, Math.sin(time * 0.85f) * 0.05f, 0f);

        float scale = 0.5f;
        poseStack.scale(scale, scale, scale);
        poseStack.translate(-0.5f, -0.5f, -0.5f);

        blockRenderer.renderSingleBlock(displayBlockState, poseStack, buffer, packedLight, packedOverlay);

        poseStack.popPose();
    }
}

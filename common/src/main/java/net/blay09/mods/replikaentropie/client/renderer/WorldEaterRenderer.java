package net.blay09.mods.replikaentropie.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.math.Axis;
import net.blay09.mods.replikaentropie.block.entity.WorldEaterBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class WorldEaterRenderer implements BlockEntityRenderer<WorldEaterBlockEntity> {

    private final BlockRenderDispatcher blockRenderer;

    public WorldEaterRenderer(BlockEntityRendererProvider.Context context) {
        blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(WorldEaterBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        final var level = blockEntity.getLevel();
        if (level == null) {
            return;
        }

        final var pos = blockEntity.getBlockPos();
        final var state = blockEntity.getBlockState();
        final var facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        if (!Block.shouldRenderFace(state, level, pos, facing, pos.relative(facing))) {
            return;
        }

        final var container = blockEntity.getPreviewContainer();

        final var columns = 5;
        final var rows = 3;
        final var gridWidth = 12f / 16f;
        final var gridHeight = 7f / 16f;

        final var cellWidth = gridWidth / columns;
        final var cellHeight = gridHeight / rows;
        final var startX = -gridWidth / 2f + cellWidth / 2f;
        final var startY = gridHeight / 2f - cellHeight / 2f;

        poseStack.pushPose();
        poseStack.translate(0.5f, 0.5f, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
        poseStack.translate(0f, 0f, 0.38f);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                final var index = row * columns + col;
                if (index >= container.getContainerSize()) {
                    continue;
                }

                final var itemStack = container.getItem(index);
                if (itemStack.isEmpty()) {
                    continue;
                }

                poseStack.pushPose();
                final var x = startX + col * cellWidth;
                final var y = startY - row * cellHeight;
                poseStack.translate(x, y, 0f);

                final var scale = Math.min(cellWidth, cellHeight) * 0.95f;
                poseStack.scale(scale, scale, scale);
                poseStack.translate(-0.5f, -0.5f, -0.5f);

                final var block = Block.byItem(itemStack.getItem());
                final var blockState = block.defaultBlockState();
                blockRenderer.renderSingleBlock(blockState, poseStack, buffer, packedLight, packedOverlay);

                final var progress = 0; // POSTJAM implement progress rendering of world eater
                if (progress > 0) {
                    final var renderBuffers = Minecraft.getInstance().renderBuffers();
                    final var consumer = new SheetedDecalTextureGenerator(renderBuffers.crumblingBufferSource().getBuffer(ModelBakery.DESTROY_TYPES.get(progress)), poseStack.last().pose(), poseStack.last().normal(), 1f);
                    blockRenderer.renderBreakingTexture(level.getBlockState(pos), pos, level, poseStack, consumer);
                }

                poseStack.popPose();
            }
        }

        poseStack.popPose();
    }
}

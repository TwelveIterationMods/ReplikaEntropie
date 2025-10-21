package net.blay09.mods.replikaentropie.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.blay09.mods.replikaentropie.block.entity.BiomassIncubatorBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;

public class BiomassIncubatorRenderer implements BlockEntityRenderer<BiomassIncubatorBlockEntity> {

    private final BlockRenderDispatcher blockRenderer;

    public BiomassIncubatorRenderer(BlockEntityRendererProvider.Context context) {
        blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(BiomassIncubatorBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        final var scale = 0.25f;
        final var xs = new float[]{4f / 16f, 8f / 16f, 12f / 16f};
        final var y = 0.126f;
        final var zs = new float[]{7f / 16f, 10f / 16f, 7f / 16f};

        final var soilContainer = blockEntity.getSoilContainer();
        final var seedsContainer = blockEntity.getSeedsContainer();

        for (int i = 0; i < 3; i++) {
            poseStack.pushPose();
            poseStack.translate(xs[i], y, zs[i]);
            poseStack.scale(scale, scale, scale);
            poseStack.translate(-0.5f, 0f, -0.5f);

            final var soilStack = soilContainer.getItem(i);
            final var soilBlock = Block.byItem(soilStack.getItem());
            final var soilState = soilBlock.defaultBlockState();
            blockRenderer.renderSingleBlock(
                    soilState,
                    poseStack,
                    buffer,
                    packedLight,
                    packedOverlay
            );

            final var seedStack = seedsContainer.getItem(i);
            final var seedBlock = Block.byItem(seedStack.getItem());
            var seedState = seedBlock instanceof CropBlock cropBlock
                    ? cropBlock.getStateForAge(Mth.floor(cropBlock.getMaxAge() * blockEntity.getGrowthProgress(i)))
                    : seedBlock.defaultBlockState();
            if (!seedState.isAir()) {
                poseStack.pushPose();
                poseStack.translate(0f, 1f, 0f);
                blockRenderer.renderSingleBlock(
                        seedState,
                        poseStack,
                        buffer,
                        packedLight,
                        packedOverlay
                );
                poseStack.popPose();
            }

            poseStack.popPose();
        }
    }

}

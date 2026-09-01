package net.blay09.mods.replikaentropie.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.blay09.mods.replikaentropie.block.entity.WorldEaterBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import org.jspecify.annotations.Nullable;

public class WorldEaterRenderer implements BlockEntityRenderer<WorldEaterBlockEntity, WorldEaterRenderer.State> {

    private static final int[] MENU_RENDER_ORDER = {
            10, 11, 12, 13, 14,
            9, 8, 7, 6, 5,
            0, 1, 2, 3, 4
    };

    private final BlockModelResolver blockModelResolver;
    private final BlockDisplayContext blockDisplayContext = BlockDisplayContext.create();

    public WorldEaterRenderer(BlockEntityRendererProvider.Context context) {
        blockModelResolver = context.blockModelResolver();
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(WorldEaterBlockEntity blockEntity, State state, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, cameraPosition, breakProgress);
        state.shouldRender = false;
        state.shouldRenderScanArea = false;
        for (final var block : state.blocks) {
            block.clear();
        }

        final var level = blockEntity.getLevel();
        if (level == null) {
            return;
        }

        final var pos = blockEntity.getBlockPos();
        final var minecraft = Minecraft.getInstance();
        state.shouldRenderScanArea = minecraft.player != null
                && minecraft.player.isShiftKeyDown()
                && minecraft.hitResult instanceof BlockHitResult hitResult
                && hitResult.getBlockPos().equals(pos);
        if (state.shouldRenderScanArea) {
            state.scanArea = blockEntity.getScanArea().inflate(0.002).move(pos.multiply(-1));
        }

        final var blockState = blockEntity.getBlockState();
        final var facing = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        if (!Block.shouldRenderFace(blockState, level.getBlockState(pos.relative(facing)), facing)) {
            return;
        }

        state.shouldRender = true;
        state.facing = facing;

        final var container = blockEntity.getPreviewContainer();
        for (int i = 0; i < container.getContainerSize() && i < state.blocks.length; i++) {
            final var itemStack = container.getItem(i);
            if (itemStack.isEmpty()) {
                continue;
            }

            final var block = Block.byItem(itemStack.getItem());
            blockModelResolver.update(state.blocks[i], block.defaultBlockState(), blockDisplayContext);
        }
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (state.shouldRenderScanArea) {
            submitScanArea(state.scanArea, poseStack, submitNodeCollector);
        }

        if (!state.shouldRender) {
            return;
        }

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
        poseStack.mulPose(Axis.YP.rotationDegrees(-state.facing.toYRot()));
        poseStack.translate(0f, 0f, 0.38f);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                final var index = MENU_RENDER_ORDER[row * columns + col];
                final var block = state.blocks[index];
                if (block.isEmpty()) {
                    continue;
                }

                poseStack.pushPose();
                final var x = startX + col * cellWidth;
                final var y = startY - row * cellHeight;
                poseStack.translate(x, y, 0f);

                final var scale = Math.min(cellWidth, cellHeight) * 0.95f;
                poseStack.scale(scale, scale, scale);
                poseStack.translate(-0.5f, -0.5f, -0.5f);

                block.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);

                poseStack.popPose();
            }
        }

        poseStack.popPose();
    }

    private static void submitScanArea(AABB area, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
        final float width = Minecraft.getInstance().gameRenderer.gameRenderState().windowRenderState.appropriateLineWidth * 2f;
        submitNodeCollector.submitShapeOutline(poseStack, Shapes.create(area), RenderTypes.lines(), 0xFFFFFF00, width, false);
    }

    public static class State extends BlockEntityRenderState {
        public final BlockModelRenderState[] blocks = new BlockModelRenderState[]{
                new BlockModelRenderState(), new BlockModelRenderState(), new BlockModelRenderState(), new BlockModelRenderState(), new BlockModelRenderState(),
                new BlockModelRenderState(), new BlockModelRenderState(), new BlockModelRenderState(), new BlockModelRenderState(), new BlockModelRenderState(),
                new BlockModelRenderState(), new BlockModelRenderState(), new BlockModelRenderState(), new BlockModelRenderState(), new BlockModelRenderState()
        };
        public boolean shouldRender;
        public boolean shouldRenderScanArea;
        public AABB scanArea = AABB.ofSize(Vec3.ZERO, 0, 0, 0);
        public Direction facing = Direction.NORTH;
    }
}

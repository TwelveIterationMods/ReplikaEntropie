package net.blay09.mods.replikaentropie.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.HashCommon;
import net.blay09.mods.replikaentropie.block.entity.FragmentAcceleratorBlockEntity;
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
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FragmentAcceleratorRenderer implements BlockEntityRenderer<FragmentAcceleratorBlockEntity, FragmentAcceleratorRenderer.State> {

    private final ItemModelResolver itemModelResolver;

    public FragmentAcceleratorRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(FragmentAcceleratorBlockEntity blockEntity, State state, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, cameraPosition, breakProgress);
        state.items.clear();

        if (!blockEntity.isClientSpinning()) {
            return;
        }

        final var inputs = blockEntity.getInputContainer();
        final var segments = inputs.getContainerSize();
        final var baseY = 7f / 16f;
        final var radius = 4f / 16f;
        final var scale = 0.375f;

        final var angle = blockEntity.getClientAngle();
        final var seed = HashCommon.long2int(blockEntity.getBlockPos().asLong());
        for (int i = 0; i < segments; i++) {
            final var itemStack = inputs.getItem(i);
            if (itemStack.isEmpty()) {
                continue;
            }

            final var slotAngle = angle + (360f / segments) * i;
            final var slotRad = Math.toRadians(slotAngle);
            final var x = 0.5f + (float) Math.cos(slotRad) * radius;
            final var z = 0.5f + (float) Math.sin(slotRad) * radius;

            final var itemState = new ItemStackRenderState();
            itemModelResolver.updateForTopItem(itemState, itemStack, ItemDisplayContext.GROUND, blockEntity.getLevel(), null, seed + i);
            state.items.add(new ItemRenderState(itemState, x, baseY, z, 180f - slotAngle, scale));
        }
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        for (final var item : state.items) {
            poseStack.pushPose();
            poseStack.translate(item.x, item.y, item.z);
            poseStack.mulPose(Axis.YP.rotationDegrees(item.yRot));
            poseStack.scale(item.scale, item.scale, item.scale);
            item.item.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }
    }

    public static class State extends BlockEntityRenderState {
        public final List<ItemRenderState> items = new ArrayList<>();
    }

    public record ItemRenderState(ItemStackRenderState item, float x, float y, float z, float yRot, float scale) {
    }
}

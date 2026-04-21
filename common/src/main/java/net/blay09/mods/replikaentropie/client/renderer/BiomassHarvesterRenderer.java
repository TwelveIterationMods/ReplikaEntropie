package net.blay09.mods.replikaentropie.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.HashCommon;
import net.blay09.mods.replikaentropie.block.entity.BiomassHarvesterBlockEntity;
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

public class BiomassHarvesterRenderer implements BlockEntityRenderer<BiomassHarvesterBlockEntity, BiomassHarvesterRenderer.State> {

    private final ItemModelResolver itemModelResolver;

    public BiomassHarvesterRenderer(BlockEntityRendererProvider.Context context) {
        itemModelResolver = context.itemModelResolver();
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(BiomassHarvesterBlockEntity blockEntity, State state, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, cameraPosition, breakProgress);
        for (int i = 0; i < state.items.length; i++) {
            state.items[i] = null;
        }

        final var weaponsContainer = blockEntity.getWeaponsContainer();
        final var level = blockEntity.getLevel();
        final var time = ((level != null ? level.getGameTime() : 0) + partialTick) / 20f;

        final var blockEntityState = blockEntity.getState();

        final var warningWobble = (float) Math.sin(time * Math.PI * 4f) * 0.02f;
        state.spinDegrees = blockEntity.getClientSpinDegrees(partialTick);
        state.warningWobble = blockEntityState == BiomassHarvesterBlockEntity.State.WARNING ? warningWobble : 0f;

        final var seed = HashCommon.long2int(blockEntity.getBlockPos().asLong());
        for (int i = 0; i < weaponsContainer.getContainerSize(); i++) {
            final var itemStack = weaponsContainer.getItem(i);
            if (itemStack.isEmpty()) {
                continue;
            }

            final var itemState = new ItemStackRenderState();
            itemModelResolver.updateForTopItem(itemState, itemStack, ItemDisplayContext.GROUND, blockEntity.getLevel(), null, seed + i);
            state.items[i] = itemState;
        }
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(Axis.XP.rotationDegrees(90f));
        final var scale = 1.25f;
        poseStack.scale(scale, scale, scale);

        for (int i = 0; i < state.items.length; i++) {
            final var itemState = state.items[i];
            if (itemState == null) {
                continue;
            }

            poseStack.pushPose();
            poseStack.mulPose(Axis.ZP.rotationDegrees(i * 90f));
            poseStack.mulPose(Axis.ZP.rotationDegrees(state.spinDegrees));

            if (state.warningWobble != 0f) {
                poseStack.translate(0.35f + state.warningWobble, 0.25f + state.warningWobble, 0f);
            } else {
                poseStack.translate(0.35f, 0.25f, 0f);
            }

            itemState.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }

        poseStack.popPose();
    }

    public static class State extends BlockEntityRenderState {
        public final ItemStackRenderState[] items = new ItemStackRenderState[4];
        public float spinDegrees;
        public float warningWobble;
    }
}

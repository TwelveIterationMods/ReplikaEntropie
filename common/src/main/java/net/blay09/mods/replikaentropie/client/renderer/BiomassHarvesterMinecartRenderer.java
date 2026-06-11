package net.blay09.mods.replikaentropie.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.blay09.mods.replikaentropie.core.harvester.BiomassHarvesterLogic;
import net.blay09.mods.replikaentropie.entity.BiomassHarvesterMinecart;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.entity.AbstractMinecartRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.MinecartRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;

public class BiomassHarvesterMinecartRenderer extends AbstractMinecartRenderer<BiomassHarvesterMinecart, BiomassHarvesterMinecartRenderer.State> {

    private final ItemModelResolver itemModelResolver;

    public BiomassHarvesterMinecartRenderer(EntityRendererProvider.Context context) {
        super(context, ModelLayers.MINECART);
        itemModelResolver = context.getItemModelResolver();
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(BiomassHarvesterMinecart entity, State state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        for (int i = 0; i < state.items.length; i++) {
            state.items[i] = null;
        }

        final var time = (entity.tickCount + partialTick) / 20f;
        final var warningWobble = (float) Math.sin(time * Math.PI * 4f) * 0.02f;
        state.spinDegrees = entity.getClientSpinDegrees(partialTick);
        state.warningWobble = entity.getState() == BiomassHarvesterLogic.State.WARNING ? warningWobble : 0f;

        for (int i = 0; i < state.items.length; i++) {
            final var itemStack = entity.getRenderTool(i);
            if (itemStack.isEmpty()) {
                continue;
            }

            final var itemState = new ItemStackRenderState();
            itemModelResolver.updateForTopItem(itemState, itemStack, ItemDisplayContext.GROUND, entity.level(), null, entity.getId() + i);
            state.items[i] = itemState;
        }
    }

    @Override
    protected void submitMinecartContents(State state, BlockModelRenderState blockModel, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords) {
        super.submitMinecartContents(state, blockModel, poseStack, submitNodeCollector, lightCoords);

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
            poseStack.translate(0.35f + state.warningWobble, 0.25f + state.warningWobble, 0f);
            itemState.submit(poseStack, submitNodeCollector, lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
            poseStack.popPose();
        }

        poseStack.popPose();
    }

    public static class State extends MinecartRenderState {
        public final ItemStackRenderState[] items = new ItemStackRenderState[4];
        public float spinDegrees;
        public float warningWobble;
    }
}

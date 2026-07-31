package net.blay09.mods.replikaentropie.client;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.client.BalmClientRegistrars;
import net.blay09.mods.balm.client.platform.event.callback.ClientTickCallback;
import net.blay09.mods.balm.client.platform.event.callback.RenderCallback;
import net.blay09.mods.replikaentropie.ReplikaEntropie;
import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.blay09.mods.replikaentropie.block.entity.WorldEaterBlockEntity;
import net.blay09.mods.replikaentropie.client.gui.screens.ModScreens;
import net.blay09.mods.replikaentropie.client.handler.ClientDataNotifications;
import net.blay09.mods.replikaentropie.client.handler.HandheldAnalyzerClient;
import net.blay09.mods.replikaentropie.compat.recipeviewers.ReplikaEntropieRecipeViewerProvider;
import net.blay09.mods.replikaentropie.core.abilities.AbilityManager;
import net.blay09.mods.replikaentropie.core.abilities.MagphaseAbility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.Shapes;

public class ReplikaEntropieClient {

    public static void initialize(BalmClientRegistrars registrars) {
        ModKeyMappings.initialize();

        registrars.menuScreens(ModScreens::initialize);
        registrars.blockEntityRenderers(ModRenderers::initialize);
        registrars.entityRenderers(ModRenderers::initialize);
        registrars.blockColors(ModRenderers::initialize);
        registrars.blockStateModels(ModBlockStateModels::initialize);

        HandheldAnalyzerClient.initialize();
        ClientDataNotifications.initialize();

        Balm.modSupport().recipeViewers().register(ReplikaEntropie.id("recipes"), new ReplikaEntropieRecipeViewerProvider());

        ClientTickCallback.ClientLevelTick.BEFORE.register(level -> {
            MagphaseAbility.resetMagphasedPositions(level);

            final var player = Minecraft.getInstance().player;
            if (player != null) {
                AbilityManager.clientTick(player);
            }
        });

        RenderCallback.BlockHighlight.EVENT.register(((hitResult, poseStack, multiBufferSource, camera, color, lineWidth) -> {
            final var player = Minecraft.getInstance().player;
            if (!player.isShiftKeyDown()) {
                return true;
            }

            if (hitResult.getType() != HitResult.Type.BLOCK) {
                return true;
            }

            final var pos = hitResult.getBlockPos();
            final var state = player.level().getBlockState(pos);
            if (!state.is(ModBlocks.worldEater)) {
                return true;
            }
            final var blockEntity = player.level().getBlockEntity(pos);
            if (blockEntity instanceof WorldEaterBlockEntity worldEater) {
                final var vertexBuilder = multiBufferSource.getBuffer(RenderTypes.LINES);
                final var shape = Shapes.create(worldEater.getScanArea().inflate(0.002));

                double camX = camera.position().x;
                double camY = camera.position().y;
                double camZ = camera.position().z;
                ShapeRenderer.renderShape(poseStack, vertexBuilder, shape, -camX, -camY, -camZ, 0xFFFFFF00, lineWidth * 2f);
                return true;
            }

            return true;
        }));
    }
}

package net.blay09.mods.replikaentropie.client.handler;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.client.platform.event.callback.ClientTickCallback;
import net.blay09.mods.balm.client.platform.event.callback.RenderCallback;
import net.blay09.mods.replikaentropie.core.analyzer.Analyzer;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.network.protocol.AnalyzeEntityMessage;
import net.blay09.mods.replikaentropie.network.protocol.AnalyzePosMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class HandheldAnalyzerClient {

    private static final Identifier ANALYZER_OVERLAY = id("textures/misc/analyzer_scope.png");

    private static float analyzerScale = 1f;
    private static float analyzerFovMultiplier = 1f;
    private static final float ANALYZER_SCALE_SPEED = 0.5f;
    private static final float ANALYZER_SCALE = 1.125f;
    private static final float ANALYZER_FOV = 0.7f;
    private static final float ANALYZER_FOV_SPEED = 1f;
    private static final float ANALYZER_FOV_RETURN_SPEED = 2f;

    private static final int ANALYZE_BLOCK_TICKS = 10;
    private static final int ANALYZE_ENTITY_TICKS = 10;

    enum AnalyzingTarget {
        NONE,
        BLOCK,
        ENTITY
    }

    private static AnalyzingTarget analyzing = AnalyzingTarget.NONE;
    private static BlockPos analyzingPos;
    private static Entity analyzingEntity;
    private static int analyzingTicks;
    private static boolean analyzingDone;

    public static boolean isOverlayVisible() {
        final var minecraft = Minecraft.getInstance();
        final var player = minecraft.player;
        if (player == null) {
            return false;
        }

        return isAnalyzing(player) && !minecraft.options.getCameraType().isFirstPerson();
    }

    public static boolean isAnalyzing(LivingEntity entity) {
        return entity.isUsingItem() && entity.getUseItem().is(ModItems.handheldAnalyzer);
    }

    public static void initialize() {
        ClientTickCallback.AFTER.register(client -> {
            final var level = client.level;
            final var player = client.player;
            var analyzingSomething = false;
            if (level != null && player != null && isAnalyzing(player)) {
                final var hitResult = client.hitResult;
                if (hitResult instanceof BlockHitResult blockHitResult) {
                    final var pos = blockHitResult.getBlockPos();
                    if (analyzing == AnalyzingTarget.BLOCK && analyzingPos.equals(pos)) {
                        analyzingTicks++;
                        if (analyzingTicks >= ANALYZE_BLOCK_TICKS && !analyzingDone) {
                            Balm.networking().sendToServer(new AnalyzePosMessage(pos));
                            analyzingDone = true;
                        }
                    } else {
                        analyzing = AnalyzingTarget.BLOCK;
                        analyzingPos = pos;
                        analyzingTicks = 0;
                        analyzingDone = false;
                    }
                    analyzingSomething = true;
                } else if (hitResult instanceof EntityHitResult entityHitResult) {
                    final var entity = entityHitResult.getEntity();
                    if (analyzing == AnalyzingTarget.ENTITY && analyzingEntity.equals(entity)) {
                        analyzingTicks++;
                        if (analyzingTicks >= ANALYZE_ENTITY_TICKS && !analyzingDone) {
                            Balm.networking().sendToServer(new AnalyzeEntityMessage(analyzingEntity.getId()));
                            analyzingDone = true;
                        }
                    } else {
                        analyzing = AnalyzingTarget.ENTITY;
                        analyzingEntity = entity;
                        analyzingTicks = 0;
                        analyzingDone = false;
                    }
                    analyzingSomething = true;
                }
            }

            if (!analyzingSomething) {
                analyzing = AnalyzingTarget.NONE;
                analyzingEntity = null;
                analyzingTicks = 0;
                analyzingDone = false;
            }
        });

        RenderCallback.Hand.EVENT.register(((hand, itemStack, swingProgress) -> {
            final var minecraft = Minecraft.getInstance();
            final var player = minecraft.player;
            return player == null || !isAnalyzing(player);
        }));

        RenderCallback.UpdateFov.EVENT.register(((entity, fov) -> {
            final var minecraft = Minecraft.getInstance();
            final var delta = minecraft.getDeltaTracker().getGameTimeDeltaTicks();
            if (isAnalyzing(entity)) {
                analyzerFovMultiplier = Mth.lerp(ANALYZER_FOV_SPEED * delta, analyzerFovMultiplier, ANALYZER_FOV);
            } else {
                analyzerFovMultiplier = Mth.lerp(ANALYZER_FOV_RETURN_SPEED * delta, analyzerFovMultiplier, 1f);
            }
            if (analyzerFovMultiplier < 1) {
                return fov * analyzerFovMultiplier;
            }
            return fov;
        }));

        RenderCallback.Gui.AFTER.register(((graphics, window) -> {
            final var minecraft = Minecraft.getInstance();
            final var player = minecraft.player;
            if (player == null) {
                return;
            }

            if (!isAnalyzing(player)) {
                analyzerScale = 1f;
                return;
            }

            float delta = minecraft.getDeltaTracker().getGameTimeDeltaTicks();
            analyzerScale = Mth.lerp(ANALYZER_SCALE_SPEED * delta, analyzerScale, ANALYZER_SCALE);

            if (!minecraft.options.getCameraType().isFirstPerson()) {
                return;
            }

            int screenWidth = graphics.guiWidth();
            int screenHeight = graphics.guiHeight();
            float f = (float) Math.min(screenWidth, screenHeight);
            float f1 = Math.min((float) screenWidth / f, (float) screenHeight / f) * analyzerScale;
            int overlayWidth = Mth.floor(f * f1);
            int overlayHeight = Mth.floor(f * f1);
            int left = (screenWidth - overlayWidth) / 2;
            int top = (screenHeight - overlayHeight) / 2;
            int right = left + overlayWidth;
            int bottom = top + overlayHeight;
            graphics.blit(RenderPipelines.GUI_TEXTURED, ANALYZER_OVERLAY, left, top, 0f, 0f, overlayWidth, overlayHeight, overlayWidth, overlayHeight);
            graphics.fill(RenderPipelines.GUI, 0, bottom, screenWidth, screenHeight, 0xFF000000);
            graphics.fill(RenderPipelines.GUI, 0, 0, screenWidth, top, 0xFF000000);
            graphics.fill(RenderPipelines.GUI, 0, top, left, bottom, 0xFF000000);
            graphics.fill(RenderPipelines.GUI, right, top, screenWidth, bottom, 0xFF000000);

            if (minecraft.hitResult instanceof BlockHitResult blockHitResult) {
                final var level = minecraft.level;
                final var pos = blockHitResult.getBlockPos();
                final var state = level.getBlockState(pos);
                final var itemStack = state.getBlock().defaultBlockState().getCloneItemStack(level, pos, false);
                if (!itemStack.isEmpty()) {
                    graphics.item(itemStack, left + 30, top + 30);
                    if (Analyzer.isAnalyzed(player, itemStack)) {
                        graphics.text(minecraft.font, itemStack.getHoverName(), left + 50, top + 34, 0xFFFFFFFF);
                    } else {
                        final var obfuscatedName = itemStack.getHoverName().copy().withStyle(ChatFormatting.OBFUSCATED);
                        graphics.text(minecraft.font, obfuscatedName, left + 50, top + 34, 0xFFFFFFFF);
                    }
                    ClientDataNotifications.render(graphics, left + 50, top + 46, delta, false);
                }
            } else if (minecraft.hitResult instanceof EntityHitResult entityHitResult) {
                final var entity = entityHitResult.getEntity();
                if (Analyzer.isAnalyzed(player, entity)) {
                    graphics.text(minecraft.font, entity.getName(), left + 50, top + 34, 0xFFFFFFFF);
                } else {
                    final var obfuscatedName = entity.getName().copy().withStyle(ChatFormatting.OBFUSCATED);
                    graphics.text(minecraft.font, obfuscatedName, left + 50, top + 34, 0xFFFFFFFF);
                }
                ClientDataNotifications.render(graphics, left + 50, top + 46, delta, false);
            }
        }));
    }
}

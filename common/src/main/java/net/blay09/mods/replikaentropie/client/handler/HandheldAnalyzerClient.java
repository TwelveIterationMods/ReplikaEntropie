package net.blay09.mods.replikaentropie.client.handler;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.event.BalmEvents;
import net.blay09.mods.balm.api.event.TickPhase;
import net.blay09.mods.balm.api.event.TickType;
import net.blay09.mods.balm.api.event.client.FovUpdateEvent;
import net.blay09.mods.balm.api.event.client.GuiDrawEvent;
import net.blay09.mods.balm.api.event.client.RenderHandEvent;
import net.blay09.mods.replikaentropie.core.analyzer.Analyzer;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.network.protocol.AnalyzeEntityMessage;
import net.blay09.mods.replikaentropie.network.protocol.AnalyzePosMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class HandheldAnalyzerClient {

    private static final ResourceLocation ANALYZER_OVERLAY = id("textures/misc/analyzer_scope.png");

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

    public static void initialize(BalmEvents events) {
        events.onTickEvent(TickType.Client, TickPhase.End, minecraft -> {
            final var level = minecraft.level;
            final var player = minecraft.player;
            var analyzingSomething = false;
            if (level != null && player != null && isAnalyzing(player)) {
                final var hitResult = minecraft.hitResult;
                if (hitResult instanceof BlockHitResult blockHitResult) {
                    final var pos = blockHitResult.getBlockPos();
                    if (analyzing == AnalyzingTarget.BLOCK && analyzingPos.equals(pos)) {
                        analyzingTicks++;
                        if (analyzingTicks >= ANALYZE_BLOCK_TICKS && !analyzingDone) {
                            Balm.getNetworking().sendToServer(new AnalyzePosMessage(pos));
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
                            Balm.getNetworking().sendToServer(new AnalyzeEntityMessage(analyzingEntity.getId()));
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

        events.onEvent(RenderHandEvent.class, event -> {
            final var minecraft = Minecraft.getInstance();
            final var player = minecraft.player;
            if (player != null && isAnalyzing(player)) {
                event.setCanceled(true);
            }
        });

        events.onEvent(FovUpdateEvent.class, event -> {
            final var minecraft = Minecraft.getInstance();
            final var delta = minecraft.getDeltaFrameTime();
            if (isAnalyzing(event.getEntity())) {
                analyzerFovMultiplier = Mth.lerp(ANALYZER_FOV_SPEED * delta, analyzerFovMultiplier, ANALYZER_FOV);
            } else {
                analyzerFovMultiplier = Mth.lerp(ANALYZER_FOV_RETURN_SPEED * delta, analyzerFovMultiplier, 1f);
            }
            if (analyzerFovMultiplier < 1) {
                event.setFov(analyzerFovMultiplier);
            }
        });

        events.onEvent(GuiDrawEvent.Pre.class, event -> {
            if (event.getElement() == GuiDrawEvent.Element.ALL) {
                final var minecraft = Minecraft.getInstance();
                final var player = minecraft.player;
                if (player == null) {
                    return;
                }

                if (!isAnalyzing(player)) {
                    analyzerScale = 1f;
                    return;
                }

                float delta = minecraft.getDeltaFrameTime();
                analyzerScale = Mth.lerp(ANALYZER_SCALE_SPEED * delta, analyzerScale, ANALYZER_SCALE);

                if (!minecraft.options.getCameraType().isFirstPerson()) {
                    return;
                }

                final var guiGraphics = event.getGuiGraphics();
                int screenWidth = guiGraphics.guiWidth();
                int screenHeight = guiGraphics.guiHeight();
                float f = (float) Math.min(screenWidth, screenHeight);
                float f1 = Math.min((float) screenWidth / f, (float) screenHeight / f) * analyzerScale;
                int overlayWidth = Mth.floor(f * f1);
                int overlayHeight = Mth.floor(f * f1);
                int left = (screenWidth - overlayWidth) / 2;
                int top = (screenHeight - overlayHeight) / 2;
                int right = left + overlayWidth;
                int bottom = top + overlayHeight;
                RenderSystem.enableBlend();
                guiGraphics.blit(ANALYZER_OVERLAY, left, top, -90, 0f, 0f, overlayWidth, overlayHeight, overlayWidth, overlayHeight);
                guiGraphics.fill(RenderType.guiOverlay(), 0, bottom, screenWidth, screenHeight, -90, 0xFF000000);
                guiGraphics.fill(RenderType.guiOverlay(), 0, 0, screenWidth, top, -90, 0xFF000000);
                guiGraphics.fill(RenderType.guiOverlay(), 0, top, left, bottom, -90, 0xFF000000);
                guiGraphics.fill(RenderType.guiOverlay(), right, top, screenWidth, bottom, -90, 0xFF000000);

                if (minecraft.hitResult instanceof BlockHitResult blockHitResult) {
                    final var level = minecraft.level;
                    final var pos = blockHitResult.getBlockPos();
                    final var state = level.getBlockState(pos);
                    final var itemStack = state.getBlock().getCloneItemStack(level, pos, state);
                    if (!itemStack.isEmpty()) {
                        guiGraphics.renderItem(itemStack, left + 30, top + 30);
                        if (Analyzer.isAnalyzed(player, itemStack)) {
                            guiGraphics.drawString(minecraft.font, itemStack.getHoverName(), left + 50, top + 34, 0xFFFFFFFF);
                        } else {
                            final var obfuscatedName = itemStack.getHoverName().copy().withStyle(ChatFormatting.OBFUSCATED);
                            guiGraphics.drawString(minecraft.font, obfuscatedName, left + 50, top + 34, 0xFFFFFFFF);
                        }
                        ClientDataNotifications.render(guiGraphics, left + 50, top + 46, delta, false);
                    }
                } else if (minecraft.hitResult instanceof EntityHitResult entityHitResult) {
                    final var entity = entityHitResult.getEntity();
                    if (Analyzer.isAnalyzed(player, entity)) {
                        guiGraphics.drawString(minecraft.font, entity.getName(), left + 50, top + 34, 0xFFFFFFFF);
                    } else {
                        final var obfuscatedName = entity.getName().copy().withStyle(ChatFormatting.OBFUSCATED);
                        guiGraphics.drawString(minecraft.font, obfuscatedName, left + 50, top + 34, 0xFFFFFFFF);
                    }
                }
            }
        });
    }
}

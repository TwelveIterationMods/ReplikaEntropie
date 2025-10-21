package net.blay09.mods.replikaentropie.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blay09.mods.replikaentropie.client.gui.components.ProgressRenderer;
import net.blay09.mods.replikaentropie.client.gui.components.SegmentedProgressRenderer;
import net.blay09.mods.replikaentropie.client.gui.components.SimpleProgressRenderer;
import net.blay09.mods.replikaentropie.menu.WorldEaterMenu;
import net.blay09.mods.replikaentropie.menu.slot.ReadonlySlot;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class WorldEaterScreen extends AbstractContainerScreen<WorldEaterMenu> {
    private static final ResourceLocation BACKGROUND = id("textures/gui/container/world_eater.png");
    private final SegmentedProgressRenderer scanningProgressRenderer;
    private final ProgressRenderer scrapProgressRenderer = SimpleProgressRenderer.reverseVertical(BACKGROUND, 256, 256).pos(156, 55).size(3, 26).uv(208, 0);

    public WorldEaterScreen(WorldEaterMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        imageHeight = 180;
        inventoryLabelY = imageHeight - 94;

        scanningProgressRenderer = new SegmentedProgressRenderer(BACKGROUND, 256, 256)
                .addInvisibleSegment(20)
                .addHorizontalSegment(18, 29, 3, 5, 194, 0)
                .addInvisibleSegment(90)
                .addHorizontalSegment(111, 29, 5, 5, 197, 0)
                .addVerticalSegment(113, 34, 3, 18, 199, 5)
                .addReverseHorizontalSegment(111, 47, 2, 5, 197, 18)
                .addInvisibleSegment(90)
                .addReverseHorizontalSegment(16, 47, 5, 5, 192, 18)
                .addVerticalSegment(16, 52, 3, 18, 192, 23)
                .addHorizontalSegment(19, 65, 2, 5, 195, 36)
                .addInvisibleSegment(90)
                .addHorizontalSegment(111, 59, 11, 16, 197, 30);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);

        for (final var slot : menu.slots) {
            if (slot instanceof ReadonlySlot) {
                if (menu.isDestroying() && menu.getCurrentDestroySlot() == slot.getContainerSlot()) {
                    final var destroyProgress = menu.getDestroyingProgress();
                    final var frameIndex = Mth.clamp((int) (destroyProgress * 8), 0, 8);
                    RenderSystem.enableBlend();
                    guiGraphics.blit(BACKGROUND, leftPos + slot.x, topPos + slot.y, 300, 176, 16 + frameIndex * 16, 16, 16, 256, 256);
                }

                guiGraphics.blit(BACKGROUND, leftPos + slot.x, topPos + slot.y, 300, 176, 0, 16, 16, 256, 256);
            }
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float delta, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        if (menu.isScanning()) {
            scanningProgressRenderer.render(guiGraphics, leftPos, topPos, menu.getScanningProgress());
        } else if (menu.isDestroying()) {
            scanningProgressRenderer.render(guiGraphics, leftPos, topPos, 1f);
        }

        scrapProgressRenderer.render(guiGraphics, leftPos, topPos, menu.getFractionalScrap());
    }

}

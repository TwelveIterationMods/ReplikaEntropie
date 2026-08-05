package net.blay09.mods.replikaentropie.client.gui.screens.inventory;

import net.blay09.mods.balm.client.gui.components.ProgressRenderer;
import net.blay09.mods.balm.client.gui.components.SimpleProgressRenderer;
import net.blay09.mods.replikaentropie.client.gui.components.EnergyBar;
import net.blay09.mods.replikaentropie.client.gui.components.FluidTankDisplay;
import net.blay09.mods.replikaentropie.client.gui.components.MakeshiftPowerButton;
import net.blay09.mods.replikaentropie.menu.BiomassIncubatorMenu;
import net.blay09.mods.replikaentropie.menu.slot.ReadonlySlot;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.Inventory;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class BiomassIncubatorScreen extends AbstractContainerScreen<BiomassIncubatorMenu> {
    private static final Identifier BACKGROUND = id("textures/gui/container/biomass_incubator.png");
    private static final Identifier LEFT_WING = id("left_wing");
    private static final int GROWTH_PROGRESS_X = 80;
    private static final int GROWTH_PROGRESS_Y = 56;
    private static final int GROWTH_PROGRESS_WIDTH = 16;
    private static final int GROWTH_PROGRESS_HEIGHT = 4;
    private static final int GROWTH_PROGRESS_U = 199;
    private static final int GROWTH_PROGRESS_V = 14;
    private static final int GROWTH_PROGRESS_PERIOD_MS = 2000;

    private final FluidTankDisplay waterTank = new FluidTankDisplay(
            SimpleProgressRenderer.reverseVertical(BACKGROUND, 256, 256).pos(45, 23).size(16, 48).uv(183, 30),
            45, 23, 16, 48,
            Component.translatable("block.minecraft.water"));
    private final EnergyBar energyBar = new EnergyBar(-23, 6);

    public BiomassIncubatorScreen(BiomassIncubatorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, DEFAULT_IMAGE_WIDTH, 203);

        inventoryLabelY = imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();

        addRenderableWidget(new MakeshiftPowerButton(leftPos - 25, topPos + 97, menu.containerId, menu::isMakeshiftPsuOverheated));
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractContents(graphics, mouseX, mouseY, a);

        for (final var slot : menu.slots) {
            if (slot instanceof ReadonlySlot) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos + slot.x, topPos + slot.y, 183, 14, 16, 16, 256, 256);
            }
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, LEFT_WING, leftPos - 27, topPos + 2, 24, 90);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, LEFT_WING, leftPos - 29, topPos + 93, 28, 28);
        waterTank.render(graphics, leftPos, topPos, menu.getWaterTankProgress());
        if (menu.getGrowthProgress() > 0f) {
            renderGrowthProgress(graphics);
        }
        energyBar.render(graphics, leftPos, topPos, menu.getPowerProgress());
    }

    private void renderGrowthProgress(GuiGraphicsExtractor graphics) {
        final var progress = (Util.getMillis() % GROWTH_PROGRESS_PERIOD_MS) / (float) GROWTH_PROGRESS_PERIOD_MS;

        final int fillStart;
        final int fillWidth;
        if (progress < 0.5f) {
            fillStart = 0;
            fillWidth = Mth.ceil(GROWTH_PROGRESS_WIDTH * progress * 2f);
        } else {
            fillStart = Mth.floor(GROWTH_PROGRESS_WIDTH * (progress - 0.5f) * 2f);
            fillWidth = GROWTH_PROGRESS_WIDTH - fillStart;
        }

        if (fillWidth > 0) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND,
                    leftPos + GROWTH_PROGRESS_X + fillStart,
                    topPos + GROWTH_PROGRESS_Y,
                    GROWTH_PROGRESS_U + fillStart,
                    GROWTH_PROGRESS_V,
                    fillWidth,
                    GROWTH_PROGRESS_HEIGHT,
                    256,
                    256);
        }
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (energyBar.extractTooltip(graphics, font, leftPos, topPos, mouseX, mouseY, menu.getCurrentPower(), menu.getMaxPower())) {
            return;
        }

        if (waterTank.extractTooltip(graphics, font, leftPos, topPos, mouseX, mouseY, menu.getWaterTank(), menu.getMaxWaterCapacity())) {
            return;
        }

        super.extractTooltip(graphics, mouseX, mouseY);
    }

}

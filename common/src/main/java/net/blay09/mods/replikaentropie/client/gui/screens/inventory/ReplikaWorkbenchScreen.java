package net.blay09.mods.replikaentropie.client.gui.screens.inventory;

import net.blay09.mods.replikaentropie.client.gui.components.EnergyBar;
import net.blay09.mods.replikaentropie.client.gui.components.MakeshiftPowerButton;
import net.blay09.mods.replikaentropie.menu.ReplikaWorkbenchMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class ReplikaWorkbenchScreen extends AbstractContainerScreen<ReplikaWorkbenchMenu> {
    private static final Identifier BACKGROUND = id("textures/gui/container/replika_workbench.png");
    private static final Identifier LEFT_WING = id("left_wing");

    private final EnergyBar energyBar = new EnergyBar(-23, 5);

    public ReplikaWorkbenchScreen(ReplikaWorkbenchMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, DEFAULT_IMAGE_WIDTH, 206);

        inventoryLabelY = imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();

        addRenderableWidget(new MakeshiftPowerButton(leftPos - 25, topPos + 96, menu.containerId));
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, LEFT_WING, leftPos - 27, topPos + 1, 24, 90);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, LEFT_WING, leftPos - 29, topPos + 92, 28, 28);
        energyBar.render(graphics, leftPos, topPos, menu.getPowerProgress());
    }
}

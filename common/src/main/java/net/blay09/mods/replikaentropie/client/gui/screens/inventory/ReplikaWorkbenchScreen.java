package net.blay09.mods.replikaentropie.client.gui.screens.inventory;

import net.blay09.mods.replikaentropie.menu.ReplikaWorkbenchMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class ReplikaWorkbenchScreen extends AbstractContainerScreen<ReplikaWorkbenchMenu> {
    private static final Identifier BACKGROUND = id("textures/gui/container/replika_workbench.png");

    private Button assembleButton;

    public ReplikaWorkbenchScreen(ReplikaWorkbenchMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, DEFAULT_IMAGE_WIDTH, 206);

        inventoryLabelY = imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();

        assembleButton = addRenderableWidget(Button.builder(Component.translatable("gui.replikaentropie.replika_workbench.assemble"), b -> {
            if (minecraft != null && minecraft.gameMode != null) {
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 0);
            }
        }).pos(leftPos + 105, topPos + 53).size(60, 20).build());
        assembleButton.active = menu.canAssemble();
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
    }

    @Override
    protected void containerTick() {
        super.containerTick();

        if (assembleButton != null) {
            assembleButton.active = menu.canAssemble();
        }
    }
}

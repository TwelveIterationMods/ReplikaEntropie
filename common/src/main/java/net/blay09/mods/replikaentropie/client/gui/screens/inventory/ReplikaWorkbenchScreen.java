package net.blay09.mods.replikaentropie.client.gui.screens.inventory;

import net.blay09.mods.replikaentropie.menu.ReplikaWorkbenchMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class ReplikaWorkbenchScreen extends AbstractContainerScreen<ReplikaWorkbenchMenu> {
    private static final ResourceLocation BACKGROUND = id("textures/gui/container/replika_workbench.png");

    private Button assembleButton;

    public ReplikaWorkbenchScreen(ReplikaWorkbenchMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        imageHeight = 206;
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
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float delta, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void containerTick() {
        super.containerTick();

        if (assembleButton != null) {
            assembleButton.active = menu.canAssemble();
        }
    }
}

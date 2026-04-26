package net.blay09.mods.replikaentropie.client.gui.screens.inventory;

import net.blay09.mods.replikaentropie.menu.EntropicDataMinerMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class EntropicDataMinerScreen extends AbstractContainerScreen<EntropicDataMinerMenu> {

    private static final Identifier TEXTURE = id("textures/gui/container/entropic_data_miner.png");

    public EntropicDataMinerScreen(EntropicDataMinerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);

        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
    }

}

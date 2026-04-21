package net.blay09.mods.replikaentropie.client.gui.components;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class NonogramHelpButton extends Button {

    private static final Identifier TEXTURE = id("textures/gui/container/nonogram.png");

    public NonogramHelpButton(int x, int y, int size) {
        super(x, y, size, size, Component.empty(), _ -> {
        }, Button.DEFAULT_NARRATION);

        final var tooltip = Component.literal("")
                .append(Component.translatable("gui.replikaentropie.nonogram.help.title").withStyle(ChatFormatting.GOLD).append("\n"))
                .append(Component.translatable("gui.replikaentropie.nonogram.help.content").withStyle(ChatFormatting.GRAY));
        setTooltip(Tooltip.create(tooltip));
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int i, int i1, float v) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, getX(), getY(), 37, 136, 16, 16, 256, 256);
    }
}

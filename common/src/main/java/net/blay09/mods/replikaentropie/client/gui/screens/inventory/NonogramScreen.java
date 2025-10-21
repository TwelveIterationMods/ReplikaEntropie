package net.blay09.mods.replikaentropie.client.gui.screens.inventory;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.replikaentropie.client.gui.ExtendedGuiGraphics;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.menu.AbstractNonogramMenu;
import net.blay09.mods.replikaentropie.network.protocol.NonogramMarkMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.blay09.mods.replikaentropie.client.gui.components.NonogramHelpButton;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class NonogramScreen extends AbstractContainerScreen<AbstractNonogramMenu> {

    private static final ResourceLocation BACKGROUND = id("textures/gui/container/nonogram.png");
    private static final int BACKGROUND_WIDTH = 135;
    private static final int BACKGROUND_HEIGHT = 135;

    private static final int PADDING_LEFT = 37;
    private static final int PADDING_RIGHT = 8;
    private static final int PADDING_TOP = 35;
    private static final int PADDING_BOTTOM = 10;

    private static final int CELL_SIZE = 18;

    private int draggingButton = -1;
    private int dragOnlyAffects;
    private boolean dragErases;

    private float completionFadeTime;
    private boolean completionSoundPlayed;

    private NonogramHelpButton helpButton;

    public NonogramScreen(AbstractNonogramMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = PADDING_LEFT + PADDING_RIGHT + CELL_SIZE * menu.getClues().width();
        this.imageHeight = PADDING_TOP + PADDING_BOTTOM + CELL_SIZE * menu.getClues().height();
    }

    @Override
    protected void init() {
        super.init();

        helpButton = new NonogramHelpButton(leftPos + 20, topPos + 18, 16);
        addRenderableWidget(helpButton);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        ExtendedGuiGraphics.blitNineSlicedTilingFill(guiGraphics, BACKGROUND, leftPos, topPos, imageWidth, imageHeight, PADDING_LEFT, PADDING_TOP, PADDING_RIGHT, PADDING_BOTTOM, BACKGROUND_WIDTH, BACKGROUND_HEIGHT, 0, 0);

        final var state = menu.getNonogramState();
        for (int column = 0; column < state.width(); column++) {
            for (int row = 0; row < state.height(); row++) {
                final var cellX = leftPos + PADDING_LEFT + column * CELL_SIZE;
                final var cellY = topPos + PADDING_TOP + row * CELL_SIZE;
                final var mark = state.mark(column, row);
                switch (mark) {
                    case 1 -> guiGraphics.blit(BACKGROUND, cellX, cellY, 135, 0, 18, 18);
                    case -1 -> guiGraphics.blit(BACKGROUND, cellX, cellY, 135, 18, 18, 18);
                }
                if ((column + 1) % 5 == 0) {
                    guiGraphics.blit(BACKGROUND, cellX, cellY, 0, 135, 18, 18);
                }
                if ((row + 1) % 5 == 0) {
                    guiGraphics.blit(BACKGROUND, cellX, cellY, 18, 135, 18, 18);
                }
            }
        }

        final var clues = menu.getClues();
        final var errors = menu.getErrors();

        for (int column = 0; column < clues.width(); column++) {
            if (errors.erroredColumns().contains(column)) {
                guiGraphics.setColor(1f, 0.21f, 0.21f, 1f);
            } else {
                guiGraphics.setColor(0.21f, 0.21f, 0.21f, 1f);
            }
            final var x = leftPos + PADDING_LEFT + column * CELL_SIZE + 1;
            var y = topPos + PADDING_TOP - 6;
            final var columnClues = clues.columnClues(column);
            for (int i = columnClues.length - 1; i >= 0; i--) {
                final var clue = columnClues[i];
                guiGraphics.blit(BACKGROUND, x, y, 135, 36 + clue * 5, 16, 5);
                y -= 5;
            }
        }

        for (int row = 0; row < clues.height(); row++) {
            if (errors.erroredRows().contains(row)) {
                guiGraphics.setColor(1f, 0.21f, 0.21f, 1f);
            } else {
                guiGraphics.setColor(0.21f, 0.21f, 0.21f, 1f);
            }
            var x = leftPos + PADDING_LEFT - 6;
            final var y = topPos + PADDING_TOP + row * CELL_SIZE + 1;
            final var rowClues = clues.rowClues(row);
            for (int i = rowClues.length - 1; i >= 0; i--) {
                final var clue = rowClues[i];
                guiGraphics.blit(BACKGROUND, x, y, 153 + clue * 5, 0, 5, 16);
                x -= 5;
            }
        }

        // Render black/white overlay on cells upon completion
        if (menu.isCompleted()) {
            final var completedState = menu.getNonogramState();
            final var alpha = Math.min(1f, completionFadeTime / 20f);
            if (alpha > 0.02f) {
                RenderSystem.setShaderColor(1f, 1f, 1f, alpha);

                for (int column = 0; column < completedState.width(); column++) {
                    for (int row = 0; row < completedState.height(); row++) {
                        final var cellX = leftPos + PADDING_LEFT + column * CELL_SIZE;
                        final var cellY = topPos + PADDING_TOP + row * CELL_SIZE;
                        final var mark = completedState.mark(column, row);
                        guiGraphics.blit(BACKGROUND, cellX, cellY, (mark == 1) ? 54 : 72, 135, 18, 18);
                    }
                }

                RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            }
        }

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    private boolean overlapsClues(int x, int y, int width, int height) {
        final var endX = x + width;
        final var endY = y + height;
        final var clues = menu.getClues();
        for (int column = 0; column < clues.width(); column++) {
            final var clueCount = clues.columnClues(column).length;
            if (clueCount == 0) {
                continue;
            }

            final var cellStartX = PADDING_LEFT + column * CELL_SIZE + 1;
            final var cellEndX = cellStartX + 16;
            final var cellStartY = PADDING_TOP - 6 + 5;
            final var cellEndY = PADDING_TOP - 6 - (clueCount - 1) * 5;

            final var overlapsX = x < cellEndX && endX > cellStartX;
            final var overlapsY = y < cellStartY && endY > cellEndY;
            if (overlapsX && overlapsY) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // We try to place the title in the left, center, or right, moving it to avoid colliding with clues
        // Worst case we don't render it at all, getting the full 10 rows is more important
        final var titleWidth = font.width(title);
        if (inventoryLabelX + titleWidth <= imageWidth - inventoryLabelX) {
            final var titleHeight = font.lineHeight;

            final int leftX = titleLabelX;
            final int centerX = (imageWidth - titleWidth) / 2;
            final int rightX = imageWidth - titleWidth - titleLabelX;
            final int y = titleLabelY;

            int effectiveTitleLabelX = -1;
            if (!overlapsClues(leftX, titleLabelY, titleWidth, titleHeight)) {
                effectiveTitleLabelX = leftX;
            } else if (!overlapsClues(centerX, titleLabelY, titleWidth, titleHeight)) {
                effectiveTitleLabelX = centerX;
            } else if (!overlapsClues(rightX, titleLabelY, titleWidth, titleHeight)) {
                effectiveTitleLabelX = rightX;
            }

            if (effectiveTitleLabelX != -1) {
                guiGraphics.drawString(font, title, effectiveTitleLabelX, y, 0xFF404040, false);
            }
        }

        // Success notification
        if (menu.isCompleted()) {
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            final var message = Component.translatable("gui.replikaentropie.nonogram.complete")
                    .withStyle(ChatFormatting.GREEN);
            final var messageWidth = font.width(message);
            final var messageHeight = font.lineHeight;

            final var centerX = PADDING_LEFT + (imageWidth - PADDING_LEFT - PADDING_RIGHT) / 2;
            final var centerY = PADDING_TOP + (imageHeight - PADDING_TOP - PADDING_BOTTOM) / 2 -11 ;
            final var paddingX = 14;
            final var paddingY = 6;

            final var boxLeft = centerX - messageWidth / 2 - paddingX;
            final var boxTop = centerY - paddingY;
            final var boxRight = centerX + messageWidth / 2 + paddingX;
            final var boxBottom = centerY + messageHeight + paddingY - 1;

            final var fadeProgress = Math.min(1f, completionFadeTime / 20f);
            final int alpha = (int) (fadeProgress * 255f) & 0xFF;
            final int bgAlpha = ((int) (fadeProgress * 0.4f * 255f)) & 0xFF;
            if (alpha > 5) {
                guiGraphics.fill(boxLeft, boxTop, boxRight, boxBottom, bgAlpha << 24);
                guiGraphics.drawCenteredString(font, message, centerX, centerY, (alpha << 24) | 0x21C45A);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        final var state = menu.getNonogramState();
        final var gridStartX = leftPos + PADDING_LEFT;
        final var gridStartY = topPos + PADDING_TOP;
        final var gridEndX = gridStartX + state.width() * CELL_SIZE;
        final var gridEndY = gridStartY + state.height() * CELL_SIZE;
        if (mouseX >= gridStartX && mouseY >= gridStartY && mouseX < gridEndX && mouseY < gridEndY) {
            final var relativeMouseX = (int) mouseX - gridStartX;
            final var relativeMouseY = (int) mouseY - gridStartY;
            final var column = relativeMouseX / CELL_SIZE;
            final var row = relativeMouseY / CELL_SIZE;
            final var markCurrent = state.mark(column, row);
            final var markToPlace = button == 0 ? 1 : -1;
            dragOnlyAffects = markCurrent;
            dragErases = (markCurrent == markToPlace);
            if (dragErases) {
                mark(column, row, 0);
            } else {
                mark(column, row, markToPlace);
            }
            draggingButton = button;
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (draggingButton == -1 || button != draggingButton) {
            return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }

        final var state = menu.getNonogramState();
        final var gridStartX = leftPos + PADDING_LEFT;
        final var gridStartY = topPos + PADDING_TOP;
        final var gridEndX = gridStartX + state.width() * CELL_SIZE;
        final var gridEndY = gridStartY + state.height() * CELL_SIZE;
        if (mouseX >= gridStartX && mouseY >= gridStartY && mouseX < gridEndX && mouseY < gridEndY) {
            final var relativeMouseX = (int) mouseX - gridStartX;
            final var relativeMouseY = (int) mouseY - gridStartY;
            final var column = relativeMouseX / CELL_SIZE;
            final var row = relativeMouseY / CELL_SIZE;
            final var currentMark = state.mark(column, row);
            if (dragErases) {
                if (currentMark == dragOnlyAffects) {
                    mark(column, row, 0);
                }
            } else {
                if (currentMark == dragOnlyAffects) {
                    final var markToPlace = button == 0 ? 1 : -1;
                    mark(column, row, markToPlace);
                }
            }
            return true;
        }

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == draggingButton) {
            draggingButton = -1;
            dragOnlyAffects = 0;
            dragErases = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (minecraft != null
                && minecraft.options.keyInventory.matches(keyCode, scanCode)
                || (keyCode == InputConstants.KEY_ESCAPE && this.shouldCloseOnEsc())) {
            onClose();
            if (minecraft != null
                    && minecraft.player != null
                    && minecraft.player.getMainHandItem().is(ModItems.skyScraper)
                    && minecraft.gameMode != null) {
                minecraft.gameMode.useItem(minecraft.player, InteractionHand.MAIN_HAND);
            }
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (menu.isCompleted()) {
            if (!completionSoundPlayed) {
                minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.PLAYER_LEVELUP, 0f));
                completionSoundPlayed = true;
            }

            completionFadeTime += partialTicks;
        } else {
            completionFadeTime = 0;
        }

        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    public void mark(int column, int row, int mark) {
        if (!menu.isCompleted()) {
            menu.mark(column, row, mark);
            Balm.getNetworking().sendToServer(new NonogramMarkMessage(menu.containerId, column, row, mark));
        }
    }
}
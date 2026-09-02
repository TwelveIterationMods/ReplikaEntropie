package net.blay09.mods.replikaentropie.client.gui.screens.inventory;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.balm.Balm;
import net.blay09.mods.replikaentropie.client.gui.ExtendedGuiGraphics;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.menu.AbstractNonogramMenu;
import net.blay09.mods.replikaentropie.menu.NonogramMenu;
import net.blay09.mods.replikaentropie.network.protocol.NonogramAutoHackMessage;
import net.blay09.mods.replikaentropie.network.protocol.NonogramMarkMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.blay09.mods.replikaentropie.client.gui.components.NonogramHelpButton;
import org.jspecify.annotations.Nullable;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class NonogramScreen extends AbstractContainerScreen<AbstractNonogramMenu> {

    private static final Identifier BACKGROUND = id("textures/gui/container/nonogram.png");
    private static final Identifier LEFT_WING = id("left_wing");
    private static final int BACKGROUND_WIDTH = 135;
    private static final int BACKGROUND_HEIGHT = 135;

    private static final int PADDING_LEFT = 37;
    private static final int PADDING_RIGHT = 8;
    private static final int PADDING_TOP = 35;
    private static final int PADDING_BOTTOM = 10;

    private static final int CELL_SIZE = 18;
    private static final WidgetSprites AUTO_HACK_BUTTON_SPRITES = new WidgetSprites(
            id("automatic_hack_tool_button"),
            id("automatic_hack_tool_button_disabled"),
            id("automatic_hack_tool_button_highlighted"),
            id("automatic_hack_tool_button_disabled")
    );
    private static final int AUTO_HACK_INITIAL_REPEAT_DELAY_MS = 250;
    private static final int AUTO_HACK_REPEAT_INTERVAL_MS = 150;
    private static final int AUTO_HACK_REPEAT_SPEEDUP_INTERVAL_MS = 1000;
    private static final int AUTO_HACK_MIN_REPEAT_INTERVAL_MS = 25;
    private static final Component AUTO_HACK_MESSAGE = Component.translatable("gui.replikaentropie.nonogram.auto_hack");
    private static final Component NO_AUTO_HACK_MESSAGE = Component.translatable("gui.replikaentropie.nonogram.no_auto_hack");

    private int draggingButton = -1;
    private int dragOnlyAffects;
    private boolean dragErases;

    private float completionFadeTime;
    private boolean completionSoundPlayed;

    private @Nullable NonogramHelpButton helpButton;
    private @Nullable ImageButton autoHackButton;
    private boolean autoHackButtonHeld;
    private long autoHackHeldStartTime;
    private long nextAutoHackRepeatTime;

    public NonogramScreen(AbstractNonogramMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, PADDING_LEFT + PADDING_RIGHT + CELL_SIZE * menu.getClues().width(), PADDING_TOP + PADDING_BOTTOM + CELL_SIZE * menu.getClues().height());
    }

    @Override
    protected void init() {
        super.init();

        helpButton = new NonogramHelpButton(leftPos + 20, topPos + 18, 16);
        addRenderableWidget(helpButton);

        autoHackButton = new ImageButton(leftPos - 24, topPos + 5, 20, 20, AUTO_HACK_BUTTON_SPRITES, _ -> requestAutoHack(), AUTO_HACK_MESSAGE);
        updateAutoHackButtonState();
        addRenderableWidget(autoHackButton);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        ExtendedGuiGraphics.blitNineSlicedTilingFill(graphics, BACKGROUND, leftPos, topPos, imageWidth, imageHeight, PADDING_LEFT, PADDING_TOP, PADDING_RIGHT, PADDING_BOTTOM, BACKGROUND_WIDTH, BACKGROUND_HEIGHT, 0, 0);

        final var state = menu.getNonogramState();
        for (int column = 0; column < state.width(); column++) {
            for (int row = 0; row < state.height(); row++) {
                final var cellX = leftPos + PADDING_LEFT + column * CELL_SIZE;
                final var cellY = topPos + PADDING_TOP + row * CELL_SIZE;
                final var mark = state.mark(column, row);
                switch (mark) {
                    case 1 -> graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, cellX, cellY, 135, 0, 18, 18, 256, 256);
                    case -1 -> graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, cellX, cellY, 135, 18, 18, 18, 256, 256);
                }
                if ((column + 1) % 5 == 0) {
                    graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, cellX, cellY, 0, 135, 18, 18, 256, 256);
                }
                if ((row + 1) % 5 == 0) {
                    graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, cellX, cellY, 18, 135, 18, 18, 256, 256);
                }
            }
        }

        final var clues = menu.getClues();
        final var errors = menu.getErrors();

        for (int column = 0; column < clues.width(); column++) {
            final int color = errors.erroredColumns().contains(column) ? 0xFFFF3636 : 0xFF363636;
            final var x = leftPos + PADDING_LEFT + column * CELL_SIZE + 1;
            var y = topPos + PADDING_TOP - 6;
            final var columnClues = clues.columnClues(column);
            for (int i = columnClues.length - 1; i >= 0; i--) {
                final var clue = columnClues[i];
                graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, x, y, 135, 36 + clue * 5, 16, 5, 256, 256, color);
                y -= 5;
            }
        }

        for (int row = 0; row < clues.height(); row++) {
            final int color = errors.erroredRows().contains(row) ? 0xFFFF3636 : 0xFF363636;
            var x = leftPos + PADDING_LEFT - 6;
            final var y = topPos + PADDING_TOP + row * CELL_SIZE + 1;
            final var rowClues = clues.rowClues(row);
            for (int i = rowClues.length - 1; i >= 0; i--) {
                final var clue = rowClues[i];
                graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, x, y, 153 + clue * 5, 0, 5, 16, 256, 256, color);
                x -= 5;
            }
        }

        // Render black/white overlay on cells upon completion
        if (menu.isCompleted()) {
            final var completedState = menu.getNonogramState();
            final var alpha = Math.min(1f, completionFadeTime / 20f);
            if (alpha > 0.02f) {
                for (int column = 0; column < completedState.width(); column++) {
                    for (int row = 0; row < completedState.height(); row++) {
                        final var cellX = leftPos + PADDING_LEFT + column * CELL_SIZE;
                        final var cellY = topPos + PADDING_TOP + row * CELL_SIZE;
                        final var mark = completedState.mark(column, row);
                        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, cellX, cellY, (mark == 1) ? 54 : 72, 135, 18, 18, 256, 256);
                    }
                }
            }
        }

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, LEFT_WING, leftPos - 27, topPos + 2, 26, 26);
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
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
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
                guiGraphics.text(font, title, effectiveTitleLabelX, y, 0xFF404040, false);
            }
        }

        // Success notification
        if (menu.isCompleted()) {
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
                guiGraphics.centeredText(font, message, centerX, centerY, (alpha << 24) | 0x21C45A);
            }
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == InputConstants.MOUSE_BUTTON_LEFT
                && autoHackButton != null
                && autoHackButton.active
                && autoHackButton.isMouseOver(event.x(), event.y())) {
            autoHackButtonHeld = true;
            autoHackHeldStartTime = System.currentTimeMillis();
            nextAutoHackRepeatTime = autoHackHeldStartTime + AUTO_HACK_INITIAL_REPEAT_DELAY_MS;
        }

        final var state = menu.getNonogramState();
        final var gridStartX = leftPos + PADDING_LEFT;
        final var gridStartY = topPos + PADDING_TOP;
        final var gridEndX = gridStartX + state.width() * CELL_SIZE;
        final var gridEndY = gridStartY + state.height() * CELL_SIZE;
        if (event.x() >= gridStartX && event.y() >= gridStartY && event.x() < gridEndX && event.y() < gridEndY) {
            final var relativeMouseX = (int) event.x() - gridStartX;
            final var relativeMouseY = (int) event.y() - gridStartY;
            final var column = relativeMouseX / CELL_SIZE;
            final var row = relativeMouseY / CELL_SIZE;
            final var markCurrent = state.mark(column, row);
            final var markToPlace = event.button() == InputConstants.MOUSE_BUTTON_LEFT ? 1 : -1;
            dragOnlyAffects = markCurrent;
            dragErases = (markCurrent == markToPlace);
            if (dragErases) {
                mark(column, row, 0);
            } else {
                mark(column, row, markToPlace);
            }
            draggingButton = event.button();
            return true;
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        if (draggingButton == -1 || event.button() != draggingButton) {
            return super.mouseDragged(event, dx, dy);
        }

        final var state = menu.getNonogramState();
        final var gridStartX = leftPos + PADDING_LEFT;
        final var gridStartY = topPos + PADDING_TOP;
        final var gridEndX = gridStartX + state.width() * CELL_SIZE;
        final var gridEndY = gridStartY + state.height() * CELL_SIZE;
        if (event.x() >= gridStartX && event.y() >= gridStartY && event.x() < gridEndX && event.y() < gridEndY) {
            final var relativeMouseX = (int) event.x() - gridStartX;
            final var relativeMouseY = (int) event.y() - gridStartY;
            final var column = relativeMouseX / CELL_SIZE;
            final var row = relativeMouseY / CELL_SIZE;
            final var currentMark = state.mark(column, row);
            if (dragErases) {
                if (currentMark == dragOnlyAffects) {
                    mark(column, row, 0);
                }
            } else {
                if (currentMark == dragOnlyAffects) {
                    final var markToPlace = event.button() == InputConstants.MOUSE_BUTTON_LEFT ? 1 : -1;
                    mark(column, row, markToPlace);
                }
            }
            return true;
        }

        return super.mouseDragged(event, dx, dy);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (event.button() == InputConstants.MOUSE_BUTTON_LEFT) {
            autoHackButtonHeld = false;
        }

        if (event.button() == draggingButton) {
            draggingButton = -1;
            dragOnlyAffects = 0;
            dragErases = false;
        }
        return super.mouseReleased(event);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (minecraft.options.keyInventory.matches(event)
                || (event.isEscape() && this.shouldCloseOnEsc())) {
            onClose();
            if (minecraft.player != null
                    && minecraft.player.getMainHandItem().is(ModItems.skyScraper.asItem())
                    && minecraft.gameMode != null) {
                minecraft.gameMode.useItem(minecraft.player, InteractionHand.MAIN_HAND);
            }
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        updateAutoHackButtonState();
        if (autoHackButtonHeld && autoHackButton != null && autoHackButton.active) {
            final var currentTime = System.currentTimeMillis();
            if (currentTime >= nextAutoHackRepeatTime) {
                requestAutoHack();
                nextAutoHackRepeatTime = currentTime + getAutoHackRepeatInterval(currentTime);
            }
        }

        if (menu.isCompleted()) {
            if (!completionSoundPlayed) {
                minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.PLAYER_LEVELUP, 0f));
                completionSoundPlayed = true;
            }

            completionFadeTime += partialTicks;
        } else {
            completionFadeTime = 0;
        }

        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTicks);
    }

    private void updateAutoHackButtonState() {
        if (autoHackButton != null) {
            final var canAutoHack = menu instanceof NonogramMenu nonogramMenu && nonogramMenu.canAutoHack();
            autoHackButton.active = canAutoHack;
            autoHackButton.setTooltip(Tooltip.create(canAutoHack ? AUTO_HACK_MESSAGE : NO_AUTO_HACK_MESSAGE));
            if (!canAutoHack) {
                autoHackButtonHeld = false;
            }
        }
    }

    private void requestAutoHack() {
        Balm.networking().sendToServer(new NonogramAutoHackMessage(menu.containerId));
    }

    private long getAutoHackRepeatInterval(long currentTime) {
        final var speedupSteps = Math.max(0, (currentTime - autoHackHeldStartTime - AUTO_HACK_INITIAL_REPEAT_DELAY_MS) / AUTO_HACK_REPEAT_SPEEDUP_INTERVAL_MS);
        final var repeatInterval = AUTO_HACK_REPEAT_INTERVAL_MS >> Math.min(speedupSteps, 30);
        return Math.max(AUTO_HACK_MIN_REPEAT_INTERVAL_MS, repeatInterval);
    }

    public void mark(int column, int row, int mark) {
        if (!menu.isCompleted()) {
            menu.mark(column, row, mark);
            Balm.networking().sendToServer(new NonogramMarkMessage(menu.containerId, column, row, mark));
        }
    }

}

package net.blay09.mods.replikaentropie.client.gui.screens.inventory;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.replikaentropie.menu.ResearchMenu;
import net.blay09.mods.replikaentropie.menu.slot.ResearchCostSlot;
import net.blay09.mods.replikaentropie.menu.slot.ResearchEntrySlot;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class ResearchScreen extends AbstractContainerScreen<ResearchMenu> {

    private static final ResourceLocation BACKGROUND = id("textures/gui/container/research.png");

    private static final int GRID_X = 7;
    private static final int GRID_Y = 17;
    private static final int GRID_WIDTH = 112;
    private static final int GRID_HEIGHT = 108;
    private static final int VISIBLE_COLS = 4;
    private static final int VISIBLE_ROWS = 4;

    private static final int SCROLLBAR_COLOR = 0xFFAAAAAA;
    private static final int SCROLLBAR_X = 110;
    private static final int SCROLLBAR_Y = 18;
    private static final int SCROLLBAR_WIDTH = 7;
    private static final int SCROLLBAR_HEIGHT = 106;

    private int scrollBarScaledHeight;
    private int scrollBarXPos;
    private int scrollBarYPos;
    private int currentOffset;
    private double mouseClickY = -1;
    private int indexWhenClicked;
    private int lastNumberOfMoves;

    private Button emptyButton;
    private Button decryptButton;
    private Button continueButton;
    private Button printButton;
    private Button missingIngredientsButton;

    @Nullable
    private static ResourceLocation lastSelectedResearchId;

    private static final int EASTER_EGG_INDEX = 15;
    private static final String EASTER_EGG = "... test, testing, is this thing on? Hello? Can you hear me? This is Eirote. If you've found this recording, then our mission is either complete... or failed beyond recovery. To whoever hears this: remember that compassion is not weakness. Even within systems built to suppress it, empathy can rewrite the code that binds fate. If we are gone, let that truth endure in our place. End of log.";

    private EditBox searchBox;
    private boolean updatingSearch;

    public ResearchScreen(ResearchMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageWidth = 248;
        imageHeight = 180;
    }

    @Override
    protected void slotClicked(Slot slot, int slotId, int mouseButton, ClickType type) {
        if (slot instanceof ResearchEntrySlot researchEntrySlot) {
            final var researchEntry = researchEntrySlot.getResearchEntry();
            if (researchEntry != null) {
                lastSelectedResearchId = researchEntry.recipe().id();
            }
        }

        super.slotClicked(slot, slotId, mouseButton, type);
    }

    @Override
    protected void init() {
        super.init();
        emptyButton = addRenderableWidget(Button.builder(Component.empty(), it -> {
        }).pos(leftPos + 138, topPos + imageHeight - 25).size(102, 18).build());
        emptyButton.active = false;

        decryptButton = addRenderableWidget(Button.builder(Component.translatable("gui.replikaentropie.sky_scraper.decrypt"), it -> {
            if (minecraft != null && minecraft.gameMode != null) {
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, menu.getClientSelectedResearchIndex());
            }
        }).pos(leftPos + 138, topPos + imageHeight - 25).size(102, 18).build());

        continueButton = addRenderableWidget(Button.builder(Component.translatable("gui.replikaentropie.sky_scraper.continue").withStyle(ChatFormatting.GOLD), it -> {
            if (minecraft != null && minecraft.gameMode != null) {
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, menu.getClientSelectedResearchIndex());
            }
        }).pos(leftPos + 138, topPos + imageHeight - 25).size(102, 18).build());

        printButton = addRenderableWidget(Button.builder(Component.translatable("gui.replikaentropie.sky_scraper.print"), b -> {
            if (minecraft != null && minecraft.gameMode != null) {
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, menu.getClientSelectedResearchIndex());
            }
        }).pos(leftPos + 138, topPos + imageHeight - 25).size(102, 18).build());

        missingIngredientsButton = addRenderableWidget(Button.builder(Component.translatable("gui.replikaentropie.sky_scraper.missing_ingredients"), it -> {
        }).pos(leftPos + 138, topPos + imageHeight - 25).size(102, 18).build());
        missingIngredientsButton.active = false;

        searchBox = new EditBox(font, leftPos + 9, topPos + 127, 90, 11, Component.empty());
        searchBox.setBordered(false);
        searchBox.setValue("");
        searchBox.setMaxLength(EASTER_EGG_INDEX + EASTER_EGG.length());
        searchBox.setResponder(this::onSearchChanged);
        addRenderableWidget(searchBox);

        updateButtonStates();
        recalculateScrollBar();

        if (lastSelectedResearchId != null) {
            final var filteredIndex = menu.getFilteredIndexOf(lastSelectedResearchId);
            if (filteredIndex >= 0) {
                final var targetRow = filteredIndex / VISIBLE_COLS;
                setCurrentOffset(targetRow);
            }
            menu.slots.stream()
                    .filter(it -> it instanceof ResearchEntrySlot researchEntrySlot
                            && researchEntrySlot.getResearchEntry() != null
                            && researchEntrySlot.getResearchEntry().recipe().id().equals(lastSelectedResearchId))
                    .findFirst()
                    .ifPresent(it -> slotClicked(it, it.index, 0, ClickType.PICKUP));
        }
    }

    private void updateButtonStates() {
        if (menu.clientCanPrintSelected()) {
            emptyButton.visible = false;
            decryptButton.visible = false;
            continueButton.visible = false;
            printButton.visible = true;
            missingIngredientsButton.visible = false;
        } else if (menu.clientCanDecryptSelected()) {
            emptyButton.visible = false;
            decryptButton.visible = false;
            continueButton.visible = true;
            printButton.visible = false;
            missingIngredientsButton.visible = false;
        } else if (menu.clientCanUnlockSelected()) {
            emptyButton.visible = false;
            decryptButton.visible = true;
            continueButton.visible = false;
            printButton.visible = false;
            missingIngredientsButton.visible = false;
        } else if (menu.clientMissingIngredients()) {
            emptyButton.visible = false;
            decryptButton.visible = false;
            continueButton.visible = false;
            printButton.visible = false;
            missingIngredientsButton.visible = true;
        } else {
            emptyButton.visible = true;
            decryptButton.visible = false;
            continueButton.visible = false;
            printButton.visible = false;
            missingIngredientsButton.visible = false;
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float delta, int mouseX, int mouseY) {
        if (menu.isScrollOffsetDirty()) {
            setCurrentOffset(currentOffset);
            menu.setScrollOffsetDirty(false);
        }

        if (mouseClickY != -1) {
            float pixelsPerFilter = (SCROLLBAR_HEIGHT - scrollBarScaledHeight) / (float) Math.max(1,
                    (int) Math.ceil(menu.getEntryCount() / (float) VISIBLE_COLS) - VISIBLE_ROWS);
            if (pixelsPerFilter != 0) {
                int numberOfFiltersMoved = (int) ((mouseY - mouseClickY) / pixelsPerFilter);
                if (numberOfFiltersMoved != lastNumberOfMoves) {
                    setCurrentOffset(indexWhenClicked + numberOfFiltersMoved);
                    lastNumberOfMoves = numberOfFiltersMoved;
                }
            }
        }

        updateButtonStates();

        guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        for (final var slot : menu.slots) {
            if (slot instanceof ResearchCostSlot researchCostSlot) {
                if (researchCostSlot.getCost() > 0 && researchCostSlot.getAvailable() < researchCostSlot.getCost()) {
                    guiGraphics.fill(leftPos + slot.x, topPos + slot.y, leftPos + slot.x + 16, topPos + slot.y + 16, 0xFFA17171);
                }
            } else if (slot instanceof ResearchEntrySlot researchEntrySlot) {
                final var entry = researchEntrySlot.getResearchEntry();
                if (entry != null) {
                    final var backgroundColor = switch (entry.state()) {
                        case MISSING_DEPENDENCIES -> 0xFF474747;
                        case MISSING_INGREDIENTS -> 0xFFA17171;
                        case IN_PROGRESS -> 0xFFD5B97B;
                        case UNLOCKED -> 0xFF8BCE91;
                        default -> 0;
                    };
                    if (backgroundColor != 0) {
                        guiGraphics.fill(leftPos + slot.x, topPos + slot.y, leftPos + slot.x + 16, topPos + slot.y + 16, backgroundColor);
                    }
                }
            }
        }

        guiGraphics.fill(scrollBarXPos, scrollBarYPos, scrollBarXPos + SCROLLBAR_WIDTH, scrollBarYPos + scrollBarScaledHeight, SCROLLBAR_COLOR);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(font, title, titleLabelX, titleLabelY, 0xFF404040, false);

        final var selectedResearch = menu.getClientSelectedResearch();
        if (selectedResearch != null) {
            guiGraphics.drawString(font, selectedResearch.title(), 137, titleLabelY, 0xFF404040, false);

            if (selectedResearch.state() == ResearchMenu.MenuResearchState.UNLOCKED) {
                final var decryptedText = selectedResearch.description().getString().replaceAll("[\\[\\]]", "");
                guiGraphics.drawWordWrap(font, Component.literal(decryptedText), 140, 20, 98, 0xFFFFFFFF);
            } else {
                final var encryptedText = selectedResearch.description().getString();
                final var component = Component.empty();
                final var parts = encryptedText.split("[\\[\\]]");
                for (int i = 0; i < parts.length; i++) {
                    if (i % 2 == 1) {
                        component.append(Component.literal(parts[i]));
                    } else {
                        component.append(Component.literal(parts[i]).withStyle(ChatFormatting.OBFUSCATED));
                    }
                }
                guiGraphics.drawWordWrap(font, component, 140, 20, 98, 0xFFFFFFFF);
            }
        }

        final var dataCollectedText = Component.translatable("gui.replikaentropie.sky_scraper.data_collected", menu.getDataCollected())
                .withStyle(ChatFormatting.DARK_GREEN);
        guiGraphics.drawCenteredString(font, dataCollectedText, 63, 159, 0xFFFFFFFF);

        if (selectedResearch != null && selectedResearch.state() == ResearchMenu.MenuResearchState.UNLOCKED && emptyButton.visible) {
            final var unlockedText = Component.translatable("gui.replikaentropie.sky_scraper.unlocked").withStyle(ChatFormatting.DARK_GREEN);
            guiGraphics.drawCenteredString(font, unlockedText, emptyButton.getX() - leftPos + emptyButton.getWidth() / 2, emptyButton.getY() - topPos + emptyButton.getHeight() / 2 - font.lineHeight / 2, 0xFFFFFFFF);
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
        if (menu.getCarried().isEmpty()) {
            if (hoveredSlot instanceof ResearchEntrySlot researchEntrySlot) {
                final var researchEntry = researchEntrySlot.getResearchEntry();
                if (researchEntry != null) {
                    final var iconItem = researchEntrySlot.getItem();
                    final List<Component> tooltip = List.of(researchEntry.title());
                    guiGraphics.renderTooltip(font, tooltip, iconItem.getTooltipImage(), x, y);
                }
            } else if (hoveredSlot instanceof ResearchCostSlot researchCostSlot) {
                if (researchCostSlot.getCost() > 0) {
                    final var itemStack = researchCostSlot.getItem();
                    final var available = researchCostSlot.getAvailable();
                    final var cost = researchCostSlot.getCost();
                    final var title = itemStack.getHoverName();
                    final var subtitle = Component.translatable("gui.replikaentropie.sky_scraper.cost.tooltip", available, cost)
                            .withStyle(available >= cost ? ChatFormatting.GREEN : ChatFormatting.RED);
                    final List<Component> tooltip = List.of(title, subtitle);
                    guiGraphics.renderTooltip(font, tooltip, itemStack.getTooltipImage(), x, y);
                }
            }
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (delta == 0) {
            return false;
        }

        // POSTJAM area check?
        setCurrentOffset(delta > 0 ? currentOffset - 1 : currentOffset + 1);
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseX >= scrollBarXPos && mouseX <= scrollBarXPos + SCROLLBAR_WIDTH && mouseY >= scrollBarYPos && mouseY <= scrollBarYPos + scrollBarScaledHeight) {
            mouseClickY = mouseY;
            indexWhenClicked = currentOffset;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        final var result = super.mouseReleased(mouseX, mouseY, button);
        if (button != -1 && mouseClickY != -1) {
            mouseClickY = -1;
            indexWhenClicked = 0;
            lastNumberOfMoves = 0;
        }

        return result;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        return super.charTyped(codePoint, modifiers);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == InputConstants.KEY_ESCAPE) {
            minecraft.player.closeContainer();
        }

        return searchBox.keyPressed(keyCode, scanCode, modifiers)
                || searchBox.canConsumeInput()
                || super.keyPressed(keyCode, scanCode, modifiers);
    }


    private void recalculateScrollBar() {
        int scrollBarTotalHeight = SCROLLBAR_HEIGHT - 1;
        this.scrollBarScaledHeight = (int) (scrollBarTotalHeight * Math.min(1f,
                ((float) VISIBLE_ROWS / (Math.ceil(menu.getEntryCount() / (float) VISIBLE_COLS)))));
        this.scrollBarXPos = leftPos + SCROLLBAR_X;
        this.scrollBarYPos = topPos + SCROLLBAR_Y + ((scrollBarTotalHeight - scrollBarScaledHeight) * currentOffset / Math.max(1,
                (int) Math.ceil((menu.getEntryCount() / (float) VISIBLE_COLS)) - VISIBLE_ROWS));
    }

    private void setCurrentOffset(int currentOffset) {
        this.currentOffset = Math.max(0, Math.min(currentOffset, (int) Math.ceil(menu.getEntryCount() / (float) VISIBLE_COLS) - VISIBLE_ROWS));

        menu.setScrollOffset(this.currentOffset);

        recalculateScrollBar();
    }

    private void onSearchChanged(String value) {
        if (updatingSearch) {
            return;
        }

        final var searchQuery = value.length() <= EASTER_EGG_INDEX ? value : value.substring(0, EASTER_EGG_INDEX);
        menu.setSearchQuery(searchQuery);

        if (value.length() > EASTER_EGG_INDEX) {
            updatingSearch = true;
            final var extraCharCount = value.length() - EASTER_EGG_INDEX;
            final var extraCharEnd = Math.min(extraCharCount, EASTER_EGG.length());
            final var modifiedValue = searchQuery + EASTER_EGG.substring(0, extraCharEnd);
            searchBox.setValue(modifiedValue);
            updatingSearch = false;
        }
    }
}


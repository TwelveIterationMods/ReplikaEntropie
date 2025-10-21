package net.blay09.mods.replikaentropie.menu;

import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractScrapGeneratorMenu extends AbstractContainerMenu {

    protected final Inventory playerInventory;
    protected final Container container;
    protected final ContainerData data;

    public static final int DATA_INPUT_GENERATION_TIME = 0;
    public static final int DATA_MAX_INPUT_GENERATION_TIME = 1;
    public static final int DATA_OUTPUT_GENERATION_TIME = 2;
    public static final int DATA_MAX_OUTPUT_GENERATION_TIME = 3;
    public static final int DATA_SCRAP_FRACTIONAL = 4;
    public static final int DATA_COUNT = 5;

    protected AbstractScrapGeneratorMenu(@Nullable MenuType<?> menuType, int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(menuType, containerId);
        this.playerInventory = playerInventory;
        this.container = container;
        this.data = data;
        addDataSlots(data);
    }

    protected void addPlayerInventorySlots() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 118 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(playerInventory, i, 8 + i * 18, 176));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        container.stopOpen(player);
    }

    public float getInputProcessingProgress() {
        final var progress = data.get(DATA_INPUT_GENERATION_TIME);
        final var maxProgress = data.get(DATA_MAX_INPUT_GENERATION_TIME);
        return maxProgress > 0 ? (float) progress / maxProgress : 0f;
    }

    public float getOutputProcessingProgress() {
        final var progress = data.get(DATA_OUTPUT_GENERATION_TIME);
        final var maxProgress = data.get(DATA_MAX_OUTPUT_GENERATION_TIME);
        return maxProgress > 0 ? (float) progress / maxProgress : 0f;
    }

    public float getFractionalScrap() {
        return Mth.clamp(data.get(DATA_SCRAP_FRACTIONAL) / 100f, 0f, 1f);
    }
}

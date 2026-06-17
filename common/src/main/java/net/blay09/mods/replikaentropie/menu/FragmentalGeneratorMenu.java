package net.blay09.mods.replikaentropie.menu;

import net.blay09.mods.replikaentropie.block.entity.FragmentalGeneratorBlockEntity;
import net.blay09.mods.replikaentropie.menu.slot.OutputSlot;
import net.blay09.mods.replikaentropie.util.QuickMove;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class FragmentalGeneratorMenu extends AbstractContainerMenu {
    private static final int INPUTS_COUNT = FragmentalGeneratorBlockEntity.INPUT_SLOT_COUNT;
    private static final int MAX_TEMPERATURE_CELSIUS = 5500;

    private final Container container;
    private final ContainerData data;
    private final QuickMove.Routing quickMove;

    public static final int DATA_PROCESSING_TIME_START = 0;
    public static final int DATA_PROCESSING_TIME_END = DATA_PROCESSING_TIME_START + INPUTS_COUNT - 1;
    public static final int DATA_MAX_PROCESSING_TIME_START = DATA_PROCESSING_TIME_END + 1;
    public static final int DATA_MAX_PROCESSING_TIME_END = DATA_MAX_PROCESSING_TIME_START + INPUTS_COUNT - 1;
    public static final int DATA_TEMPERATURE = DATA_MAX_PROCESSING_TIME_END + 1;
    public static final int DATA_MAX_TEMPERATURE = DATA_TEMPERATURE + 1;
    public static final int DATA_COUNT = DATA_MAX_TEMPERATURE + 1;

    public FragmentalGeneratorMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(FragmentalGeneratorBlockEntity.CONTAINER_SIZE), new SimpleContainerData(DATA_COUNT));
    }

    public FragmentalGeneratorMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(ModMenus.fragmentalGenerator.value(), containerId);
        this.container = container;
        checkContainerSize(container, FragmentalGeneratorBlockEntity.CONTAINER_SIZE);
        this.data = data;
        addDataSlots(data);

        addSlot(new FragmentalHeaterOutputSlot(container, FragmentalGeneratorBlockEntity.OUTPUT_SLOT, 77, 48));

        addSlot(new FragmentalHeaterInputSlot(container, FragmentalGeneratorBlockEntity.INPUT_SLOT_START, 47, 21));
        addSlot(new FragmentalHeaterInputSlot(container, FragmentalGeneratorBlockEntity.INPUT_SLOT_START + 1, 108, 21));
        addSlot(new FragmentalHeaterInputSlot(container, FragmentalGeneratorBlockEntity.INPUT_SLOT_START + 2, 22, 46));
        addSlot(new FragmentalHeaterInputSlot(container, FragmentalGeneratorBlockEntity.INPUT_SLOT_START + 3, 133, 46));
        addSlot(new FragmentalHeaterInputSlot(container, FragmentalGeneratorBlockEntity.INPUT_SLOT_START + 4, 47, 71));
        addSlot(new FragmentalHeaterInputSlot(container, FragmentalGeneratorBlockEntity.INPUT_SLOT_START + 5, 108, 71));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 125 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(playerInventory, i, 8 + i * 18, 183));
        }

        quickMove = QuickMove.create(this, this::moveItemStackTo)
                .slotRange("inputs", 1, 1 + INPUTS_COUNT)
                .route(QuickMove.PLAYER, "inputs")
                .build();

        container.startOpen(playerInventory.player);
    }

    public float getProcessingProgress(int slot) {
        final var processingTime = data.get(DATA_PROCESSING_TIME_START + slot);
        final var maxProcessingTime = data.get(DATA_MAX_PROCESSING_TIME_START + slot);
        return maxProcessingTime == 0 ? 0f : (float) processingTime / maxProcessingTime;
    }

    public int getTemperature() {
        return data.get(DATA_TEMPERATURE);
    }

    public int getMaxTemperature() {
        return data.get(DATA_MAX_TEMPERATURE);
    }

    public float getTemperatureProgress() {
        final var maxTemperature = getMaxTemperature();
        if (maxTemperature <= 0) {
            return 0f;
        }

        return Mth.clamp(getTemperature() / (float) maxTemperature, 0f, 1f);
    }

    public Component getProgressTooltip() {
        final var temperatureCelsius = Math.round(getTemperatureProgress() * MAX_TEMPERATURE_CELSIUS);
        return Component.translatable("gui.replikaentropie.fragmental_generator.progress", temperatureCelsius, MAX_TEMPERATURE_CELSIUS);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (index == 0 && !canExtractOutput()) {
            punishUnsafeOutputPickup(player);
            return ItemStack.EMPTY;
        }

        return quickMove.transfer(this, player, index);
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

    private static class FragmentalHeaterInputSlot extends Slot {
        public FragmentalHeaterInputSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }

    private boolean canExtractOutput() {
        return getTemperatureProgress() <= 0.25f;
    }

    private void punishUnsafeOutputPickup(Player player) {
        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.playSound(null, player, SoundEvents.FLINTANDSTEEL_USE, SoundSource.PLAYERS, 1f, 1f);
            player.igniteForSeconds(4f);
        }
    }

    private class FragmentalHeaterOutputSlot extends OutputSlot {
        public FragmentalHeaterOutputSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPickup(Player player) {
            final var canExtractOutput = canExtractOutput();
            if (!canExtractOutput) {
                punishUnsafeOutputPickup(player);
            }

            return canExtractOutput;
        }

        @Override
        public boolean isHighlightable() {
            return canExtractOutput();
        }
    }
}

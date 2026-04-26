package net.blay09.mods.replikaentropie.menu;

import net.blay09.mods.replikaentropie.block.entity.FragmentalGeneratorBlockEntity;
import net.blay09.mods.replikaentropie.util.QuickMove;
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
    private static final int INPUTS_COUNT = FragmentalGeneratorBlockEntity.CONTAINER_SIZE;

    private final Container container;
    private final ContainerData data;
    private final QuickMove.Routing quickMove;

    public static final int DATA_PROCESSING_TIME_START = 0;
    public static final int DATA_PROCESSING_TIME_END = DATA_PROCESSING_TIME_START + INPUTS_COUNT - 1;
    public static final int DATA_MAX_PROCESSING_TIME_START = DATA_PROCESSING_TIME_END + 1;
    public static final int DATA_MAX_PROCESSING_TIME_END = DATA_MAX_PROCESSING_TIME_START + INPUTS_COUNT - 1;
    public static final int DATA_TEMPERATURE = DATA_MAX_PROCESSING_TIME_END + 1;
    public static final int DATA_MAX_TEMPERATURE = DATA_TEMPERATURE + 1;
    public static final int DATA_CURRENT_POWER = DATA_MAX_TEMPERATURE + 1;
    public static final int DATA_MAX_POWER = DATA_CURRENT_POWER + 1;
    public static final int DATA_COUNT = DATA_MAX_POWER + 1;

    public FragmentalGeneratorMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(FragmentalGeneratorBlockEntity.CONTAINER_SIZE), new SimpleContainerData(DATA_COUNT));
    }

    public FragmentalGeneratorMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(ModMenus.fragmentalGenerator.value(), containerId);
        this.container = container;
        checkContainerSize(container, FragmentalGeneratorBlockEntity.CONTAINER_SIZE);
        this.data = data;
        addDataSlots(data);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                addSlot(new Slot(container, j + i * 4, 22 + j * 37, 21 + i * 25) {
                    @Override
                    public int getMaxStackSize() {
                        return 1;
                    }
                });
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 125 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(playerInventory, i, 8 + i * 18, 183));
        }

        quickMove = QuickMove.create(this, this::moveItemStackTo)
                .slotRange("inputs", 0, 12)
                .route(QuickMove.PLAYER, "inputs")
                .build();

        container.startOpen(playerInventory.player);
    }

    public float getProcessingProgress(int slot) {
        final var processingTime = data.get(DATA_PROCESSING_TIME_START + slot);
        final var maxProcessingTime = data.get(DATA_MAX_PROCESSING_TIME_START + slot);
        return maxProcessingTime == 0 ? 0f : (float) processingTime / maxProcessingTime;
    }

    public float getPowerProgress() {
        final var maxPower = data.get(DATA_MAX_POWER);
        if (maxPower <= 0) {
            return 0f;
        }

        return Mth.clamp(data.get(DATA_CURRENT_POWER) / (float) maxPower, 0f, 1f);
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

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
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
}

package net.blay09.mods.replikaentropie.menu;

import net.blay09.mods.replikaentropie.block.entity.RecyclerBlockEntity;
import net.blay09.mods.replikaentropie.menu.slot.OutputSlot;
import net.blay09.mods.replikaentropie.menu.slot.RecyclerSlot;
import net.blay09.mods.replikaentropie.recipe.RecyclerRecipe;
import net.blay09.mods.replikaentropie.util.QuickMove;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public class RecyclerMenu extends AbstractContainerMenu implements MakeshiftPoweredMenu {

    private final Inventory inventory;
    private final Container container;
    private final ContainerData data;
    private final ContainerLevelAccess access;
    private final QuickMove.Routing quickMove;

    public static final int DATA_PROCESSING_TIME = 0;
    public static final int DATA_MAX_PROCESSING_TIME = 1;
    public static final int DATA_CURRENT_POWER = 2;
    public static final int DATA_MAX_POWER = 3;
    public static final int DATA_COUNT = 4;

    public RecyclerMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainer(4), new SimpleContainerData(DATA_COUNT), ContainerLevelAccess.NULL);
    }

    public RecyclerMenu(int containerId, Inventory inventory, Container container, ContainerData data, ContainerLevelAccess access) {
        this(ModMenus.recycler.value(), containerId, inventory, container, data, access);
    }

    public RecyclerMenu(@Nullable MenuType<?> menuType, int containerId, Inventory inventory, Container container, ContainerData data, ContainerLevelAccess access) {
        super(menuType, containerId);
        this.inventory = inventory;
        this.container = container;
        checkContainerSize(container, 4);
        this.data = data;
        this.access = access;
        addDataSlots(data);

        addSlot(new RecyclerSlot(container, 0, 48, 55));
        addSlot(new OutputSlot(container, 1, 108, 24));
        addSlot(new OutputSlot(container, 2, 108, 55));
        addSlot(new OutputSlot(container, 3, 108, 86));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 125 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(inventory, i, 8 + i * 18, 183));
        }

        quickMove = QuickMove.create(this, this::moveItemStackTo)
                .slot("input", 0)
                .route(it -> RecyclerRecipe.getRecipe(inventory.player.level(), it).isPresent(), QuickMove.PLAYER, "input")
                .build();

        container.startOpen(inventory.player);
    }

    public float getProcessingProgress() {
        return data.get(DATA_PROCESSING_TIME) / (float) data.get(DATA_MAX_PROCESSING_TIME);
    }

    public float getPowerProgress() {
        final var maxPower = data.get(DATA_MAX_POWER);
        if (maxPower <= 0) {
            return 0f;
        }

        return Math.clamp(data.get(DATA_CURRENT_POWER) / (float) maxPower, 0f, 1f);
    }

    @Override
    public void convertClickToPower() {
        access.execute((level, pos) -> {
            final BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof RecyclerBlockEntity recycler) {
                recycler.getEnergyStorage().fill(inventory.player.isCreative() ? Integer.MAX_VALUE : 250, false);
            }
        });
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

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return quickMove.transfer(this, player, index);
    }
}

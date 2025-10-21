package net.blay09.mods.replikaentropie.menu;

import net.blay09.mods.replikaentropie.menu.slot.OutputSlot;
import net.blay09.mods.replikaentropie.menu.slot.RecyclerSlot;
import net.blay09.mods.replikaentropie.recipe.RecyclerRecipe;
import net.blay09.mods.replikaentropie.util.QuickMove;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class RecyclerMenu extends AbstractContainerMenu {

    private final Container container;
    private final ContainerData data;
    private final QuickMove.Routing quickMove;

    public static final int DATA_PROCESSING_TIME = 0;
    public static final int DATA_MAX_PROCESSING_TIME = 1;
    public static final int DATA_FRACTIONAL_SCRAP = 2;
    public static final int DATA_FRACTIONAL_BIOMASS = 3;
    public static final int DATA_FRACTIONAL_FRAGMENTS = 4;
    public static final int DATA_COUNT = 5;

    public RecyclerMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(4), new SimpleContainerData(DATA_COUNT));
    }

    public RecyclerMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        this(ModMenus.recycler.get(), containerId, playerInventory, container, data);
    }

    public RecyclerMenu(@Nullable MenuType<?> menuType, int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(menuType, containerId);
        this.container = container;
        checkContainerSize(container, 4);
        this.data = data;
        addDataSlots(data);

        addSlot(new RecyclerSlot(container, 0, 48, 55));
        addSlot(new OutputSlot(container, 1, 108, 24));
        addSlot(new OutputSlot(container, 2, 108, 55));
        addSlot(new OutputSlot(container, 3, 108, 86));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 125 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(playerInventory, i, 8 + i * 18, 183));
        }

        quickMove = QuickMove.create(this, this::moveItemStackTo)
                .slot("input", 0)
                .route(it -> RecyclerRecipe.getRecipe(playerInventory.player.level(), it).isPresent(), QuickMove.PLAYER, "input")
                .build();

        container.startOpen(playerInventory.player);
    }

    public float getProcessingProgress() {
        return data.get(DATA_PROCESSING_TIME) / (float) data.get(DATA_MAX_PROCESSING_TIME);
    }

    public float getFractionalScrap() {
        return Mth.clamp(data.get(DATA_FRACTIONAL_SCRAP) / 100f, 0f, 0.99f);
    }

    public float getFractionalBiomass() {
        return Mth.clamp(data.get(DATA_FRACTIONAL_BIOMASS) / 100f, 0f, 0.99f);
    }

    public float getFractionalFragments() {
        return Mth.clamp(data.get(DATA_FRACTIONAL_FRAGMENTS) / 100f, 0f, 0.99f);
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

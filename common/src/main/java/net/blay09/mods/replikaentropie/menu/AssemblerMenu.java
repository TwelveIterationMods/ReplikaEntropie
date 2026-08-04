package net.blay09.mods.replikaentropie.menu;

import net.blay09.mods.replikaentropie.component.ModDataComponents;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.menu.slot.AssemblerTicketSlot;
import net.blay09.mods.replikaentropie.menu.slot.OutputSlot;
import net.blay09.mods.replikaentropie.menu.slot.ReadonlySlot;
import net.blay09.mods.replikaentropie.power.MakeshiftPsu;
import net.blay09.mods.replikaentropie.recipe.AssemblerRecipe;
import net.blay09.mods.replikaentropie.util.QuickMove;
import net.minecraft.util.Mth;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class AssemblerMenu extends AbstractContainerMenu implements MakeshiftPoweredMenu {

    private final Inventory inventory;
    private final Container previewContainer = new SimpleContainer(9);
    private final Container container;
    private final ContainerData data;
    private final MakeshiftPsu makeshiftPsu;
    private final QuickMove.Routing quickMove;

    public static final int DATA_PROCESSING_TIME = 0;
    public static final int DATA_MAX_PROCESSING_TIME = 1;
    public static final int DATA_CURRENT_POWER = 2;
    public static final int DATA_MAX_POWER = 3;
    public static final int DATA_OVERHEATED = 4;
    public static final int DATA_COUNT = 5;

    public AssemblerMenu(int id, Inventory inventory) {
        this(id, inventory, new SimpleContainer(11), new SimpleContainerData(DATA_COUNT), ContainerLevelAccess.NULL, MakeshiftPsu.EMPTY);
    }

    public AssemblerMenu(int id, Inventory inventory, Container container, ContainerData data, ContainerLevelAccess access, MakeshiftPsu makeshiftPsu) {
        super(ModMenus.assembler.value(), id);
        this.inventory = inventory;
        this.container = container;
        checkContainerSize(container, 11);
        this.data = data;
        this.makeshiftPsu = makeshiftPsu;
        addDataSlots(data);

        addSlot(new OutputSlot(container, 0, 98, 63));
        addSlot(new AssemblerTicketSlot(container, 1, 26, 63) {
            @Override
            public void setChanged() {
                super.setChanged();
                slotsChanged(container);
            }
        });

        for (int i = 0; i < 5; i++) {
            addSlot(new Slot(container, 2 + i, 62 + i * 18, 92));
        }
        for (int i = 0; i < 4; i++) {
            addSlot(new Slot(container, 2 + 5 + i, 70 + i * 18, 110));
        }

        for (int i = 0; i < 4; i++) {
            addSlot(new ReadonlySlot(previewContainer, i, 71 + i * 18, 16));
        }
        for (int i = 0; i < 5; i++) {
            addSlot(new ReadonlySlot(previewContainer, 4 + i, 62 + i * 18, 34));
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 140 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(inventory, i, 8 + i * 18, 198));
        }

        quickMove = QuickMove.create(this, this::moveItemStackTo)
                .slot("ticket", 1)
                .slotRange("inputs", 2, 7)
                .route(it -> it.is(ModItems.assemblyTicket), QuickMove.PLAYER, "ticket")
                .route(it -> !it.is(ModItems.assemblyTicket), QuickMove.PLAYER, "inputs")
                .build();

        container.startOpen(inventory.player);

        updatePreviewFromTicket();
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

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);
        if (container == this.container) {
            updatePreviewFromTicket();
        }
    }

    public float getAssemblyProgress() {
        final var processingTime = data.get(DATA_PROCESSING_TIME);
        final var maxProcessingTime = data.get(DATA_MAX_PROCESSING_TIME);
        return maxProcessingTime > 0 ? Mth.clamp(processingTime / (float) maxProcessingTime, 0f, 1f) : 0;
    }

    public float getPowerProgress() {
        final var maxPower = data.get(DATA_MAX_POWER);
        if (maxPower <= 0) {
            return 0f;
        }

        return Mth.clamp(data.get(DATA_CURRENT_POWER) / (float) maxPower, 0f, 1f);
    }

    public int getCurrentPower() {
        return data.get(DATA_CURRENT_POWER);
    }

    public int getMaxPower() {
        return data.get(DATA_MAX_POWER);
    }

    @Override
    public boolean isMakeshiftPsuOverheated() {
        return data.get(DATA_OVERHEATED) != 0;
    }

    @Override
    public MakeshiftPsu getMakeshiftPsu() {
        return makeshiftPsu;
    }

    private void updatePreviewFromTicket() {
        final var ticketStack = container.getItem(1);
        if (ticketStack.isEmpty()) {
            clearPreview();
            return;
        }

        final var ticketData = ticketStack.get(ModDataComponents.assemblyTicket());
        if (ticketData == null || ticketData.recipeId().isEmpty()) {
            clearPreview();
            return;
        }

        if (!(inventory.player.level() instanceof ServerLevel serverLevel)) {
            clearPreview();
            return;
        }

        final var recipe = serverLevel.recipeAccess().byKey(ResourceKey.create(Registries.RECIPE, ticketData.recipeId().get())).orElse(null);
        if (recipe == null || !(recipe.value() instanceof AssemblerRecipe assemblerRecipe)) {
            clearPreview();
            return;
        }

        int i = 0;
        for (final var countedIngredient : assemblerRecipe.ingredients()) {
            if (i >= previewContainer.getContainerSize()) {
                break;
            }

            final var validItems = countedIngredient.ingredient().items();
            final var firstItem = validItems.findFirst().orElse(null);
            if (firstItem != null) {
                // POSTJAM Have preview slots support Ingredient
                final var itemStack = new ItemStack(firstItem);
                itemStack.setCount(Math.max(1, countedIngredient.count()));
                previewContainer.setItem(i, itemStack);
            } else {
                previewContainer.setItem(i, ItemStack.EMPTY);
            }
            i++;
        }

        for (; i < previewContainer.getContainerSize(); i++) {
            previewContainer.setItem(i, ItemStack.EMPTY);
        }
    }

    private void clearPreview() {
        for (int i = 0; i < previewContainer.getContainerSize(); i++) {
            previewContainer.setItem(i, ItemStack.EMPTY);
        }
    }
}

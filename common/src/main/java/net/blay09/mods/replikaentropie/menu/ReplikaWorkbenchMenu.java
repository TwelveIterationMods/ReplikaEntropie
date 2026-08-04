package net.blay09.mods.replikaentropie.menu;

import net.blay09.mods.replikaentropie.block.entity.ReplikaWorkbenchBlockEntity;
import net.blay09.mods.replikaentropie.component.ReplikaParts;
import net.blay09.mods.replikaentropie.core.replika.ReplikaArmor;
import net.blay09.mods.replikaentropie.power.MakeshiftPsu;
import net.blay09.mods.replikaentropie.tag.ModItemTags;
import net.blay09.mods.replikaentropie.util.QuickMove;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class ReplikaWorkbenchMenu extends AbstractContainerMenu implements MakeshiftPoweredMenu {

    public static final int DATA_CURRENT_POWER = 0;
    public static final int DATA_MAX_POWER = 1;
    public static final int DATA_OVERHEATED = 2;
    public static final int DATA_COUNT = 3;
    private static final int PART_SLOT_COUNT = 8;
    private static final int CENTER_SLOT_X = 51;
    private static final int CENTER_SLOT_Y = 55;

    private final Inventory inventory;
    private final Container container;
    private final ContainerData data;
    private final ContainerLevelAccess access;
    private final MakeshiftPsu makeshiftPsu;
    private final QuickMove.Routing quickMove;

    private final SimpleContainer partContainer = new SimpleContainer(PART_SLOT_COUNT) {
        @Override
        public boolean canPlaceItem(int slot, ItemStack itemStack) {
            return canInstallPart(itemStack);
        }
    };

    public ReplikaWorkbenchMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainer(1), new SimpleContainerData(DATA_COUNT), ContainerLevelAccess.NULL, MakeshiftPsu.EMPTY);
    }

    public ReplikaWorkbenchMenu(int containerId, Inventory inventory, Container container, ContainerData data, ContainerLevelAccess access, MakeshiftPsu makeshiftPsu) {
        this(ModMenus.replikaWorkbench.value(), containerId, inventory, container, data, access, makeshiftPsu);
    }

    public ReplikaWorkbenchMenu(@Nullable MenuType<?> menuType, int containerId, Inventory inventory, Container container, ContainerData data, ContainerLevelAccess access, MakeshiftPsu makeshiftPsu) {
        super(menuType, containerId);
        this.inventory = inventory;
        this.container = container;
        this.data = data;
        this.access = access;
        this.makeshiftPsu = makeshiftPsu;
        addDataSlots(data);

        addSlot(new ReplikaWorkbenchSlot(container, ReplikaWorkbenchBlockEntity.CENTER_SLOT, CENTER_SLOT_X, CENTER_SLOT_Y));

        var partSlot = 0;
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                if (column == 1 && row == 1) {
                    continue;
                }

                final var x = 22 + column * 29;
                final var y = 26 + row * 29;
                addSlot(new ReplikaPartSlot(partContainer, partSlot++, x, y));
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 125 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(inventory, i, 8 + i * 18, 183));
        }

        quickMove = QuickMove.create(this, this::moveItemStackTo)
                .slot("center", ReplikaWorkbenchBlockEntity.CENTER_SLOT)
                .slotRange("parts", 1, 9)
                .route(itemStack -> itemStack.is(ModItemTags.REPLIKA_WORKBENCH_MODDABLE) || itemStack.is(ModItemTags.CHARGEABLE), QuickMove.PLAYER, "center")
                .route(itemStack -> itemStack.is(ModItemTags.REPLIKA_WORKBENCH_PARTS), QuickMove.PLAYER, "parts")
                .build();

        container.startOpen(inventory.player);
        refreshPartSlotsFromCenter();
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

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }

    private void refreshPartSlotsFromCenter() {
        partContainer.clearContent();

        final var centerStack = container.getItem(ReplikaWorkbenchBlockEntity.CENTER_SLOT);
        if (!ReplikaArmor.isReplikaArmor(centerStack)) {
            return;
        }

        for (final var installedPart : ReplikaArmor.getInstalledParts(centerStack)) {
            final var partSlot = installedPart.slot() - 1;
            final var part = installedPart.part().create();
            if (partSlot >= 0 && partSlot < PART_SLOT_COUNT && ReplikaArmor.isMatchingPart(centerStack, part)) {
                partContainer.setItem(partSlot, part.copyWithCount(1));
            }
        }
    }

    private void commitPartSlotsToCenter() {
        final var centerStack = container.getItem(ReplikaWorkbenchBlockEntity.CENTER_SLOT);
        if (!ReplikaArmor.isReplikaArmor(centerStack)) {
            partContainer.clearContent();
            return;
        }

        final var parts = new ArrayList<ReplikaParts.InstalledPart>();
        for (int slot = 0; slot < PART_SLOT_COUNT; slot++) {
            final var part = partContainer.getItem(slot);
            if (part.isEmpty()) {
                continue;
            }

            if (ReplikaArmor.isMatchingPart(centerStack, part)) {
                parts.add(new ReplikaParts.InstalledPart(slot + 1, ItemStackTemplate.fromNonEmptyStack(part.copyWithCount(1))));
            } else {
                partContainer.setItem(slot, ItemStack.EMPTY);
            }
        }

        ReplikaArmor.setInstalledParts(centerStack, parts);
        container.setChanged();
        broadcastChanges();
    }

    private boolean canInstallPart(ItemStack part) {
        final var centerStack = container.getItem(ReplikaWorkbenchBlockEntity.CENTER_SLOT);
        return ReplikaArmor.isReplikaArmor(centerStack) && ReplikaArmor.isMatchingPart(centerStack, part);
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

    private class ReplikaWorkbenchSlot extends Slot {
        public ReplikaWorkbenchSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public void setChanged() {
            super.setChanged();
            refreshPartSlotsFromCenter();
        }

        @Override
        public boolean mayPlace(ItemStack itemStack) {
            return container.canPlaceItem(getContainerSlot(), itemStack);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }

    private class ReplikaPartSlot extends Slot {
        private ItemStack previousStack = ItemStack.EMPTY;

        public ReplikaPartSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public void set(ItemStack itemStack) {
            previousStack = getItem().copy();
            super.set(itemStack);
        }

        @Override
        public ItemStack remove(int amount) {
            previousStack = getItem().copy();
            return super.remove(amount);
        }

        @Override
        public void setChanged() {
            super.setChanged();
            commitPartSlotsToCenter();

            final var installedPart = getItem();
            if (!ItemStack.matches(previousStack, installedPart)) {
                if (!installedPart.isEmpty()) {
                    access.execute((level, pos) -> {
                        if (!level.isClientSide()) {
                            level.playSound(null, pos, SoundEvents.SMITHING_TABLE_USE, SoundSource.BLOCKS, 0.5f, 1f);
                        }
                    });
                } else {
                    access.execute((level, pos) -> {
                        if (!level.isClientSide()) {
                            level.playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.5f, 1f);
                        }
                    });
                }
            }

            previousStack = ItemStack.EMPTY;
        }

        @Override
        public boolean mayPlace(ItemStack itemStack) {
            return container.canPlaceItem(getContainerSlot(), itemStack);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }
}

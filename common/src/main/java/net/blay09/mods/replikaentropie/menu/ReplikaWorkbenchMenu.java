package net.blay09.mods.replikaentropie.menu;

import net.blay09.mods.replikaentropie.core.replika.ReplikaArmor;
import net.blay09.mods.replikaentropie.menu.slot.ReplikaWorkbenchSlot;
import net.blay09.mods.replikaentropie.tag.ModItemTags;
import net.blay09.mods.replikaentropie.util.QuickMove;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

public class ReplikaWorkbenchMenu extends AbstractContainerMenu {

    private final Container container;
    private final QuickMove.Routing quickMove;

    public ReplikaWorkbenchMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainer(9));
    }

    public ReplikaWorkbenchMenu(int containerId, Inventory inventory, Container container) {
        super(ModMenus.replikaWorkbench.get(), containerId);
        this.container = container;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                final var index = j * 3 + i;
                final var x = 22 + i * 29;
                final var y = 26 + j * 29;
                if (index == 4) {
                    addSlot(new ReplikaWorkbenchSlot(container, index, x, y));
                } else {
                    addSlot(new Slot(container, index, x, y));
                }
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
                .slotRange("parts1", 0, 4)
                .slotRange("parts2", 5, 10)
                .slot("center", 4)
                .route(it -> it.is(ModItemTags.REPLIKA_FRAME), QuickMove.PLAYER, "center")
                .route(it -> it.is(ModItemTags.REPLIKA_PART), QuickMove.PLAYER, "parts1")
                .route(it -> it.is(ModItemTags.REPLIKA_PART), QuickMove.PLAYER, "parts2")
                .build();
    }

    public void assemble() {
        final var frameStack = container.getItem(4);
        final var resultStack = ReplikaArmor.assembleFrame(frameStack);
        final var parts = new ArrayList<ItemStack>();
        for (int i = 0; i < 9; i++) {
            if (i == 4) {
                continue;
            }
            final var part = container.getItem(i);
            if (ReplikaArmor.isMatchingPart(frameStack, part)) {
                parts.add(part);
            }
        }
        ReplikaArmor.setParts(resultStack, parts.stream().map(it -> it.split(1)).toList());
        container.setItem(4, resultStack);
    }

    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        if (buttonId == 0) {
            assemble();
            if (!player.level().isClientSide) {
                player.level().playSound(null, player.blockPosition(), SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 1f, 1f);
            }
            return true;
        }
        return false;
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

    public boolean canAssemble() {
        final var frameStack = getSlot(4).getItem();
        if (!frameStack.is(ModItemTags.REPLIKA_FRAME)) {
            return false;
        }

        for (int i = 0; i < 9; i++) {
            if (i == 4) {
                continue;
            }

            final var part = container.getItem(i);
            if (ReplikaArmor.isMatchingPart(frameStack, part)) {
                return true;
            }
        }

        return false;
    }
}

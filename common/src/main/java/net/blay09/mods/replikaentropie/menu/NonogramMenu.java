package net.blay09.mods.replikaentropie.menu;

import it.unimi.dsi.fastutil.ints.IntList;
import net.blay09.mods.replikaentropie.core.nonogram.NonogramClueProvider;
import net.blay09.mods.replikaentropie.core.nonogram.NonogramClues;
import net.blay09.mods.replikaentropie.core.nonogram.NonogramState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.DataSlot;

public class NonogramMenu extends AbstractNonogramMenu {

    public record Data(NonogramClues clues, NonogramState state) implements NonogramClueProvider {
        public static Data read(FriendlyByteBuf buf) {
            final var clues = NonogramClues.read(buf);
            final var state = NonogramState.read(buf);
            return new Data(clues, state);
        }

        public void write(FriendlyByteBuf buf) {
            clues.write(buf);
            NonogramState.write(buf, state);
        }

        @Override
        public boolean validate(NonogramState nonogramState) {
            return false;
        }
    }

    private final DataSlot complete = DataSlot.standalone();

    protected final Inventory playerInventory;
    protected final NonogramClueProvider blues;
    protected final NonogramState nonogramState;
    private NonogramClues.ErrorState errors = new NonogramClues.ErrorState(IntList.of(), IntList.of());

    public NonogramMenu(int containerId, Inventory inventory, Data data) {
        this(containerId, inventory, data, data.state());
    }

    public NonogramMenu(int containerId, Inventory inventory, NonogramClueProvider clues, NonogramState nonogramState) {
        super(ModMenus.nonogram.get(), containerId);
        this.playerInventory = inventory;
        this.blues = clues;
        this.nonogramState = nonogramState;
        addDataSlot(complete);
    }

    @Override
    public NonogramClues getClues() {
        return blues.clues();
    }

    @Override
    public NonogramState getNonogramState() {
        return nonogramState;
    }

    @Override
    public NonogramClues.ErrorState getErrors() {
        return errors;
    }

    @Override
    public void mark(int column, int row, int mark) {
        if (isCompleted()) {
            // No more edits upon completion
            return;
        }

        nonogramState.mark(column, row, mark);
        errors = blues.clues().partialValidate(nonogramState);

        // See if we've completed
        if (blues.validate(nonogramState)) {
            markCompleted();
        }
    }

    public void markCompleted() {
        complete.set(1);
    }

    @Override
    public boolean isCompleted() {
        return complete.get() == 1;
    }
}


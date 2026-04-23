package net.blay09.mods.replikaentropie.menu;

import it.unimi.dsi.fastutil.ints.IntList;
import net.blay09.mods.replikaentropie.core.nonogram.NonogramClueProvider;
import net.blay09.mods.replikaentropie.core.nonogram.NonogramClues;
import net.blay09.mods.replikaentropie.core.nonogram.NonogramState;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.DataSlot;

import java.util.ArrayList;
import java.util.Optional;

public class NonogramMenu extends AbstractNonogramMenu {

    public record AutoHackResult(int column, int row, int mark) {}

    public record Data(NonogramClues clues, NonogramState state) implements NonogramClueProvider {
        public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                NonogramClues.STREAM_CODEC,
                Data::clues,
                NonogramState.STREAM_CODEC,
                Data::state,
                Data::new
        );

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
        super(ModMenus.nonogram.value(), containerId);
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

    public boolean canAutoHack() {
        return playerInventory.hasAnyMatching(it -> it.is(ModItems.automaticHackTool));
    }

    public Optional<AutoHackResult> autoHack(Player player) {
        if (isCompleted()) {
            return Optional.empty();
        }

        final var revealableCells = new ArrayList<AutoHackResult>();
        for (int column = 0; column < nonogramState.width(); column++) {
            for (int row = 0; row < nonogramState.height(); row++) {
                final var targetMark = blues.solution(column, row) == 1 ? 1 : -1;
                if (nonogramState.mark(column, row) != targetMark) {
                    revealableCells.add(new AutoHackResult(column, row, targetMark));
                }
            }
        }

        if (revealableCells.isEmpty()) {
            return Optional.empty();
        }

        final var inventory = player.getInventory();
        final var removed = inventory.clearOrCountMatchingItems(it -> it.is(ModItems.automaticHackTool), 1, inventory);
        if (removed < 1) {
            return Optional.empty();
        }

        final var reveal = revealableCells.get(player.getRandom().nextInt(revealableCells.size()));
        mark(reveal.column(), reveal.row(), reveal.mark());
        player.inventoryMenu.broadcastChanges();
        return Optional.of(reveal);
    }
}

package net.blay09.mods.replikaentropie.power;

import net.blay09.mods.balm.platform.energy.DefaultEnergyStorage;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class MakeshiftPsu extends DefaultEnergyStorage {
    public static final int ENERGY_PER_CLICK = 50;
    public static final MakeshiftPsu EMPTY = new MakeshiftPsu(0, 0, 0, 0);
    private static final long OVERHEAT_WINDOW_TICKS = 40;
    private static final int OVERHEAT_CLICK_LIMIT = 5;
    private static final long OVERHEAT_DURATION_TICKS = 60;
    private static final int CLICK_BUDGET_SCALE = 1000;
    private static final int CLICK_BUDGET_COST = CLICK_BUDGET_SCALE;
    private static final int MAX_CLICK_BUDGET = OVERHEAT_CLICK_LIMIT * CLICK_BUDGET_SCALE;

    private int clickBudget = MAX_CLICK_BUDGET;
    private long lastClickBudgetGameTime = Long.MIN_VALUE;
    private long overheatedUntilGameTime = Long.MIN_VALUE;

    public MakeshiftPsu(int maxExtract, int capacity, int maxReceive, int energy) {
        super(maxExtract, capacity, maxReceive, energy);
    }

    public void convertClickToPower(Player player) {
        final var level = player.level();
        final var gameTime = level.getGameTime();
        if (isOverheated(level)) {
            return;
        }

        updateClickBudget(gameTime);

        if (clickBudget < CLICK_BUDGET_COST) {
            overheatedUntilGameTime = gameTime + OVERHEAT_DURATION_TICKS;
            clickBudget = 0;
            setChanged();
            return;
        }

        clickBudget -= CLICK_BUDGET_COST;
        setChanged();
        fill(player.isCreative() ? Integer.MAX_VALUE : ENERGY_PER_CLICK, false);
    }

    public boolean isOverheated(Level level) {
        return level.getGameTime() < overheatedUntilGameTime;
    }

    private void updateClickBudget(long gameTime) {
        if (lastClickBudgetGameTime == Long.MIN_VALUE) {
            lastClickBudgetGameTime = gameTime;
            return;
        }

        final var elapsedTicks = gameTime - lastClickBudgetGameTime;
        if (elapsedTicks <= 0) {
            return;
        }

        final var regeneratedBudget = elapsedTicks * MAX_CLICK_BUDGET / OVERHEAT_WINDOW_TICKS;
        clickBudget = (int) Math.min(MAX_CLICK_BUDGET, clickBudget + regeneratedBudget);
        lastClickBudgetGameTime = gameTime;
    }

    @Override
    public void serialize(ValueOutput output) {
        super.serialize(output);
        output.putInt("ClickBudget", clickBudget);
        output.putLong("LastClickBudgetGameTime", lastClickBudgetGameTime);
        output.putLong("OverheatedUntilGameTime", overheatedUntilGameTime);
    }

    @Override
    public void deserialize(ValueInput input) {
        super.deserialize(input);
        clickBudget = Math.clamp(input.getIntOr("ClickBudget", MAX_CLICK_BUDGET), 0, MAX_CLICK_BUDGET);
        lastClickBudgetGameTime = input.getLongOr("LastClickBudgetGameTime", Long.MIN_VALUE);
        overheatedUntilGameTime = input.getLongOr("OverheatedUntilGameTime", Long.MIN_VALUE);
    }
}

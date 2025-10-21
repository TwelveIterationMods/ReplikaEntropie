package net.blay09.mods.replikaentropie.core.nonogram;

import net.blay09.mods.replikaentropie.ReplikaEntropie;

public class Nonogram implements NonogramClueProvider {
    private final int width;
    private final int height;
    private final int[] grid;
    private final NonogramClues clues;

    public Nonogram(int width, int height, int[] grid) {
        this.width = width;
        this.height = height;
        this.grid = grid;
        clues = NonogramClues.forGrid(width, height, grid);
    }

    public static Nonogram ofGrid(int width, int height, int[][] grid) {
        final var indexedGrid = new int[width * height];
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                indexedGrid[index(column, row, width)] = grid[column][row];
            }
        }
        return new Nonogram(width, height, indexedGrid);
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public int[] grid() {
        return grid;
    }

    @Override
    public NonogramClues clues() {
        return clues;
    }

    @Override
    public boolean validate(NonogramState state) {
        for (int column = 0; column < width; column++) {
            for (int row = 0; row < height; row++) {
                final var mark = state.mark(column, row);
                final var shouldBeFilled = grid[index(column, row)] == 1;
                if (!shouldBeFilled && mark == 1) {
                    return false;
                } else if (shouldBeFilled && mark != 1) {
                    return false;
                }
            }
        }
        return true;
    }

    private int index(int column, int row) {
        return row * width + column;
    }

    private static int index(int column, int row, int width) {
        return row * width + column;
    }

    public NonogramState createState() {
        return new NonogramState(width, height);
    }

    public NonogramState createCompletedState() {
        final var state = createState();
        final var marks = state.marks();
        System.arraycopy(grid, 0, marks, 0, grid.length);
        return state;
    }

    public NonogramState ensureState(NonogramState state) {
        if (state.width() != width || state.height() != height) {
            ReplikaEntropie.logger.warn("Resetting nonogram state because sizes do not match");
            return createState();
        }
        return state;
    }
}

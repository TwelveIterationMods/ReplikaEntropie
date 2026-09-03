package net.blay09.mods.replikaentropie.core.nonogram;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.blay09.mods.replikaentropie.ReplikaEntropie;

import java.util.List;

public class Nonogram implements NonogramClueProvider {
    public static final Codec<Nonogram> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("width").forGetter(Nonogram::width),
            Codec.INT.fieldOf("height").forGetter(Nonogram::height),
            Codec.INT.listOf().listOf().fieldOf("grid").forGetter(Nonogram::serializedGrid)
    ).apply(instance, Nonogram::ofSerializedGrid));

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

    private static Nonogram ofSerializedGrid(int width, int height, List<List<Integer>> grid) {
        final var indexedGrid = new int[width * height];
        for (int column = 0; column < width; column++) {
            for (int row = 0; row < height; row++) {
                indexedGrid[index(column, row, width)] = grid.get(column).get(row);
            }
        }
        return new Nonogram(width, height, indexedGrid);
    }

    private List<List<Integer>> serializedGrid() {
        return java.util.stream.IntStream.range(0, width)
                .mapToObj(column -> java.util.stream.IntStream.range(0, height)
                        .mapToObj(row -> grid[index(column, row)])
                        .toList())
                .toList();
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

    @Override
    public int solution(int column, int row) {
        return grid[index(column, row)];
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

    public static Nonogram createFallbackNonogram() {
        return Nonogram.ofGrid(5, 5, new int[][] {
                { 0, 0, 1, 0, 0 },
                { 0, 1, 1, 1, 0 },
                { 1, 1, 1, 1, 1 },
                { 0, 1, 1, 1, 0 },
                { 0, 0, 1, 0, 0 },
        });
    }
}

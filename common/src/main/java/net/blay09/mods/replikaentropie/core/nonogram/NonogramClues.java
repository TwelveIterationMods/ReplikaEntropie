package net.blay09.mods.replikaentropie.core.nonogram;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Arrays;
import java.util.function.IntUnaryOperator;

public record NonogramClues(int width, int height, int[][] columnClues, int[][] rowClues) {

    public static NonogramClues forGrid(int width, int height, int[] grid) {
        return new NonogramClues(width, height, extractColumnClues(width, height, grid), extractRowClues(width, height, grid));
    }

    public static NonogramClues read(FriendlyByteBuf buf) {
        final var width = buf.readInt();
        final var height = buf.readInt();
        final var columnClues = new int[width][];
        final var rowClues = new int[height][];
        for (int column = 0; column < width; column++) {
            columnClues[column] = buf.readVarIntArray();
        }
        for (int row = 0; row < height; row++) {
            rowClues[row] = buf.readVarIntArray();
        }
        return new NonogramClues(width, height, columnClues, rowClues);
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(width);
        buf.writeInt(height);
        for (int column = 0; column < width; column++) {
            buf.writeVarIntArray(columnClues[column]);
        }
        for (int row = 0; row < height; row++) {
            buf.writeVarIntArray(rowClues[row]);
        }
    }

    public int[] columnClues(int column) {
        return columnClues[column];
    }

    public int[] rowClues(int row) {
        return rowClues[row];
    }

    private static int index(int column, int row, int width) {
        return row * width + column;
    }

    private static int[][] extractColumnClues(int width, int height, int[] grid) {
        final int[][] clues = new int[width][];
        for (int column = 0; column < width; column++) {
            int currentClue = 0;
            final var columnClues = new IntArrayList();
            for (int row = 0; row < height; row++) {
                final var filled = grid[index(column, row, width)] == 1;
                if (filled) {
                    currentClue++;
                } else if (currentClue > 0) {
                    columnClues.add(currentClue);
                    currentClue = 0;
                }
            }
            if (currentClue > 0) {
                columnClues.add(currentClue);
            }

            if (columnClues.isEmpty()) {
                clues[column] = new int[]{0};
            } else {
                clues[column] = columnClues.toIntArray();
            }
        }
        return clues;
    }

    private static int[][] extractRowClues(int width, int height, int[] grid) {
        final int[][] clues = new int[height][];
        for (int row = 0; row < height; row++) {
            int currentClue = 0;
            final var rowClues = new IntArrayList();
            for (int column = 0; column < width; column++) {
                final var filled = grid[index(column, row, width)] == 1;
                if (filled) {
                    currentClue++;
                } else if (currentClue > 0) {
                    rowClues.add(currentClue);
                    currentClue = 0;
                }
            }
            if (currentClue > 0) {
                rowClues.add(currentClue);
            }

            if (rowClues.isEmpty()) {
                clues[row] = new int[]{0};
            } else {
                clues[row] = rowClues.toIntArray();
            }
        }
        return clues;
    }

    public NonogramState createState() {
        return new NonogramState(width, height);
    }

    public record ErrorState(IntList erroredColumns, IntList erroredRows) {
        public boolean isEmpty() {
            return erroredColumns.isEmpty() && erroredRows.isEmpty();
        }
    }

    public ErrorState partialValidate(NonogramState state) {
        final var erroredColumns = new IntArrayList();
        final var stateColumnRuns = extractColumnClues(state.width(), state.height(), state.marks());
        for (int column = 0; column < width; column++) {
            final var expectedRuns = columnClues[column];
            final var expectedTotal = Arrays.stream(expectedRuns).sum();
            final var expectedMaxRunLength = Arrays.stream(expectedRuns).max().orElse(0);
            final var currentRuns = stateColumnRuns[column];
            final var currentTotal = Arrays.stream(currentRuns).sum();
            if (currentTotal > expectedTotal) {
                erroredColumns.add(column);
                continue;
            }

            final var anyRunTooLong = Arrays.stream(currentRuns).anyMatch(run -> run > expectedMaxRunLength);
            if (anyRunTooLong) {
                erroredColumns.add(column);
                continue;
            }

            final var finalColumn = column;
            final IntUnaryOperator markLookup = (int row) -> state.marks()[index(finalColumn, row, state.width())];
            if (hasIgnoreCellContradictions(markLookup, state.height(), expectedTotal, expectedMaxRunLength, expectedRuns.length)) {
                erroredColumns.add(column);
            }
        }

        final var erroredRows = new IntArrayList();
        final var stateRowClues = extractRowClues(state.width(), state.height(), state.marks());
        for (int row = 0; row < height; row++) {
            final var expectedRuns = rowClues[row];
            final var expectedTotal = Arrays.stream(expectedRuns).sum();
            final var expectedMaxRunLength = Arrays.stream(expectedRuns).max().orElse(0);
            final var currentRuns = stateRowClues[row];
            final var currentTotal = Arrays.stream(currentRuns).sum();
            if (currentTotal > expectedTotal) {
                erroredRows.add(row);
                continue;
            }

            final var anyRunTooLong = Arrays.stream(currentRuns).anyMatch(run -> run > expectedMaxRunLength);
            if (anyRunTooLong) {
                erroredRows.add(row);
                continue;
            }

            final int finalRow = row;
            final IntUnaryOperator markAtIndex = (int column) -> state.marks()[index(column, finalRow, state.width())];
            if (hasIgnoreCellContradictions(markAtIndex, state.width(), expectedTotal, expectedMaxRunLength, expectedRuns.length)) {
                erroredRows.add(row);
            }
        }

        return new ErrorState(erroredColumns, erroredRows);
    }

    private static boolean hasIgnoreCellContradictions(IntUnaryOperator markLookup,
                                                       int lineLength,
                                                       int expectedTotal,
                                                       int expectedMaxRunLength,
                                                       int expectedRunCount) {
        var filledCells = 0;
        var freeCells = 0;
        var currentSegmentLength = 0;
        var currentSegmentHasFill = false;
        var maxSegmentLength = 0;
        var segmentsWithFills = 0;

        for (int i = 0; i < lineLength; i++) {
            final var mark = markLookup.applyAsInt(i);
            switch (mark) {
                case -1 -> {
                    if (currentSegmentLength > 0) {
                        maxSegmentLength = Math.max(maxSegmentLength, currentSegmentLength);
                        if (currentSegmentHasFill) {
                            segmentsWithFills++;
                        }
                        currentSegmentLength = 0;
                        currentSegmentHasFill = false;
                    }
                }
                case 1 -> {
                    currentSegmentLength++;
                    filledCells++;
                    currentSegmentHasFill = true;
                }
                case 0 -> {
                    currentSegmentLength++;
                    freeCells++;
                }
            }
        }
        if (currentSegmentLength > 0) {
            maxSegmentLength = Math.max(maxSegmentLength, currentSegmentLength);
            if (currentSegmentHasFill) {
                segmentsWithFills++;
            }
        }

        // Unable to reach total with this amount of ignored cells
        if (filledCells + freeCells < expectedTotal) {
            return true;
        }

        // Too many runs with ignored cells in between
        if (segmentsWithFills > expectedRunCount) {
            return true;
        }

        // Not enough space for the largest expected run
        return expectedMaxRunLength > 0 && maxSegmentLength < expectedMaxRunLength;
    }
}

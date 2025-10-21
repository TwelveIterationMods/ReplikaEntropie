package net.blay09.mods.replikaentropie.menu;

import it.unimi.dsi.fastutil.ints.IntList;
import net.blay09.mods.replikaentropie.core.nonogram.NonogramClueProvider;
import net.blay09.mods.replikaentropie.core.nonogram.NonogramClues;
import net.blay09.mods.replikaentropie.core.nonogram.NonogramState;
import net.blay09.mods.replikaentropie.core.nonogram.NonogramLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import com.google.gson.Gson;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class NonogramEditorMenu extends AbstractNonogramMenu {

    private final NonogramState nonogramState;
    @Nullable
    private final ResourceLocation outputId;

    private NonogramClues clues;

    public NonogramEditorMenu(int containerId, NonogramMenu.Data data) {
        this(containerId, data, data.state(), null);
    }

    public NonogramEditorMenu(int containerId, NonogramClueProvider clues, NonogramState nonogramState, @Nullable ResourceLocation outputId) {
        super(ModMenus.nonogramEditor.get(), containerId);
        this.clues = clues.clues();
        this.nonogramState = nonogramState;
        this.outputId = outputId;
    }

    @Override
    public NonogramClues getClues() {
        return clues;
    }

    @Override
    public NonogramState getNonogramState() {
        return nonogramState;
    }

    @Override
    public NonogramClues.ErrorState getErrors() {
        return new NonogramClues.ErrorState(IntList.of(), IntList.of());
    }

    @Override
    public void removed(Player player) {
        super.removed(player);

        if (!player.level().isClientSide) {
            saveCurrentAsJson(clues.width(), clues.height(), nonogramState.marks(), outputId);
        }
    }

    @Override
    public void mark(int column, int row, int mark) {
        nonogramState.mark(column, row, mark);
        clues = NonogramClues.forGrid(nonogramState.width(), nonogramState.height(), nonogramState.marks());
    }

    private void saveCurrentAsJson(int width, int height, int[] marks, ResourceLocation id) {
        final var grid = new int[width][height];
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                final var mark = marks[row * width + column];
                grid[column][row] = mark == 1 ? 1 : 0;
            }
        }

        final var nonogramJson = new NonogramLoader.JsonNonogram(width, height, grid);
        final var jsonContent = new Gson().toJson(nonogramJson);

        final var outputDir = Path.of("replikaentropie_nonogram_output");
        try {
            Files.createDirectories(outputDir);
            final var outputFile = outputDir.resolve(id.getNamespace() + "/" + id.getPath() + ".json");
            Files.createDirectories(outputFile.getParent());
            Files.writeString(outputFile, jsonContent);
        } catch (IOException ignored) {
        }
    }
}

package net.blay09.mods.replikaentropie.core.nonogram;

import com.google.gson.Gson;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class NonogramLoader implements ResourceManagerReloadListener {

    public static class JsonNonogram {
        public int width;
        public int height;
        public int[][] grid;

        public JsonNonogram() {}

        public JsonNonogram(int width, int height, int[][] grid) {
            this.width = width;
            this.height = height;
            this.grid = grid;
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(NonogramLoader.class);
    private static final Gson gson = new Gson();
    private static final FileToIdConverter JSONS = FileToIdConverter.json("replikaentropie_nonogram");

    private static final Map<ResourceLocation, Nonogram> nonograms = new HashMap<>();

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        nonograms.clear();

        for (final var entry : JSONS.listMatchingResources(resourceManager).entrySet()) {
            try (final var reader = entry.getValue().openAsReader()) {
                final var nonogram = gson.fromJson(reader, JsonNonogram.class);
                nonograms.put(new ResourceLocation(entry.getKey().getNamespace(), entry.getKey().getPath().replace("replikaentropie_nonogram/", "").replace(".json", "")), Nonogram.ofGrid(nonogram.width, nonogram.height, nonogram.grid));
            } catch (Exception e) {
                logger.error("Parsing error loading Replika Entropie nonogram file at {}", entry.getKey(), e);
            }
        }
    }

    public static Optional<Nonogram> getNonogram(ResourceLocation id) {
        return Optional.ofNullable(nonograms.get(id));
    }

    public static Set<ResourceLocation> getNonogramIds() {
        return nonograms.keySet();
    }

    public static Nonogram createFallback() {
        return Nonogram.ofGrid(5, 5, new int[][] {
                { 0, 0, 1, 0, 0 },
                { 0, 1, 1, 1, 0 },
                { 1, 1, 1, 1, 1 },
                { 0, 1, 1, 1, 0 },
                { 0, 0, 1, 0, 0 },
        });
    }
}

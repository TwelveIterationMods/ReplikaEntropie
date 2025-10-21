package net.blay09.mods.replikaentropie.core.analyzer;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface AnalyzerManager {
    void analyzeItem(Player player, ItemStack itemStack);

    boolean isAnalyzed(Player player, ItemStack itemStack);

    void analyzeEntity(Player player, Entity entity);

    void grantData(Player player, int amount);

    boolean isAnalyzed(Player player, Entity entity);

    void resetAll(Player player);

    void resetAnalyzedItems(Player player);

    void resetAnalyzedPlayers(Player player);

    void resetAnalyzedEntities(Player player);

    int getDataCollected(Player player);
}

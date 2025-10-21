package net.blay09.mods.replikaentropie.core.analyzer;

import net.blay09.mods.balm.api.event.BalmEvents;
import net.blay09.mods.balm.api.event.PlayerLoginEvent;
import net.blay09.mods.replikaentropie.core.dataminer.DataMinedEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class Analyzer {

    private static final InMemoryAnalyzerManager localManager = new InMemoryAnalyzerManager();
    private static final PersistentAnalyzerManager persistentManager = new PersistentAnalyzerManager();

    public static void initialize(BalmEvents events) {
        events.onEvent(PlayerLoginEvent.class, event -> persistentManager.sendAllToPlayer(event.getPlayer()));
    }

    public static InMemoryAnalyzerManager getLocalManager() {
        return localManager;
    }

    public static AnalyzerManager getManager(Player player) {
        return player.isLocalPlayer() ? localManager : persistentManager;
    }

    public static void analyzeItem(Player player, ItemStack itemStack) {
        getManager(player).analyzeItem(player, itemStack);
    }

    public static boolean isAnalyzed(Player player, ItemStack itemStack) {
        return getManager(player).isAnalyzed(player, itemStack);
    }

    public static void analyzeEntity(Player player, Entity entity) {
        getManager(player).analyzeEntity(player, entity);
    }

    public static boolean isAnalyzed(Player player, Entity entity) {
        return getManager(player).isAnalyzed(player, entity);
    }

    public static void resetAllAnalyzed(Player player) {
        getManager(player).resetAll(player);
    }

    public static void resetDataMinedEvents(Player player) {
        if (getManager(player) instanceof PersistentAnalyzerManager manager) {
            manager.resetDataMinedEvents(player);
        }
    }

    public static void resetAnalyzedItems(Player player) {
        getManager(player).resetAnalyzedItems(player);
    }

    public static void resetAnalyzedPlayers(Player player) {
        getManager(player).resetAnalyzedPlayers(player);
    }

    public static void resetAnalyzedEntities(Player player) {
        getManager(player).resetAnalyzedEntities(player);
    }

    public static boolean isDataMinedEventDownloaded(Player player, DataMinedEvent event) {
        return getManager(player) instanceof PersistentAnalyzerManager manager && manager.isDataMinedEventDownloaded(player, event);
    }

    public static void downloadDataMinedEvent(Player player, DataMinedEvent event) {
        if (getManager(player) instanceof PersistentAnalyzerManager manager) {
            manager.downloadDataMinedEvent(player, event);
        }
    }

    public static void grantData(Player player, int amount) {
        getManager(player).grantData(player, amount);
    }
}

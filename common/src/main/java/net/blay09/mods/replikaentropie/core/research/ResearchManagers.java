package net.blay09.mods.replikaentropie.core.research;

import net.blay09.mods.replikaentropie.core.nonogram.NonogramState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public class ResearchManagers {

    private static final InMemoryResearchManager localManager = new InMemoryResearchManager();
    private static final PersistentResearchManager persistentManager = new PersistentResearchManager();

    public static ResearchManager getLocalManager() {
        return localManager;
    }

    public static ResearchManager getManager(Player player) {
        return player.isLocalPlayer() ? localManager : persistentManager;
    }

    public static ResearchState getResearchState(Player player, Identifier id) {
        return getManager(player).getResearchState(player, id);
    }

    public static Optional<NonogramState> getNonogramState(Player player, Identifier id) {
        return getManager(player).getNonogramState(player, id);
    }

    public static void updateResearch(Player player, Identifier id, ResearchState state) {
        getManager(player).updateResearch(player, id, state);
    }

    public static void updateNonogram(Player player, Identifier id, NonogramState state) {
        getManager(player).updateNonogram(player, id, state);
    }

    public static void resetAllResearch(Player player) {
        getManager(player).resetAllResearch(player);
    }
}


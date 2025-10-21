package net.blay09.mods.replikaentropie.network.protocol;

import net.blay09.mods.replikaentropie.core.analyzer.Analyzer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record AnalyzedPlayersMessage(boolean reset, List<UUID> players) {

    public static void encode(AnalyzedPlayersMessage message, FriendlyByteBuf buf) {
        buf.writeBoolean(message.reset);
        buf.writeCollection(message.players, FriendlyByteBuf::writeUUID);
    }

    public static AnalyzedPlayersMessage decode(FriendlyByteBuf buf) {
        final var reset = buf.readBoolean();
        final var items = buf.readList(FriendlyByteBuf::readUUID);
        return new AnalyzedPlayersMessage(reset, items);
    }

    public static void handle(Player player, AnalyzedPlayersMessage message) {
        if (message.reset) {
            Analyzer.resetAnalyzedPlayers(player);
        }
        for (final var uuid : message.players) {
            Analyzer.getLocalManager().analyzePlayer(player, uuid);
        }
    }

}

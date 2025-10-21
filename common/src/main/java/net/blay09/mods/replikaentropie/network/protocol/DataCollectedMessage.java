package net.blay09.mods.replikaentropie.network.protocol;

import net.blay09.mods.replikaentropie.client.handler.ClientDataNotifications;
import net.blay09.mods.replikaentropie.client.handler.HandheldAnalyzerClient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public record DataCollectedMessage(int dataCollected, int totalData) {

    public static void encode(DataCollectedMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.dataCollected);
        buf.writeInt(message.totalData);
    }

    public static DataCollectedMessage decode(FriendlyByteBuf buf) {
        final var dataCollected = buf.readInt();
        final var totalData = buf.readInt();
        return new DataCollectedMessage(dataCollected, totalData);
    }

    public static void handle(Player player, DataCollectedMessage message) {
        ClientDataNotifications.onDataCollected(message.dataCollected);
    }

}

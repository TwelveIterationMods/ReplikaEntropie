package net.blay09.mods.replikaentropie.network.protocol;

import net.blay09.mods.replikaentropie.client.handler.ClientDataNotifications;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public record DataCollectedMessage(int dataCollected, int totalData) implements CustomPacketPayload {
    public static final Type<DataCollectedMessage> TYPE = new Type<>(id("data_collected"));
    public static final StreamCodec<RegistryFriendlyByteBuf, DataCollectedMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            DataCollectedMessage::dataCollected,
            ByteBufCodecs.INT,
            DataCollectedMessage::totalData,
            DataCollectedMessage::new
    );

    @Override
    public Type<DataCollectedMessage> type() {
        return TYPE;
    }

    public static void handle(Player player, DataCollectedMessage message) {
        ClientDataNotifications.onDataCollected(message.dataCollected);
    }

}

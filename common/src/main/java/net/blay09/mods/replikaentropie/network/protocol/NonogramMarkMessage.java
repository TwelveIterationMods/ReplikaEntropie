package net.blay09.mods.replikaentropie.network.protocol;

import net.blay09.mods.replikaentropie.menu.AbstractNonogramMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public record NonogramMarkMessage(int containerId, int column, int row, int mark) implements CustomPacketPayload {
    public static final Type<NonogramMarkMessage> TYPE = new Type<>(id("nonogram_mark"));
    public static final StreamCodec<RegistryFriendlyByteBuf, NonogramMarkMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            NonogramMarkMessage::containerId,
            ByteBufCodecs.VAR_INT,
            NonogramMarkMessage::column,
            ByteBufCodecs.VAR_INT,
            NonogramMarkMessage::row,
            ByteBufCodecs.BYTE.map(Byte::intValue, Integer::byteValue),
            NonogramMarkMessage::mark,
            NonogramMarkMessage::new
    );

    @Override
    public Type<NonogramMarkMessage> type() {
        return TYPE;
    }

    public static void handle(ServerPlayer player, NonogramMarkMessage message) {
        if (player.containerMenu instanceof AbstractNonogramMenu menu
                && player.containerMenu.containerId == message.containerId) {
            menu.mark(message.column, message.row, message.mark);
        }
    }
}

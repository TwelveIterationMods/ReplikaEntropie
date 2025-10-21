package net.blay09.mods.replikaentropie.network.protocol;

import net.blay09.mods.replikaentropie.menu.AbstractNonogramMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public record NonogramMarkMessage(int containerId, int column, int row, int mark) {

    public static void encode(NonogramMarkMessage message, FriendlyByteBuf buf) {
        buf.writeVarInt(message.containerId);
        buf.writeVarInt(message.column);
        buf.writeVarInt(message.row);
        buf.writeByte(message.mark);
    }

    public static NonogramMarkMessage decode(FriendlyByteBuf buf) {
        final var containerId = buf.readVarInt();
        final var column = buf.readVarInt();
        final var row = buf.readVarInt();
        final var mark = buf.readByte();
        return new NonogramMarkMessage(containerId, column, row, mark);
    }

    public static void handle(ServerPlayer player, NonogramMarkMessage message) {
        if (player.containerMenu instanceof AbstractNonogramMenu menu
                && player.containerMenu.containerId == message.containerId) {
            menu.mark(message.column, message.row, message.mark);
        }
    }
}

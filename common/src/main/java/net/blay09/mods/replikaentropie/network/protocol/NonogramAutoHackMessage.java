package net.blay09.mods.replikaentropie.network.protocol;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.replikaentropie.menu.NonogramMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public record NonogramAutoHackMessage(int containerId) implements CustomPacketPayload {
    public static final Type<NonogramAutoHackMessage> TYPE = new Type<>(id("nonogram_auto_hack"));
    public static final StreamCodec<RegistryFriendlyByteBuf, NonogramAutoHackMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            NonogramAutoHackMessage::containerId,
            NonogramAutoHackMessage::new
    );

    @Override
    public Type<NonogramAutoHackMessage> type() {
        return TYPE;
    }

    public static void handle(ServerPlayer player, NonogramAutoHackMessage message) {
        if (player.containerMenu instanceof NonogramMenu menu
                && player.containerMenu.containerId == message.containerId) {
            menu.autoHack(player).ifPresent(result -> Balm.networking().sendTo(player, new NonogramAutoHackResultMessage(
                    message.containerId,
                    result.column(),
                    result.row(),
                    result.mark()
            )));
        }
    }
}

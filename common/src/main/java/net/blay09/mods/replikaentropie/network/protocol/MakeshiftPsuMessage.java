package net.blay09.mods.replikaentropie.network.protocol;

import net.blay09.mods.replikaentropie.menu.MakeshiftPoweredMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public record MakeshiftPsuMessage(int containerId) implements CustomPacketPayload {
    public static final Type<MakeshiftPsuMessage> TYPE = new Type<>(id("makeshift_psu"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MakeshiftPsuMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            MakeshiftPsuMessage::containerId,
            MakeshiftPsuMessage::new
    );

    @Override
    public Type<MakeshiftPsuMessage> type() {
        return TYPE;
    }

    public static void handle(ServerPlayer player, MakeshiftPsuMessage message) {
        if (player.containerMenu instanceof MakeshiftPoweredMenu menu
                && player.containerMenu.containerId == message.containerId) {
            menu.convertClickToPower();
        }
    }
}

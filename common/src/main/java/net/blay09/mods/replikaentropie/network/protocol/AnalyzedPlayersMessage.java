package net.blay09.mods.replikaentropie.network.protocol;

import net.blay09.mods.replikaentropie.core.analyzer.Analyzer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.UUID;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public record AnalyzedPlayersMessage(boolean reset, List<UUID> players) implements CustomPacketPayload {
    public static final Type<AnalyzedPlayersMessage> TYPE = new Type<>(id("analyzed_players"));
    private static final StreamCodec<RegistryFriendlyByteBuf, UUID> UUID_STREAM_CODEC = StreamCodec.of(
            (buf, uuid) -> FriendlyByteBuf.writeUUID(buf, uuid),
            buf -> FriendlyByteBuf.readUUID(buf)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, AnalyzedPlayersMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            AnalyzedPlayersMessage::reset,
            UUID_STREAM_CODEC.apply(ByteBufCodecs.list()),
            AnalyzedPlayersMessage::players,
            AnalyzedPlayersMessage::new
    );

    @Override
    public Type<AnalyzedPlayersMessage> type() {
        return TYPE;
    }

    public static void handle(Player player, AnalyzedPlayersMessage message) {
        if (message.reset) {
            Analyzer.resetAnalyzedPlayers(player);
        }
        for (final var uuid : message.players) {
            Analyzer.getLocalManager().analyzePlayer(player, uuid);
        }
        if (!message.reset && !message.players.isEmpty()) {
            player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.8f, 1.2f);
        }
    }

}

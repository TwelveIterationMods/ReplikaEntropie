package net.blay09.mods.replikaentropie.network.protocol;

import net.blay09.mods.replikaentropie.core.analyzer.Analyzer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import net.blay09.mods.replikaentropie.ReplikaEntropie;

public record AnalyzeEntityMessage(int id) implements CustomPacketPayload {
    public static final Type<AnalyzeEntityMessage> TYPE = new Type<>(ReplikaEntropie.id("analyze_entity"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AnalyzeEntityMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            AnalyzeEntityMessage::id,
            AnalyzeEntityMessage::new
    );

    @Override
    public Type<AnalyzeEntityMessage> type() {
        return TYPE;
    }

    public static void handle(ServerPlayer player, AnalyzeEntityMessage message) {
        // POSTJAM range check, cooldown check
        final var level = player.level();
        final var entity = level.getEntity(message.id);
        if (entity != null) {
            Analyzer.analyzeEntity(player, entity);
        }
    }

}

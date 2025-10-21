package net.blay09.mods.replikaentropie.network.protocol;

import net.blay09.mods.replikaentropie.core.analyzer.Analyzer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public record AnalyzeEntityMessage(int id) {

    public static void encode(AnalyzeEntityMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.id);
    }

    public static AnalyzeEntityMessage decode(FriendlyByteBuf buf) {
        return new AnalyzeEntityMessage(buf.readInt());
    }

    public static void handle(ServerPlayer player, AnalyzeEntityMessage message) {
        // POSTJAM range check, cooldown check
        final var level = player.level();
        final var entity = level.getEntity(message.id);
        Analyzer.analyzeEntity(player, entity);
    }

}

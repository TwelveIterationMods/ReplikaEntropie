package net.blay09.mods.replikaentropie.network.protocol;

import net.blay09.mods.replikaentropie.core.analyzer.Analyzer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public record AnalyzePosMessage(BlockPos pos) implements CustomPacketPayload {
    public static final Type<AnalyzePosMessage> TYPE = new Type<>(id("analyze_pos"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AnalyzePosMessage> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            AnalyzePosMessage::pos,
            AnalyzePosMessage::new
    );

    @Override
    public Type<AnalyzePosMessage> type() {
        return TYPE;
    }

    public static void handle(ServerPlayer player, AnalyzePosMessage message) {
        // POSTJAM range check, cooldown check
        final var level = player.level();
        final var pos = message.pos;
        final var state = level.getBlockState(pos);
        final var itemStack = state.getCloneItemStack(level, pos, false);
        Analyzer.analyzeItem(player, itemStack);
    }

}

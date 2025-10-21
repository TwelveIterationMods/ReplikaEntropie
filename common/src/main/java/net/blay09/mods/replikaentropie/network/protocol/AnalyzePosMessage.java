package net.blay09.mods.replikaentropie.network.protocol;

import net.blay09.mods.replikaentropie.core.analyzer.Analyzer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public record AnalyzePosMessage(BlockPos pos) {

    public static void encode(AnalyzePosMessage message, FriendlyByteBuf buf) {
        buf.writeBlockPos(message.pos);
    }

    public static AnalyzePosMessage decode(FriendlyByteBuf buf) {
        return new AnalyzePosMessage(buf.readBlockPos());
    }

    public static void handle(ServerPlayer player, AnalyzePosMessage message) {
        // POSTJAM range check, cooldown check
        final var level = player.level();
        final var pos = message.pos;
        final var state = level.getBlockState(pos);
        final var itemStack = state.getBlock().getCloneItemStack(level, pos, state);
        Analyzer.analyzeItem(player, itemStack);
    }

}

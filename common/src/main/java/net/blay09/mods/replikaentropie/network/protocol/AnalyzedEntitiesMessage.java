package net.blay09.mods.replikaentropie.network.protocol;

import net.blay09.mods.replikaentropie.core.analyzer.Analyzer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;

import java.util.List;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public record AnalyzedEntitiesMessage(boolean reset, List<EntityType<?>> entityTypes) implements CustomPacketPayload {
    public static final Type<AnalyzedEntitiesMessage> TYPE = new Type<>(id("analyzed_entities"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AnalyzedEntitiesMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            AnalyzedEntitiesMessage::reset,
            ByteBufCodecs.idMapper(BuiltInRegistries.ENTITY_TYPE).apply(ByteBufCodecs.list()),
            AnalyzedEntitiesMessage::entityTypes,
            AnalyzedEntitiesMessage::new
    );

    @Override
    public Type<AnalyzedEntitiesMessage> type() {
        return TYPE;
    }

    public static void handle(Player player, AnalyzedEntitiesMessage message) {
        if (message.reset) {
            Analyzer.resetAnalyzedEntities(player);
        }
        for (final var entityType : message.entityTypes) {
            Analyzer.getLocalManager().analyzeEntity(player, entityType);
        }
        if (!message.reset && !message.entityTypes.isEmpty()) {
            player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.8f, 1.2f);
        }
    }

}

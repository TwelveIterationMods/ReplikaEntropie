package net.blay09.mods.replikaentropie.network.protocol;

import net.blay09.mods.replikaentropie.core.analyzer.Analyzer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public record AnalyzedEntitiesMessage(boolean reset, List<? extends EntityType<?>> entityTypes) {

    public static void encode(AnalyzedEntitiesMessage message, FriendlyByteBuf buf) {
        buf.writeBoolean(message.reset);
        buf.writeCollection(message.entityTypes, (it, item) -> it.writeId(BuiltInRegistries.ENTITY_TYPE, item));
    }

    public static AnalyzedEntitiesMessage decode(FriendlyByteBuf buf) {
        final var reset = buf.readBoolean();
        final var entityTypes = buf.readList((it) -> it.readById(BuiltInRegistries.ENTITY_TYPE));
        return new AnalyzedEntitiesMessage(reset, entityTypes);
    }

    public static void handle(Player player, AnalyzedEntitiesMessage message) {
        if (message.reset) {
            Analyzer.resetAnalyzedEntities(player);
        }
        for (final var entityType : message.entityTypes) {
            Analyzer.getLocalManager().analyzeEntity(player, entityType);
        }
    }

}

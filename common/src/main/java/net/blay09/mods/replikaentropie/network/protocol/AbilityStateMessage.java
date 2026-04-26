package net.blay09.mods.replikaentropie.network.protocol;

import net.blay09.mods.replikaentropie.core.abilities.AbilityManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import net.blay09.mods.replikaentropie.ReplikaEntropie;

public record AbilityStateMessage(Identifier id, boolean active) implements CustomPacketPayload {
    public static final Type<AbilityStateMessage> TYPE = new Type<>(ReplikaEntropie.id("ability_state"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AbilityStateMessage> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC,
            AbilityStateMessage::id,
            ByteBufCodecs.BOOL,
            AbilityStateMessage::active,
            AbilityStateMessage::new
    );

    @Override
    public Type<AbilityStateMessage> type() {
        return TYPE;
    }
    
    public static void handle(Player player, AbilityStateMessage message) {
        final var manager = AbilityManager.getLocalStateManager();
        final var ability = AbilityManager.getAbility(player, message.id);
        manager.setActive(player, ability, message.active, AbilityManager.resolveSource(player, ability));
    }
}

package net.blay09.mods.replikaentropie.network.protocol;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public record ToggleAbilityMessage(Identifier abilityId, boolean enabled) implements CustomPacketPayload {
    public static final Type<ToggleAbilityMessage> TYPE = new Type<>(id("toggle_ability"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ToggleAbilityMessage> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC,
            ToggleAbilityMessage::abilityId,
            ByteBufCodecs.BOOL,
            ToggleAbilityMessage::enabled,
            ToggleAbilityMessage::new
    );

    @Override
    public Type<ToggleAbilityMessage> type() {
        return TYPE;
    }
    
    public static void handle(ServerPlayer player, ToggleAbilityMessage message) {
        // POSTJAM handle ability toggle
    }
}

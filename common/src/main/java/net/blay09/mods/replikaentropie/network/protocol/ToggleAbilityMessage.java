package net.blay09.mods.replikaentropie.network.protocol;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public record ToggleAbilityMessage(ResourceLocation abilityId, boolean enabled) {
    
    public static void encode(ToggleAbilityMessage message, FriendlyByteBuf buf) {
        buf.writeResourceLocation(message.abilityId);
        buf.writeBoolean(message.enabled);
    }
    
    public static ToggleAbilityMessage decode(FriendlyByteBuf buf) {
        final var abilityId = buf.readResourceLocation();
        final var enabled = buf.readBoolean();
        return new ToggleAbilityMessage(abilityId, enabled);
    }
    
    public static void handle(ServerPlayer player, ToggleAbilityMessage message) {
        // POSTJAM handle ability toggle
    }
}

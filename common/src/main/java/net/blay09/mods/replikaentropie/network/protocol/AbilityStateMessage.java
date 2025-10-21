package net.blay09.mods.replikaentropie.network.protocol;

import net.blay09.mods.replikaentropie.core.abilities.AbilityManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public record AbilityStateMessage(ResourceLocation id, boolean active, float burstCost) {
    
    public static void encode(AbilityStateMessage message, FriendlyByteBuf buf) {
        buf.writeResourceLocation(message.id);
        buf.writeBoolean(message.active);
        buf.writeFloat(message.burstCost);
    }
    
    public static AbilityStateMessage decode(FriendlyByteBuf buf) {
        final var id = buf.readResourceLocation();
        final var active = buf.readBoolean();
        final var burstCost = buf.readFloat();
        return new AbilityStateMessage(id, active, burstCost);
    }
    
    public static void handle(Player player, AbilityStateMessage message) {
        final var manager = AbilityManager.getLocalStateManager();
        final var ability = AbilityManager.getAbility(player, message.id);
        manager.setActive(player, ability, message.active);
        manager.setBurstCost(player, ability, message.burstCost);
    }
}

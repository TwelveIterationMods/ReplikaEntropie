package net.blay09.mods.replikaentropie.network.protocol;

import net.blay09.mods.replikaentropie.core.burst.BurstEnergy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public record BurstEnergyMessage(float energy) {

    public static void encode(BurstEnergyMessage message, FriendlyByteBuf buf) {
        buf.writeFloat(message.energy);
    }

    public static BurstEnergyMessage decode(FriendlyByteBuf buf) {
        final var energy = buf.readFloat();
        return new BurstEnergyMessage(energy);
    }

    public static void handle(Player player, BurstEnergyMessage message) {
        BurstEnergy.getLocalManager().setEnergy(player, message.energy);
    }

}

package net.blay09.mods.replikaentropie.network.protocol;

import net.blay09.mods.replikaentropie.core.burst.BurstEnergy;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public record BurstEnergyMessage(float energy) implements CustomPacketPayload {
    public static final Type<BurstEnergyMessage> TYPE = new Type<>(id("burst_energy"));
    public static final StreamCodec<RegistryFriendlyByteBuf, BurstEnergyMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT,
            BurstEnergyMessage::energy,
            BurstEnergyMessage::new
    );

    @Override
    public Type<BurstEnergyMessage> type() {
        return TYPE;
    }

    public static void handle(Player player, BurstEnergyMessage message) {
        BurstEnergy.getLocalManager().setEnergy(player, message.energy);
    }

}

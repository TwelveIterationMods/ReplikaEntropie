package net.blay09.mods.replikaentropie.network.protocol;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector3fc;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public record ParticleTrailMessage(Vector3fc start, Vector3fc end, int segments, ParticleOptions particleOptions) implements CustomPacketPayload {
    public static final Type<ParticleTrailMessage> TYPE = new Type<>(id("particle_trail"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ParticleTrailMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VECTOR3F,
            ParticleTrailMessage::start,
            ByteBufCodecs.VECTOR3F,
            ParticleTrailMessage::end,
            ByteBufCodecs.INT,
            ParticleTrailMessage::segments,
            ParticleTypes.STREAM_CODEC,
            ParticleTrailMessage::particleOptions,
            ParticleTrailMessage::new
    );

    @Override
    public Type<ParticleTrailMessage> type() {
        return TYPE;
    }

    public static void handle(Player player, ParticleTrailMessage message) {
        final var level = player.level();
        for (int i = 0; i <= message.segments; i++) {
            final var t = i / (double) message.segments;
            final var x = message.start.x() + (message.end.x() - message.start.x()) * t;
            final var y = message.start.y() + (message.end.y() - message.start.y()) * t;
            final var z = message.start.z() + (message.end.z() - message.start.z()) * t;
            level.addParticle(message.particleOptions, x, y, z, 0, 0, 0);
        }
    }
}

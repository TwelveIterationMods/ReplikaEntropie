package net.blay09.mods.replikaentropie.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.Optional;

public record AssemblyTicket(Optional<Identifier> recipeId, int usesLeft, boolean showHint) {
    public static final Codec<AssemblyTicket> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.optionalFieldOf("recipe_id").forGetter(AssemblyTicket::recipeId),
            Codec.INT.optionalFieldOf("uses_left", 0).forGetter(AssemblyTicket::usesLeft),
            Codec.BOOL.optionalFieldOf("show_hint", false).forGetter(AssemblyTicket::showHint)
    ).apply(instance, AssemblyTicket::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AssemblyTicket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(Identifier.STREAM_CODEC),
            AssemblyTicket::recipeId,
            ByteBufCodecs.VAR_INT,
            AssemblyTicket::usesLeft,
            ByteBufCodecs.BOOL,
            AssemblyTicket::showHint,
            AssemblyTicket::new
    );

    public AssemblyTicket(Identifier recipeId, int usesLeft) {
        this(Optional.of(recipeId), usesLeft, false);
    }
}

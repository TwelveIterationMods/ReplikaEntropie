package net.blay09.mods.replikaentropie.component;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.List;

public record AbilityHolder(List<Identifier> abilities) {
    public static final AbilityHolder EMPTY = new AbilityHolder(List.of());

    public static final Codec<AbilityHolder> CODEC = Identifier.CODEC
            .listOf()
            .xmap(AbilityHolder::new, AbilityHolder::abilities);

    public static final StreamCodec<RegistryFriendlyByteBuf, AbilityHolder> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC.apply(ByteBufCodecs.list()),
            AbilityHolder::abilities,
            AbilityHolder::new
    );

    public boolean hasAbility(Identifier abilityId) {
        return abilities.contains(abilityId);
    }
}

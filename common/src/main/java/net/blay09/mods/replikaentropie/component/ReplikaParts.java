package net.blay09.mods.replikaentropie.component;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStackTemplate;

import java.util.List;

public record ReplikaParts(List<ItemStackTemplate> parts) {
    public static final ReplikaParts EMPTY = new ReplikaParts(List.of());

    public static final Codec<ReplikaParts> CODEC = ItemStackTemplate.CODEC
            .listOf()
            .xmap(ReplikaParts::new, ReplikaParts::parts);

    public static final StreamCodec<RegistryFriendlyByteBuf, ReplikaParts> STREAM_CODEC = ItemStackTemplate.STREAM_CODEC
            .apply(ByteBufCodecs.list())
            .map(ReplikaParts::new, ReplikaParts::parts);
}

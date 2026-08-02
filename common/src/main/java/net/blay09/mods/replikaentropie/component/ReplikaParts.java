package net.blay09.mods.replikaentropie.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStackTemplate;

import java.util.List;

public record ReplikaParts(List<InstalledPart> parts) {
    public static final ReplikaParts EMPTY = new ReplikaParts(List.of());

    public static final Codec<ReplikaParts> CODEC = InstalledPart.CODEC
            .listOf()
            .xmap(ReplikaParts::new, ReplikaParts::parts);

    public static final StreamCodec<RegistryFriendlyByteBuf, ReplikaParts> STREAM_CODEC = InstalledPart.STREAM_CODEC
            .apply(ByteBufCodecs.list())
            .map(ReplikaParts::new, ReplikaParts::parts);

    public record InstalledPart(int slot, ItemStackTemplate part) {
        public static final Codec<InstalledPart> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("slot").forGetter(InstalledPart::slot),
                ItemStackTemplate.CODEC.fieldOf("part").forGetter(InstalledPart::part)
        ).apply(instance, InstalledPart::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, InstalledPart> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT,
                InstalledPart::slot,
                ItemStackTemplate.STREAM_CODEC,
                InstalledPart::part,
                InstalledPart::new
        );
    }
}

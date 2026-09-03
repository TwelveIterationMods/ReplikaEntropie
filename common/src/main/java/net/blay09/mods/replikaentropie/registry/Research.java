package net.blay09.mods.replikaentropie.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;

import java.util.List;
import java.util.Optional;

public record Research(
        ItemStackTemplate icon,
        List<Identifier> hardDependencies,
        List<Identifier> softDependencies,
        List<Identifier> unlockedRecipes,
        int scrapCost,
        int biomassCost,
        int fragmentsCost,
        int dataCost,
        int sortOrder,
        Type type,
        Identifier nonogram
) {
    private static final Codec<Type> TYPE_CODEC = Codec.STRING.xmap(value -> Type.valueOf(value.toUpperCase()), type -> type.name().toLowerCase());

    public static final Codec<Research> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStackTemplate.CODEC.fieldOf("icon").forGetter(Research::icon),
            Identifier.CODEC.listOf().optionalFieldOf("hardDependencies", List.of()).forGetter(Research::hardDependencies),
            Identifier.CODEC.listOf().optionalFieldOf("softDependencies", List.of()).forGetter(Research::softDependencies),
            Identifier.CODEC.listOf().optionalFieldOf("unlocked_recipes", List.of()).forGetter(Research::unlockedRecipes),
            Codec.INT.optionalFieldOf("scrap", 0).forGetter(Research::scrapCost),
            Codec.INT.optionalFieldOf("biomass", 0).forGetter(Research::biomassCost),
            Codec.INT.optionalFieldOf("fragments", 0).forGetter(Research::fragmentsCost),
            Codec.INT.optionalFieldOf("data", 0).forGetter(Research::dataCost),
            Codec.INT.optionalFieldOf("sort_order", 0).forGetter(Research::sortOrder),
            TYPE_CODEC.optionalFieldOf("research_type", Type.LORE).forGetter(Research::type),
            Identifier.CODEC.optionalFieldOf("nonogram").forGetter(research -> Optional.ofNullable(research.nonogram))
    ).apply(instance, (icon, hardDependencies, softDependencies, unlockedRecipes, scrapCost, biomassCost, fragmentsCost, dataCost, sortOrder, type, nonogram) ->
            new Research(icon, hardDependencies, softDependencies, unlockedRecipes, scrapCost, biomassCost, fragmentsCost, dataCost, sortOrder, type, nonogram.orElse(null))));

    public static final StreamCodec<RegistryFriendlyByteBuf, Research> STREAM_CODEC = StreamCodec.of(
            Research::toNetwork,
            Research::fromNetwork
    );

    public enum Type {
        LORE,
        ASSEMBLER,
        FABRICATOR,
        CRAFTING
    }

    private static Research fromNetwork(RegistryFriendlyByteBuf buf) {
        final var icon = ItemStackTemplate.STREAM_CODEC.decode(buf);
        final var hardDependencies = Identifier.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buf);
        final var softDependencies = Identifier.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buf);
        final var unlockedRecipes = Identifier.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buf);
        final var scrap = buf.readVarInt();
        final var biomass = buf.readVarInt();
        final var fragments = buf.readVarInt();
        final var data = buf.readVarInt();
        final var sortOrder = buf.readVarInt();
        final var type = buf.readEnum(Type.class);
        final var nonogram = buf.readNullable(FriendlyByteBuf::readIdentifier);
        return new Research(icon, hardDependencies, softDependencies, unlockedRecipes, scrap, biomass, fragments, data, sortOrder, type, nonogram);
    }

    private static void toNetwork(RegistryFriendlyByteBuf buf, Research research) {
        ItemStackTemplate.STREAM_CODEC.encode(buf, research.icon);
        Identifier.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, research.hardDependencies);
        Identifier.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, research.softDependencies);
        Identifier.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, research.unlockedRecipes);
        buf.writeVarInt(research.scrapCost);
        buf.writeVarInt(research.biomassCost);
        buf.writeVarInt(research.fragmentsCost);
        buf.writeVarInt(research.dataCost);
        buf.writeVarInt(research.sortOrder);
        buf.writeEnum(research.type);
        buf.writeNullable(research.nonogram, FriendlyByteBuf::writeIdentifier);
    }

}

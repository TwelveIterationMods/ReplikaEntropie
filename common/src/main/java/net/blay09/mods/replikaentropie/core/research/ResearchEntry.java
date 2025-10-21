package net.blay09.mods.replikaentropie.core.research;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.List;

public record ResearchEntry(
        ItemStack icon,
        List<ResourceLocation> dependencies,
        ResourceLocation assemblerRecipe,
        int scrapCost,
        int biomassCost,
        int fragmentsCost,
        int dataCost,
        int sortOrder,
        Type type
) {

    public static ResearchEntry read(FriendlyByteBuf buf) {
        final var icon = buf.readItem();
        final var dependencies = buf.readList(FriendlyByteBuf::readResourceLocation);
        final var assemblerRecipe = buf.readNullable(FriendlyByteBuf::readResourceLocation);
        final var scrapCost = buf.readVarInt();
        final var biomassCost = buf.readVarInt();
        final var fragmentsCost = buf.readVarInt();
        final var dataCost = buf.readVarInt();
        final var sortOrder = buf.readVarInt();
        final var type = buf.readEnum(Type.class);
        return new ResearchEntry(icon, dependencies, assemblerRecipe, scrapCost, biomassCost, fragmentsCost, dataCost, sortOrder, type);
    }

    public static void write(FriendlyByteBuf buf, ResearchEntry entry) {
        buf.writeItem(entry.icon);
        buf.writeCollection(entry.dependencies != null ? entry.dependencies : Collections.emptyList(), FriendlyByteBuf::writeResourceLocation);
        buf.writeNullable(entry.assemblerRecipe, FriendlyByteBuf::writeResourceLocation);
        buf.writeVarInt(entry.scrapCost);
        buf.writeVarInt(entry.biomassCost);
        buf.writeVarInt(entry.fragmentsCost);
        buf.writeVarInt(entry.dataCost);
        buf.writeVarInt(entry.sortOrder);
        buf.writeEnum(entry.type != null ? entry.type : Type.LORE);
    }

    public enum Type {
        LORE,
        ASSEMBLER,
        FABRICATOR
    }
}

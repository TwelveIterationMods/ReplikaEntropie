package net.blay09.mods.replikaentropie.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public record ResearchRecipe(
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
) implements Recipe<RecipeInput>, PreviewableRecipe {
    private static final Codec<Type> TYPE_CODEC = Codec.STRING.xmap(value -> Type.valueOf(value.toUpperCase()), type -> type.name().toLowerCase());

    private static final MapCodec<ResearchRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemStackTemplate.CODEC.fieldOf("icon").forGetter(ResearchRecipe::icon),
            Identifier.CODEC.listOf().optionalFieldOf("hardDependencies", List.of()).forGetter(ResearchRecipe::hardDependencies),
            Identifier.CODEC.listOf().optionalFieldOf("softDependencies", List.of()).forGetter(ResearchRecipe::softDependencies),
            Identifier.CODEC.listOf().optionalFieldOf("unlocked_recipes", List.of()).forGetter(ResearchRecipe::unlockedRecipes),
            Codec.INT.optionalFieldOf("scrap", 0).forGetter(ResearchRecipe::scrapCost),
            Codec.INT.optionalFieldOf("biomass", 0).forGetter(ResearchRecipe::biomassCost),
            Codec.INT.optionalFieldOf("fragments", 0).forGetter(ResearchRecipe::fragmentsCost),
            Codec.INT.optionalFieldOf("data", 0).forGetter(ResearchRecipe::dataCost),
            Codec.INT.optionalFieldOf("sort_order", 0).forGetter(ResearchRecipe::sortOrder),
            TYPE_CODEC.optionalFieldOf("research_type", Type.LORE).forGetter(ResearchRecipe::type),
            Identifier.CODEC.optionalFieldOf("nonogram").forGetter(recipe -> Optional.ofNullable(recipe.nonogram))
    ).apply(instance, (icon, hardDependencies, softDependencies, unlockedRecipes, scrapCost, biomassCost, fragmentsCost, dataCost, sortOrder, type, nonogram) ->
            new ResearchRecipe(icon, hardDependencies, softDependencies, unlockedRecipes, scrapCost, biomassCost, fragmentsCost, dataCost, sortOrder, type, nonogram.orElse(null))));

    private static final StreamCodec<RegistryFriendlyByteBuf, ResearchRecipe> STREAM_CODEC = StreamCodec.of(
            ResearchRecipe::toNetwork,
            ResearchRecipe::fromNetwork
    );

    public static Stream<Identifier> getRecipeIds(Level level) {
        return level != null
                && level instanceof ServerLevel serverLevel
                ? serverLevel.recipeAccess().getRecipes().stream()
                .filter(holder -> holder.value().getType() == ModRecipes.research.type())
                .map(holder -> holder.id().identifier())
                : Stream.empty();
    }

    public enum Type {
        LORE,
        ASSEMBLER,
        FABRICATOR,
        CRAFTING
    }

    public static List<ResearchRecipe> getRecipes(Level level) {
        return level != null
                && level instanceof ServerLevel serverLevel
                ? serverLevel.recipeAccess().getRecipes().stream()
                .filter(holder -> holder.value().getType() == ModRecipes.research.type())
                .map(holder -> (ResearchRecipe) holder.value())
                .toList()
                : Collections.emptyList();
    }

    @Override
    public boolean matches(RecipeInput input, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(RecipeInput input) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public String group() {
        return "";
    }

    public ItemStack getResultItem() {
        return icon.create();
    }

    @Override
    public ItemStack previewResultItem() {
        return icon.create();
    }

    @Override
    public RecipeSerializer<ResearchRecipe> getSerializer() {
        return ModRecipes.research.serializer();
    }

    @Override
    public RecipeType<ResearchRecipe> getType() {
        return ModRecipes.research.type();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return ModRecipes.research.bookCategory();
    }

    private static ResearchRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
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
        final var nonogram = buf.readNullable(it -> it.readIdentifier());
        return new ResearchRecipe(icon, hardDependencies, softDependencies, unlockedRecipes, scrap, biomass, fragments, data, sortOrder, type, nonogram);
    }

    private static void toNetwork(RegistryFriendlyByteBuf buf, ResearchRecipe recipe) {
        ItemStackTemplate.STREAM_CODEC.encode(buf, recipe.icon);
        Identifier.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, recipe.hardDependencies);
        Identifier.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, recipe.softDependencies);
        Identifier.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, recipe.unlockedRecipes);
        buf.writeVarInt(recipe.scrapCost);
        buf.writeVarInt(recipe.biomassCost);
        buf.writeVarInt(recipe.fragmentsCost);
        buf.writeVarInt(recipe.dataCost);
        buf.writeVarInt(recipe.sortOrder);
        buf.writeEnum(recipe.type);
        buf.writeNullable(recipe.nonogram, (it, nonogram) -> it.writeIdentifier(nonogram));
    }

    public static RecipeSerializer<ResearchRecipe> serializer() {
        return new RecipeSerializer<>(CODEC, STREAM_CODEC);
    }
}

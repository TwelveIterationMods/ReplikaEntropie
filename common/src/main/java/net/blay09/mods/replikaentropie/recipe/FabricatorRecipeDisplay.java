package net.blay09.mods.replikaentropie.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

public record FabricatorRecipeDisplay(SlotDisplay result, SlotDisplay craftingStation, int scrap, int biomass, int fragments) implements RecipeDisplay {
    public static final MapCodec<FabricatorRecipeDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SlotDisplay.CODEC.fieldOf("result").forGetter(FabricatorRecipeDisplay::result),
            SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(FabricatorRecipeDisplay::craftingStation),
            Codec.INT.optionalFieldOf("scrap", 0).forGetter(FabricatorRecipeDisplay::scrap),
            Codec.INT.optionalFieldOf("biomass", 0).forGetter(FabricatorRecipeDisplay::biomass),
            Codec.INT.optionalFieldOf("fragments", 0).forGetter(FabricatorRecipeDisplay::fragments)
    ).apply(instance, FabricatorRecipeDisplay::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FabricatorRecipeDisplay> STREAM_CODEC = StreamCodec.composite(
            SlotDisplay.STREAM_CODEC,
            FabricatorRecipeDisplay::result,
            SlotDisplay.STREAM_CODEC,
            FabricatorRecipeDisplay::craftingStation,
            ByteBufCodecs.VAR_INT,
            FabricatorRecipeDisplay::scrap,
            ByteBufCodecs.VAR_INT,
            FabricatorRecipeDisplay::biomass,
            ByteBufCodecs.VAR_INT,
            FabricatorRecipeDisplay::fragments,
            FabricatorRecipeDisplay::new
    );

    @Override
    public Type<FabricatorRecipeDisplay> type() {
        return ModRecipes.fabricatorDisplay.value();
    }
}

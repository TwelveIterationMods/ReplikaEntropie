package net.blay09.mods.replikaentropie.fabric.datagen;

import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.tag.ModItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagsProvider.ItemTagsProvider {
    public ModItemTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup) {
        valueLookupBuilder(ModItemTags.PROTECTS_FROM_FRAGMENTAL_WASTE).add(
                ModItems.hazmatHelmet.value(),
                ModItems.hazmatChestplate.value(),
                ModItems.hazmatLeggings.value(),
                ModItems.hazmatBoots.value()
        );

        valueLookupBuilder(ModItemTags.COOLS_FRAGMENTAL_GENERATOR).add(
                Items.SNOWBALL,
                Items.ICE,
                Items.PACKED_ICE,
                Items.BLUE_ICE
        );

        valueLookupBuilder(ModItemTags.BIOMASS_INCUBATOR_SEEDS).add(
                Items.WHEAT_SEEDS,
                Items.BEETROOT_SEEDS,
                Items.MELON_SEEDS,
                Items.PUMPKIN_SEEDS,
                Items.TORCHFLOWER_SEEDS,
                Items.NETHER_WART,
                Items.CHORUS_FLOWER
        );

        valueLookupBuilder(ModItemTags.BIOMASS_INCUBATOR_SOILS).add(
                Items.DIRT,
                Items.FARMLAND,
                Items.SOUL_SAND,
                Items.END_STONE
        );

        valueLookupBuilder(ModItemTags.BIOMASS_INCUBATOR_FARMLAND_LIKE).add(
                Items.DIRT,
                Items.FARMLAND
        );
    }
}

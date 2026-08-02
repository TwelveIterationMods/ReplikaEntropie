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
        valueLookupBuilder(ModItemTags.CHARGEABLE).add(
                ModItems.nullphaser.value(),
                ModItems.oreVacuum.value(),
                ModItems.nightVisionGoggles.value(),
                ModItems.brightVisionGoggles.value(),
                ModItems.replikaHelmet.value(),
                ModItems.replikaChestplate.value(),
                ModItems.replikaLeggings.value(),
                ModItems.replikaBoots.value()
        );

        valueLookupBuilder(ModItemTags.PROTECTS_FROM_FRAGMENTAL_WASTE).add(
                ModItems.hazmatHelmet.value(),
                ModItems.hazmatChestplate.value(),
                ModItems.hazmatLeggings.value(),
                ModItems.hazmatBoots.value()
        );

        valueLookupBuilder(ModItemTags.REPLIKA_WORKBENCH_MODDABLE).add(
                ModItems.replikaHelmet.value(),
                ModItems.replikaChestplate.value(),
                ModItems.replikaLeggings.value(),
                ModItems.replikaBoots.value()
        );

        valueLookupBuilder(ModItemTags.REPLIKA_WORKBENCH_HEAD).add(ModItems.replikaHelmet.value());
        valueLookupBuilder(ModItemTags.REPLIKA_WORKBENCH_CHEST).add(ModItems.replikaChestplate.value());
        valueLookupBuilder(ModItemTags.REPLIKA_WORKBENCH_LEGS).add(ModItems.replikaLeggings.value());
        valueLookupBuilder(ModItemTags.REPLIKA_WORKBENCH_FEET).add(ModItems.replikaBoots.value());

        valueLookupBuilder(ModItemTags.REPLIKA_WORKBENCH_PARTS)
                .addTag(ModItemTags.REPLIKA_WORKBENCH_HEAD_PARTS)
                .addTag(ModItemTags.REPLIKA_WORKBENCH_CHEST_PARTS)
                .addTag(ModItemTags.REPLIKA_WORKBENCH_LEGS_PARTS)
                .addTag(ModItemTags.REPLIKA_WORKBENCH_FEET_PARTS);

        valueLookupBuilder(ModItemTags.REPLIKA_WORKBENCH_HEAD_PARTS).add(
                ModItems.nightVisionGoggles.value(),
                ModItems.brightVisionGoggles.value()
        );

        valueLookupBuilder(ModItemTags.REPLIKA_WORKBENCH_CHEST_PARTS).add(ModItems.graviliftEngine.value());
        valueLookupBuilder(ModItemTags.REPLIKA_WORKBENCH_LEGS_PARTS).add(ModItems.semisonicSpeeders.value());

        valueLookupBuilder(ModItemTags.REPLIKA_WORKBENCH_FEET_PARTS).add(
                ModItems.magphasers.value(),
                ModItems.slowphasers.value(),
                ModItems.stompers.value(),
                ModItems.bouncers.value()
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

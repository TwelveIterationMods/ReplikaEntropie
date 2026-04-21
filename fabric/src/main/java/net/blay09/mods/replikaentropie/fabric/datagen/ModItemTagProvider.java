package net.blay09.mods.replikaentropie.fabric.datagen;

import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.tag.ModItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;

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

        valueLookupBuilder(ModItemTags.REPLIKA_FRAME).add(
                ModItems.replikaHelmetFrame.value(),
                ModItems.replikaChestplateFrame.value(),
                ModItems.replikaLeggingsFrame.value(),
                ModItems.replikaBootsFrame.value()
        );

        valueLookupBuilder(ModItemTags.REPLIKA_SUIT).add(
                ModItems.replikaHelmet.value(),
                ModItems.replikaChestplate.value(),
                ModItems.replikaLeggings.value(),
                ModItems.replikaBoots.value()
        );

        valueLookupBuilder(ModItemTags.REPLIKA_PART).add(
                ModItems.nightVisionGoggles.value(),
                ModItems.brightVisionGoggles.value(),
                ModItems.graviliftHarness.value(),
                ModItems.semisonicSpeeders.value(),
                ModItems.springBoots.value(),
                ModItems.stompers.value(),
                ModItems.magphasers.value(),
                ModItems.slowphasers.value(),
                ModItems.biosteel.value()
        );

        valueLookupBuilder(ModItemTags.REPLIKA_HELMET_PART).add(
                ModItems.nightVisionGoggles.value(),
                ModItems.brightVisionGoggles.value(),
                ModItems.biosteel.value()
        );

        valueLookupBuilder(ModItemTags.REPLIKA_CHESTPLATE_PART).add(
                ModItems.graviliftHarness.value(),
                ModItems.biosteel.value()
        );

        valueLookupBuilder(ModItemTags.REPLIKA_LEGGINGS_PART).add(
                ModItems.semisonicSpeeders.value(),
                ModItems.biosteel.value()
        );

        valueLookupBuilder(ModItemTags.REPLIKA_BOOTS_PART).add(
                ModItems.springBoots.value(),
                ModItems.magphasers.value(),
                ModItems.slowphasers.value(),
                ModItems.stompers.value(),
                ModItems.biosteel.value()
        );
    }
}

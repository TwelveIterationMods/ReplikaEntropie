package net.blay09.mods.replikaentropie.fabric.datagen;

import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.tag.ModItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider<Item> {
    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.ITEM, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup) {
        getOrCreateTagBuilder(ModItemTags.PROTECTS_FROM_FRAGMENTAL_WASTE).add(
                ModItems.hazmatHelmet,
                ModItems.hazmatChestplate,
                ModItems.hazmatLeggings,
                ModItems.hazmatBoots
        );

        getOrCreateTagBuilder(ModItemTags.REPLIKA_FRAME).add(
                ModItems.replikaHelmetFrame,
                ModItems.replikaChestplateFrame,
                ModItems.replikaLeggingsFrame,
                ModItems.replikaBootsFrame
        );

        getOrCreateTagBuilder(ModItemTags.REPLIKA_SUIT).add(
                ModItems.replikaHelmet,
                ModItems.replikaChestplate,
                ModItems.replikaLeggings,
                ModItems.replikaBoots
        );

        getOrCreateTagBuilder(ModItemTags.REPLIKA_PART).add(
                ModItems.nightVisionGoggles,
                ModItems.brightVisionGoggles,
                ModItems.graviliftHarness,
                ModItems.semisonicSpeeders,
                ModItems.springBoots,
                ModItems.stompers,
                ModItems.magphasers,
                ModItems.slowphasers,
                ModItems.biosteel
        );

        getOrCreateTagBuilder(ModItemTags.REPLIKA_HELMET_PART).add(
                ModItems.nightVisionGoggles,
                ModItems.brightVisionGoggles,
                ModItems.biosteel
        );

        getOrCreateTagBuilder(ModItemTags.REPLIKA_CHESTPLATE_PART).add(
                ModItems.graviliftHarness,
                ModItems.biosteel
        );

        getOrCreateTagBuilder(ModItemTags.REPLIKA_LEGGINGS_PART).add(
                ModItems.semisonicSpeeders,
                ModItems.biosteel
        );

        getOrCreateTagBuilder(ModItemTags.REPLIKA_BOOTS_PART).add(
                ModItems.springBoots,
                ModItems.magphasers,
                ModItems.slowphasers,
                ModItems.stompers,
                ModItems.biosteel
        );
    }
}

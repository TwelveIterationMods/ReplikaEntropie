package net.blay09.mods.replikaentropie.fabric.datagen;

import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.tag.ModItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.references.BlockItemIds;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagsProvider.ItemTagsProvider {
    public ModItemTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup) {
        tag(ModItemTags.CHARGEABLE).add(
                ModItems.nullphaser.asResourceKey(),
                ModItems.oreVacuum.asResourceKey(),
                ModItems.nightVisionGoggles.asResourceKey(),
                ModItems.brightVisionGoggles.asResourceKey(),
                ModItems.replikaHelmet.asResourceKey(),
                ModItems.replikaChestplate.asResourceKey(),
                ModItems.replikaLeggings.asResourceKey(),
                ModItems.replikaBoots.asResourceKey()
        );

        tag(ModItemTags.PROTECTS_FROM_FRAGMENTAL_WASTE).add(
                ModItems.hazmatHelmet.asResourceKey(),
                ModItems.hazmatChestplate.asResourceKey(),
                ModItems.hazmatLeggings.asResourceKey(),
                ModItems.hazmatBoots.asResourceKey()
        );

        tag(ModItemTags.REPLIKA_WORKBENCH_MODDABLE).add(
                ModItems.replikaHelmet.asResourceKey(),
                ModItems.replikaChestplate.asResourceKey(),
                ModItems.replikaLeggings.asResourceKey(),
                ModItems.replikaBoots.asResourceKey()
        );

        tag(ModItemTags.REPLIKA_WORKBENCH_HEAD).add(ModItems.replikaHelmet.asResourceKey());
        tag(ModItemTags.REPLIKA_WORKBENCH_CHEST).add(ModItems.replikaChestplate.asResourceKey());
        tag(ModItemTags.REPLIKA_WORKBENCH_LEGS).add(ModItems.replikaLeggings.asResourceKey());
        tag(ModItemTags.REPLIKA_WORKBENCH_FEET).add(ModItems.replikaBoots.asResourceKey());

        tag(ModItemTags.REPLIKA_WORKBENCH_PARTS)
                .addTag(ModItemTags.REPLIKA_WORKBENCH_HEAD_PARTS)
                .addTag(ModItemTags.REPLIKA_WORKBENCH_CHEST_PARTS)
                .addTag(ModItemTags.REPLIKA_WORKBENCH_LEGS_PARTS)
                .addTag(ModItemTags.REPLIKA_WORKBENCH_FEET_PARTS);

        tag(ModItemTags.REPLIKA_WORKBENCH_HEAD_PARTS).add(
                ModItems.nightVisionGoggles.asResourceKey(),
                ModItems.brightVisionGoggles.asResourceKey()
        );

        tag(ModItemTags.REPLIKA_WORKBENCH_CHEST_PARTS).add(ModItems.graviliftEngine.asResourceKey());
        tag(ModItemTags.REPLIKA_WORKBENCH_LEGS_PARTS).add(ModItems.semisonicSpeeders.asResourceKey());

        tag(ModItemTags.REPLIKA_WORKBENCH_FEET_PARTS).add(
                ModItems.magphasers.asResourceKey(),
                ModItems.slowphasers.asResourceKey(),
                ModItems.stompers.asResourceKey(),
                ModItems.bouncers.asResourceKey()
        );

        tag(ModItemTags.BIOMASS_INCUBATOR_SEEDS).add(
                BlockItemIds.WHEAT_CROP.item(),
                BlockItemIds.BEETROOT_CROP.item(),
                BlockItemIds.MELON.item(),
                BlockItemIds.PUMPKIN.item(),
                BlockItemIds.TORCHFLOWER.item(),
                BlockItemIds.NETHER_WART.item(),
                BlockItemIds.CHORUS_FLOWER.item()
        );

        tag(ModItemTags.BIOMASS_INCUBATOR_SOILS).add(
                BlockItemIds.DIRT.item(),
                BlockItemIds.FARMLAND.item(),
                BlockItemIds.SOUL_SAND.item(),
                BlockItemIds.END_STONE.item()
        );

        tag(ModItemTags.BIOMASS_INCUBATOR_FARMLAND_LIKE).add(
                BlockItemIds.DIRT.item(),
                BlockItemIds.FARMLAND.item()
        );
    }
}

package net.blay09.mods.replikaentropie.item;

import net.blay09.mods.balm.world.item.BalmCreativeModeTabRegistrar;
import net.blay09.mods.balm.world.item.BalmItemRegistrar;
import net.blay09.mods.balm.world.item.DeferredItem;
import net.blay09.mods.replikaentropie.component.AbilityHolder;
import net.blay09.mods.replikaentropie.component.ItemDescription;
import net.blay09.mods.replikaentropie.component.ModDataComponents;
import net.blay09.mods.replikaentropie.ReplikaEntropie;
import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.blay09.mods.replikaentropie.entity.ModEntities;
import net.blay09.mods.replikaentropie.core.abilities.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.MinecartDispenseItemBehavior;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.MinecartItem;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.block.DispenserBlock;

import java.util.List;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class ModItems {
    public static DeferredItem damagedChipset;
    public static DeferredItem handheldAnalyzer;
    public static DeferredItem skyScraper;
    public static DeferredItem data;
    public static DeferredItem scrap;
    public static DeferredItem biomass;
    public static DeferredItem fragments;
    public static DeferredItem fragmentalSun;
    public static DeferredItem biomash;
    public static DeferredItem chipset;
    public static DeferredItem biosteel;
    public static DeferredItem biosteelHelmet;
    public static DeferredItem biosteelChestplate;
    public static DeferredItem biosteelLeggings;
    public static DeferredItem biosteelBoots;
    public static DeferredItem hazmatLining;
    public static DeferredItem hazmatHelmet;
    public static DeferredItem hazmatChestplate;
    public static DeferredItem hazmatLeggings;
    public static DeferredItem hazmatBoots;
    public static DeferredItem replikaAlloy;
    public static DeferredItem replikaHelmet;
    public static DeferredItem replikaChestplate;
    public static DeferredItem replikaLeggings;
    public static DeferredItem replikaBoots;
    public static DeferredItem nightVisionGoggles;
    public static DeferredItem brightVisionGoggles;
    public static DeferredItem graviliftEngine;
    public static DeferredItem magphasers;
    public static DeferredItem nullphaser;
    public static DeferredItem automaticHackTool;
    public static DeferredItem oreVacuum;
    public static DeferredItem slowphasers;
    public static DeferredItem stompers;
    public static DeferredItem bouncers;
    public static DeferredItem semisonicSpeeders;
    public static DeferredItem assemblyTicket;
    public static DeferredItem makeshiftPSU;
    public static DeferredItem metalDetector;
    public static DeferredItem wasteBarrelMinecart;
    public static DeferredItem fragmentalWasteMinecart;
    public static DeferredItem biomassHarvesterMinecart;

    public static void initialize(BalmItemRegistrar items) {
        handheldAnalyzer = items.register("handheld_analyzer", HandheldAnalyzerItem::new, it -> withTooltip(it.stacksTo(1), "handheld_analyzer")).asDeferredItem();
        skyScraper = items.register("sky_scraper", SkyScraperItem::new, it -> withTooltip(it.stacksTo(1), "sky_scraper")).asDeferredItem();
        makeshiftPSU = items.register("makeshift_psu", Item::new, it -> it).asDeferredItem();
        damagedChipset = items.register("damaged_chipset", Item::new, it -> it).asDeferredItem();
        data = items.register("data", DataItem::new, it -> it.food(new FoodProperties.Builder().alwaysEdible().build(), Consumable.builder().consumeSeconds(0.1f).build())).asDeferredItem();
        scrap = items.register("scrap", Item::new, it -> it).asDeferredItem();
        biomass = items.register("biomass", Item::new, it -> it).asDeferredItem();
        fragments = items.register("fragments", Item::new, it -> it).asDeferredItem();
        fragmentalSun = items.register("fragmental_sun", Item::new, it -> it).asDeferredItem();
        chipset = items.register("chipset", Item::new, it -> it).asDeferredItem();
        biosteel = items.register("biosteel", Item::new, it -> it).asDeferredItem();
        replikaAlloy = items.register("replika_alloy", Item::new, it -> it).asDeferredItem();
        assemblyTicket = items.register("assembly_ticket", AssemblyTicketItem::new, it -> it).asDeferredItem();
        biomash = items.register("biomash", Item::new, it -> it.food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.1f).build())).asDeferredItem();
        nightVisionGoggles = items.register("nightvision_goggles", ReplikaPieceArmorItem::new, it -> withTooltip(withAbility(humanoidArmor(it, ModArmorMaterials.GOGGLES, ArmorType.HELMET), NightVisionAbility.ID), "nightvision_goggles")).asDeferredItem();
        brightVisionGoggles = items.register("brightvision_goggles", ReplikaPieceArmorItem::new, it -> withTooltip(withAbility(humanoidArmor(it, ModArmorMaterials.GOGGLES, ArmorType.HELMET), BrightVisionAbility.ID), "brightvision_goggles")).asDeferredItem();
        nullphaser = items.register("nullphaser", NullphaserItem::new, it -> withTooltip(it.durability(64), "nullphaser")).asDeferredItem();
        automaticHackTool = items.register("automatic_hack_tool", Item::new, it -> withTooltip(it.durability(16).component(DataComponents.BREAK_SOUND, SoundEvents.ITEM_BREAK), "automatic_hack_tool")).asDeferredItem();
        oreVacuum = items.register("ore_vacuum", OreVacuumItem::new, it -> withTooltip(it.durability(256), "ore_vacuum")).asDeferredItem();
        metalDetector = items.register("metal_detector", MetalDetectorItem::new, it ->it.durability(600)).asDeferredItem();
        wasteBarrelMinecart = items.register("waste_barrel_minecart", properties -> createMinecartItem(ModEntities.wasteBarrelMinecart.value(), properties), it -> it.stacksTo(1)).asDeferredItem();
        fragmentalWasteMinecart = items.register("fragmental_waste_minecart", properties -> createMinecartItem(ModEntities.fragmentalWasteMinecart.value(), properties), it -> it.stacksTo(1)).asDeferredItem();
        biomassHarvesterMinecart = items.register("biomass_harvester_minecart", properties -> createMinecartItem(ModEntities.biomassHarvesterMinecart.value(), properties), it -> it.stacksTo(1)).asDeferredItem();
        graviliftEngine = items.register("gravilift_engine", ReplikaPartItem::new, it -> withTooltip(withAbility(it.stacksTo(1), GraviliftAbility.ID), "gravilift_engine")).asDeferredItem();
        magphasers = items.register("magphasers", ReplikaPartItem::new, it -> withTooltip(withAbility(it.stacksTo(1), MagphaseAbility.ID), "magphasers")).asDeferredItem();
        slowphasers = items.register("slowphasers", ReplikaPartItem::new, it -> withTooltip(withAbility(it.stacksTo(1), SlowphaseAbility.ID), "slowphasers")).asDeferredItem();
        stompers = items.register("stompers", ReplikaPartItem::new, it -> withTooltip(withAbility(it.stacksTo(1), StompingAbility.ID), "stompers")).asDeferredItem();
        bouncers = items.register("bouncers", ReplikaPartItem::new, it -> withTooltip(withAbility(it.stacksTo(1), JumpBoostAbility.ID), "bouncers")).asDeferredItem();
        semisonicSpeeders = items.register("semisonic_speeders", ReplikaPartItem::new, it -> withTooltip(withAbility(it.stacksTo(1), SpeedBoostAbility.ID), "semisonic_speeders")).asDeferredItem();
        biosteelHelmet = items.register("biosteel_helmet", BiosteelArmorItem::new, it -> humanoidArmor(it, ModArmorMaterials.BIOSTEEL, ArmorType.HELMET)).asDeferredItem();
        biosteelChestplate = items.register("biosteel_chestplate", BiosteelArmorItem::new, it -> humanoidArmor(it, ModArmorMaterials.BIOSTEEL, ArmorType.CHESTPLATE)).asDeferredItem();
        biosteelLeggings = items.register("biosteel_leggings", BiosteelArmorItem::new, it -> humanoidArmor(it, ModArmorMaterials.BIOSTEEL, ArmorType.LEGGINGS)).asDeferredItem();
        biosteelBoots = items.register("biosteel_boots", BiosteelArmorItem::new, it -> humanoidArmor(it, ModArmorMaterials.BIOSTEEL, ArmorType.BOOTS)).asDeferredItem();
        hazmatLining = items.register("hazmat_lining", Item::new, it -> it).asDeferredItem();
        hazmatHelmet = items.register("hazmat_helmet", HazmatArmorItem::new, it -> humanoidArmor(it, ModArmorMaterials.HAZMAT, ArmorType.HELMET)).asDeferredItem();
        hazmatChestplate = items.register("hazmat_chestplate", HazmatArmorItem::new, it -> humanoidArmor(it, ModArmorMaterials.HAZMAT, ArmorType.CHESTPLATE)).asDeferredItem();
        hazmatLeggings = items.register("hazmat_leggings", HazmatArmorItem::new, it -> humanoidArmor(it, ModArmorMaterials.HAZMAT, ArmorType.LEGGINGS)).asDeferredItem();
        hazmatBoots = items.register("hazmat_boots", HazmatArmorItem::new, it -> humanoidArmor(it, ModArmorMaterials.HAZMAT, ArmorType.BOOTS)).asDeferredItem();
        replikaHelmet = items.register("replika_helmet", ReplikaArmorItem::new, it -> humanoidArmor(it, ModArmorMaterials.REPLIKA, ArmorType.HELMET)).asDeferredItem();
        replikaChestplate = items.register("replika_chestplate", ReplikaArmorItem::new, it -> humanoidArmor(it, ModArmorMaterials.REPLIKA, ArmorType.CHESTPLATE)).asDeferredItem();
        replikaLeggings = items.register("replika_leggings", ReplikaArmorItem::new, it -> humanoidArmor(it, ModArmorMaterials.REPLIKA, ArmorType.LEGGINGS)).asDeferredItem();
        replikaBoots = items.register("replika_boots", ReplikaArmorItem::new, it -> humanoidArmor(it, ModArmorMaterials.REPLIKA, ArmorType.BOOTS)).asDeferredItem();
    }

    private static MinecartItem createMinecartItem(EntityType<? extends AbstractMinecart> entityType, Item.Properties properties) {
        final var item = new MinecartItem(entityType, properties);
        DispenserBlock.registerBehavior(item, new MinecartDispenseItemBehavior(entityType));
        return item;
    }

    public static void initialize(BalmCreativeModeTabRegistrar creativeModeTabs) {
        creativeModeTabs.register(ReplikaEntropie.MOD_ID, builder -> builder
                .icon(() -> handheldAnalyzer.createStack())
                .title(Component.translatable(id(ReplikaEntropie.MOD_ID).toLanguageKey("itemGroup")))
                .displayItems(((_, output) -> {
                    output.accept(metalDetector);
                    output.accept(damagedChipset);
                    output.accept(chipset);
                    output.accept(skyScraper);
                    output.accept(handheldAnalyzer);
                    output.accept(automaticHackTool);
                    output.accept(scrap);
                    output.accept(biomass);
                    output.accept(fragments);
                    output.accept(fragmentalSun);
                    output.accept(biomash);
                    output.accept(data);
                    output.accept(makeshiftPSU);
                    output.accept(ModBlocks.recycler);
                    output.accept(ModBlocks.assembler);
                    output.accept(ModBlocks.solarSink);
                    output.accept(ModBlocks.waterSink);
                    output.accept(ModBlocks.lavaSink);
                    output.accept(ModBlocks.bluePrinter);
                    output.accept(ModBlocks.funnel);
                    output.accept(ModBlocks.crane);
                    output.accept(ModBlocks.fabricator);
                    output.accept(ModBlocks.entropicDataMiner);
                    output.accept(ModBlocks.cobblescrap);
                    output.accept(ModBlocks.lavascrap);
                    output.accept(ModBlocks.biomassIncubator);
                    output.accept(ModBlocks.biomassHarvester);
                    output.accept(ModBlocks.worldEater);
                    output.accept(ModBlocks.fragmentalGenerator);
                    output.accept(ModBlocks.fragmentAccelerator);
                    output.accept(ModBlocks.chaosEngine);
                    output.accept(ModBlocks.wasteBarrel);
                    output.accept(ModBlocks.fragmentalWaste);
                    output.accept(wasteBarrelMinecart);
                    output.accept(fragmentalWasteMinecart);
                    output.accept(biomassHarvesterMinecart);
                    output.accept(ModBlocks.replikaWorkbench);
                    output.accept(oreVacuum);
                    output.accept(nullphaser);
                    output.accept(nightVisionGoggles);
                    output.accept(brightVisionGoggles);
                    output.accept(assemblyTicket);
                    output.accept(biosteel);
                    output.accept(biosteelHelmet);
                    output.accept(biosteelChestplate);
                    output.accept(biosteelLeggings);
                    output.accept(biosteelBoots);
                    output.accept(replikaAlloy);
                    output.accept(replikaHelmet);
                    output.accept(replikaChestplate);
                    output.accept(replikaLeggings);
                    output.accept(replikaBoots);
                    output.accept(hazmatLining);
                    output.accept(hazmatHelmet);
                    output.accept(hazmatChestplate);
                    output.accept(hazmatLeggings);
                    output.accept(hazmatBoots);
                    output.accept(graviliftEngine);
                    output.accept(semisonicSpeeders);
                    output.accept(bouncers);
                    output.accept(stompers);
                    output.accept(slowphasers);
                    output.accept(magphasers);
                })));
    }

    private static Item.Properties humanoidArmor(Item.Properties properties, ArmorMaterial material, ArmorType type) {
        var result = properties.durability(type.getDurability(material.durability()))
                .attributes(material.createAttributes(type))
                .component(DataComponents.EQUIPPABLE, Equippable.builder(type.getSlot())
                        .setEquipSound(material.equipSound())
                        .setAsset(material.assetId()).build())
                .repairable(material.repairIngredient());
        if (material.enchantmentValue() > 0) {
            result = result.enchantable(material.enchantmentValue());
        }
        return result;
    }

    private static Item.Properties withAbility(Item.Properties properties, Identifier... abilityIds) {
        return properties.component(ModDataComponents.abilityHolder(), new AbilityHolder(List.of(abilityIds)));
    }

    private static Item.Properties withTooltip(Item.Properties properties, String name) {
        return properties.component(ModDataComponents.itemDescription(), new ItemDescription("item." + ReplikaEntropie.MOD_ID + "." + name + ".tooltip"));
    }

}

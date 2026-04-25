package net.blay09.mods.replikaentropie.item;

import net.blay09.mods.balm.world.item.BalmCreativeModeTabRegistrar;
import net.blay09.mods.balm.world.item.BalmItemRegistrar;
import net.blay09.mods.balm.world.item.DeferredItem;
import net.blay09.mods.replikaentropie.ReplikaEntropie;
import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.Equippable;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class ModItems {
    public static DeferredItem damagedChipset;
    public static DeferredItem handheldAnalyzer;
    public static DeferredItem skyScraper;
    public static DeferredItem data;
    public static DeferredItem scrap;
    public static DeferredItem biomass;
    public static DeferredItem fragments;
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
    public static DeferredItem scubaHelmet;
    public static DeferredItem nightVisionGoggles;
    public static DeferredItem brightVisionGoggles;
    public static DeferredItem graviliftHarness;
    public static DeferredItem magphasers;
    public static DeferredItem nullphaser;
    public static DeferredItem automaticHackTool;
    public static DeferredItem oreVacuum;
    public static DeferredItem slowphasers;
    public static DeferredItem stompers;
    public static DeferredItem springBoots;
    public static DeferredItem semisonicSpeeders;
    public static DeferredItem assemblyTicket;
    public static DeferredItem makeshiftPSU;
    public static DeferredItem metalDetector;
    public static DeferredItem wasteBarrelMinecart;
    public static DeferredItem fragmentalWasteMinecart;

    public static void initialize(BalmItemRegistrar items) {
        handheldAnalyzer = items.register("handheld_analyzer", HandheldAnalyzerItem::new, it -> it.stacksTo(1)).asDeferredItem();
        skyScraper = items.register("sky_scraper", SkyScraperItem::new, it -> it.stacksTo(1)).asDeferredItem();
        makeshiftPSU = items.register("makeshift_psu", Item::new, it -> it).asDeferredItem();
        damagedChipset = items.register("damaged_chipset", Item::new, it -> it).asDeferredItem();
        data = items.register("data", DataItem::new, it -> it.food(new FoodProperties.Builder().alwaysEdible().build())).asDeferredItem();
        scrap = items.register("scrap", Item::new, it -> it).asDeferredItem();
        biomass = items.register("biomass", Item::new, it -> it).asDeferredItem();
        fragments = items.register("fragments", Item::new, it -> it).asDeferredItem();
        chipset = items.register("chipset", Item::new, it -> it).asDeferredItem();
        biosteel = items.register("biosteel", Item::new, it -> it).asDeferredItem();
        assemblyTicket = items.register("assembly_ticket", AssemblyTicketItem::new, it -> it).asDeferredItem();
        biomash = items.register("biomash", Item::new, it -> it.food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.1f).build())).asDeferredItem();
        nightVisionGoggles = items.register("nightvision_goggles", ReplikaPieceArmorItem::new, it -> humanoidArmor(it, ModArmorMaterials.GOGGLES, ArmorType.HELMET)).asDeferredItem();
        brightVisionGoggles = items.register("brightvision_goggles", ReplikaPieceArmorItem::new, it -> humanoidArmor(it, ModArmorMaterials.GOGGLES, ArmorType.HELMET)).asDeferredItem();
        graviliftHarness = items.register("gravilift_harness", ReplikaPieceArmorItem::new, it -> humanoidArmor(it, ModArmorMaterials.REPLIKA, ArmorType.CHESTPLATE).durability(-1)).asDeferredItem();
        magphasers = items.register("magphasers", ReplikaPieceArmorItem::new, it -> humanoidArmor(it, ModArmorMaterials.REPLIKA, ArmorType.BOOTS).durability(-1)).asDeferredItem();
        nullphaser = items.register("nullphaser", NullphaserItem::new, it -> it.durability(-1)).asDeferredItem();
        automaticHackTool = items.register("automatic_hack_tool", Item::new, it -> it.durability(16).component(DataComponents.BREAK_SOUND, SoundEvents.ITEM_BREAK)).asDeferredItem();
        oreVacuum = items.register("ore_vacuum", OreVacuumItem::new, it -> it.durability(-1)).asDeferredItem();
        metalDetector = items.register("metal_detector", MetalDetectorItem::new, it -> it.durability(600)).asDeferredItem();
        slowphasers = items.register("slowphasers", ReplikaPieceArmorItem::new, it -> humanoidArmor(it, ModArmorMaterials.REPLIKA, ArmorType.BOOTS).durability(-1)).asDeferredItem();
        stompers = items.register("stompers", ReplikaPieceArmorItem::new, it -> humanoidArmor(it, ModArmorMaterials.REPLIKA, ArmorType.BOOTS).durability(-1)).asDeferredItem();
        springBoots = items.register("spring_boots", ReplikaPieceArmorItem::new, it -> humanoidArmor(it, ModArmorMaterials.REPLIKA, ArmorType.BOOTS).durability(-1)).asDeferredItem();
        semisonicSpeeders = items.register("semisonic_speeders", ReplikaPieceArmorItem::new, it -> humanoidArmor(it, ModArmorMaterials.REPLIKA, ArmorType.LEGGINGS).durability(-1)).asDeferredItem();
        biosteelHelmet = items.register("biosteel_helmet", BiosteelArmorItem::new, it -> humanoidArmor(it, ModArmorMaterials.BIOSTEEL, ArmorType.HELMET)).asDeferredItem();
        biosteelChestplate = items.register("biosteel_chestplate", BiosteelArmorItem::new, it -> humanoidArmor(it, ModArmorMaterials.BIOSTEEL, ArmorType.CHESTPLATE)).asDeferredItem();
        biosteelLeggings = items.register("biosteel_leggings", BiosteelArmorItem::new, it -> humanoidArmor(it, ModArmorMaterials.BIOSTEEL, ArmorType.LEGGINGS)).asDeferredItem();
        biosteelBoots = items.register("biosteel_boots", BiosteelArmorItem::new, it -> humanoidArmor(it, ModArmorMaterials.BIOSTEEL, ArmorType.BOOTS)).asDeferredItem();
        hazmatLining = items.register("hazmat_lining", Item::new, it -> it).asDeferredItem();
        hazmatHelmet = items.register("hazmat_helmet", HazmatArmorItem::new, it -> humanoidArmor(it, ModArmorMaterials.HAZMAT, ArmorType.HELMET)).asDeferredItem();
        hazmatChestplate = items.register("hazmat_chestplate", HazmatArmorItem::new, it -> humanoidArmor(it, ModArmorMaterials.HAZMAT, ArmorType.CHESTPLATE)).asDeferredItem();
        hazmatLeggings = items.register("hazmat_leggings", HazmatArmorItem::new, it -> humanoidArmor(it, ModArmorMaterials.HAZMAT, ArmorType.LEGGINGS)).asDeferredItem();
        hazmatBoots = items.register("hazmat_boots", HazmatArmorItem::new, it -> humanoidArmor(it, ModArmorMaterials.HAZMAT, ArmorType.BOOTS)).asDeferredItem();
        scubaHelmet = items.register("replika_helmet", ReplikaArmorItem::new, it -> humanoidArmor(it, ModArmorMaterials.REPLIKA, ArmorType.HELMET)).asDeferredItem();
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
                    output.accept(biomash);
                    output.accept(data);
                    output.accept(makeshiftPSU);
                    output.accept(ModBlocks.recycler);
                    output.accept(ModBlocks.assembler);
                    output.accept(ModBlocks.solarSink);
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
                    output.accept(ModBlocks.replikaWorkbench);
                    output.accept(oreVacuum);
                    output.accept(nullphaser);
                    output.accept(nightVisionGoggles);
                    output.accept(brightVisionGoggles);
                    output.accept(scubaHelmet);
                    output.accept(graviliftHarness);
                    output.accept(semisonicSpeeders);
                    output.accept(springBoots);
                    output.accept(stompers);
                    output.accept(slowphasers);
                    output.accept(magphasers);
                    output.accept(biosteel);
                    output.accept(biosteelHelmet);
                    output.accept(biosteelChestplate);
                    output.accept(biosteelLeggings);
                    output.accept(biosteelBoots);
                    output.accept(hazmatLining);
                    output.accept(hazmatHelmet);
                    output.accept(hazmatChestplate);
                    output.accept(hazmatLeggings);
                    output.accept(hazmatBoots);
                    output.accept(assemblyTicket);
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

}

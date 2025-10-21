package net.blay09.mods.replikaentropie.item;

import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.item.BalmItems;
import net.blay09.mods.replikaentropie.ReplikaEntropie;
import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.level.ItemLike;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static net.blay09.mods.balm.api.item.BalmItems.itemProperties;
import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class ModItems {
    public static DeferredObject<CreativeModeTab> creativeModeTab;

    public static Item damagedChipset;
    public static Item handheldAnalyzer;
    public static Item skyScraper;

    public static Item data;
    public static Item scrap;
    public static Item biomass;
    public static Item fragments;
    public static Item biomash;
    public static Item chipset;
    public static Item biosteel;
    public static Item biosteelHelmet;
    public static Item biosteelChestplate;
    public static Item biosteelLeggings;
    public static Item biosteelBoots;
    public static Item hazmatLining;
    public static Item hazmatHelmet;
    public static Item hazmatChestplate;
    public static Item hazmatLeggings;
    public static Item hazmatBoots;
    public static Item replikaSkin;
    public static Item replikaHelmetFrame;
    public static Item replikaChestplateFrame;
    public static Item replikaLeggingsFrame;
    public static Item replikaBootsFrame;
    public static Item replikaHelmet;
    public static Item replikaChestplate;
    public static Item replikaLeggings;
    public static Item replikaBoots;
    public static Item nightVisionGoggles;
    public static Item brightVisionGoggles;
    public static Item graviliftHarness;
    public static Item magphasers;
    public static Item nullphaser;
    public static Item burstDrill;
    public static Item oreVacuum;
    public static Item slowphasers;
    public static Item stompers;
    public static Item springBoots;
    public static Item semisonicSpeeders;
    public static Item assemblyTicket;

    public static Item fragmentalWaste;

    public static void initialize(BalmItems items) {
        items.registerItem((identifier) -> handheldAnalyzer = new HandheldAnalyzerItem(itemProperties(identifier).stacksTo(1)), id("handheld_analyzer"));
        items.registerItem((identifier) -> skyScraper = new SkyScraperItem(itemProperties(identifier).stacksTo(1)), id("sky_scraper"));
        items.registerItem((identifier) -> damagedChipset = new Item(itemProperties(identifier)), id("damaged_chipset"));
        items.registerItem((identifier) -> data = new DataItem(itemProperties(identifier).food(new FoodProperties.Builder().fast().alwaysEat().build())), id("data"));
        items.registerItem((identifier) -> scrap = new Item(itemProperties(identifier)), id("scrap"));
        items.registerItem((identifier) -> biomass = new Item(itemProperties(identifier)), id("biomass"));
        items.registerItem((identifier) -> fragments = new Item(itemProperties(identifier)), id("fragments"));
        items.registerItem((identifier) -> chipset = new Item(itemProperties(identifier)), id("chipset"));
        items.registerItem((identifier) -> biosteel = new Item(itemProperties(identifier)), id("biosteel"));
        items.registerItem((identifier) -> assemblyTicket = new AssemblyTicketItem(itemProperties(identifier)), id("assembly_ticket"));
        items.registerItem((identifier) -> biomash = new Item(itemProperties(identifier).food(new FoodProperties.Builder().nutrition(4).saturationMod(0.1f).build())), id("biomash"));

        items.registerItem((identifier) -> nightVisionGoggles = new ReplikaPieceArmorItem(GogglesArmorMaterial.INSTANCE, ArmorItem.Type.HELMET, itemProperties(identifier)), id("nightvision_goggles"));
        items.registerItem((identifier) -> brightVisionGoggles = new ReplikaPieceArmorItem(GogglesArmorMaterial.INSTANCE, ArmorItem.Type.HELMET, itemProperties(identifier)), id("brightvision_goggles"));

        items.registerItem((identifier) -> graviliftHarness = new ReplikaPieceArmorItem(ArmorItem.Type.CHESTPLATE, itemProperties(identifier).durability(-1)), id("gravilift_harness"));

        items.registerItem((identifier) -> magphasers = new ReplikaPieceArmorItem(ArmorItem.Type.BOOTS, itemProperties(identifier).durability(-1)), id("magphasers"));
        items.registerItem((identifier) -> nullphaser = new NullphaserItem(itemProperties(identifier).durability(-1)), id("nullphaser"));
        items.registerItem((identifier) -> burstDrill = new BurstDrillItem(itemProperties(identifier).durability(-1)), id("burst_drill"));
        items.registerItem((identifier) -> oreVacuum = new OreVacuumItem(itemProperties(identifier).durability(-1)), id("ore_vacuum"));
        items.registerItem((identifier) -> slowphasers = new ReplikaPieceArmorItem(ArmorItem.Type.BOOTS, itemProperties(identifier).durability(-1)), id("slowphasers"));
        items.registerItem((identifier) -> stompers = new ReplikaPieceArmorItem(ArmorItem.Type.BOOTS, itemProperties(identifier).durability(-1)), id("stompers"));
        items.registerItem((identifier) -> springBoots = new ReplikaPieceArmorItem(ArmorItem.Type.BOOTS, itemProperties(identifier).durability(-1)), id("spring_boots"));
        items.registerItem((identifier) -> semisonicSpeeders = new ReplikaPieceArmorItem(ArmorItem.Type.LEGGINGS, itemProperties(identifier).durability(-1)), id("semisonic_speeders"));

        items.registerItem((identifier) -> biosteelHelmet = new BiosteelArmorItem(ArmorItem.Type.HELMET, itemProperties(identifier)), id("biosteel_helmet"));
        items.registerItem((identifier) -> biosteelChestplate = new BiosteelArmorItem(ArmorItem.Type.CHESTPLATE, itemProperties(identifier)), id("biosteel_chestplate"));
        items.registerItem((identifier) -> biosteelLeggings = new BiosteelArmorItem(ArmorItem.Type.LEGGINGS, itemProperties(identifier)), id("biosteel_leggings"));
        items.registerItem((identifier) -> biosteelBoots = new BiosteelArmorItem(ArmorItem.Type.BOOTS, itemProperties(identifier)), id("biosteel_boots"));

        items.registerItem((identifier) -> hazmatLining = new Item(itemProperties(identifier)), id("hazmat_lining"));
        items.registerItem((identifier) -> hazmatHelmet = new HazmatArmorItem(ArmorItem.Type.HELMET, itemProperties(identifier)), id("hazmat_helmet"));
        items.registerItem((identifier) -> hazmatChestplate = new HazmatArmorItem(ArmorItem.Type.CHESTPLATE, itemProperties(identifier)), id("hazmat_chestplate"));
        items.registerItem((identifier) -> hazmatLeggings = new HazmatArmorItem(ArmorItem.Type.LEGGINGS, itemProperties(identifier)), id("hazmat_leggings"));
        items.registerItem((identifier) -> hazmatBoots = new HazmatArmorItem(ArmorItem.Type.BOOTS, itemProperties(identifier)), id("hazmat_boots"));

        items.registerItem((identifier) -> replikaSkin = new Item(itemProperties(identifier)), id("replika_skin"));
        items.registerItem((identifier) -> replikaHelmetFrame = new Item(itemProperties(identifier)), id("replika_helmet_frame"));
        items.registerItem((identifier) -> replikaChestplateFrame = new Item(itemProperties(identifier)), id("replika_chestplate_frame"));
        items.registerItem((identifier) -> replikaLeggingsFrame = new Item(itemProperties(identifier)), id("replika_leggings_frame"));
        items.registerItem((identifier) -> replikaBootsFrame = new Item(itemProperties(identifier)), id("replika_boots_frame"));

        items.registerItem((identifier) -> replikaHelmet = new ReplikaArmorItem(ArmorItem.Type.HELMET, itemProperties(identifier)), id("replika_helmet"));
        items.registerItem((identifier) -> replikaChestplate = new ReplikaArmorItem(ArmorItem.Type.CHESTPLATE, itemProperties(identifier)), id("replika_chestplate"));
        items.registerItem((identifier) -> replikaLeggings = new ReplikaArmorItem(ArmorItem.Type.LEGGINGS, itemProperties(identifier)), id("replika_leggings"));
        items.registerItem((identifier) -> replikaBoots = new ReplikaArmorItem(ArmorItem.Type.BOOTS, itemProperties(identifier)), id("replika_boots"));

        fragmentalWaste = ModBlocks.fragmentalWaste.asItem();
        creativeModeTab = items.registerCreativeModeTab(() -> new ItemStack(handheldAnalyzer), id(ReplikaEntropie.MOD_ID));

        items.setCreativeModeTabSorting(id(ReplikaEntropie.MOD_ID), new Comparator<>() {
            private static final String[] patternStrings = new String[]{
                    "damaged_chipset",
                    "chipset",
                    "sky_scraper",
                    "handheld_analyzer",
                    "scrap",
                    "biomass",
                    "fragments",
                    "biomash",
                    "data",
                    "recycler",
                    "assembler",
                    "fabricator",
                    "entropic_data_miner",
                    "cobblescrap",
                    "lavascrap",
                    "world_eater",
                    "biomass_incubator",
                    "biomass_harvester",
                    "defragmentizer",
                    "fragment_accelerator",
                    "chaos_engine",
                    "fragmental_waste",
                    "burst_drill",
                    "ore_vacuum",
                    "nullphaser",
                    "nightvision_goggles",
                    "brightvision_goggles",
                    "gravilift_harness",
                    "semisonic_speeders",
                    "spring_boots",
                    "stompers",
                    "slowphasers",
                    "magphasers",
                    "biosteel",
                    "biosteel_.+",
                    "hazmat_lining",
                    "hazmat_.+",
                    "replika_skin",
                    "replika_.+_frame",
                    "replika_.+",
                    "assembly_ticket",
            };

            private static final Map<String, Integer> indexMap = new HashMap<>();
            private static final Map<Pattern, Integer> patternIndexMap = new HashMap<>();

            static {
                for (int i = 0; i < patternStrings.length; i++) {
                    final var patternString = patternStrings[i];
                    indexMap.put(patternString, i);
                    patternIndexMap.put(Pattern.compile(patternString), i);
                }
            }

            private static int getIndex(String name) {
                final var index = indexMap.get(name);
                if (index != null) {
                    return index;
                }

                for (var entry : patternIndexMap.entrySet()) {
                    if (entry.getKey().matcher(name).matches()) {
                        return entry.getValue();
                    }
                }

                return -1;
            }

            @Override
            public int compare(ItemLike o1, ItemLike o2) {
                final var id1 = BuiltInRegistries.ITEM.getKey(o1.asItem());
                final var id2 = BuiltInRegistries.ITEM.getKey(o2.asItem());
                final var name1 = id1.getPath();
                final var name2 = id2.getPath();
                final var index1 = getIndex(name1);
                final var index2 = getIndex(name2);
                if (index1 != -1 && index2 != -1) {
                    return Integer.compare(index1, index2);
                } else if (index1 != -1) {
                    return -1;
                } else if (index2 != -1) {
                    return 1;
                }

                return name1.compareTo(name2);
            }
        });
    }

}

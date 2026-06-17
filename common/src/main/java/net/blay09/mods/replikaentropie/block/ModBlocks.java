package net.blay09.mods.replikaentropie.block;

import net.blay09.mods.balm.world.level.block.BalmBlockRegistrar;
import net.blay09.mods.balm.world.level.block.DeferredBlock;
import net.blay09.mods.replikaentropie.ReplikaEntropie;
import net.blay09.mods.replikaentropie.component.ItemDescription;
import net.blay09.mods.replikaentropie.component.ModDataComponents;
import net.blay09.mods.replikaentropie.item.FragmentalWasteItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

public class ModBlocks {

    public static DeferredBlock replikaWorkbench;
    public static DeferredBlock entropicDataMiner;
    public static DeferredBlock fabricator;
    public static DeferredBlock assembler;
    public static DeferredBlock wasteBarrel;
    public static DeferredBlock fragmentalWaste;
    public static DeferredBlock biomassIncubator;
    public static DeferredBlock biomassHarvester;
    public static DeferredBlock cobblescrap;
    public static DeferredBlock lavascrap;
    public static DeferredBlock worldEater;
    public static DeferredBlock fragmentalHeater;
    public static DeferredBlock fragmentAccelerator;
    public static DeferredBlock chaosEngine;
    public static DeferredBlock recycler;
    public static DeferredBlock digSpot;
    public static DeferredBlock waterSink;
    public static DeferredBlock lavaSink;
    public static DeferredBlock bluePrinter;
    public static DeferredBlock solarSink;
    public static DeferredBlock fragmentedSun;
    public static DeferredBlock entropicGenerator;
    public static DeferredBlock funnel;
    public static DeferredBlock crane;

    public static void initialize(BalmBlockRegistrar blocks) {
        blocks.enableBlockDescriptionPrefixForItems();

        replikaWorkbench = blocks.register("replika_workbench", ReplikaWorkbenchBlock::new, it -> it.mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL))
                .withDefaultItem(it -> withTooltip(it, "replika_workbench"))
                .asDeferredBlock();

        entropicDataMiner = blocks.register("entropic_data_miner", EntropicDataMinerBlock::new, it -> it.mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL))
                .withDefaultItem(it -> withTooltip(it, "entropic_data_miner"))
                .asDeferredBlock();

        fabricator = blocks.register("fabricator", FabricatorBlock::new, it -> it.mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL))
                .withDefaultItem(it -> withTooltip(it, "fabricator"))
                .asDeferredBlock();

        assembler = blocks.register("assembler", AssemblerBlock::new, it -> it.mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL))
                .withDefaultItem(it -> withTooltip(it, "assembler"))
                .asDeferredBlock();

        wasteBarrel = blocks.register("waste_barrel", WasteBarrelBlock::new, it -> it.mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL))
                .withDefaultItem()
                .asDeferredBlock();

        fragmentalWaste = blocks.register("fragmental_waste", FragmentalWasteBlock::new, it -> it.mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 0f).sound(SoundType.METAL))
                .withItem(FragmentalWasteItem::new, it -> withTooltip(it.stacksTo(1), "fragmental_waste"))
                .asDeferredBlock();

        biomassIncubator = blocks.register("biomass_incubator", BiomassIncubatorBlock::new, it -> it.mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL))
                .withDefaultItem(it -> withTooltip(it, "biomass_incubator"))
                .asDeferredBlock();

        biomassHarvester = blocks.register("biomass_harvester", BiomassHarvesterBlock::new, it -> it.mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL))
                .withDefaultItem(it -> withTooltip(it, "biomass_harvester"))
                .asDeferredBlock();

        cobblescrap = blocks.register("cobblescrap", CobblescrapBlock::new, it -> it.mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL))
                .withDefaultItem(it -> withTooltip(it, "cobblescrap"))
                .asDeferredBlock();

        lavascrap = blocks.register("lavascrap", LavaScrapBlock::new, it -> it.mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL))
                .withDefaultItem(it -> withTooltip(it, "lavascrap"))
                .asDeferredBlock();

        worldEater = blocks.register("world_eater", WorldEaterBlock::new, it -> it.mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL))
                .withDefaultItem(it -> withTooltip(it, "world_eater"))
                .asDeferredBlock();

        fragmentalHeater = blocks.register("fragmental_heater", FragmentalHeaterBlock::new, it -> it.mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL))
                .withDefaultItem(it -> withTooltip(it, "fragmental_heater"))
                .asDeferredBlock();

        fragmentAccelerator = blocks.register("fragment_accelerator", FragmentAcceleratorBlock::new, it -> it.mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL))
                .withDefaultItem(it -> withTooltip(it, "fragment_accelerator"))
                .asDeferredBlock();

        chaosEngine = blocks.register("chaos_engine", ChaosEngineBlock::new, it -> it.mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL))
                .withDefaultItem(it -> withTooltip(it, "chaos_engine"))
                .asDeferredBlock();

        recycler = blocks.register("recycler", RecyclerBlock::new, it -> it.mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL))
                .withDefaultItem(it -> withTooltip(it, "recycler"))
                .asDeferredBlock();

        waterSink = blocks.register("water_sink", WaterSinkBlock::new, it -> it.mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL))
                .withDefaultItem(it -> withTooltip(it, "water_sink"))
                .asDeferredBlock();

        lavaSink = blocks.register("lava_sink", LavaSinkBlock::new, it -> it.mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL))
                .withDefaultItem(it -> withTooltip(it, "lava_sink"))
                .asDeferredBlock();

        bluePrinter = blocks.register("blue_printer", BluePrinterBlock::new, it -> it.mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL))
                .withDefaultItem(it -> withTooltip(it, "blue_printer"))
                .asDeferredBlock();

        solarSink = blocks.register("solar_sink", SolarSinkBlock::new, it -> it.mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL))
                .withDefaultItem(it -> withTooltip(it, "solar_sink"))
                .asDeferredBlock();

        fragmentedSun = blocks.register("fragmented_sun", FragmentedSunBlock::new, it -> it.mapColor(MapColor.COLOR_YELLOW).instrument(NoteBlockInstrument.BELL).strength(0f, 0f).sound(SoundType.AMETHYST).lightLevel(_ -> 15))
                .withDefaultItem()
                .asDeferredBlock();

        funnel = blocks.register("funnel", FunnelBlock::new, it -> it.mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL))
                .withDefaultItem(it -> withTooltip(it, "funnel"))
                .asDeferredBlock();

        crane = blocks.register("crane", CraneBlock::new, it -> it.mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL))
                .withDefaultItem(it -> withTooltip(it, "crane"))
                .asDeferredBlock();

        digSpot = blocks.register("dig_spot", DigSpotBlock::new, it -> it)
                .withDefaultItem()
                .asDeferredBlock();
    }

    private static Item.Properties withTooltip(Item.Properties properties, String name) {
        return properties.component(ModDataComponents.itemDescription(), new ItemDescription("block." + ReplikaEntropie.MOD_ID + "." + name + ".tooltip"));
    }
}

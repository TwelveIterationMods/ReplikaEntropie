package net.blay09.mods.replikaentropie.block;

import net.blay09.mods.balm.world.level.block.BalmBlockRegistrar;
import net.blay09.mods.balm.world.level.block.DeferredBlock;
import net.blay09.mods.replikaentropie.item.FragmentalWasteItem;
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
    public static DeferredBlock fragmentalGenerator;
    public static DeferredBlock fragmentAccelerator;
    public static DeferredBlock chaosEngine;
    public static DeferredBlock recycler;
    public static DeferredBlock digSpot;
    public static DeferredBlock waterSink;
    public static DeferredBlock lavaSink;
    public static DeferredBlock bluePrinter;
    public static DeferredBlock solarSink;
    public static DeferredBlock entropicGenerator;

    public static void initialize(BalmBlockRegistrar blocks) {
        blocks.enableBlockDescriptionPrefixForItems();

        replikaWorkbench = blocks.register("replika_workbench", ReplikaWorkbenchBlock::new, it -> it.mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL))
                .withDefaultItem()
                .asDeferredBlock();

        entropicDataMiner = blocks.register("entropic_data_miner", EntropicDataMinerBlock::new, it -> it.mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL))
                .withDefaultItem()
                .asDeferredBlock();

        fabricator = blocks.register("fabricator", FabricatorBlock::new, it -> it.mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL))
                .withDefaultItem()
                .asDeferredBlock();

        assembler = blocks.register("assembler", AssemblerBlock::new, it -> it.mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL))
                .withDefaultItem()
                .asDeferredBlock();

        wasteBarrel = blocks.register("waste_barrel", WasteBarrelBlock::new, it -> it.mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL))
                .withDefaultItem()
                .asDeferredBlock();

        fragmentalWaste = blocks.register("fragmental_waste", FragmentalWasteBlock::new, it -> it.mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 0f).sound(SoundType.METAL))
                .withItem(FragmentalWasteItem::new, it -> it.stacksTo(1))
                .asDeferredBlock();

        biomassIncubator = blocks.register("biomass_incubator", BiomassIncubatorBlock::new, it -> it.mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL))
                .withDefaultItem()
                .asDeferredBlock();

        biomassHarvester = blocks.register("biomass_harvester", BiomassHarvesterBlock::new, it -> it.mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL))
                .withDefaultItem()
                .asDeferredBlock();

        cobblescrap = blocks.register("cobblescrap", CobblescrapBlock::new, it -> it.mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL))
                .withDefaultItem()
                .asDeferredBlock();

        lavascrap = blocks.register("lavascrap", LavaScrapBlock::new, it -> it.mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL))
                .withDefaultItem()
                .asDeferredBlock();

        worldEater = blocks.register("world_eater", WorldEaterBlock::new, it -> it.mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL))
                .withDefaultItem()
                .asDeferredBlock();

        fragmentalGenerator = blocks.register("fragmental_generator", FragmentalGeneratorBlock::new, it -> it.mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL))
                .withDefaultItem()
                .asDeferredBlock();

        fragmentAccelerator = blocks.register("fragment_accelerator", FragmentAcceleratorBlock::new, it -> it.mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL))
                .withDefaultItem()
                .asDeferredBlock();

        chaosEngine = blocks.register("chaos_engine", ChaosEngineBlock::new, it -> it.mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL))
                .withDefaultItem()
                .asDeferredBlock();

        recycler = blocks.register("recycler", RecyclerBlock::new, it -> it.mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL))
                .withDefaultItem()
                .asDeferredBlock();

        waterSink = blocks.register("water_sink", WaterSinkBlock::new, it -> it.mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL))
                .withDefaultItem()
                .asDeferredBlock();

        lavaSink = blocks.register("lava_sink", LavaSinkBlock::new, it -> it.mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL))
                .withDefaultItem()
                .asDeferredBlock();

        solarSink = blocks.register("solar_sink", SolarSinkBlock::new, it -> it.mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL))
                .withDefaultItem()
                .asDeferredBlock();

        digSpot = blocks.register("dig_spot", DigSpotBlock::new, it -> it)
                .withDefaultItem()
                .asDeferredBlock();
    }
}

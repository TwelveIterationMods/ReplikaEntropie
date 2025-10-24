package net.blay09.mods.replikaentropie.block;

import net.blay09.mods.balm.api.block.BalmBlocks;
import net.blay09.mods.balm.api.item.BalmItems;
import net.blay09.mods.replikaentropie.item.FragmentalWasteItem;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

import static net.blay09.mods.balm.api.block.BalmBlocks.blockProperties;
import static net.blay09.mods.balm.api.item.BalmItems.itemProperties;
import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

 public class ModBlocks {

    public static Block replikaWorkbench;
    public static Block entropicDataMiner;
    public static Block fabricator;
    public static Block assembler;
    public static Block fragmentalWaste;
    public static Block biomassIncubator;
    public static Block biomassHarvester;
    public static Block cobblescrap;
    public static Block lavascrap;
    public static Block worldEater;
    public static Block defragmentizer;
    public static Block fragmentAccelerator;
    public static Block chaosEngine;
    public static Block recycler;

    public static void initialize(BalmBlocks blocks) {
        blocks.register(
                (identifier) -> replikaWorkbench = new ReplikaWorkbenchBlock(blockProperties(identifier).mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL)),
                BalmItems::blockItem,
                id("replika_workbench"));

        blocks.register(
                (identifier) -> entropicDataMiner = new EntropicDataMinerBlock(blockProperties(identifier).mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL)),
                BalmItems::blockItem,
                id("entropic_data_miner"));

        blocks.register(
                (identifier) -> fabricator = new FabricatorBlock(blockProperties(identifier).mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL)),
                BalmItems::blockItem,
                id("fabricator"));

        blocks.register(
                (identifier) -> assembler = new AssemblerBlock(blockProperties(identifier).mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL)),
                BalmItems::blockItem,
                id("assembler"));

        blocks.register(
                (identifier) -> fragmentalWaste = new FragmentalWasteBlock(blockProperties(identifier).mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL)),
                (block, identifier) -> new FragmentalWasteItem(block, itemProperties(identifier)),
                id("fragmental_waste"));

        blocks.register(
                (identifier) -> biomassIncubator = new BiomassIncubatorBlock(blockProperties(identifier).mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL)),
                BalmItems::blockItem,
                id("biomass_incubator"));

        blocks.register(
                (identifier) -> biomassHarvester = new BiomassHarvesterBlock(blockProperties(identifier).mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL)),
                BalmItems::blockItem,
                id("biomass_harvester"));

        blocks.register(
                (identifier) -> cobblescrap = new CobblescrapBlock(blockProperties(identifier).mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL)),
                BalmItems::blockItem,
                id("cobblescrap"));

        blocks.register(
                (identifier) -> lavascrap = new LavaScrapBlock(blockProperties(identifier).mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL)),
                BalmItems::blockItem,
                id("lavascrap"));

        blocks.register(
                (identifier) -> worldEater = new WorldEaterBlock(blockProperties(identifier).mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL)),
                BalmItems::blockItem,
                id("world_eater"));

        blocks.register(
                (identifier) -> defragmentizer = new DefragmentizerBlock(blockProperties(identifier).mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL)),
                BalmItems::blockItem,
                id("defragmentizer"));

        blocks.register(
                (identifier) -> fragmentAccelerator = new FragmentAcceleratorBlock(blockProperties(identifier).mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL)),
                BalmItems::blockItem,
                id("fragment_accelerator"));

        blocks.register(
                (identifier) -> chaosEngine = new ChaosEngineBlock(blockProperties(identifier).mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL)),
                BalmItems::blockItem,
                id("chaos_engine"));

        blocks.register(
                (identifier) -> recycler = new RecyclerBlock(blockProperties(identifier).mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5f, 6f).sound(SoundType.METAL)),
                BalmItems::blockItem,
                id("recycler"));
    }
}

package net.blay09.mods.replikaentropie.block.entity;

import net.blay09.mods.balm.world.level.block.entity.BalmBlockEntityTypeRegistrar;
import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntities {
    public static Holder<BlockEntityType<ReplikaWorkbenchBlockEntity>> replikaWorkbench;
    public static Holder<BlockEntityType<FabricatorBlockEntity>> fabricator;
    public static Holder<BlockEntityType<AssemblerBlockEntity>> assembler;
    public static Holder<BlockEntityType<CobblescrapBlockEntity>> cobblescrap;
    public static Holder<BlockEntityType<LavascrapBlockEntity>> lavascrap;
    public static Holder<BlockEntityType<WorldEaterBlockEntity>> worldEater;
    public static Holder<BlockEntityType<FragmentAcceleratorBlockEntity>> fragmentAccelerator;
    public static Holder<BlockEntityType<DefragmentizerBlockEntity>> defragmentizer;
    public static Holder<BlockEntityType<BiomassHarvesterBlockEntity>> biomassHarvester;
    public static Holder<BlockEntityType<BiomassIncubatorBlockEntity>> biomassIncubator;
    public static Holder<BlockEntityType<FragmentalWasteBlockEntity>> fragmentalWaste;
    public static Holder<BlockEntityType<EntropicDataMinerBlockEntity>> entropicDataMiner;
    public static Holder<BlockEntityType<RecyclerBlockEntity>> recycler;
    public static Holder<BlockEntityType<ChaosEngineBlockEntity>> chaosEngine;

    public static void initialize(BalmBlockEntityTypeRegistrar blockEntities) {
        replikaWorkbench = blockEntities.register("replika_workbench",
                ReplikaWorkbenchBlockEntity::new,
                ModBlocks.replikaWorkbench).asHolder();

        fabricator = blockEntities.register("fabricator",
                FabricatorBlockEntity::new,
                ModBlocks.fabricator).asHolder();

        assembler = blockEntities.register("assembler",
                AssemblerBlockEntity::new,
                ModBlocks.assembler).asHolder();

        cobblescrap = blockEntities.register("cobblescrap",
                CobblescrapBlockEntity::new,
                ModBlocks.cobblescrap).asHolder();

        lavascrap = blockEntities.register("lavascrap",
                LavascrapBlockEntity::new,
                ModBlocks.lavascrap).asHolder();

        worldEater = blockEntities.register("world_eater",
                WorldEaterBlockEntity::new,
                ModBlocks.worldEater).asHolder();

        fragmentAccelerator = blockEntities.register("fragment_accelerator",
                FragmentAcceleratorBlockEntity::new,
                ModBlocks.fragmentAccelerator).asHolder();

        defragmentizer = blockEntities.register("defragmentizer",
                DefragmentizerBlockEntity::new,
                ModBlocks.defragmentizer).asHolder();

        biomassHarvester = blockEntities.register("biomass_harvester",
                BiomassHarvesterBlockEntity::new,
                ModBlocks.biomassHarvester).asHolder();

        biomassIncubator = blockEntities.register("biomass_incubator",
                BiomassIncubatorBlockEntity::new,
                ModBlocks.biomassIncubator).asHolder();

        fragmentalWaste = blockEntities.register("fragmental_waste",
                FragmentalWasteBlockEntity::new,
                ModBlocks.fragmentalWaste).asHolder();

        entropicDataMiner = blockEntities.register("entropic_data_miner",
                EntropicDataMinerBlockEntity::new,
                ModBlocks.entropicDataMiner).asHolder();

        recycler = blockEntities.register("recycler",
                RecyclerBlockEntity::new,
                ModBlocks.recycler).asHolder();

        chaosEngine = blockEntities.register("chaos_engine",
                ChaosEngineBlockEntity::new,
                ModBlocks.chaosEngine).asHolder();
    }
}


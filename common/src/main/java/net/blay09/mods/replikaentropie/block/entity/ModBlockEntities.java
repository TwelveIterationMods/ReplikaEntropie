package net.blay09.mods.replikaentropie.block.entity;

import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.block.BalmBlockEntities;
import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

 public class ModBlockEntities {
    public static DeferredObject<BlockEntityType<ReplikaWorkbenchBlockEntity>> replikaWorkbench;
    public static DeferredObject<BlockEntityType<FabricatorBlockEntity>> fabricator;
    public static DeferredObject<BlockEntityType<AssemblerBlockEntity>> assembler;
    public static DeferredObject<BlockEntityType<CobblescrapBlockEntity>> cobblescrap;
    public static DeferredObject<BlockEntityType<LavascrapBlockEntity>> lavascrap;
    public static DeferredObject<BlockEntityType<WorldEaterBlockEntity>> worldEater;
    public static DeferredObject<BlockEntityType<FragmentAcceleratorBlockEntity>> fragmentAccelerator;
    public static DeferredObject<BlockEntityType<DefragmentizerBlockEntity>> defragmentizer;
    public static DeferredObject<BlockEntityType<BiomassHarvesterBlockEntity>> biomassHarvester;
    public static DeferredObject<BlockEntityType<BiomassIncubatorBlockEntity>> biomassIncubator;
    public static DeferredObject<BlockEntityType<FragmentalWasteBlockEntity>> fragmentalWaste;
    public static DeferredObject<BlockEntityType<EntropicDataMinerBlockEntity>> entropicDataMiner;
    public static DeferredObject<BlockEntityType<RecyclerBlockEntity>> recycler;
    public static DeferredObject<BlockEntityType<ChaosEngineBlockEntity>> chaosEngine;

    public static void initialize(BalmBlockEntities blockEntities) {
        replikaWorkbench = blockEntities.registerBlockEntity(id("replika_workbench"),
                ReplikaWorkbenchBlockEntity::new,
                () -> new Block[]{ModBlocks.replikaWorkbench});

        fabricator = blockEntities.registerBlockEntity(id("fabricator"),
                FabricatorBlockEntity::new,
                () -> new Block[]{ModBlocks.fabricator});

        assembler = blockEntities.registerBlockEntity(id("assembler"),
                AssemblerBlockEntity::new,
                () -> new Block[]{ModBlocks.assembler});

        cobblescrap = blockEntities.registerBlockEntity(id("cobblescrap"),
                CobblescrapBlockEntity::new,
                () -> new Block[]{ModBlocks.cobblescrap});

        lavascrap = blockEntities.registerBlockEntity(id("lavascrap"),
                LavascrapBlockEntity::new,
                () -> new Block[]{ModBlocks.lavascrap});

        worldEater = blockEntities.registerBlockEntity(id("world_eater"),
                WorldEaterBlockEntity::new,
                () -> new Block[]{ModBlocks.worldEater});

        fragmentAccelerator = blockEntities.registerBlockEntity(id("fragment_accelerator"),
                FragmentAcceleratorBlockEntity::new,
                () -> new Block[]{ModBlocks.fragmentAccelerator});

        defragmentizer = blockEntities.registerBlockEntity(id("defragmentizer"),
                DefragmentizerBlockEntity::new,
                () -> new Block[]{ModBlocks.defragmentizer});

        biomassHarvester = blockEntities.registerBlockEntity(id("biomass_harvester"),
                BiomassHarvesterBlockEntity::new,
                () -> new Block[]{ModBlocks.biomassHarvester});

        biomassIncubator = blockEntities.registerBlockEntity(id("biomass_incubator"),
                BiomassIncubatorBlockEntity::new,
                () -> new Block[]{ModBlocks.biomassIncubator});

        fragmentalWaste = blockEntities.registerBlockEntity(id("fragmental_waste"),
                FragmentalWasteBlockEntity::new,
                () -> new Block[]{ModBlocks.fragmentalWaste});

        entropicDataMiner = blockEntities.registerBlockEntity(id("entropic_data_miner"),
                EntropicDataMinerBlockEntity::new,
                () -> new Block[]{ModBlocks.entropicDataMiner});

        recycler = blockEntities.registerBlockEntity(id("recycler"),
                RecyclerBlockEntity::new,
                () -> new Block[]{ModBlocks.recycler});

        chaosEngine = blockEntities.registerBlockEntity(id("chaos_engine"),
                ChaosEngineBlockEntity::new,
                () -> new Block[]{ModBlocks.chaosEngine});
    }
}


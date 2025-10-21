package net.blay09.mods.replikaentropie.worldgen;

import net.blay09.mods.balm.api.world.BalmWorldGen;
import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.village.poi.PoiType;

import java.util.HashSet;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class ModWorldGen {

    public static ResourceLocation ENTROPIC_DATA_MINER_POI = id("entropic_data_miner");

    public static void initialize(BalmWorldGen worldGen) {
        worldGen.registerPoiType(ENTROPIC_DATA_MINER_POI,
                () -> new PoiType(new HashSet<>(ModBlocks.entropicDataMiner.getStateDefinition().getPossibleStates()), 1, 1));
    }
}

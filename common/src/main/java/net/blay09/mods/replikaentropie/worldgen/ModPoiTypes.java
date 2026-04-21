package net.blay09.mods.replikaentropie.worldgen;

import net.blay09.mods.balm.world.entity.ai.village.poi.BalmPoiTypeRegistrar;
import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.village.poi.PoiType;

import java.util.Set;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class ModPoiTypes {

    public static Identifier ENTROPIC_DATA_MINER_POI = id("entropic_data_miner");

    public static void initialize(BalmPoiTypeRegistrar poiTypes) {
        poiTypes.register(ENTROPIC_DATA_MINER_POI.getPath(),
                () -> new PoiType(Set.copyOf(ModBlocks.entropicDataMiner.asBlock().getStateDefinition().getPossibleStates()), 1, 1));
    }
}

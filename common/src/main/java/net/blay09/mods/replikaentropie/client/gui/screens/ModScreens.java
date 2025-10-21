package net.blay09.mods.replikaentropie.client.gui.screens;

import net.blay09.mods.balm.api.client.screen.BalmScreens;
import net.blay09.mods.replikaentropie.client.gui.screens.inventory.*;
import net.blay09.mods.replikaentropie.menu.ModMenus;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class ModScreens {
    public static void initialize(BalmScreens screens) {
        screens.registerScreen(id("replika_workbench"), ModMenus.replikaWorkbench::get, ReplikaWorkbenchScreen::new);
        screens.registerScreen(id("research"), ModMenus.research::get, ResearchScreen::new);
        screens.registerScreen(id("fabricator"), ModMenus.fabricator::get, FabricatorScreen::new);
        screens.registerScreen(id("assembler"), ModMenus.assembler::get, AssemblerScreen::new);
        screens.registerScreen(id("cobblescrap"), ModMenus.cobblescrap::get, CobblescrapScreen::new);
        screens.registerScreen(id("lavascrap"), ModMenus.lavascrap::get, LavascrapScreen::new);
        screens.registerScreen(id("world_eater"), ModMenus.worldEater::get, WorldEaterScreen::new);
        screens.registerScreen(id("fragment_accelerator"), ModMenus.fragmentAccelerator::get, FragmentAcceleratorScreen::new);
        screens.registerScreen(id("defragmentizer"), ModMenus.defragmentizer::get, DefragmentizerScreen::new);
        screens.registerScreen(id("biomass_harvester"), ModMenus.biomassHarvester::get, BiomassHarvesterScreen::new);
        screens.registerScreen(id("biomass_incubator"), ModMenus.biomassIncubator::get, BiomassIncubatorScreen::new);
        screens.registerScreen(id("entropic_data_miner"), ModMenus.entropicDataMiner::get, EntropicDataMinerScreen::new);
        screens.registerScreen(id("recycler"), ModMenus.recycler::get, RecyclerScreen::new);
        screens.registerScreen(id("nonogram"), ModMenus.nonogram::get, NonogramScreen::new);
        screens.registerScreen(id("nonogram_editor"), ModMenus.nonogramEditor::get, NonogramScreen::new);
    }
}
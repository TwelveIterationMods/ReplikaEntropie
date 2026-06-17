package net.blay09.mods.replikaentropie.client.gui.screens;

import net.blay09.mods.balm.client.gui.screens.inventory.BalmMenuScreenRegistrar;
import net.blay09.mods.replikaentropie.client.gui.screens.inventory.*;
import net.blay09.mods.replikaentropie.menu.ModMenus;

public class ModScreens {
    public static void initialize(BalmMenuScreenRegistrar screens) {
        screens.register(ModMenus.replikaWorkbench, ReplikaWorkbenchScreen::new);
        screens.register(ModMenus.research, ResearchScreen::new);
        screens.register(ModMenus.fabricator, FabricatorScreen::new);
        screens.register(ModMenus.assembler, AssemblerScreen::new);
        screens.register(ModMenus.cobblescrap, CobblescrapScreen::new);
        screens.register(ModMenus.lavascrap, LavascrapScreen::new);
        screens.register(ModMenus.lavaSink, LavaSinkScreen::new);
        screens.register(ModMenus.worldEater, WorldEaterScreen::new);
        screens.register(ModMenus.fragmentAccelerator, FragmentAcceleratorScreen::new);
        screens.register(ModMenus.fragmentalHeater, FragmentalHeaterScreen::new);
        screens.register(ModMenus.biomassHarvester, BiomassHarvesterScreen::new);
        screens.register(ModMenus.biomassIncubator, BiomassIncubatorScreen::new);
        screens.register(ModMenus.entropicDataMiner, EntropicDataMinerScreen::new);
        screens.register(ModMenus.recycler, RecyclerScreen::new);
        screens.register(ModMenus.bluePrinter, BluePrinterScreen::new);
        screens.register(ModMenus.nonogram, NonogramScreen::new);
        screens.register(ModMenus.nonogramEditor, NonogramScreen::new);
    }
}

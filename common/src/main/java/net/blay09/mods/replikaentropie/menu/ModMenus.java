package net.blay09.mods.replikaentropie.menu;

import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.menu.BalmMenus;
import net.blay09.mods.replikaentropie.recipe.FabricatorRecipe;
import net.minecraft.world.inventory.MenuType;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class ModMenus {
    public static DeferredObject<MenuType<ReplikaWorkbenchMenu>> replikaWorkbench;
    public static DeferredObject<MenuType<ResearchMenu>> research;
    public static DeferredObject<MenuType<FabricatorMenu>> fabricator;
    public static DeferredObject<MenuType<AssemblerMenu>> assembler;
    public static DeferredObject<MenuType<CobblescrapMenu>> cobblescrap;
    public static DeferredObject<MenuType<LavascrapMenu>> lavascrap;
    public static DeferredObject<MenuType<WorldEaterMenu>> worldEater;
    public static DeferredObject<MenuType<FragmentAcceleratorMenu>> fragmentAccelerator;
    public static DeferredObject<MenuType<DefragmentizerMenu>> defragmentizer;
    public static DeferredObject<MenuType<BiomassHarvesterMenu>> biomassHarvester;
    public static DeferredObject<MenuType<BiomassIncubatorMenu>> biomassIncubator;
    public static DeferredObject<MenuType<EntropicDataMinerMenu>> entropicDataMiner;
    public static DeferredObject<MenuType<RecyclerMenu>> recycler;
    public static DeferredObject<MenuType<NonogramMenu>> nonogram;
    public static DeferredObject<MenuType<NonogramEditorMenu>> nonogramEditor;

    public static void initialize(BalmMenus menus) {
        replikaWorkbench = menus.registerMenu(id("replika_workbench"), (containerId, inventory, buf) -> new ReplikaWorkbenchMenu(containerId, inventory));
        research = menus.registerMenu(id("research"), (containerId, inventory, buf) -> new ResearchMenu(containerId, inventory, ResearchMenu.Data.read(buf)));
        fabricator = menus.registerMenu(id("fabricator"), (containerId, inventory, buf) -> new FabricatorMenu(containerId, inventory, FabricatorRecipe.getRecipes(inventory.player.level())));
        assembler = menus.registerMenu(id("assembler"), (containerId, inventory, buf) -> new AssemblerMenu(containerId, inventory));
        cobblescrap = menus.registerMenu(id("cobblescrap"), (containerId, inventory, buf) -> new CobblescrapMenu(containerId, inventory));
        lavascrap = menus.registerMenu(id("lavascrap"), (containerId, inventory, buf) -> new LavascrapMenu(containerId, inventory));
        worldEater = menus.registerMenu(id("world_eater"), (containerId, inventory, buf) -> new WorldEaterMenu(containerId, inventory));
        fragmentAccelerator = menus.registerMenu(id("fragment_accelerator"), (containerId, inventory, buf) -> new FragmentAcceleratorMenu(containerId, inventory));
        defragmentizer = menus.registerMenu(id("defragmentizer"), (containerId, inventory, buf) -> new DefragmentizerMenu(containerId, inventory));
        biomassHarvester = menus.registerMenu(id("biomass_harvester"), (containerId, inventory, buf) -> new BiomassHarvesterMenu(containerId, inventory));
        biomassIncubator = menus.registerMenu(id("biomass_incubator"), (containerId, inventory, buf) -> new BiomassIncubatorMenu(containerId, inventory));
        entropicDataMiner = menus.registerMenu(id("entropic_data_miner"), (containerId, inventory, buf) -> new EntropicDataMinerMenu(containerId, EntropicDataMinerMenu.Data.read(buf)));
        recycler = menus.registerMenu(id("recycler"), (containerId, inventory, buf) -> new RecyclerMenu(containerId, inventory));
        nonogram = menus.registerMenu(id("nonogram"), (containerId, inventory, buf) -> new NonogramMenu(containerId, inventory, NonogramMenu.Data.read(buf)));
        nonogramEditor = menus.registerMenu(id("nonogram_editor"), (containerId, inventory, buf) -> new NonogramEditorMenu(containerId, NonogramMenu.Data.read(buf)));
    }
}



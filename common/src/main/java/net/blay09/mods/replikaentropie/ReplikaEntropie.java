package net.blay09.mods.replikaentropie;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.core.BalmRegistrars;
import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.blay09.mods.replikaentropie.block.entity.ModBlockEntities;
import net.blay09.mods.replikaentropie.command.ReplikaEntropieCommand;
import net.blay09.mods.replikaentropie.component.ModDataComponents;
import net.blay09.mods.replikaentropie.core.abilities.*;
import net.blay09.mods.replikaentropie.core.analyzer.Analyzer;
import net.blay09.mods.replikaentropie.core.burst.BurstEnergy;
import net.blay09.mods.replikaentropie.core.dataminer.GlobalEventLog;
import net.blay09.mods.replikaentropie.core.dataminer.LocalEventLog;
import net.blay09.mods.replikaentropie.core.nonogram.NonogramLoader;
import net.blay09.mods.replikaentropie.core.waste.FragmentalWaste;
import net.blay09.mods.replikaentropie.effect.ModEffects;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.loot.ModLoot;
import net.blay09.mods.replikaentropie.menu.ModMenus;
import net.blay09.mods.replikaentropie.network.ModNetworking;
import net.blay09.mods.replikaentropie.recipe.ModRecipes;
import net.blay09.mods.replikaentropie.worldgen.ModPoiTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReplikaEntropie {

    // TODO a way to skip research, e.g. an Auto Decrypter Tool. Maybe a sidebar with all tools that can be dropped onto the nonogram.
    // TODO Sheep and Pig gave me no data?
    // TODO "Blue Printer" to print assembly tickets, needs paper, ink, and cyan dye
    // TODO Biomass Incubator should do a single crop, and do biomass as an optional secondary step
    // TODO World Eater should do scrap as an optional secondary step, so it can be used as a Quarry
    // TODO Would be nice to have some better visuals for the World Eater. It's not very obvious what blocks it would target right now.
    // TODO Change the Defragmentizer. Instead of being another convert redstone to fragment machine, make it convert fragments to energy.
    // TODO Maybe have the Entropic Data Miner create Data items instead of downloadable list items
    // TODO not the biggest fan of the Fabricator yet. It feels odd to turn Scrap into Hazmat Lining.
    // TODO Armor is not rendering
    // TODO Decontaminate Fragmental Waste barrels by placing them below a Water Sink (random tick)
    // TODO Add Fragmental Waste Minecarts so they can be run below Water Sinks for decontamination. Hook Dispenser and Hopper to allow input/output from cart.
    // TODO The Burst Drill sucks. Redesign it.
    // TODO Broken culling in Replika Workbench
    // TODO Replika Workbench is trash right now. Should be redesigned to be a block for overall upgrading and configuration of RE items.
    // TODO Introduce durability (energy-based) for all items. Burst still exists, and can be separately upgraded. Essentialy, durability recharges the burst, while burst determines use duration at a time.
    // TODO Stompers not working
    // TODO Remove Replika Frame

    public static final Logger logger = LoggerFactory.getLogger(ReplikaEntropie.class);

    public static final String MOD_ID = "replikaentropie";

    public static void initialize(BalmRegistrars registrars) {
        Balm.config().registerConfig(ReplikaEntropieConfig.class);

        registrars.blocks(ModBlocks::initialize);
        registrars.blockEntityTypes(ModBlockEntities::initialize);
        registrars.dataComponentTypes(ModDataComponents::initialize);
        registrars.items(ModItems::initialize);
        registrars.creativeModeTabs(ModItems::initialize);
        registrars.menuTypes(ModMenus::initialize);
        registrars.recipeTypes(ModRecipes::initialize);
        registrars.poiTypes(ModPoiTypes::initialize);
        registrars.registrar(Registries.MOB_EFFECT, ModEffects::initialize);

        ModNetworking.initialize(Balm.networking());
        ModLoot.initialize(Balm.lootModifiers());
        Balm.commands().register(ReplikaEntropieCommand::register);

        GlobalEventLog.initialize();
        LocalEventLog.initialize();
        Analyzer.initialize();
        FragmentalWaste.initialize();
        BurstEnergy.initialize();

        AbilityManager.initialize();
        MagphaseAbility.initialize();
        StompingAbility.initialize();

        registrars.resourceReloadListeners(registrar -> registrar.register("nonogram_loader", new NonogramLoader()));

        AbilityManager.registerAbility(MagphaseAbility.INSTANCE);
        AbilityManager.registerAbility(SlowphaseAbility.INSTANCE);
        AbilityManager.registerAbility(StompingAbility.INSTANCE);
        AbilityManager.registerAbility(NightVisionAbility.INSTANCE);
        AbilityManager.registerAbility(BrightVisionAbility.INSTANCE);
        AbilityManager.registerAbility(GraviliftAbility.INSTANCE);
        AbilityManager.registerAbility(JumpBoostAbility.INSTANCE);
        AbilityManager.registerAbility(SpeedBoostAbility.INSTANCE);
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

}

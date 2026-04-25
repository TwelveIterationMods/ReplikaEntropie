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

    // Rework Blocks
    // - World Eater should do have an intermediate output buffer, so it can be used as a Quarry
    // - World Eater should have some in-world visuals indicating the area it's eating
    // - Entropic Data Miner should create Data items instead of the downloadable list. No more tracking event log.
    // - Replika Workbench is trash right now. Should be redesigned to be a block for overall upgrading and configuration of RE items.

    // New Blocks
    // - A block that heats cobblestone into lava
    // - "Blue Printer" to print assembly tickets, needs paper, ink, and cyan dye
    // - Solar Sink as the first energy-providing upgrade, placed on top of machine
    // - Water Sink as an infinite water generator, rains down to fill machines below and decontaminate Fragmental Waste
    // - Fragmental Generator, generates power from fragments
    // - Entropic Generator
    // - I think we should have a Biofuel generator operating on refined biomass

    // New Items
    // - Introduce durability (energy-based) for all items. Burst still exists, and can be separately upgraded. Essentialy, durability recharges the burst, while burst determines use duration at a time.

    // New Entities
    // - Fragmental Waste Minecarts so they can be run below Water Sinks for decontamination.
    // - Hook Dispenser and Hopper to allow input/output of Fragmental Waste from cart.

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

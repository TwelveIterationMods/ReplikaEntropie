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

    // Highest Priority
    // - Entropic Data Miner should create Data items instead of the downloadable list. No more tracking event log.
    // - Replika Workbench is trash right now. Should be redesigned to be a block for overall upgrading and configuration of RE items.
    // - Fragmental Generator, generates power from fragments
    // - Introduce durability (energy-based) for all items. Burst still exists, and can be separately upgraded. Essentially, durability recharges the burst, while burst determines use duration at a time.

    // Lower Priority
    // - Lava Sink that heats cobblestone into lava, to complete the Cobblescrap -> Lavascrap -> Renewable Obsidian -> ??? -> Profit cycle
    // - "Blue Printer" to print custom assembly tickets (or duplicate them), needs paper, ink, and cyan dye
    // - Entropic Generator
    // - Fragmental Waste Minecarts so they can be run below Water Sinks for decontamination.
    // - Hook Dispenser and Hopper to allow input/output of Fragmental Waste from cart.
    // - Would be cool to have a Biofuel Generator path

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

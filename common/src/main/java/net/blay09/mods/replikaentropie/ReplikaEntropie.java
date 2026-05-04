package net.blay09.mods.replikaentropie;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.core.BalmRegistrars;
import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.blay09.mods.replikaentropie.block.entity.ModBlockEntities;
import net.blay09.mods.replikaentropie.command.ReplikaEntropieCommand;
import net.blay09.mods.replikaentropie.component.ModDataComponents;
import net.blay09.mods.replikaentropie.core.abilities.*;
import net.blay09.mods.replikaentropie.core.analyzer.Analyzer;
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

    // TODO Blue Printer has no recipe
    // TODO Fragmental Generator should have a tooltip on the Temperature Bar

    // TODO Handheld Analyzer item is too big in hand
    // TODO Recycler JEI slots are misplaced
    // TODO Assembler JEI is outdated
    // TODO Blue Printer model is ugly and broken
    // TODO Entropic Data Miner is spammed with the same events
    // TODO Biomass Incubator has no outputs
    // TODO Fragment Accelerator is a bit confusing right now - no output at all on low speeds?
    // TODO Ore Vacuum behaves badly on redstone because it gets lit
    // TODO Nullphaser should be able to be used to climb up a ledge
    // TODO Replika Suit needs a crafting recipe
    // TODO The part upgrades should get their graphics tweaked
    // TODO How to get Power from A to B?
    // TODO Waste Barrel Minecarts would be cool

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

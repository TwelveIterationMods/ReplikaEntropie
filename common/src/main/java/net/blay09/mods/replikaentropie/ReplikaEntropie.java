package net.blay09.mods.replikaentropie;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.BalmRegistries;
import net.blay09.mods.balm.api.block.BalmBlockEntities;
import net.blay09.mods.balm.api.block.BalmBlocks;
import net.blay09.mods.balm.api.command.BalmCommands;
import net.blay09.mods.balm.api.config.BalmConfig;
import net.blay09.mods.balm.api.event.BalmEvents;
import net.blay09.mods.balm.api.event.UseBlockEvent;
import net.blay09.mods.balm.api.item.BalmItems;
import net.blay09.mods.balm.api.loot.BalmLootTables;
import net.blay09.mods.balm.api.menu.BalmMenus;
import net.blay09.mods.balm.api.module.BalmModule;
import net.blay09.mods.balm.api.network.BalmNetworking;
import net.blay09.mods.balm.api.recipe.BalmRecipes;
import net.blay09.mods.balm.api.world.BalmWorldGen;
import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.blay09.mods.replikaentropie.block.entity.ModBlockEntities;
import net.blay09.mods.replikaentropie.command.ReplikaEntropieCommand;
import net.blay09.mods.replikaentropie.core.abilities.*;
import net.blay09.mods.replikaentropie.core.analyzer.Analyzer;
import net.blay09.mods.replikaentropie.core.burst.BurstEnergy;
import net.blay09.mods.replikaentropie.core.nonogram.NonogramLoader;
import net.blay09.mods.replikaentropie.core.dataminer.GlobalEventLog;
import net.blay09.mods.replikaentropie.core.dataminer.LocalEventLog;
import net.blay09.mods.replikaentropie.core.waste.FragmentalWaste;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.effect.ModEffects;
import net.blay09.mods.replikaentropie.loot.ModLoot;
import net.blay09.mods.replikaentropie.menu.ModMenus;
import net.blay09.mods.replikaentropie.network.ModNetworking;
import net.blay09.mods.replikaentropie.recipe.ModRecipes;
import net.blay09.mods.replikaentropie.worldgen.ModWorldGen;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReplikaEntropie implements BalmModule {

    public static final Logger logger = LoggerFactory.getLogger(ReplikaEntropie.class);

    public static final String MOD_ID = "replikaentropie";

    @Override
    public ResourceLocation getId() {
        return id("common");
    }

    @Override
    public void registerConfig(BalmConfig config) {
        config.registerConfig(ReplikaEntropieConfig.class);
    }

    @Override
    public void registerNetworking(BalmNetworking networking) {
        ModNetworking.initialize(networking);
    }

    @Override
    public void registerBlocks(BalmBlocks blocks) {
        ModBlocks.initialize(blocks);
    }

    @Override
    public void registerBlockEntities(BalmBlockEntities blockEntities) {
        ModBlockEntities.initialize(blockEntities);
    }

    @Override
    public void registerItems(BalmItems items) {
        ModItems.initialize(items);
    }

    @Override
    public void registerMenus(BalmMenus menus) {
        ModMenus.initialize(menus);
    }

    @Override
    public void registerWorldGen(BalmWorldGen worldGen) {
        ModWorldGen.initialize(worldGen);
    }

    @Override
    public void registerCommands(BalmCommands commands) {
        commands.register(ReplikaEntropieCommand::register);
    }

    @Override
    public void registerRecipes(BalmRecipes recipes) {
        ModRecipes.initialize(recipes);
    }

    @Override
    public void registerAdditional(BalmRegistries registries) {
        ModEffects.initialize(registries);
    }

    @Override
    public void registerLootTables(BalmLootTables lootTables) {
        ModLoot.initialize(lootTables);
    }

    @Override
    public void registerEvents(BalmEvents events) {
        GlobalEventLog.initialize(events);
        LocalEventLog.initialize(events);
        Analyzer.initialize(events);
        FragmentalWaste.initialize(events);
        BurstEnergy.initialize(events);
        
        AbilityManager.initialize(events);
        MagphaseAbility.initialize(events);
        StompingAbility.initialize(events);
    }

    @Override
    public void initialize() {
        Balm.addServerReloadListener(id("nonogram_loader"), new NonogramLoader());

        AbilityManager.registerAbility(MagphaseAbility.INSTANCE);
        AbilityManager.registerAbility(SlowphaseAbility.INSTANCE);
        AbilityManager.registerAbility(StompingAbility.INSTANCE);
        AbilityManager.registerAbility(NightVisionAbility.INSTANCE);
        AbilityManager.registerAbility(BrightVisionAbility.INSTANCE);
        AbilityManager.registerAbility(GraviliftAbility.INSTANCE);
        AbilityManager.registerAbility(JumpBoostAbility.INSTANCE);
        AbilityManager.registerAbility(SpeedBoostAbility.INSTANCE);
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

}

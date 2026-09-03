package net.blay09.mods.replikaentropie;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.core.BalmRegistrars;
import net.blay09.mods.balm.platform.event.callback.BlockCallback;
import net.blay09.mods.balm.platform.event.callback.InteractionEventResult;
import net.blay09.mods.balm.platform.event.callback.ItemCallback;
import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.blay09.mods.replikaentropie.block.entity.ModBlockEntities;
import net.blay09.mods.replikaentropie.command.ReplikaEntropieCommand;
import net.blay09.mods.replikaentropie.component.ModDataComponents;
import net.blay09.mods.replikaentropie.core.abilities.*;
import net.blay09.mods.replikaentropie.core.analyzer.Analyzer;
import net.blay09.mods.replikaentropie.core.crane.BlockCraneHandlers;
import net.blay09.mods.replikaentropie.core.crane.ContainerCraneHandlers;
import net.blay09.mods.replikaentropie.core.crane.MinecartCraneHandlers;
import net.blay09.mods.replikaentropie.core.dataminer.LocalEventLog;
import net.blay09.mods.replikaentropie.core.nonogram.NonogramLoader;
import net.blay09.mods.replikaentropie.core.waste.FragmentalWaste;
import net.blay09.mods.replikaentropie.effect.ModEffects;
import net.blay09.mods.replikaentropie.entity.ModEntities;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.loot.ModLoot;
import net.blay09.mods.replikaentropie.menu.ModMenus;
import net.blay09.mods.replikaentropie.network.ModNetworking;
import net.blay09.mods.replikaentropie.recipe.ModRecipes;
import net.blay09.mods.replikaentropie.registry.ModResearch;
import net.blay09.mods.replikaentropie.worldgen.ModPoiTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReplikaEntropie {

    public static final Logger logger = LoggerFactory.getLogger(ReplikaEntropie.class);

    public static final String MOD_ID = "replikaentropie";

    public static void initialize(BalmRegistrars registrars) {
        Balm.config().registerConfig(ReplikaEntropieConfig.class);

        registrars.dataComponentTypes(ModDataComponents::initialize);
        registrars.blocks(ModBlocks::initialize);
        registrars.entityTypes(ModEntities::initialize);
        registrars.blockEntityTypes(ModBlockEntities::initialize);
        registrars.items(ModItems::initialize);
        registrars.creativeModeTabs(ModItems::initialize);
        registrars.menuTypes(ModMenus::initialize);
        registrars.recipeTypes(ModRecipes::initialize);
        ModResearch.initialize(registrars.registrar());
        registrars.poiTypes(ModPoiTypes::initialize);
        registrars.registrar(Registries.MOB_EFFECT, ModEffects::initialize);

        ModNetworking.initialize(Balm.networking());
        ModLoot.initialize(Balm.lootModifiers());
        Balm.commands().register(ReplikaEntropieCommand::register);

        LocalEventLog.initialize();
        Analyzer.initialize();
        ContainerCraneHandlers.initialize();
        MinecartCraneHandlers.initialize();
        BlockCraneHandlers.initialize();
        FragmentalWaste.initialize();
        AbilityManager.initialize();
        MagphaseAbility.initialize();
        StompingAbility.initialize();

        registrars.resourceReloadListeners(registrar -> registrar.register("nonogram_loader", new NonogramLoader()));

        ItemCallback.Tooltip.EVENT.register((itemStack, tooltip, flags) -> {
            final var itemDescription = itemStack.get(ModDataComponents.itemDescription());
            if (itemDescription != null) {
                itemDescription.addToTooltip(Item.TooltipContext.EMPTY, tooltip::add, flags, itemStack);
            }
        });

        AbilityManager.registerAbility(MagphaseAbility.INSTANCE);
        AbilityManager.registerAbility(SlowphaseAbility.INSTANCE);
        AbilityManager.registerAbility(StompingAbility.INSTANCE);
        AbilityManager.registerAbility(NightVisionAbility.INSTANCE);
        AbilityManager.registerAbility(BrightVisionAbility.INSTANCE);
        AbilityManager.registerAbility(GraviliftAbility.INSTANCE);
        AbilityManager.registerAbility(JumpBoostAbility.INSTANCE);
        AbilityManager.registerAbility(SpeedBoostAbility.INSTANCE);

        // Redstone ore hijacks use interactions, so we hijack them back for the Vacuum Ore
        BlockCallback.Use.EVENT.register((player, level, hand, hitResult) -> {
            if (player.getMainHandItem().is(ModItems.oreVacuum.asItem())
                    && hitResult instanceof BlockHitResult blockHitResult
                    && level.getBlockState(blockHitResult.getBlockPos()).is(Blocks.REDSTONE_ORE)) {
                player.startUsingItem(hand);
                return InteractionEventResult.SUCCESS;
            }
            return InteractionEventResult.DEFAULT;
        });
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

}

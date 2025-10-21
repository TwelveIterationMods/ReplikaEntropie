package net.blay09.mods.replikaentropie.client;

import net.blay09.mods.balm.api.client.keymappings.BalmKeyMappings;
import net.blay09.mods.balm.api.client.module.BalmClientModule;
import net.blay09.mods.balm.api.client.rendering.BalmRenderers;
import net.blay09.mods.balm.api.client.screen.BalmScreens;
import net.blay09.mods.balm.api.event.BalmEvents;
import net.blay09.mods.balm.api.event.TickPhase;
import net.blay09.mods.balm.api.event.TickType;
import net.blay09.mods.replikaentropie.ReplikaEntropie;
import net.blay09.mods.replikaentropie.client.gui.components.BurstEnergyBarRenderer;
import net.blay09.mods.replikaentropie.client.handler.ClientDataNotifications;
import net.blay09.mods.replikaentropie.client.handler.HandheldAnalyzerClient;
import net.blay09.mods.replikaentropie.client.gui.screens.ModScreens;
import net.blay09.mods.replikaentropie.core.abilities.AbilityManager;
import net.blay09.mods.replikaentropie.core.abilities.MagphaseAbility;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

public class ReplikaEntropieClient implements BalmClientModule {
    @Override
    public ResourceLocation getId() {
        return ReplikaEntropie.id("client");
    }

    @Override
    public void registerKeyMappings(BalmKeyMappings keyMappings) {
        ModKeyMappings.initialize();
    }

    @Override
    public void registerScreens(BalmScreens screens) {
        ModScreens.initialize(screens);
    }

    @Override
    public void registerEvents(BalmEvents events) {
        HandheldAnalyzerClient.initialize(events);
        ClientDataNotifications.initialize(events);
        BurstEnergyBarRenderer.initialize(events);

        events.onTickEvent(TickType.ClientLevel, TickPhase.Start, level -> {
            MagphaseAbility.resetMagphasedPositions(level);

            final var player = Minecraft.getInstance().player;
            if (player != null) {
                AbilityManager.clientTick(player);
            }
        });
    }

    @Override
    public void registerRenderers(BalmRenderers renderers) {
        ModRenderers.initialize(renderers);
    }
}

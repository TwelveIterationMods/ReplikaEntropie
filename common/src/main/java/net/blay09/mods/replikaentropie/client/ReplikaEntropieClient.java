package net.blay09.mods.replikaentropie.client;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.client.BalmClientRegistrars;
import net.blay09.mods.balm.client.platform.event.callback.ClientTickCallback;
import net.blay09.mods.replikaentropie.ReplikaEntropie;
import net.blay09.mods.replikaentropie.client.gui.components.BurstEnergyBarRenderer;
import net.blay09.mods.replikaentropie.client.gui.screens.ModScreens;
import net.blay09.mods.replikaentropie.client.handler.ClientDataNotifications;
import net.blay09.mods.replikaentropie.client.handler.HandheldAnalyzerClient;
import net.blay09.mods.replikaentropie.compat.recipeviewers.ReplikaEntropieRecipeViewerProvider;
import net.blay09.mods.replikaentropie.core.abilities.AbilityManager;
import net.blay09.mods.replikaentropie.core.abilities.MagphaseAbility;
import net.minecraft.client.Minecraft;

public class ReplikaEntropieClient {

    public static void initialize(BalmClientRegistrars registrars) {
        ModKeyMappings.initialize();

        registrars.menuScreens(ModScreens::initialize);
        registrars.blockEntityRenderers(ModRenderers::initialize);
        registrars.blockColors(ModRenderers::initialize);

        HandheldAnalyzerClient.initialize();
        ClientDataNotifications.initialize();
        BurstEnergyBarRenderer.initialize();

        Balm.modSupport().recipeViewers().register(ReplikaEntropie.id("recipes"), new ReplikaEntropieRecipeViewerProvider());

        ClientTickCallback.ClientLevelTick.BEFORE.register(level -> {
            MagphaseAbility.resetMagphasedPositions(level);

            final var player = Minecraft.getInstance().player;
            if (player != null) {
                AbilityManager.clientTick(player);
            }
        });
    }
}

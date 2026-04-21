package net.blay09.mods.sortables.neoforge.client;

import net.blay09.mods.balm.client.BalmClient;
import net.blay09.mods.balm.neoforge.platform.runtime.NeoForgeLoadContext;
import net.blay09.mods.sortables.Sortables;
import net.blay09.mods.sortables.client.SortablesClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(value = ReplikaEntropie.MOD_ID, dist = Dist.CLIENT)
public class NeoForgeReplikaEntropieClient {

    public NeoForgeReplikaEntropieClient(ModContainer modContainer, IEventBus modEventBus) {
        final var context = new NeoForgeLoadContext(modContainer, modEventBus);
        BalmClient.initializeMod(ReplikaEntropie.MOD_ID, context, SortablesClient::initialize);
    }
}

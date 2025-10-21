package net.blay09.mods.replikaentropie.fabric.client;

import net.blay09.mods.balm.api.EmptyLoadContext;
import net.blay09.mods.balm.api.client.BalmClient;
import net.fabricmc.api.ClientModInitializer;
import net.blay09.mods.replikaentropie.ReplikaEntropie;
import net.blay09.mods.replikaentropie.client.ReplikaEntropieClient;

public class FabricReplikaEntropieClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BalmClient.initializeMod(ReplikaEntropie.MOD_ID, EmptyLoadContext.INSTANCE, new ReplikaEntropieClient());
    }
}

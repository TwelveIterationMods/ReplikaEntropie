package net.blay09.mods.replikaentropie.fabric.client;

import net.blay09.mods.balm.client.BalmClient;
import net.blay09.mods.balm.fabric.platform.runtime.FabricLoadContext;
import net.fabricmc.api.ClientModInitializer;
import net.blay09.mods.replikaentropie.ReplikaEntropie;
import net.blay09.mods.replikaentropie.client.ReplikaEntropieClient;

public class FabricReplikaEntropieClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BalmClient.initializeMod(ReplikaEntropie.MOD_ID, FabricLoadContext.INSTANCE, ReplikaEntropieClient::initialize);
    }
}

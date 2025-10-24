package net.blay09.mods.replikaentropie.forge.client;

import net.blay09.mods.balm.api.client.BalmClient;
import net.blay09.mods.replikaentropie.client.ReplikaEntropieClient;

public class ForgeReplikaEntropieClient {

    public static void initialize() {
        BalmClient.registerModule(new ReplikaEntropieClient());
    }

}

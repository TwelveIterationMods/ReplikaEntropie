package net.blay09.mods.replikaentropie.forge.client;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.replikaentropie.ReplikaEntropie;

public class ForgeReplikaEntropieClient {

    public static void initialize() {
        Balm.registerModule(new ReplikaEntropie());
    }

}

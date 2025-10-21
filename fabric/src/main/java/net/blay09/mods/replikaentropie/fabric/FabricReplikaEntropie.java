package net.blay09.mods.replikaentropie.fabric;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.EmptyLoadContext;
import net.fabricmc.api.ModInitializer;
import net.blay09.mods.replikaentropie.ReplikaEntropie;

public class FabricReplikaEntropie implements ModInitializer {
    @Override
    public void onInitialize() {
        Balm.initializeMod(ReplikaEntropie.MOD_ID, EmptyLoadContext.INSTANCE, new ReplikaEntropie());
    }
}

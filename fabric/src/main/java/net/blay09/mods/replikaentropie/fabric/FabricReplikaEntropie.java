package net.blay09.mods.replikaentropie.fabric;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.fabric.platform.runtime.FabricLoadContext;
import net.fabricmc.api.ModInitializer;
import net.blay09.mods.replikaentropie.ReplikaEntropie;

public class FabricReplikaEntropie implements ModInitializer {
    @Override
    public void onInitialize() {
        Balm.initializeMod(ReplikaEntropie.MOD_ID, FabricLoadContext.INSTANCE, ReplikaEntropie::initialize);
    }
}

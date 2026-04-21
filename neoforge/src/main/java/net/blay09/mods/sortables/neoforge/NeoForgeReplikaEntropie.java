package net.blay09.mods.sortables.neoforge;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.neoforge.platform.runtime.NeoForgeLoadContext;
import net.blay09.mods.replikaentropie.ReplikaEntropie;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(ReplikaEntropie.MOD_ID)
public class NeoForgeReplikaEntropie {
    public NeoForgeReplikaEntropie(ModContainer modContainer, IEventBus modEventBus) {
        final var context = new NeoForgeLoadContext(modContainer, modEventBus);
        Balm.initializeMod(ReplikaEntropie.MOD_ID, context, ReplikaEntropie::initialize);
    }
}

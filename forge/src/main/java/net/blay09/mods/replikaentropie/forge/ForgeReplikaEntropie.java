package net.blay09.mods.replikaentropie.forge;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.client.BalmClient;
import net.blay09.mods.balm.forge.ForgeLoadContext;
import net.blay09.mods.replikaentropie.ReplikaEntropie;
import net.blay09.mods.replikaentropie.forge.client.ForgeReplikaEntropieClient;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(ReplikaEntropie.MOD_ID)
public class ForgeReplikaEntropie {

    public ForgeReplikaEntropie(FMLJavaModLoadingContext context) {
        final var loadContext = new ForgeLoadContext(context.getModEventBus());
        Balm.initializeMod(ReplikaEntropie.MOD_ID, loadContext, new ReplikaEntropie());
        if (FMLEnvironment.dist.isClient()) {
            BalmClient.initializeMod(ReplikaEntropie.MOD_ID, loadContext, ForgeReplikaEntropieClient::initialize);
        }
    }

}

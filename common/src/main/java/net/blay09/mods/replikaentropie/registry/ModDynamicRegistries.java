package net.blay09.mods.replikaentropie.registry;

import net.blay09.mods.balm.core.BalmRegistrar;
import net.blay09.mods.replikaentropie.ReplikaEntropie;
import net.blay09.mods.replikaentropie.core.nonogram.Nonogram;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;

public class ModDynamicRegistries {
    public static final ResourceKey<Registry<Research>> RESEARCH = ResourceKey.createRegistryKey(ReplikaEntropie.id("research"));
    public static final ResourceKey<Registry<Nonogram>> NONOGRAM = ResourceKey.createRegistryKey(ReplikaEntropie.id("nonogram"));

    public static void initialize(BalmRegistrar registrar) {
        registrar.createDynamicRegistry(RESEARCH, Research.CODEC, builder -> builder.sync().skipSyncWhenEmpty());
        registrar.createDynamicRegistry(NONOGRAM, Nonogram.CODEC, builder -> builder.sync().skipSyncWhenEmpty());
    }

    public static Registry<Research> research(RegistryAccess registryAccess) {
        return registryAccess.lookupOrThrow(RESEARCH);
    }

    public static Registry<Nonogram> nonograms(RegistryAccess registryAccess) {
        return registryAccess.lookupOrThrow(NONOGRAM);
    }
}

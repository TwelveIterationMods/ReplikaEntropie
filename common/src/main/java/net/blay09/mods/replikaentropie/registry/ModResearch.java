package net.blay09.mods.replikaentropie.registry;

import net.blay09.mods.balm.core.BalmRegistrar;
import net.blay09.mods.replikaentropie.ReplikaEntropie;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;

public class ModResearch {
    public static final ResourceKey<Registry<Research>> REGISTRY_KEY = ResourceKey.createRegistryKey(ReplikaEntropie.id("research"));

    public static void initialize(BalmRegistrar registrar) {
        registrar.createDynamicRegistry(REGISTRY_KEY, Research.CODEC, builder -> builder.sync().skipSyncWhenEmpty());
    }

    public static Registry<Research> registry(RegistryAccess registryAccess) {
        return registryAccess.lookupOrThrow(REGISTRY_KEY);
    }
}

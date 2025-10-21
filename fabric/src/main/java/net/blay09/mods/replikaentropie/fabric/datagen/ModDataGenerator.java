package net.blay09.mods.replikaentropie.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class ModDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(ModBlockTagProvider::new);
        pack.addProvider(ModItemTagProvider::new);
        pack.addProvider(ModBlockLootTableProvider::new);
        pack.addProvider(ModModelProvider::new);
        pack.addProvider(ModRecipeProvider::new);
        pack.addProvider(ModRecycleRecipeProvider::new);
        pack.addProvider(ModBiomassIncubatorRecipeProvider::new);
        pack.addProvider(ModVacuumableOreRecipeProvider::new);
        pack.addProvider(ModFabricatorRecipeProvider::new);
        pack.addProvider(ModAssemblerRecipeProvider::new);
        pack.addProvider(ModResearchRecipeProvider::new);
    }
}

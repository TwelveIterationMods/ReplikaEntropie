package net.blay09.mods.replikaentropie.client;

import net.blay09.mods.balm.client.renderer.block.model.BalmBlockStateModelRegistrar;
import net.blay09.mods.balm.client.renderer.block.model.DeferredBlockStateModel;
import net.blay09.mods.replikaentropie.ReplikaEntropie;

public class ModBlockStateModels {
    public static DeferredBlockStateModel craneArm;
    public static DeferredBlockStateModel craneMagnet;

    public static void initialize(BalmBlockStateModelRegistrar registrar) {
        craneArm = registrar.register(ReplikaEntropie.id("block/crane_arm"));
        craneMagnet = registrar.register(ReplikaEntropie.id("block/crane_magnet"));
    }
}

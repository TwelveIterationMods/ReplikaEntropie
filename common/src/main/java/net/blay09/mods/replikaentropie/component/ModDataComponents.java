package net.blay09.mods.replikaentropie.component;

import net.blay09.mods.balm.core.component.BalmDataComponentTypeRegistrar;
import net.blay09.mods.balm.core.component.BalmDataComponentTypeRegistration;
import net.minecraft.core.component.DataComponentType;

public class ModDataComponents {
    private static BalmDataComponentTypeRegistration<AssemblyTicket> assemblyTicket;
    private static BalmDataComponentTypeRegistration<ReplikaParts> replikaArmor;

    public static void initialize(BalmDataComponentTypeRegistrar registrar) {
        assemblyTicket = registrar.register("assembly_ticket", AssemblyTicket.CODEC, AssemblyTicket.STREAM_CODEC);
        replikaArmor = registrar.register("replika_parts", ReplikaParts.CODEC, ReplikaParts.STREAM_CODEC);
    }

    public static DataComponentType<AssemblyTicket> assemblyTicket() {
        return assemblyTicket.asHolder().value();
    }

    public static DataComponentType<ReplikaParts> replikaParts() {
        return replikaArmor.asHolder().value();
    }
}

package net.blay09.mods.replikaentropie.component;

import net.blay09.mods.balm.core.component.BalmDataComponentTypeRegistrar;
import net.blay09.mods.balm.core.component.BalmDataComponentTypeRegistration;
import net.minecraft.core.component.DataComponentType;

public class ModDataComponents {
    private static BalmDataComponentTypeRegistration<AssemblyTicket> assemblyTicket;
    private static BalmDataComponentTypeRegistration<ReplikaParts> replikaArmor;
    private static BalmDataComponentTypeRegistration<AbilityHolder> abilityHolder;
    private static BalmDataComponentTypeRegistration<ItemDescription> itemDescription;

    public static void initialize(BalmDataComponentTypeRegistrar registrar) {
        assemblyTicket = registrar.register("assembly_ticket", AssemblyTicket.CODEC, AssemblyTicket.STREAM_CODEC);
        replikaArmor = registrar.register("replika_parts", ReplikaParts.CODEC, ReplikaParts.STREAM_CODEC);
        abilityHolder = registrar.register("ability_holder", AbilityHolder.CODEC, AbilityHolder.STREAM_CODEC);
        itemDescription = registrar.register("item_description", ItemDescription.CODEC, ItemDescription.STREAM_CODEC);
    }

    public static DataComponentType<AssemblyTicket> assemblyTicket() {
        return assemblyTicket.asHolder().value();
    }

    public static DataComponentType<ReplikaParts> replikaParts() {
        return replikaArmor.asHolder().value();
    }

    public static DataComponentType<AbilityHolder> abilityHolder() {
        return abilityHolder.asHolder().value();
    }

    public static DataComponentType<ItemDescription> itemDescription() {
        return itemDescription.asHolder().value();
    }
}

package net.blay09.mods.replikaentropie.entity;

import net.blay09.mods.balm.world.entity.BalmEntityTypeRegistrar;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class ModEntities {

    public static Holder<EntityType<WasteBarrelMinecart>> wasteBarrelMinecart;
    public static Holder<EntityType<FragmentalWasteMinecart>> fragmentalWasteMinecart;

    public static void initialize(BalmEntityTypeRegistrar entities) {
        wasteBarrelMinecart = entities.register("waste_barrel_minecart",
                () -> EntityType.Builder.<WasteBarrelMinecart>of(WasteBarrelMinecart::new, MobCategory.MISC)
                        .sized(0.98f, 0.7f)
                        .clientTrackingRange(8)
                        .updateInterval(3))
                .asHolder();

        fragmentalWasteMinecart = entities.register("fragmental_waste_minecart",
                () -> EntityType.Builder.<FragmentalWasteMinecart>of(FragmentalWasteMinecart::new, MobCategory.MISC)
                        .sized(0.98f, 0.7f)
                        .clientTrackingRange(8)
                        .updateInterval(3))
                .asHolder();
    }

}

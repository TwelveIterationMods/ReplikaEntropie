package net.blay09.mods.replikaentropie.item;

import net.blay09.mods.replikaentropie.tag.ModItemTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class BiosteelArmorItem extends Item {

    public static final int REPAIR_TICK_INTERVAL = 100;
    public static final int REPAIR_STEP = 1;

    public BiosteelArmorItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, ServerLevel level, Entity owner, @Nullable EquipmentSlot slot) {
        if (itemStack.isDamaged()) {
            if (owner.tickCount % REPAIR_TICK_INTERVAL == 0) {
                itemStack.setDamageValue(itemStack.getDamageValue() - REPAIR_STEP);
            }
        }
    }
}

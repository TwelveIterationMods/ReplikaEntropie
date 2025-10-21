package net.blay09.mods.replikaentropie.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BiosteelArmorItem extends ArmorItem {

    public static final int REPAIR_TICK_INTERVAL = 100;
    public static final int REPAIR_STEP = 1;

    public BiosteelArmorItem(Type type, Properties properties) {
        super(BiosteelArmorMaterial.INSTANCE, type, properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (stack.isDamaged()) {
            if (entity.tickCount % REPAIR_TICK_INTERVAL == 0) {
                stack.setDamageValue(stack.getDamageValue() - REPAIR_STEP);
            }
        }
    }
}

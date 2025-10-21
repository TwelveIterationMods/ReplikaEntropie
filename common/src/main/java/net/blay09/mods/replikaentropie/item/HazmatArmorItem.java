package net.blay09.mods.replikaentropie.item;

import net.blay09.mods.replikaentropie.tag.ModItemTags;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class HazmatArmorItem extends ArmorItem {
    public HazmatArmorItem(Type type, Properties properties) {
        super(HazmatArmorMaterial.INSTANCE, type, properties);
    }

    public static boolean tryProtect(LivingEntity entity) {
        if (wearsFullSuit(entity)) {
            damageSuitPart(entity, EquipmentSlot.HEAD, 1);
            damageSuitPart(entity, EquipmentSlot.CHEST, 1);
            damageSuitPart(entity, EquipmentSlot.LEGS, 1);
            damageSuitPart(entity, EquipmentSlot.FEET, 1);
            return true;
        }

        return false;
    }

    public static boolean wearsFullSuit(LivingEntity entity) {
        return entity.getItemBySlot(EquipmentSlot.HEAD).is(ModItemTags.PROTECTS_FROM_FRAGMENTAL_WASTE)
                && entity.getItemBySlot(EquipmentSlot.CHEST).is(ModItemTags.PROTECTS_FROM_FRAGMENTAL_WASTE)
                && entity.getItemBySlot(EquipmentSlot.LEGS).is(ModItemTags.PROTECTS_FROM_FRAGMENTAL_WASTE)
                && entity.getItemBySlot(EquipmentSlot.FEET).is(ModItemTags.PROTECTS_FROM_FRAGMENTAL_WASTE);
    }

    private static void damageSuitPart(LivingEntity entity, EquipmentSlot slot, int damage) {
        final var itemStack = entity.getItemBySlot(slot);
        if (itemStack.isDamageableItem() && itemStack.is(ModItemTags.PROTECTS_FROM_FRAGMENTAL_WASTE)) {
            itemStack.hurtAndBreak(damage, entity, e -> e.broadcastBreakEvent(slot));
        }
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }
}

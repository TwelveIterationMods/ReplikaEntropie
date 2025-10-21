package net.blay09.mods.replikaentropie.item;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.crafting.Ingredient;

public class BiosteelArmorMaterial implements ArmorMaterial {

    public static final BiosteelArmorMaterial INSTANCE = new BiosteelArmorMaterial();

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        return ArmorMaterials.IRON.getDurabilityForType(type);
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return ArmorMaterials.IRON.getDefenseForType(type);
    }

    @Override
    public int getEnchantmentValue() {
        return 0;
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_IRON;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(ModItems.biosteel);
    }

    @Override
    public String getName() {
        return "replikaentropie_biosteel";
    }

    @Override
    public float getToughness() {
        return ArmorMaterials.IRON.getToughness();
    }

    @Override
    public float getKnockbackResistance() {
        return ArmorMaterials.IRON.getKnockbackResistance();
    }
}

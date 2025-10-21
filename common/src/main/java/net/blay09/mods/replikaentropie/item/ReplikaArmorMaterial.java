package net.blay09.mods.replikaentropie.item;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.crafting.Ingredient;

public class ReplikaArmorMaterial implements ArmorMaterial {

    public static final ReplikaArmorMaterial INSTANCE = new ReplikaArmorMaterial();

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        return ArmorMaterials.DIAMOND.getDurabilityForType(type);
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return ArmorMaterials.DIAMOND.getDefenseForType(type);
    }

    @Override
    public int getEnchantmentValue() {
        return 0;
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_CHAIN;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.EMPTY;
    }

    @Override
    public String getName() {
        return "replikaentropie_replika";
    }

    @Override
    public float getToughness() {
        return ArmorMaterials.DIAMOND.getToughness();
    }

    @Override
    public float getKnockbackResistance() {
        return ArmorMaterials.DIAMOND.getKnockbackResistance();
    }
}

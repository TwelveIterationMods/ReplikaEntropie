package net.blay09.mods.replikaentropie.item;

import net.blay09.mods.replikaentropie.tag.ModItemTags;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.equipment.EquipmentAssets;

import java.util.Collections;

public class ModArmorMaterials {
    public static final ArmorMaterial BIOSTEEL = new ArmorMaterial(
            ArmorMaterials.IRON.durability(),
            ArmorMaterials.IRON.defense(),
            0,
            ArmorMaterials.IRON.equipSound(),
            ArmorMaterials.IRON.toughness(),
            ArmorMaterials.IRON.knockbackResistance(),
            ModItemTags.REPAIRS_BIOSTEEL_ARMOR,
            EquipmentAssets.createId("replikaentropie_biosteel")
    );

    public static final ArmorMaterial REPLIKA = new ArmorMaterial(
            ArmorMaterials.DIAMOND.durability(),
            ArmorMaterials.DIAMOND.defense(),
            0,
            ArmorMaterials.CHAINMAIL.equipSound(),
            ArmorMaterials.DIAMOND.toughness(),
            ArmorMaterials.DIAMOND.knockbackResistance(),
            ModItemTags.REPAIRS_REPLIKA_ARMOR,
            EquipmentAssets.createId("replikaentropie_replika")
    );

    public static final ArmorMaterial GOGGLES = new ArmorMaterial(
            REPLIKA.durability(),
            REPLIKA.defense(),
            0,
            REPLIKA.equipSound(),
            REPLIKA.toughness(),
            REPLIKA.knockbackResistance(),
            ModItemTags.REPAIRS_REPLIKA_ARMOR,
            EquipmentAssets.createId("replikaentropie_goggles")
    );

    public static final ArmorMaterial HAZMAT = new ArmorMaterial(
            33 * 8,
            ArmorMaterials.LEATHER.defense(),
            0,
            ArmorMaterials.LEATHER.equipSound(),
            ArmorMaterials.LEATHER.toughness(),
            ArmorMaterials.LEATHER.knockbackResistance(),
            ModItemTags.REPAIRS_HAZMAT_ARMOR,
            EquipmentAssets.createId("replikaentropie_hazmat")
    );

}

package net.blay09.mods.replikaentropie.item;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ReplikaPieceArmorItem extends ArmorItem {

    public ReplikaPieceArmorItem(Type type, Properties properties) {
        this(ReplikaArmorMaterial.INSTANCE, type, properties);
    }

    public ReplikaPieceArmorItem(ArmorMaterial material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

}

package net.blay09.mods.replikaentropie.item;

import net.blay09.mods.replikaentropie.core.replika.ReplikaArmor;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ReplikaArmorItem extends ArmorItem {

    public ReplikaArmorItem(Type type, Properties properties) {
        super(ReplikaArmorMaterial.INSTANCE, type, properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        final var parts = ReplikaArmor.getParts(itemStack);
        final var showParts = true; // POSTJAM hook to sneaking
        if (parts.isEmpty()) {
            tooltip.add(Component.translatable("item.replikaentropie.replika_armor.empty").withStyle(ChatFormatting.GRAY));
        } else if (showParts) {
            for (final var partItemStack : parts) {
                if (!partItemStack.isEmpty()) {
                    tooltip.add(Component.translatable("item.replikaentropie.replika_armor.part", partItemStack.getHoverName()));
                }
            }
        } else {
            tooltip.add(Component.translatable("item.replikaentropie.replika_armor.parts", parts.size()).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }
}

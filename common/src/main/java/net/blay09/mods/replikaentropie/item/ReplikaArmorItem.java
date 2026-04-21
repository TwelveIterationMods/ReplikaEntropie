package net.blay09.mods.replikaentropie.item;

import net.blay09.mods.replikaentropie.core.replika.ReplikaArmor;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class ReplikaArmorItem extends Item {

    public ReplikaArmorItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        final var parts = ReplikaArmor.getParts(itemStack);
        final var showParts = true; // POSTJAM hook to sneaking
        if (parts.isEmpty()) {
            builder.accept(Component.translatable("item.replikaentropie.replika_armor.empty").withStyle(ChatFormatting.GRAY));
        } else if (showParts) {
            for (final var partItemStack : parts) {
                if (!partItemStack.isEmpty()) {
                    builder.accept(Component.translatable("item.replikaentropie.replika_armor.part", partItemStack.getHoverName()));
                }
            }
        } else {
            builder.accept(Component.translatable("item.replikaentropie.replika_armor.parts", parts.size()).withStyle(ChatFormatting.GRAY));
        }
    }
}

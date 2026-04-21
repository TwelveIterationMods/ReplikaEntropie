package net.blay09.mods.replikaentropie.item;

import net.blay09.mods.replikaentropie.component.AssemblyTicket;
import net.blay09.mods.replikaentropie.component.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class AssemblyTicketItem extends Item {
    public AssemblyTicketItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        final var ticketData = itemStack.get(ModDataComponents.assemblyTicket());

        if (ticketData != null && ticketData.showHint()) {
            builder.accept(Component.translatable("item.replikaentropie.assembly_ticket.tooltip.hint").withStyle(ChatFormatting.GRAY));
        } else {
            if (ticketData == null || ticketData.recipeId().isEmpty()) {
                builder.accept(Component.translatable("item.replikaentropie.assembly_ticket.tooltip.empty").withStyle(ChatFormatting.RED));
            }
        }

        final var usesLeft = ticketData != null ? ticketData.usesLeft() : 0;
        if (usesLeft > 1) {
            builder.accept(Component.translatable("item.replikaentropie.assembly_ticket.tooltip.uses_left", usesLeft).withStyle(ChatFormatting.GRAY));
        }
    }

    public static ItemStack create(Component title, Identifier recipeId, int uses) {
        final var itemStack = ModItems.assemblyTicket.createStack();
        itemStack.set(DataComponents.CUSTOM_NAME, title);
        itemStack.set(ModDataComponents.assemblyTicket(), new AssemblyTicket(recipeId, uses));
        return itemStack;
    }
}

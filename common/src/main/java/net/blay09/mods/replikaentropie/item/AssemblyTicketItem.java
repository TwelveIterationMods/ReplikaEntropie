package net.blay09.mods.replikaentropie.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AssemblyTicketItem extends Item {
    public AssemblyTicketItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(itemStack, level, tooltip, flag);

        final var itemData = itemStack.getTag();

        final var hintId = itemData != null && itemData.getBoolean("ReplikaEntropieShowHint");
        if (hintId) {
            tooltip.add(Component.translatable("item.replikaentropie.assembly_ticket.tooltip.hint").withStyle(ChatFormatting.GRAY));
        } else {
            final var recipeId = itemData != null ? ResourceLocation.tryParse(itemData.getString("ReplikaEntropieAssemblerResult")) : null;
            if (recipeId == null) {
                tooltip.add(Component.translatable("item.replikaentropie.assembly_ticket.tooltip.empty").withStyle(ChatFormatting.RED));
            }
        }

        final var usesLeft = itemData != null ? itemData.getInt("ReplikaEntropieAssemblerUsesLeft") : 0;
        if (usesLeft > 1) {
            tooltip.add(Component.translatable("item.replikaentropie.assembly_ticket.tooltip.uses_left", usesLeft).withStyle(ChatFormatting.GRAY));
        }
    }

    public static ItemStack create(Component title, ResourceLocation recipeId, int uses) {
        final var itemStack = new ItemStack(ModItems.assemblyTicket);
        itemStack.setHoverName(title);
        final var itemData = itemStack.getOrCreateTag();
        itemData.putString("ReplikaEntropieAssemblerResult", recipeId.toString());
        itemData.putInt("ReplikaEntropieAssemblerUsesLeft", uses);
        return itemStack;
    }
}

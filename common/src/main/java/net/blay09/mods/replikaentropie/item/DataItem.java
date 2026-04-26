package net.blay09.mods.replikaentropie.item;

import net.blay09.mods.replikaentropie.component.ModDataComponents;
import net.blay09.mods.replikaentropie.core.analyzer.Analyzer;
import net.blay09.mods.replikaentropie.core.dataminer.DataMinedEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class DataItem extends Item {
    public DataItem(Properties properties) {
        super(properties);
    }

    public static ItemStack create(DataMinedEvent event) {
        final var itemStack = ModItems.data.createStack();
        itemStack.set(ModDataComponents.dataMinedEvent(), event);
        return itemStack;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity) {
        if (livingEntity instanceof ServerPlayer player) {
            final var minedEvent = itemStack.get(ModDataComponents.dataMinedEvent());
            final var amount = minedEvent != null ? minedEvent.dataMined() : level.getRandom().nextInt(1, 10);
            Analyzer.getManager(player).grantData(player, amount);
        }
        return super.finishUsingItem(itemStack, level, livingEntity);
    }

    @Override
    public Component getName(ItemStack itemStack) {
        final var minedEvent = itemStack.get(ModDataComponents.dataMinedEvent());
        if (minedEvent != null) {
            return Component.translatable("item.replikaentropie.data.mined", minedEvent.getDisplayName());
        }
        return super.getName(itemStack);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        final var minedEvent = itemStack.get(ModDataComponents.dataMinedEvent());
        if (minedEvent != null) {
            builder.accept(minedEvent.getDisplayName().copy().withStyle(ChatFormatting.GRAY));
            builder.accept(Component.translatable("item.replikaentropie.data.tooltip.value", minedEvent.dataMined()).withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}

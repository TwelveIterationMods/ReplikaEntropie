package net.blay09.mods.replikaentropie.component;

import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import java.util.function.Consumer;

public record ItemDescription(String translationKey) implements TooltipProvider {
    public static final Codec<ItemDescription> CODEC = Codec.STRING.xmap(ItemDescription::new, ItemDescription::translationKey);
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemDescription> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.<RegistryFriendlyByteBuf>cast()
            .map(ItemDescription::new, ItemDescription::translationKey);

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> builder, TooltipFlag tooltipFlag, DataComponentGetter components) {
        builder.accept(Component.translatable(translationKey).withStyle(ChatFormatting.GRAY));
    }
}

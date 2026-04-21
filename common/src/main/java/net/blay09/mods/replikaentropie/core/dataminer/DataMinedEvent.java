package net.blay09.mods.replikaentropie.core.dataminer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.entity.monster.skeleton.WitherSkeleton;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.IntFunction;

public record DataMinedEvent(Type type, long timestamp, int dataMined, @Nullable String variant, ItemStack icon,
                             @Nullable Component label) {
    public static final Codec<DataMinedEvent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Type.CODEC.optionalFieldOf("type", Type.UNKNOWN).forGetter(DataMinedEvent::type),
            Codec.LONG.optionalFieldOf("timestamp", 0L).forGetter(DataMinedEvent::timestamp),
            Codec.INT.optionalFieldOf("dataMined", 0).forGetter(DataMinedEvent::dataMined),
            Codec.STRING.optionalFieldOf("variant").forGetter(event -> Optional.ofNullable(event.variant)),
            ItemStack.OPTIONAL_CODEC.optionalFieldOf("icon", ItemStack.EMPTY).forGetter(DataMinedEvent::icon),
            ComponentSerialization.CODEC.optionalFieldOf("label").forGetter(event -> Optional.ofNullable(event.label))
    ).apply(instance, DataMinedEvent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, DataMinedEvent> STREAM_CODEC = StreamCodec.composite(
            Type.STREAM_CODEC,
            DataMinedEvent::type,
            ByteBufCodecs.LONG,
            DataMinedEvent::timestamp,
            ByteBufCodecs.VAR_INT,
            DataMinedEvent::dataMined,
            ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8).map(optional -> optional.orElse(null), Optional::ofNullable),
            DataMinedEvent::variant,
            ItemStack.OPTIONAL_STREAM_CODEC,
            DataMinedEvent::icon,
            ComponentSerialization.OPTIONAL_STREAM_CODEC.map(optional -> optional.orElse(null), Optional::ofNullable),
            DataMinedEvent::label,
            DataMinedEvent::new
    );

    private DataMinedEvent(Type type, long timestamp, int dataMined, Optional<String> variant, ItemStack icon, Optional<Component> label) {
        this(type, timestamp, dataMined, variant.orElse(null), icon, label.orElse(null));
    }

    public static DataMinedEvent of(Type type, String variant, ItemStack icon) {
        return new DataMinedEvent(type, System.currentTimeMillis(), type.getDefaultDataMined(), variant, icon, icon.getHoverName());
    }

    public static DataMinedEvent of(Type type, String variant, ItemStack icon, Component label) {
        return new DataMinedEvent(type, System.currentTimeMillis(), type.getDefaultDataMined(), variant, icon, label);
    }

    public static DataMinedEvent ofEntity(Type type, Entity entity) {
        final var icon = DataMinedEvent.createEntityIcon(entity);
        final var variant = DataMinedEvent.getEntityVariant(entity);
        return of(type, variant, icon, entity.getName());
    }

    public static DataMinedEvent ofItem(Type type, ItemStack itemStack) {
        final var variant = BuiltInRegistries.ITEM.getKey(itemStack.getItem()).toString();
        return of(type, variant, itemStack.copy(), itemStack.getHoverName());
    }

    public String asKey() {
        return type().ordinal() + ":" + (variant() == null ? "" : variant());
    }

    public static ItemStack createPlayerIcon(Player player) {
        final var itemStack = new ItemStack(Items.PLAYER_HEAD);
        final var name = player.getGameProfile().name();
        itemStack.set(DataComponents.PROFILE, ResolvableProfile.createUnresolved(name));
        return itemStack;
    }

    public static String getEntityVariant(Entity entity) {
        if (entity instanceof Player player) {
            return player.getGameProfile().name();
        } else {
            final var entityTypeId = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
            return entityTypeId.toString();
        }
    }

    public static ItemStack createEntityIcon(Entity entity) {
        if (entity instanceof Player player) {
            return createPlayerIcon(player);
        } else if (entity instanceof Zombie) {
            return new ItemStack(Items.ZOMBIE_HEAD);
        } else if (entity instanceof Creeper) {
            return new ItemStack(Items.CREEPER_HEAD);
        } else if (entity instanceof EnderDragon) {
            return new ItemStack(Items.DRAGON_HEAD);
        } else if (entity instanceof Piglin) {
            return new ItemStack(Items.PIGLIN_HEAD);
        } else if (entity instanceof Skeleton) {
            return new ItemStack(Items.SKELETON_SKULL);
        } else if (entity instanceof WitherSkeleton) {
            return new ItemStack(Items.WITHER_SKELETON_SKULL);
        } else {
            final var pickResult = entity.getPickResult();
            if (pickResult != null && !pickResult.isEmpty()) {
                return pickResult;
            } else {
                return new ItemStack(Items.ZOMBIE_HEAD);
            }
        }
    }

    public enum Type {
        UNKNOWN(0),
        ITEM_TOSSED(1),
        DIMENSION_CHANGED(1),
        RESPAWNED(1),
        DAMAGE_TAKEN(1),
        ITEM_CRAFTED(1),
        BLOCK_USED(1),
        BLOCK_PLACED(1), // POSTJAM
        FALLEN(1),
        CROP_GROWN(1),
        ITEM_USED(1),
        HEALTH_REGENERATED(1), // POSTJAM
        BLOCK_BROKEN(1),
        PLAYER_DIED(1), // POSTJAM not working
        ENTITY_DIED(1),
        ENTITY_SPAWNED(1),
        PLAYER_JOINED(1),
        PLAYER_LEFT(1),
        CHAOS(1),
        JUMP(1),
        TWERK(1),
        SNEAK(1),
        CHAT(1); // POSTJAM

        public static final IntFunction<Type> BY_ID = ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        public static final Codec<Type> CODEC = Codec.INT.xmap(BY_ID::apply, Type::ordinal);
        public static final StreamCodec<RegistryFriendlyByteBuf, Type> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Type::ordinal).cast();

        private final int defaultDataMined;

        Type(int defaultDataMined) {
            this.defaultDataMined = defaultDataMined;
        }

        public int getDefaultDataMined() {
            return defaultDataMined;
        }
    }
}

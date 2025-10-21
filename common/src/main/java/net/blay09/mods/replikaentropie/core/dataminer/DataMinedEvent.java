package net.blay09.mods.replikaentropie.core.dataminer;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ByIdMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PlayerHeadItem;
import org.jetbrains.annotations.Nullable;

import java.util.function.IntFunction;

public record DataMinedEvent(Type type, long timestamp, int dataMined, @Nullable String variant, ItemStack icon,
                             @Nullable Component label) {

    public static DataMinedEvent of(Type type, String variant, ItemStack icon) {
        return new DataMinedEvent(type, System.currentTimeMillis(), type.getDefaultDataMined(), variant, icon, icon.getHoverName());
    }

    public static DataMinedEvent of(Type type, String variant, ItemStack icon, Component label) {
        return new DataMinedEvent(type, System.currentTimeMillis(), type.getDefaultDataMined(), variant, icon, label);
    }

    public static DataMinedEvent of(CompoundTag compoundTag) {
        final var type = Type.BY_ID.apply(compoundTag.getInt("type"));
        final var timestamp = compoundTag.getLong("timestamp");
        final var dataMined = compoundTag.getInt("dataMined");
        final var variant = compoundTag.getString("variant");
        final var icon = compoundTag.contains("icon") ? ItemStack.of(compoundTag.getCompound("icon")) : ItemStack.EMPTY;
        final var label = compoundTag.contains("label") ? Component.Serializer.fromJson(compoundTag.getString("label")) : null;
        return new DataMinedEvent(type, timestamp, dataMined, variant, icon, label);
    }

    public static DataMinedEvent read(FriendlyByteBuf buf) {
        final var type = buf.readEnum(DataMinedEvent.Type.class);
        final var timestamp = buf.readLong();
        final var dataMined = buf.readVarInt();
        final var variant = buf.readNullable(FriendlyByteBuf::readUtf);
        final var icon = buf.readItem();
        final var label = buf.readNullable(FriendlyByteBuf::readComponent);
        return new DataMinedEvent(type, timestamp, dataMined, variant, icon, label);
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

    public void write(FriendlyByteBuf buf) {
        buf.writeEnum(type);
        buf.writeLong(timestamp);
        buf.writeVarInt(dataMined);
        buf.writeNullable(variant, FriendlyByteBuf::writeUtf);
        buf.writeItem(icon);
        buf.writeNullable(label, FriendlyByteBuf::writeComponent);
    }

    public CompoundTag save(CompoundTag compoundTag) {
        compoundTag.putInt("type", type.ordinal());
        compoundTag.putLong("timestamp", timestamp);
        compoundTag.putInt("dataMined", dataMined);
        if (variant != null) {
            compoundTag.putString("variant", variant);
        }
        if (!icon.isEmpty()) {
            compoundTag.put("icon", icon.save(new CompoundTag()));
        }
        if (label != null) {
            compoundTag.putString("label", Component.Serializer.toJson(label));
        }
        return compoundTag;
    }

    public String asKey() {
        return type().ordinal() + ":" + (variant() == null ? "" : variant());
    }

    public static ItemStack createPlayerIcon(Player player) {
        final var itemStack = new ItemStack(Items.PLAYER_HEAD);
        final var name = player.getGameProfile().getName();
        itemStack.getOrCreateTag().putString(PlayerHeadItem.TAG_SKULL_OWNER, name);
        return itemStack;
    }

    public static String getEntityVariant(Entity entity) {
        if (entity instanceof Player player) {
            return player.getGameProfile().getName();
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

        private final int defaultDataMined;

        Type(int defaultDataMined) {
            this.defaultDataMined = defaultDataMined;
        }

        public int getDefaultDataMined() {
            return defaultDataMined;
        }
    }
}

package net.blay09.mods.replikaentropie.block.entity;

import net.blay09.mods.balm.platform.energy.BalmEnergyStorageProvider;
import net.blay09.mods.balm.platform.energy.DefaultEnergyStorage;
import net.blay09.mods.balm.platform.energy.EnergyStorage;
import net.blay09.mods.balm.world.BalmContainerProvider;
import net.blay09.mods.balm.world.BalmMenuProvider;
import net.blay09.mods.balm.world.DefaultContainer;
import net.blay09.mods.balm.world.SubContainer;
import net.blay09.mods.balm.world.level.block.entity.BalmBlockEntityUtils;
import net.blay09.mods.replikaentropie.menu.BiomassHarvesterMenu;
import net.blay09.mods.replikaentropie.tag.ModEntityTypeTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class BiomassHarvesterBlockEntity extends BlockEntity implements BalmContainerProvider, BalmMenuProvider<Unit>, BalmEnergyStorageProvider {

    public static final int CONTAINER_SIZE = 4;

    private static final int WARNING_TICKS = 60;
    private static final int SLAUGHTER_TICKS = 60;
    private static final int COOLDOWN_TICKS = 60;
    private static final int ENERGY_CAPACITY = 10000;
    private static final int ENERGY_INPUT_RATE = 1000;
    private static final int ENERGY_COST_PER_TICK = 10;

    private static final float ATTACK_RANGE = 1f;
    private static final int ATTACK_INTERVAL_TICKS = 20;

    private static final float PULL_BASE_STRENGTH = 0.08f;
    private static final float PULL_MIN_DISTANCE = 1.5f;
    private static final float PULL_MAX_DISTANCE = 10f;
    private static final float PULL_MAX_SPEED = 0.6f;

    public enum State {IDLE, WARNING, SLAUGHTERING, COOLING}

    private final DefaultContainer backingContainer = new DefaultContainer(CONTAINER_SIZE) {
        @Override
        public void setChanged() {
            BiomassHarvesterBlockEntity.this.setChanged();
            isSyncDirty = true;
        }

        @Override
        public boolean canPlaceItem(int slot, ItemStack itemStack) {
            return weaponsContainer.containsOuterSlot(slot) && isValidHarvesterTool(itemStack);
        }
    };

    private final SubContainer weaponsContainer = new SubContainer(backingContainer, 0, 4);
    private final DefaultEnergyStorage energyStorage = new DefaultEnergyStorage(0, ENERGY_CAPACITY, ENERGY_INPUT_RATE, 0) {
        @Override
        public void setChanged() {
            BiomassHarvesterBlockEntity.this.setChanged();
            isSyncDirty = true;
        }
    };

    private State state = State.IDLE;
    private int stateTicks;

    private boolean isSyncDirty;

    private float clientPrevSpinAngleDeg;
    private float clientSpinAngleDeg;
    private float clientSpinSpeedDegPerSec;

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case BiomassHarvesterMenu.DATA_CURRENT_POWER -> energyStorage.getEnergy();
                case BiomassHarvesterMenu.DATA_MAX_POWER -> energyStorage.getCapacity();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return BiomassHarvesterMenu.DATA_COUNT;
        }
    };

    public BiomassHarvesterBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.biomassHarvester.value(), pos, blockState);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.replikaentropie.biomass_harvester");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new BiomassHarvesterMenu(containerId, inventory, backingContainer, dataAccess, ContainerLevelAccess.create(level, worldPosition));
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Unit> getScreenStreamCodec() {
        return Unit.STREAM_CODEC.cast();
    }

    @Override
    public Unit getScreenOpeningData(ServerPlayer player) {
        return Unit.INSTANCE;
    }

    @Override
    public Container getContainer() {
        return backingContainer;
    }

    @Override
    public EnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    @Override
    public EnergyStorage getEnergyStorage(Direction side) {
        return energyStorage;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, BiomassHarvesterBlockEntity blockEntity) {
        blockEntity.broadcastChanges();
        blockEntity.processState();
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, BiomassHarvesterBlockEntity blockEntity) {
        if (blockEntity.state == State.SLAUGHTERING) {
            blockEntity.pullNearbyEntities();
        }
        blockEntity.updateClientAnimation();
    }

    private void broadcastChanges() {
        if (isSyncDirty) {
            BalmBlockEntityUtils.sync(this);
            isSyncDirty = false;
        }
    }

    private void transition(State state) {
        this.state = state;
        stateTicks = 0;
        setChanged();
        BalmBlockEntityUtils.sync(this);
    }

    private void processState() {
        switch (state) {
            case WARNING -> {
                if (!consumeEnergy()) {
                    transition(State.COOLING);
                    return;
                }
                stateTicks++;
                if (stateTicks >= WARNING_TICKS) {
                    transition(State.SLAUGHTERING);
                }
            }
            case SLAUGHTERING -> {
                if (!hasAnyWeapon()) {
                    transition(State.IDLE);
                    return;
                }

                if (!consumeEnergy()) {
                    transition(State.COOLING);
                    return;
                }

                stateTicks++;
                pullNearbyEntities();

                if (stateTicks % ATTACK_INTERVAL_TICKS == 0) {
                    attackNearbyEntities();
                }

                if (stateTicks >= SLAUGHTER_TICKS) {
                    transition(State.COOLING);
                }
            }
            case COOLING -> {
                if (!consumeEnergy()) {
                    return;
                }
                stateTicks++;
                if (stateTicks >= COOLDOWN_TICKS) {
                    transition(State.IDLE);
                }
            }
            default -> {
                if (hasAnyWeapon()) {
                    transition(State.WARNING);
                }
            }
        }
    }

    private boolean consumeEnergy() {
        if (energyStorage.getEnergy() < ENERGY_COST_PER_TICK) {
            return false;
        }

        energyStorage.setEnergy(energyStorage.getEnergy() - ENERGY_COST_PER_TICK);
        return true;
    }

    private void updateClientAnimation() {
        clientPrevSpinAngleDeg = clientSpinAngleDeg;

        final var delta = 1f / 20f;
        final var maxSpeed = 360f * 2f;
        final var accelerationSpeed = 0.045f;
        final var decelerationSpeed = 0.09f;

        switch (state) {
            case SLAUGHTERING -> {
                clientSpinSpeedDegPerSec += (maxSpeed - clientSpinSpeedDegPerSec) * accelerationSpeed;
                clientSpinAngleDeg += clientSpinSpeedDegPerSec * delta;
            }
            case COOLING, WARNING, IDLE -> {
                clientSpinSpeedDegPerSec += (0f - clientSpinSpeedDegPerSec) * decelerationSpeed;
                clientSpinAngleDeg += clientSpinSpeedDegPerSec * delta;
            }
        }

        clientSpinAngleDeg = Mth.positiveModulo(clientSpinAngleDeg, 360f);
        clientPrevSpinAngleDeg = Mth.positiveModulo(clientPrevSpinAngleDeg, 360f);
    }

    public float getClientSpinDegrees(float partialTick) {
        final var prev = clientPrevSpinAngleDeg;
        final var curr = clientSpinAngleDeg;
        final var delta = Mth.wrapDegrees(curr - prev);
        return prev + delta * partialTick;
    }

    private boolean hasAnyWeapon() {
        for (int i = 0; i < weaponsContainer.getContainerSize(); i++) {
            final var itemStack = weaponsContainer.getItem(i);
            if (isValidHarvesterTool(itemStack)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValidHarvesterTool(ItemStack itemStack) {
        return getWeaponDamage(itemStack) > 0f || itemStack.is(ItemTags.HOES) || itemStack.is(Items.SHEARS);
    }

    private static float getWeaponDamage(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return 0f;
        }
        final var attributeModifiers = itemStack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
        return attributeModifiers.modifiers().stream()
                .filter(it -> it.matches(Attributes.ATTACK_DAMAGE, Item.BASE_ATTACK_DAMAGE_ID))
                .map(it -> it.modifier().amount())
                .findFirst().orElse(0.0).floatValue();
    }

    private void pullNearbyEntities() {
        if (level != null) {
            final var pullArea = new AABB(worldPosition).inflate(PULL_MAX_DISTANCE);
            final var nearbyEntities = level.getEntitiesOfClass(Entity.class, pullArea);
            final var center = Vec3.atCenterOf(worldPosition);
            nearbyEntities.forEach(entity -> {
                if (!entity.isAlive()) {
                    return;
                }

                final var entityPos = entity.position();
                final var direction = new Vec3(center.x - entityPos.x, 0, center.z - entityPos.z);

                final var distance = direction.length();
                if (distance < PULL_MIN_DISTANCE || distance > PULL_MAX_DISTANCE) {
                    return;
                }

                final var proximity = Mth.clamp(1f - (float) (distance / PULL_MAX_DISTANCE), 0f, 1f);
                final var pullStrength = PULL_BASE_STRENGTH * proximity;

                final var normalizedDirection = direction.normalize();
                final var currentVelocity = entity.getDeltaMovement();
                var newVelocity = new Vec3(
                        currentVelocity.x + normalizedDirection.x * pullStrength,
                        currentVelocity.y,
                        currentVelocity.z + normalizedDirection.z * pullStrength
                );

                final var horizontalSpeed = Math.sqrt(newVelocity.x * newVelocity.x + newVelocity.z * newVelocity.z);
                if (horizontalSpeed > PULL_MAX_SPEED) {
                    final var dampening = PULL_MAX_SPEED / horizontalSpeed;
                    newVelocity = newVelocity.multiply(dampening, 1, dampening);
                }

                entity.setDeltaMovement(newVelocity);

                // We need to manually send a packet to players, or they won't get pulled
                if (entity instanceof ServerPlayer serverPlayer) {
                    serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(serverPlayer));
                }
            });
        }
    }

    private void attackNearbyEntities() {
        if (level instanceof ServerLevel serverLevel) {
            final var attackArea = new AABB(worldPosition).inflate(ATTACK_RANGE, 0f, ATTACK_RANGE);
            final var nearbyEntities = level.getEntitiesOfClass(LivingEntity.class, attackArea);
            int entityIndex = 0;
            for (int i = 0; i < weaponsContainer.getContainerSize(); i++) {
                final var weaponStack = weaponsContainer.getItem(i);
                final var damage = getWeaponDamage(weaponStack);
                if (damage > 0f) {
                    final var entity = entityIndex < nearbyEntities.size() ? nearbyEntities.get(entityIndex) : null;
                    if (entity != null && entity.isAlive() && !entity.is(ModEntityTypeTags.IMMUNE_TO_BIOMASS_HARVESTER)) {
                        final var damageSource = entity.damageSources().generic();
                        entity.hurtServer(serverLevel, damageSource, damage);
                        weaponStack.hurtAndBreak(1, serverLevel, null, (_) -> {
                        });
                        entityIndex++;
                    }
                }
            }
        }
    }

    private float getBiomassForEntity(LivingEntity entity) {
        // Players always give one biomass
        if (entity instanceof Player) {
            return 1f;
        }

        final var baseBiomass = 0.5f;

        // Animals give more because they're harder to farm
        var typeMultiplier = entity instanceof Animal ? 2f : 0f;

        // Scale by max health so entities that take longer to kill give more biomass
        final var healthMultiplier = entity.getMaxHealth() / 20f;

        return baseBiomass * typeMultiplier * healthMultiplier;
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        backingContainer.getItems().clear();
        ContainerHelper.loadAllItems(input, backingContainer.getItems());
        try {
            state = input.getString("State").map(State::valueOf).orElse(State.IDLE);
        } catch (IllegalArgumentException e) {
            state = State.IDLE;
        }
        stateTicks = input.getIntOr("StateTicks", 0);
        energyStorage.deserialize(input);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        ContainerHelper.saveAllItems(output, backingContainer.getItems());
        output.putString("State", state.name());
        output.putInt("StateTicks", stateTicks);
        energyStorage.serialize(output);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return BalmBlockEntityUtils.createUpdateTag(registries, this::saveAdditional);
    }

    @Override
    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return BalmBlockEntityUtils.createUpdatePacket(this);
    }

    public Container getWeaponsContainer() {
        return weaponsContainer;
    }

    public State getState() {
        return state;
    }
}

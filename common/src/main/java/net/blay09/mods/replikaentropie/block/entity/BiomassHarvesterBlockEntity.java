package net.blay09.mods.replikaentropie.block.entity;

import net.blay09.mods.balm.api.container.BalmContainerProvider;
import net.blay09.mods.balm.api.container.DefaultContainer;
import net.blay09.mods.balm.api.container.SubContainer;
import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.blay09.mods.balm.common.BalmBlockEntity;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.menu.BiomassHarvesterMenu;
import net.blay09.mods.replikaentropie.tag.ModEntityTypeTags;
import net.blay09.mods.replikaentropie.util.FractionalResource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class BiomassHarvesterBlockEntity extends BalmBlockEntity implements BalmContainerProvider, BalmMenuProvider {

    private static final int WARNING_TICKS = 60;
    private static final int SLAUGHTER_TICKS = 60;
    private static final int COOLDOWN_TICKS = 60;

    private static final float ATTACK_RANGE = 1f;
    private static final int ATTACK_INTERVAL_TICKS = 20;

    private static final float PULL_BASE_STRENGTH = 0.08f;
    private static final float PULL_MIN_DISTANCE = 1.5f;
    private static final float PULL_MAX_DISTANCE = 10f;
    private static final float PULL_MAX_SPEED = 0.6f;

    public enum State {IDLE, WARNING, SLAUGHTERING, COOLING}

    private final DefaultContainer backingContainer = new DefaultContainer(5) {
        @Override
        public void setChanged() {
            BiomassHarvesterBlockEntity.this.setChanged();
            isSyncDirty = true;
        }

        @Override
        public boolean canPlaceItem(int slot, ItemStack itemStack) {
            return weaponsContainer.containsOuterSlot(slot) && isValidWeapon(itemStack);
        }
    };

    private final Container outputContainer = new SubContainer(backingContainer, 0, 1);
    private final SubContainer weaponsContainer = new SubContainer(backingContainer, 1, 5);

    private State state = State.IDLE;
    private int stateTicks;

    private boolean isSyncDirty;

    private final FractionalResource biomass = new FractionalResource(outputContainer, 0, ModItems.biomass);

    private float clientPrevSpinAngleDeg;
    private float clientSpinAngleDeg;
    private float clientSpinSpeedDegPerSec;

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            //noinspection SwitchStatementWithTooFewBranches
            return switch (index) {
                case BiomassHarvesterMenu.DATA_FRACTIONAL_BIOMASS -> biomass.getFractionalAmountAsMenuData();
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
        super(ModBlockEntities.biomassHarvester.get(), pos, blockState);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.replikaentropie.biomass_harvester");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new BiomassHarvesterMenu(containerId, inventory, backingContainer, dataAccess);
    }

    @Override
    public Container getContainer() {
        return backingContainer;
    }

    @Override
    public Container getContainer(Direction side) {
        //noinspection SwitchStatementWithTooFewBranches
        return switch (side) {
            case DOWN -> outputContainer;
            default -> backingContainer;
        };
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, BiomassHarvesterBlockEntity blockEntity) {
        blockEntity.broadcastChanges();
        blockEntity.processState();
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, BiomassHarvesterBlockEntity blockEntity) {
        blockEntity.updateClientAnimation();
    }

    private void broadcastChanges() {
        if (isSyncDirty) {
            sync();
            isSyncDirty = false;
        }
    }

    private void transition(State state) {
        this.state = state;
        stateTicks = 0;
        setChanged();
        sync();
    }

    private void processState() {
        stateTicks++;

        switch (state) {
            case WARNING -> {
                if (stateTicks >= WARNING_TICKS) {
                    transition(State.SLAUGHTERING);
                }
            }
            case SLAUGHTERING -> {
                if (!hasAnyWeapon()) {
                    transition(State.IDLE);
                    return;
                }

                pullNearbyEntities();

                if (stateTicks % ATTACK_INTERVAL_TICKS == 0) {
                    attackNearbyEntities();
                }

                if (stateTicks >= SLAUGHTER_TICKS) {
                    transition(State.COOLING);
                }
            }
            case COOLING -> {
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
            if (isValidWeapon(itemStack)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValidWeapon(ItemStack itemStack) {
        return getWeaponDamage(itemStack) > 0f;
    }

    private static float getWeaponDamage(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return 0f;
        } else if (itemStack.getItem() instanceof SwordItem sword) {
            return sword.getDamage();
        } else if (itemStack.getItem() instanceof AxeItem axe) {
            return axe.getAttackDamage();
        } else if (itemStack.getItem() instanceof TridentItem) {
            return TridentItem.BASE_DAMAGE;
        }
        return 0f;
    }

    private void pullNearbyEntities() {
        if (level != null) {
            final var pullArea = new AABB(worldPosition).inflate(PULL_MAX_DISTANCE);
            final var nearbyEntities = level.getEntitiesOfClass(LivingEntity.class, pullArea);
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

                // We need to manually send a packet to players or they won't get pulled
                if (entity instanceof ServerPlayer serverPlayer) {
                    serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(serverPlayer));
                }
            });
        }
    }

    private void attackNearbyEntities() {
        if (level != null) {
            final var attackArea = new AABB(worldPosition).inflate(ATTACK_RANGE, 0f, ATTACK_RANGE);
            final var nearbyEntities = level.getEntitiesOfClass(LivingEntity.class, attackArea);
            int entityIndex = 0;
            for (int i = 0; i < weaponsContainer.getContainerSize(); i++) {
                final var weaponStack = weaponsContainer.getItem(i);
                final var damage = getWeaponDamage(weaponStack);
                if (damage > 0f) {
                    final var entity = entityIndex < nearbyEntities.size() ? nearbyEntities.get(entityIndex) : null;
                    if (entity != null && entity.isAlive() && !entity.getType().is(ModEntityTypeTags.IMMUNE_TO_BIOMASS_HARVESTER)) {
                        final var damageSource = entity.damageSources().generic();
                        if (entity.hurt(damageSource, damage) && !entity.isAlive()) {
                            biomass.add(getBiomassForEntity(entity));
                        }
                        weaponStack.hurt(1, level.random, null);
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
    public void load(CompoundTag tag) {
        super.load(tag);
        backingContainer.clearContent();
        ContainerHelper.loadAllItems(tag, backingContainer.getItems());
        try {
            state = State.valueOf(tag.getString("State"));
        } catch (IllegalArgumentException e) {
            state = State.IDLE;
        }
        stateTicks = tag.getInt("StateTicks");
        biomass.setFractionalAmount(tag.getFloat("FractionalBiomass"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, backingContainer.getItems());
        tag.putString("State", state.name());
        tag.putInt("StateTicks", stateTicks);
        tag.putFloat("FractionalBiomass", biomass.getFractionalAmount());
    }

    @Override
    protected void writeUpdateTag(CompoundTag tag) {
        super.writeUpdateTag(tag);

        ContainerHelper.saveAllItems(tag, backingContainer.getItems());
        tag.putString("State", state.name());
    }

    public Container getWeaponsContainer() {
        return weaponsContainer;
    }

    public State getState() {
        return state;
    }
}

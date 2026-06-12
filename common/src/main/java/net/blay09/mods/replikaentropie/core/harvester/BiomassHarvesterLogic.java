package net.blay09.mods.replikaentropie.core.harvester;

import net.blay09.mods.balm.platform.energy.EnergyStorage;
import net.blay09.mods.replikaentropie.tag.ModBlockTags;
import net.blay09.mods.replikaentropie.tag.ModEntityTypeTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;

public class BiomassHarvesterLogic {
    public static final int ENERGY_CAPACITY = 10000;
    public static final int ENERGY_INPUT_RATE = 1000;

    private static final int WARNING_TICKS = 60;
    private static final int SLAUGHTER_TICKS = 60;
    private static final int COOLDOWN_TICKS = 60;
    private static final int ENERGY_COST_PER_TICK = 10;
    private static final float ATTACK_RANGE = 1f;
    private static final int ATTACK_INTERVAL_TICKS = 20;
    private static final float PULL_BASE_STRENGTH = 0.08f;
    private static final float PULL_MIN_DISTANCE = 1.5f;
    private static final float PULL_MAX_DISTANCE = 10f;
    private static final float PULL_MAX_SPEED = 0.6f;

    public enum State {IDLE, WARNING, SLAUGHTERING, COOLING}

    private final Container weaponsContainer;
    private final EnergyStorage energyStorage;
    private final Runnable changedCallback;
    private final Runnable syncCallback;
    private State state = State.IDLE;
    private int stateTicks;

    public BiomassHarvesterLogic(Container weaponsContainer, EnergyStorage energyStorage, Runnable changedCallback, Runnable syncCallback) {
        this.weaponsContainer = weaponsContainer;
        this.energyStorage = energyStorage;
        this.changedCallback = changedCallback;
        this.syncCallback = syncCallback;
    }

    public void serverTick(Level level, Vec3 center, BlockPos blockPosition) {
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
                pullNearbyEntities(level, center);
                if (stateTicks % ATTACK_INTERVAL_TICKS == 0) {
                    attackNearbyEntities(level, blockPosition);
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

    public void clientTick(Level level, Vec3 center) {
        if (state == State.SLAUGHTERING) {
            pullNearbyEntities(level, center);
        }
    }

    private void transition(State state) {
        this.state = state;
        stateTicks = 0;
        changedCallback.run();
        syncCallback.run();
    }

    private boolean consumeEnergy() {
        if (energyStorage.getEnergy() < ENERGY_COST_PER_TICK) {
            return false;
        }
        energyStorage.setEnergy(energyStorage.getEnergy() - ENERGY_COST_PER_TICK);
        return true;
    }

    private boolean hasAnyWeapon() {
        for (int i = 0; i < weaponsContainer.getContainerSize(); i++) {
            if (isValidHarvesterTool(weaponsContainer.getItem(i))) {
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

    private void pullNearbyEntities(Level level, Vec3 center) {
        final var pullArea = new AABB(center.x - 0.5, center.y - 0.5, center.z - 0.5, center.x + 0.5, center.y + 0.5, center.z + 0.5)
                .inflate(PULL_MAX_DISTANCE);
        final var nearbyEntities = level.getEntitiesOfClass(Entity.class, pullArea);
        nearbyEntities.forEach(entity -> {
            if (!entity.isAlive()) {
                return;
            }
            final var direction = new Vec3(center.x - entity.getX(), 0, center.z - entity.getZ());
            final var distance = direction.length();
            if (distance < PULL_MIN_DISTANCE || distance > PULL_MAX_DISTANCE) {
                return;
            }
            final var proximity = Mth.clamp(1f - (float) (distance / PULL_MAX_DISTANCE), 0f, 1f);
            final var pullStrength = PULL_BASE_STRENGTH * proximity;
            final var currentVelocity = entity.getDeltaMovement();
            final var normalizedDirection = direction.normalize();
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
            if (entity instanceof ServerPlayer serverPlayer) {
                serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(serverPlayer));
            }
        });
    }

    private void attackNearbyEntities(Level level, BlockPos blockPosition) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        final var nearbyEntities = level.getEntitiesOfClass(LivingEntity.class, new AABB(blockPosition).inflate(ATTACK_RANGE, 0f, ATTACK_RANGE));
        final var blockTargets = findHarvestableBlockTargets(level, blockPosition);
        int entityIndex = 0;
        int cropIndex = 0;
        int slashableBlockIndex = 0;
        for (int i = 0; i < weaponsContainer.getContainerSize(); i++) {
            final var weaponStack = weaponsContainer.getItem(i);
            final var entity = entityIndex < nearbyEntities.size() ? nearbyEntities.get(entityIndex) : null;
            if (weaponStack.is(ItemTags.HOES) && cropIndex < blockTargets.harvestableCrops().size()) {
                harvestCrop(serverLevel, blockTargets.harvestableCrops().get(cropIndex++));
                hurtTool(weaponStack, serverLevel);
                continue;
            }
            final var canAttackEntity = entity != null && entity.isAlive() && !entity.is(ModEntityTypeTags.IMMUNE_TO_BIOMASS_HARVESTER);
            if (!canAttackEntity && weaponStack.is(ItemTags.SWORDS) && slashableBlockIndex < blockTargets.slashableBlocks().size()) {
                serverLevel.destroyBlock(blockTargets.slashableBlocks().get(slashableBlockIndex++), true);
                hurtTool(weaponStack, serverLevel);
                continue;
            }
            if (!canAttackEntity) {
                continue;
            }
            if (weaponStack.is(Items.SHEARS) && entity instanceof Shearable shearable && shearable.readyForShearing()) {
                shearable.shear(serverLevel, SoundSource.BLOCKS, weaponStack);
                hurtTool(weaponStack, serverLevel);
                entityIndex++;
                continue;
            }
            final var damage = getWeaponDamage(weaponStack);
            if (damage > 0f) {
                entity.hurtServer(serverLevel, entity.damageSources().generic(), damage);
                hurtTool(weaponStack, serverLevel);
                entityIndex++;
            }
        }
    }

    private static void hurtTool(ItemStack tool, ServerLevel level) {
        tool.hurtAndBreak(1, level, null, (_) -> {
        });
    }

    private static HarvestableBlockTargets findHarvestableBlockTargets(Level level, BlockPos center) {
        final var crops = new ArrayList<BlockPos>();
        final var blocks = new ArrayList<BlockPos>();
        for (final var pos : BlockPos.betweenClosed(center.offset(-1, 0, -1), center.offset(1, 0, 1))) {
            final var state = level.getBlockState(pos);
            if (state.getBlock() instanceof CropBlock cropBlock && cropBlock.isMaxAge(state)) {
                crops.add(pos.immutable());
            }
            if (!state.isAir() && (state.canBeReplaced() || state.is(ModBlockTags.SLASHED_BY_BIOMASS_HARVESTER))) {
                blocks.add(pos.immutable());
            }
        }
        return new HarvestableBlockTargets(crops, blocks);
    }

    private static void harvestCrop(ServerLevel level, BlockPos pos) {
        final var state = level.getBlockState(pos);
        if (state.getBlock() instanceof CropBlock cropBlock && cropBlock.isMaxAge(state)) {
            Block.dropResources(state, level, pos, level.getBlockEntity(pos));
            level.setBlockAndUpdate(pos, cropBlock.defaultBlockState());
        }
    }

    public void save(ValueOutput output) {
        output.putString("State", state.name());
        output.putInt("StateTicks", stateTicks);
    }

    public void load(ValueInput input) {
        try {
            state = input.getString("State").map(State::valueOf).orElse(State.IDLE);
        } catch (IllegalArgumentException e) {
            state = State.IDLE;
        }
        stateTicks = input.getIntOr("StateTicks", 0);
    }

    public State getState() {
        return state;
    }

    public int getStateTicks() {
        return stateTicks;
    }

    public void restoreState(State state, int stateTicks) {
        this.state = state;
        this.stateTicks = stateTicks;
    }

    private record HarvestableBlockTargets(ArrayList<BlockPos> harvestableCrops, ArrayList<BlockPos> slashableBlocks) {
    }
}

package net.blay09.mods.replikaentropie.entity;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.platform.energy.BalmEnergyStorageProvider;
import net.blay09.mods.balm.platform.energy.EnergyStorage;
import net.blay09.mods.balm.world.BalmContainerProvider;
import net.blay09.mods.balm.world.BalmMenuProvider;
import net.blay09.mods.replikaentropie.block.ModBlocks;
import net.blay09.mods.replikaentropie.core.harvester.BiomassHarvesterLogic;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.menu.BiomassHarvesterMenu;
import net.blay09.mods.replikaentropie.power.MakeshiftPsu;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecartContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class BiomassHarvesterMinecart extends AbstractMinecartContainer implements BalmContainerProvider, BalmMenuProvider<Unit>, BalmEnergyStorageProvider {
    private static final EntityDataAccessor<ItemStack> DATA_TOOL_0 = SynchedEntityData.defineId(BiomassHarvesterMinecart.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ItemStack> DATA_TOOL_1 = SynchedEntityData.defineId(BiomassHarvesterMinecart.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ItemStack> DATA_TOOL_2 = SynchedEntityData.defineId(BiomassHarvesterMinecart.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ItemStack> DATA_TOOL_3 = SynchedEntityData.defineId(BiomassHarvesterMinecart.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Integer> DATA_STATE = SynchedEntityData.defineId(BiomassHarvesterMinecart.class, EntityDataSerializers.INT);
    private static final List<EntityDataAccessor<ItemStack>> DATA_TOOLS = List.of(DATA_TOOL_0, DATA_TOOL_1, DATA_TOOL_2, DATA_TOOL_3);

    private final MakeshiftPsu energyStorage = new MakeshiftPsu(0, BiomassHarvesterLogic.ENERGY_CAPACITY, BiomassHarvesterLogic.ENERGY_INPUT_RATE, 0);
    private final BiomassHarvesterLogic logic = new BiomassHarvesterLogic(this, energyStorage, () -> {
    }, () -> {
    });
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
    private float clientPrevSpinAngleDeg;
    private float clientSpinAngleDeg;
    private float clientSpinSpeedDegPerSec;

    public BiomassHarvesterMinecart(EntityType<? extends BiomassHarvesterMinecart> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder entityData) {
        super.defineSynchedData(entityData);
        for (final var dataTool : DATA_TOOLS) {
            entityData.define(dataTool, ItemStack.EMPTY);
        }
        entityData.define(DATA_STATE, BiomassHarvesterLogic.State.IDLE.ordinal());
    }

    @Override
    public void tick() {
        super.tick();
        final var center = new Vec3(getX(), getY() + 0.5, getZ());
        if (level().isClientSide()) {
            logic.clientTick(level(), center);
            updateClientAnimation();
        } else {
            logic.serverTick(level(), center, blockPosition());
            syncRenderData();
        }
    }

    private void syncRenderData() {
        for (int i = 0; i < DATA_TOOLS.size(); i++) {
            final var dataTool = DATA_TOOLS.get(i);
            final var itemStack = getItem(i);
            if (!ItemStack.matches(itemStack, entityData.get(dataTool))) {
                entityData.set(dataTool, itemStack.copy());
            }
        }
        entityData.set(DATA_STATE, logic.getState().ordinal());
    }

    private void updateClientAnimation() {
        clientPrevSpinAngleDeg = clientSpinAngleDeg;
        final var delta = 1f / 20f;
        final var maxSpeed = 360f * 2f;
        final var accelerationSpeed = 0.045f;
        final var decelerationSpeed = 0.09f;
        switch (getState()) {
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
        return clientPrevSpinAngleDeg + Mth.wrapDegrees(clientSpinAngleDeg - clientPrevSpinAngleDeg) * partialTick;
    }

    public ItemStack getRenderTool(int slot) {
        return entityData.get(DATA_TOOLS.get(slot));
    }

    public BiomassHarvesterLogic.State getState() {
        return level().isClientSide()
                ? BiomassHarvesterLogic.State.values()[entityData.get(DATA_STATE)]
                : logic.getState();
    }

    public int getStateTicks() {
        return logic.getStateTicks();
    }

    public void restoreState(BiomassHarvesterLogic.State state, int stateTicks) {
        logic.restoreState(state, stateTicks);
        syncRenderData();
    }

    @Override
    public int getContainerSize() {
        return 4;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack itemStack) {
        return BiomassHarvesterLogic.isValidHarvesterTool(itemStack);
    }

    @Override
    public void setChanged() {
        if (!level().isClientSide()) {
            syncRenderData();
        }
    }

    @Override
    public Container getContainer() {
        return this;
    }

    @Override
    public EnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    @Override
    public EnergyStorage getEnergyStorage(Direction side) {
        return energyStorage;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.replikaentropie.biomass_harvester");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new BiomassHarvesterMenu(containerId, inventory, this, dataAccess, energyStorage);
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
    public InteractionResult interact(Player player, InteractionHand hand, Vec3 location) {
        if (!level().isClientSide()) {
            Balm.networking().openMenu(player, this);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    protected Item getDropItem() {
        return ModItems.biomassHarvesterMinecart.asItem();
    }

    @Override
    public BlockState getDefaultDisplayBlockState() {
        return ModBlocks.biomassHarvester.defaultBlockState();
    }

    @Override
    public int getDefaultDisplayOffset() {
        return 8;
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(ModItems.biomassHarvesterMinecart.asItem());
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        logic.save(output);
        energyStorage.serialize(output);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        logic.load(input);
        energyStorage.deserialize(input);
        syncRenderData();
    }
}

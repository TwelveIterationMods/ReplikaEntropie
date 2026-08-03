package net.blay09.mods.replikaentropie.block.entity;

import net.blay09.mods.balm.platform.energy.BalmEnergyStorageProvider;
import net.blay09.mods.balm.platform.energy.DefaultEnergyStorage;
import net.blay09.mods.balm.platform.energy.EnergyStorage;
import net.blay09.mods.balm.world.BalmContainerProvider;
import net.blay09.mods.balm.world.BalmMenuProvider;
import net.blay09.mods.balm.world.DefaultContainer;
import net.blay09.mods.replikaentropie.menu.ReplikaWorkbenchMenu;
import net.blay09.mods.replikaentropie.tag.ModItemTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class ReplikaWorkbenchBlockEntity extends BlockEntity implements BalmContainerProvider, BalmEnergyStorageProvider {

    public static final int CENTER_SLOT = 0;
    private static final int ENERGY_CAPACITY = 10000;
    private static final int ENERGY_INPUT_RATE = 1000;
    private static final int RECHARGE_ENERGY_COST = 100;
    private static final int RECHARGE_TICK_INTERVAL = 1;
    private static final int RECHARGE_PER_CYCLE = 10;

    private final DefaultContainer backingContainer = new DefaultContainer(1) {
        @Override
        public void setChanged() {
            ReplikaWorkbenchBlockEntity.this.setChanged();
        }

        @Override
        public boolean canPlaceItem(int slot, ItemStack itemStack) {
            return slot == CENTER_SLOT && getItem(slot).isEmpty() && canUseAsCenterItem(itemStack);
        }
    };

    private final DefaultEnergyStorage energyStorage = new DefaultEnergyStorage(0, ENERGY_CAPACITY, ENERGY_INPUT_RATE, 0) {
        @Override
        public void setChanged() {
            ReplikaWorkbenchBlockEntity.this.setChanged();
        }
    };

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case ReplikaWorkbenchMenu.DATA_CURRENT_POWER -> energyStorage.getEnergy();
                case ReplikaWorkbenchMenu.DATA_MAX_POWER -> energyStorage.getCapacity();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return ReplikaWorkbenchMenu.DATA_COUNT;
        }
    };

    public ReplikaWorkbenchBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.replikaWorkbench.value(), pos, blockState);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ReplikaWorkbenchBlockEntity blockEntity) {
        blockEntity.rechargeCenterSlotItem(level);
    }

    public BalmMenuProvider<Unit> getMenuProvider() {
        return new BalmMenuProvider<Unit>() {
            @Override
            public Component getDisplayName() {
                return Component.translatable("container.replikaentropie.replika_workbench");
            }

            @Override
            public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                return new ReplikaWorkbenchMenu(i, inventory, backingContainer, dataAccess, ContainerLevelAccess.create(level, worldPosition));
            }

            @Override
            public Unit getScreenOpeningData(ServerPlayer player) {
                return Unit.INSTANCE;
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, Unit> getScreenStreamCodec() {
                return Unit.STREAM_CODEC.cast();
            }
        };
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

    private void rechargeCenterSlotItem(Level level) {
        if (level.getGameTime() % RECHARGE_TICK_INTERVAL != 0) {
            return;
        }

        final var itemStack = backingContainer.getItem(CENTER_SLOT);
        if (!canRechargeItem(itemStack) || energyStorage.getEnergy() < RECHARGE_ENERGY_COST) {
            return;
        }

        energyStorage.setEnergy(energyStorage.getEnergy() - RECHARGE_ENERGY_COST);
        rechargeItem(itemStack, RECHARGE_PER_CYCLE);
        setChanged();
    }

    private static boolean canRechargeItem(ItemStack itemStack) {
        return itemStack.is(ModItemTags.CHARGEABLE) && itemStack.isDamageableItem() && itemStack.isDamaged();
    }

    private static void rechargeItem(ItemStack itemStack, int amount) {
        itemStack.setDamageValue(Math.max(0, itemStack.getDamageValue() - amount));
    }

    private static boolean canUseAsCenterItem(ItemStack itemStack) {
        return itemStack.is(ModItemTags.REPLIKA_WORKBENCH_MODDABLE) || itemStack.is(ModItemTags.CHARGEABLE);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        ContainerHelper.loadAllItems(input, backingContainer.getItems());
        energyStorage.deserialize(input);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        ContainerHelper.saveAllItems(output, backingContainer.getItems());
        energyStorage.serialize(output);
    }
}

package net.blay09.mods.replikaentropie.block.entity;

import net.blay09.mods.balm.world.BalmContainerProvider;
import net.blay09.mods.balm.world.BalmMenuProvider;
import net.blay09.mods.balm.world.DefaultContainer;
import net.blay09.mods.balm.world.SubContainer;
import net.blay09.mods.balm.platform.energy.BalmEnergyStorageProvider;
import net.blay09.mods.balm.platform.energy.EnergyStorage;
import net.blay09.mods.replikaentropie.component.AssemblyTicket;
import net.blay09.mods.replikaentropie.component.ModDataComponents;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.menu.AssemblerMenu;
import net.blay09.mods.replikaentropie.power.MakeshiftPsu;
import net.blay09.mods.replikaentropie.recipe.AssemblerRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
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

public class AssemblerBlockEntity extends BlockEntity implements BalmContainerProvider, BalmMenuProvider<Unit>, BalmEnergyStorageProvider {

    private static final int PROCESSING_TICKS = 60;
    private static final int ENERGY_CAPACITY = 10000;
    private static final int ENERGY_INPUT_RATE = 1000;
    private static final int ENERGY_COST_PER_TICK = 10;

    private final DefaultContainer backingContainer = new DefaultContainer(11) {
        @Override
        public void setChanged() {
            AssemblerBlockEntity.this.setChanged();
        }

        @Override
        public boolean canPlaceItem(int slot, ItemStack itemStack) {
            return switch (slot) {
                case 0 -> false;
                case 1 -> itemStack.is(ModItems.assemblyTicket);
                default -> !itemStack.is(ModItems.assemblyTicket);
            };
        }
    };

    private final Container resultContainer = new SubContainer(backingContainer, 0, 1);
    private final Container ticketContainer = new SubContainer(backingContainer, 1, 2);
    private final Container inputContainer = new SubContainer(backingContainer, 2, 11);

    private final MakeshiftPsu energyStorage = new MakeshiftPsu(0, ENERGY_CAPACITY, ENERGY_INPUT_RATE, 0) {
        @Override
        public void setChanged() {
            AssemblerBlockEntity.this.setChanged();
        }
    };

    private int processingTicks;

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case AssemblerMenu.DATA_PROCESSING_TIME -> processingTicks;
                case AssemblerMenu.DATA_MAX_PROCESSING_TIME -> PROCESSING_TICKS;
                case AssemblerMenu.DATA_CURRENT_POWER -> energyStorage.getEnergy();
                case AssemblerMenu.DATA_MAX_POWER -> energyStorage.getCapacity();
                case AssemblerMenu.DATA_OVERHEATED -> energyStorage.isOverheated(level) ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return AssemblerMenu.DATA_COUNT;
        }
    };

    public AssemblerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.assembler.value(), pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.replikaentropie.assembler");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new AssemblerMenu(id, inv, backingContainer, dataAccess, ContainerLevelAccess.create(level, worldPosition), energyStorage);
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
    public Container getContainer(Direction side) {
        return side == Direction.DOWN ? resultContainer : backingContainer;
    }

    @Override
    public EnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    @Override
    public EnergyStorage getEnergyStorage(Direction side) {
        return energyStorage;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, AssemblerBlockEntity assembler) {
        assembler.serverTick(level);
    }

    private void serverTick(Level level) {
        final var ticketStack = ticketContainer.getItem(0);
        if (ticketStack.isEmpty()) {
            processingTicks = 0;
            return;
        }

        final var ticketData = ticketStack.get(ModDataComponents.assemblyTicket());
        if (ticketData == null || ticketData.recipeId().isEmpty()) {
            processingTicks = 0;
            return;
        }

        if (!(level instanceof ServerLevel serverLevel)) {
            processingTicks = 0;
            return;
        }

        final var recipe = serverLevel.recipeAccess().byKey(ResourceKey.create(Registries.RECIPE, ticketData.recipeId().get())).orElse(null);
        if (recipe == null || !(recipe.value() instanceof AssemblerRecipe assemblerRecipe)) {
            processingTicks = 0;
            return;
        }

        if (!assemblerRecipe.matches(inputContainer, level)) {
            processingTicks = 0;
            return;
        }

        final var resultStack = assemblerRecipe.assemble(inputContainer);
        final var output = resultContainer.getItem(0);
        if (!output.isEmpty()) {
            if (!ItemStack.isSameItemSameComponents(output, resultStack)
                    || output.getCount() + resultStack.getCount() >= output.getMaxStackSize()) {
                processingTicks = 0;
                return;
            }
        }

        if (energyStorage.getEnergy() < ENERGY_COST_PER_TICK) {
            return;
        }

        energyStorage.setEnergy(energyStorage.getEnergy() - ENERGY_COST_PER_TICK);
        processingTicks++;
        if (processingTicks >= PROCESSING_TICKS) {
            for (final var countedIngredient : assemblerRecipe.ingredients()) {
                final var ingredient = countedIngredient.ingredient();
                var needed = Math.max(1, countedIngredient.count());
                for (int i = 0; i < inputContainer.getContainerSize() && needed > 0; i++) {
                    final var inputStack = inputContainer.getItem(i);
                    if (!inputStack.isEmpty() && ingredient.test(inputStack)) {
                        var toTake = Math.min(inputStack.getCount(), needed);
                        inputStack.shrink(toTake);
                        if (inputStack.isEmpty()) {
                            inputContainer.setItem(i, ItemStack.EMPTY);
                        }
                        needed -= toTake;
                    }
                }
            }

            final var usesLeft = ticketData.usesLeft();
            if (usesLeft == 1) {
                ticketStack.shrink(1);
                if (ticketStack.isEmpty()) {
                    ticketContainer.setItem(0, ItemStack.EMPTY);
                }
            } else if (usesLeft > 0) {
                ticketStack.set(ModDataComponents.assemblyTicket(), new AssemblyTicket(ticketData.recipeId(), usesLeft - 1, ticketData.showHint()));
            }

            if (output.isEmpty()) {
                resultContainer.setItem(0, resultStack);
            } else {
                output.grow(resultStack.getCount());
            }

            processingTicks = 0;
            setChanged();
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        ContainerHelper.loadAllItems(input, backingContainer.getItems());
        processingTicks = input.getIntOr("ProcessingTicks", 0);
        energyStorage.deserialize(input);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        ContainerHelper.saveAllItems(output, backingContainer.getItems());
        output.putInt("ProcessingTicks", processingTicks);
        energyStorage.serialize(output);
    }
}

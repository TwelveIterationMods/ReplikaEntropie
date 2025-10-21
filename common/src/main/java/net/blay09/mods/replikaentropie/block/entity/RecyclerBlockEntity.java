package net.blay09.mods.replikaentropie.block.entity;

import net.blay09.mods.balm.api.container.BalmContainerProvider;
import net.blay09.mods.balm.api.container.DefaultContainer;
import net.blay09.mods.balm.api.container.SubContainer;
import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.menu.RecyclerMenu;
import net.blay09.mods.replikaentropie.recipe.RecyclerRecipe;
import net.blay09.mods.replikaentropie.util.FractionalResource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class RecyclerBlockEntity extends BlockEntity implements BalmContainerProvider {

    private final DefaultContainer backingContainer = new DefaultContainer(4) {
        @Override
        public void setChanged() {
            RecyclerBlockEntity.this.setChanged();
        }

        @Override
        public boolean canPlaceItem(int slot, ItemStack itemStack) {
            //noinspection SwitchStatementWithTooFewBranches
            return switch(slot) {
                case 0 -> true;
                default -> false;
            };
        }
    };

    private static final int PROCESSING_TICKS = 100;

    private final Container inputContainer = new SubContainer(backingContainer, 0, 1);
    private final Container scrapContainer = new SubContainer(backingContainer, 1, 2);
    private final Container biomassContainer = new SubContainer(backingContainer, 2, 3);
    private final Container fragmentsContainer = new SubContainer(backingContainer, 3, 4);
    private final Container outputContainer = new SubContainer(backingContainer, 1, 4);

    private final FractionalResource scrap = new FractionalResource(scrapContainer, 0, ModItems.scrap);
    private final FractionalResource biomass = new FractionalResource(biomassContainer, 0, ModItems.biomass);
    private final FractionalResource fragments = new FractionalResource(fragmentsContainer, 0, ModItems.fragments);

    private int processingTicks;

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case RecyclerMenu.DATA_PROCESSING_TIME -> processingTicks;
                case RecyclerMenu.DATA_MAX_PROCESSING_TIME -> PROCESSING_TICKS;
                case RecyclerMenu.DATA_FRACTIONAL_SCRAP -> scrap.getFractionalAmountAsMenuData();
                case RecyclerMenu.DATA_FRACTIONAL_BIOMASS -> biomass.getFractionalAmountAsMenuData();
                case RecyclerMenu.DATA_FRACTIONAL_FRAGMENTS -> fragments.getFractionalAmountAsMenuData();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return RecyclerMenu.DATA_COUNT;
        }
    };

    public RecyclerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.recycler.get(), pos, state);
    }

    public BalmMenuProvider getMenuProvider() {
        return new BalmMenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.translatable("container.replikaentropie.recycler");
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
                return new RecyclerMenu(containerId, inventory, backingContainer, dataAccess);
            }
        };
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

    public static void serverTick(Level level, BlockPos pos, BlockState state, RecyclerBlockEntity blockEntity) {
        blockEntity.processTick(level);
    }

    private void processTick(Level level) {
        final var inputStack = inputContainer.getItem(0);
        if (inputStack.isEmpty()) {
            processingTicks = 0;
            return;
        }

        final var foundRecipe = RecyclerRecipe.getRecipe(level, inputStack);
        if (foundRecipe.isEmpty()) {
            processingTicks = 0;
            return;
        }

        processingTicks++;
        if (processingTicks >= PROCESSING_TICKS) {
            inputStack.shrink(1);

            final var recipe = foundRecipe.get();
            scrap.add(recipe.scrap());
            biomass.add(recipe.biomass());
            fragments.add(recipe.fragments());

            processingTicks = 0;
            setChanged();
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        ContainerHelper.loadAllItems(tag, backingContainer.getItems());
        processingTicks = tag.getInt("ProcessingTicks");
        scrap.setFractionalAmount(tag.getFloat("FractionalScrap"));
        biomass.setFractionalAmount(tag.getFloat("FractionalBiomass"));
        fragments.setFractionalAmount(tag.getFloat("FractionalFragments"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, backingContainer.getItems());
        tag.putInt("ProcessingTicks", processingTicks);
        tag.putFloat("FractionalScrap", scrap.getFractionalAmount());
        tag.putFloat("FractionalBiomass", biomass.getFractionalAmount());
        tag.putFloat("FractionalFragments", fragments.getFractionalAmount());
    }
}

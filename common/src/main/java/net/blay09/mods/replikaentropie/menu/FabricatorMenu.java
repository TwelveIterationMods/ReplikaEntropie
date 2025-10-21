package net.blay09.mods.replikaentropie.menu;

import net.blay09.mods.replikaentropie.container.RecipeContainer;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.menu.slot.*;
import net.blay09.mods.replikaentropie.recipe.FabricatorRecipe;
import net.blay09.mods.replikaentropie.util.QuickMove;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class FabricatorMenu extends AbstractContainerMenu {

    private final Inventory playerInventory;
    private final Container container;
    private final ContainerData data;
    private final List<FabricatorRecipe> recipes;
    private final RecipeContainer<FabricatorRecipe> recipeContainer = new RecipeContainer<>(7 * 4);
    private final QuickMove.Routing quickMove;

    public static final int DATA_OUTPUT_PROCESSING_TIME = 0;
    public static final int DATA_MAX_OUTPUT_PROCESSING_TIME = 1;
    public static final int DATA_MISSING_SCRAP = 2;
    public static final int DATA_MISSING_BIOMASS = 3;
    public static final int DATA_MISSING_FRAGMENTS = 4;
    public static final int DATA_RECIPES_START = 5;
    public static final int DATA_RECIPES_SIZE = 4 * 7;
    public static final int DATA_RECIPES_END = DATA_RECIPES_START + DATA_RECIPES_SIZE;

    public static final int DATA_COUNT = DATA_RECIPES_END;

    public FabricatorMenu(int containerId, Inventory playerInventory, List<FabricatorRecipe> recipes) {
        this(containerId, playerInventory, new SimpleContainer(8), new SimpleContainerData(DATA_COUNT), recipes);
    }

    public FabricatorMenu(int containerId, Inventory playerInventory, Container container, ContainerData data, List<FabricatorRecipe> recipes) {
        super(ModMenus.fabricator.get(), containerId);
        this.playerInventory = playerInventory;
        this.container = container;
        checkContainerSize(container, 8);
        this.data = data;
        addDataSlots(data);
        this.recipes = recipes;

        addSlot(new OutputSlot(container, 0, 146, 99));
        addSlot(new ScrapSlot(container, 1, 37, 22));
        addSlot(new BiomassSlot(container, 2, 62, 22));
        addSlot(new FragmentsSlot(container, 3, 87, 22));

        for (int i = 0; i < 3; i++) {
            addSlot(new FabricatorBufferSlot(container, i + 4, 146, 45 + i * 16));
        }

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 7; j++) {
                addSlot(new FabricatorRecipeSlot(recipeContainer, j + i * 7, 8 + j * 18, 49 + i * 18));
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 140 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(playerInventory, i, 8 + i * 18, 198));
        }

        updateRecipeDisplays();

        quickMove = QuickMove.create(this, this::moveItemStackTo)
                .slot("scrap", 1)
                .slot("biomass", 2)
                .slot("fragments", 3)
                .route(it -> it.is(ModItems.scrap), QuickMove.PLAYER, "scrap")
                .route(it -> it.is(ModItems.biomass), QuickMove.PLAYER, "biomass")
                .route(it -> it.is(ModItems.fragments), QuickMove.PLAYER, "fragments")
                .build();

        container.startOpen(playerInventory.player);
    }

    private void updateRecipeDisplays() {
        for (int i = 0; i < recipeContainer.getContainerSize(); i++) {
            final var recipe = i < recipes.size() ? recipes.get(i) : null;
            recipeContainer.setRecipe(playerInventory.player.level().registryAccess(), i, recipe);
        }
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (slotId >= 7 && slotId <= 34 && !player.level().isClientSide()) {
            int recipeIndex = slots.get(slotId).getContainerSlot();
            final var recipe = recipeContainer.getRecipe(recipeIndex);
            if (recipe != null) {
                if (clickType == ClickType.PICKUP) {
                    data.set(DATA_RECIPES_START + recipeIndex, Mth.clamp(data.get(DATA_RECIPES_START + recipeIndex) + (button == 1 ? -1 : 1), 0, 64));
                } else if (clickType == ClickType.QUICK_MOVE) {
                    data.set(DATA_RECIPES_START + recipeIndex, data.get(DATA_RECIPES_START + recipeIndex) == -1 ? 0 : -1);
                }
            }
        } else {
            super.clicked(slotId, button, clickType, player);
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        return quickMove.transfer(this, player, slotIndex);
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        container.stopOpen(player);
    }

    public float getOutputProcessingProgress() {
        return data.get(DATA_OUTPUT_PROCESSING_TIME) / (float) data.get(DATA_MAX_OUTPUT_PROCESSING_TIME);
    }

    public boolean isInfinitelyQueued(FabricatorRecipeSlot slot) {
        return data.get(DATA_RECIPES_START + slot.getContainerSlot()) == -1;
    }

    public int getQueuedCount(FabricatorRecipeSlot slot) {
        return Math.max(0, data.get(DATA_RECIPES_START + slot.getContainerSlot()));
    }

    public boolean isMissingScrap() {
        return data.get(DATA_MISSING_SCRAP) == 1;
    }

    public boolean isMissingBiomass() {
        return data.get(DATA_MISSING_BIOMASS) == 1;
    }

    public boolean isMissingFragments() {
        return data.get(DATA_MISSING_FRAGMENTS) == 1;
    }
}

package net.blay09.mods.replikaentropie.menu;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.menu.slot.*;
import net.blay09.mods.replikaentropie.power.MakeshiftPsu;
import net.blay09.mods.replikaentropie.recipe.FabricatorRecipeDisplay;
import net.blay09.mods.replikaentropie.util.QuickMove;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class FabricatorMenu extends AbstractContainerMenu implements MakeshiftPoweredMenu {

    private final Inventory playerInventory;
    private final Container container;
    private final ContainerData data;
    private final MakeshiftPsu makeshiftPsu;
    private final List<FabricatorRecipeDisplay> displays;
    private final Container displayContainer = new SimpleContainer(7 * 4);
    private final QuickMove.Routing quickMove;

    public static final int DATA_OUTPUT_PROCESSING_TIME = 0;
    public static final int DATA_MAX_OUTPUT_PROCESSING_TIME = 1;
    public static final int DATA_CURRENT_POWER = 2;
    public static final int DATA_MAX_POWER = 3;
    public static final int DATA_MISSING_SCRAP = 4;
    public static final int DATA_MISSING_BIOMASS = 5;
    public static final int DATA_MISSING_FRAGMENTS = 6;
    public static final int DATA_RECIPES_START = 7;
    public static final int DATA_RECIPES_SIZE = 4 * 7;
    public static final int DATA_RECIPES_END = DATA_RECIPES_START + DATA_RECIPES_SIZE;
    public static final int DATA_OVERHEATED = DATA_RECIPES_END;

    public static final int DATA_COUNT = DATA_OVERHEATED + 1;

    public record Data(List<FabricatorRecipeDisplay> displays) {
        public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC = FabricatorRecipeDisplay.STREAM_CODEC
                .apply(ByteBufCodecs.list(DATA_RECIPES_SIZE))
                .map(Data::new, Data::displays);
    }

    public FabricatorMenu(int containerId, Inventory playerInventory, Data menuData) {
        this(containerId, playerInventory, new SimpleContainer(8), new SimpleContainerData(DATA_COUNT), ContainerLevelAccess.NULL, menuData.displays(), MakeshiftPsu.EMPTY);
    }

    public FabricatorMenu(int containerId, Inventory playerInventory, Container container, ContainerData data, ContainerLevelAccess access, List<FabricatorRecipeDisplay> displays, MakeshiftPsu makeshiftPsu) {
        super(ModMenus.fabricator.value(), containerId);
        this.playerInventory = playerInventory;
        this.container = container;
        checkContainerSize(container, 8);
        this.data = data;
        this.makeshiftPsu = makeshiftPsu;
        addDataSlots(data);
        this.displays = displays;

        addSlot(new OutputSlot(container, 0, 146, 99));
        addSlot(new ScrapSlot(container, 1, 37, 22));
        addSlot(new BiomassSlot(container, 2, 62, 22));
        addSlot(new FragmentsSlot(container, 3, 87, 22));

        for (int i = 0; i < 3; i++) {
            addSlot(new FabricatorBufferSlot(container, i + 4, 146, 45 + i * 16));
        }

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 7; j++) {
                addSlot(new FabricatorRecipeSlot(displayContainer, j + i * 7, 8 + j * 18, 49 + i * 18));
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
                .route(it -> it.is(ModItems.scrap.asItem()), QuickMove.PLAYER, "scrap")
                .route(it -> it.is(ModItems.biomass.asItem()), QuickMove.PLAYER, "biomass")
                .route(it -> it.is(ModItems.fragments.asItem()), QuickMove.PLAYER, "fragments")
                .build();

        container.startOpen(playerInventory.player);
    }

    private void updateRecipeDisplays() {
        final var displayContext = SlotDisplayContext.fromLevel(playerInventory.player.level());
        for (int i = 0; i < displayContainer.getContainerSize(); i++) {
            final var display = i < displays.size() ? displays.get(i) : null;
            displayContainer.setItem(i, display != null ? display.result().resolveForFirstStack(displayContext) : ItemStack.EMPTY);
        }
    }

    @Override
    public void clicked(int slotId, int button, ContainerInput clickType, Player player) {
        if (slotId >= 7 && slotId <= 34 && !player.level().isClientSide()) {
            int recipeIndex = slots.get(slotId).getContainerSlot();
            if (recipeIndex < displays.size()) {
                if (clickType == ContainerInput.PICKUP) {
                    data.set(DATA_RECIPES_START + recipeIndex, Mth.clamp(data.get(DATA_RECIPES_START + recipeIndex) + (button == InputConstants.MOUSE_BUTTON_RIGHT ? -1 : 1), 0, 64));
                } else if (clickType == ContainerInput.QUICK_MOVE) {
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

    public float getPowerProgress() {
        final var maxPower = data.get(DATA_MAX_POWER);
        if (maxPower <= 0) {
            return 0f;
        }

        return Mth.clamp(data.get(DATA_CURRENT_POWER) / (float) maxPower, 0f, 1f);
    }

    public int getCurrentPower() {
        return data.get(DATA_CURRENT_POWER);
    }

    public int getMaxPower() {
        return data.get(DATA_MAX_POWER);
    }

    @Override
    public boolean isMakeshiftPsuOverheated() {
        return data.get(DATA_OVERHEATED) != 0;
    }

    public boolean isInfinitelyQueued(FabricatorRecipeSlot slot) {
        return data.get(DATA_RECIPES_START + slot.getContainerSlot()) == -1;
    }

    public int getQueuedCount(FabricatorRecipeSlot slot) {
        return Math.max(0, data.get(DATA_RECIPES_START + slot.getContainerSlot()));
    }

    public @Nullable FabricatorRecipeDisplay getDisplay(FabricatorRecipeSlot slot) {
        final var containerSlot = slot.getContainerSlot();
        return containerSlot < displays.size() ? displays.get(containerSlot) : null;
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

    @Override
    public MakeshiftPsu getMakeshiftPsu() {
        return makeshiftPsu;
    }

}

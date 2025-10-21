package net.blay09.mods.replikaentropie.menu;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.blay09.mods.replikaentropie.core.analyzer.Analyzer;
import net.blay09.mods.replikaentropie.core.nonogram.NonogramLoader;
import net.blay09.mods.replikaentropie.core.research.Research;
import net.blay09.mods.replikaentropie.core.research.ResearchState;
import net.blay09.mods.replikaentropie.item.ModItems;
import net.blay09.mods.replikaentropie.menu.slot.ResearchCostSlot;
import net.blay09.mods.replikaentropie.menu.slot.ResearchEntrySlot;
import net.blay09.mods.replikaentropie.recipe.ModRecipes;
import net.blay09.mods.replikaentropie.recipe.ResearchRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Locale;

public class ResearchMenu extends AbstractContainerMenu {

    public enum MenuResearchState {
        MISSING_DEPENDENCIES(4),
        MISSING_INGREDIENTS(3),
        AVAILABLE(2),
        IN_PROGRESS(1),
        UNLOCKED(0);

        private final int priority;

        MenuResearchState(int priority) {
            this.priority = priority;
        }

        public int priority() {
            return priority;
        }

        public boolean requiresPayment() {
            return this == AVAILABLE || this == MISSING_INGREDIENTS || this == MISSING_DEPENDENCIES;
        }
    }

    public record StatefulResearchEntry(ResearchRecipe recipe, MenuResearchState state) {
        public static StatefulResearchEntry read(FriendlyByteBuf buf) {
            final var recipe = ModRecipes.researchSerializer.fromNetwork(buf.readResourceLocation(), buf);
            final var state = buf.readEnum(MenuResearchState.class);
            return new StatefulResearchEntry(recipe, state);
        }

        public static void write(FriendlyByteBuf buf, StatefulResearchEntry entry) {
            buf.writeResourceLocation(entry.recipe().id());
            ModRecipes.researchSerializer.toNetwork(buf, entry.recipe);
            buf.writeEnum(entry.state);
        }

        public Component title() {
            return Component.translatable(recipe.getId().toLanguageKey("research", "title"));
        }

        public Component description() {
            return Component.translatable(recipe.getId().toLanguageKey("research", "description"));
        }
    }

    public record Data(List<StatefulResearchEntry> entries, int dataCollected) {
        public static Data read(FriendlyByteBuf buf) {
            final var entries = buf.readList(StatefulResearchEntry::read);
            final var dataCollected = buf.readVarInt();
            return new Data(entries, dataCollected);
        }

        public void write(FriendlyByteBuf buf) {
            buf.writeCollection(entries, StatefulResearchEntry::write);
            buf.writeVarInt(dataCollected);
        }
    }

    private final List<ResearchEntrySlot> researchSlots = new ArrayList<>();
    private final ResearchCostSlot dataCostSlot;
    private final ResearchCostSlot scrapCostSlot;
    private final ResearchCostSlot biomassCostSlot;
    private final ResearchCostSlot fragmentsCostSlot;

    private final Inventory playerInventory;
    private final List<StatefulResearchEntry> researchEntries;
    private final int dataCollected;

    private final List<StatefulResearchEntry> filteredEntries;
    private final Comparator<StatefulResearchEntry> currentSorting =
            Comparator.comparingInt((StatefulResearchEntry it) -> it.state().priority())
                    .thenComparingInt(it -> it.recipe().sortOrder())
                    .thenComparing(it -> it.recipe().id());

    @Nullable
    private StatefulResearchEntry clientSelectedResearch;

    private boolean scrollOffsetDirty;
    private int scrollOffset;

    public ResearchMenu(int containerId, Inventory playerInventory, Data data) {
        super(ModMenus.research.get(), containerId);
        this.playerInventory = playerInventory;
        researchEntries = data.entries();
        dataCollected = data.dataCollected();

        filteredEntries = new ArrayList<>(researchEntries);
        filteredEntries.sort(currentSorting);

        dataCostSlot = new ResearchCostSlot(143, 133, ResearchCostSlot.Type.DATA);
        addSlot(dataCostSlot);
        scrapCostSlot = new ResearchCostSlot(169, 133, ResearchCostSlot.Type.SCRAP);
        addSlot(scrapCostSlot);
        biomassCostSlot = new ResearchCostSlot(194, 133, ResearchCostSlot.Type.BIOMASS);
        addSlot(biomassCostSlot);
        fragmentsCostSlot = new ResearchCostSlot(219, 133, ResearchCostSlot.Type.FRAGMENTS);
        addSlot(fragmentsCostSlot);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                final var slot = new ResearchEntrySlot(12 + j * (18 + 7), 25 + i * (18 + 7));
                researchSlots.add(slot);
                addSlot(slot);
            }
        }

        updateResearchSlots();
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        final var slot = slotId >= 0 && slotId < slots.size() ? slots.get(slotId) : null;
        if (player.level().isClientSide() && slot instanceof ResearchEntrySlot researchEntrySlot) {
            final var researchEntry = researchEntrySlot.getResearchEntry();
            if (researchEntry != null) {
                setClientSelectedResearch(researchEntry);
            }
        } else {
            super.clicked(slotId, button, clickType, player);
        }
    }

    private void setClientSelectedResearch(@Nullable StatefulResearchEntry researchEntry) {
        clientSelectedResearch = researchEntry;
        updateCostSlots(researchEntry);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        final var entry = buttonId >= 0 && buttonId < researchEntries.size() ? researchEntries.get(buttonId) : null;
        if (entry != null) {
            if (entry.state() == MenuResearchState.AVAILABLE) {
                final var recipe = entry.recipe();
                if (recipe != null && recipe.dataCost() > 0) {
                    Analyzer.getManager(player).grantData(player, -recipe.dataCost());
                }
                final var inventory = player.getInventory();
                if (recipe != null && recipe.scrapCost() > 0) {
                    inventory.clearOrCountMatchingItems(it -> it.is(ModItems.scrap), recipe.scrapCost(), inventory);
                }
                if (recipe != null && recipe.biomassCost() > 0) {
                    inventory.clearOrCountMatchingItems(it -> it.is(ModItems.biomass), recipe.biomassCost(), inventory);
                }
                if (recipe != null && recipe.fragmentsCost() > 0) {
                    inventory.clearOrCountMatchingItems(it -> it.is(ModItems.fragments), recipe.fragmentsCost(), inventory);
                }
                player.inventoryMenu.broadcastChanges();
                Research.updateResearch(player, entry.recipe().id(), ResearchState.IN_PROGRESS);
                openNonogram(player, entry);
            } else if (entry.state() == MenuResearchState.IN_PROGRESS) {
                openNonogram(player, entry);
            } else if (entry.state() == MenuResearchState.UNLOCKED) {
                final var itemStack = printAssemblyTicket(entry);
                if (!player.addItem(itemStack)) {
                    player.drop(itemStack, false);
                }
                player.inventoryMenu.broadcastChanges();
            }
        }
        return false;
    }

    private ItemStack printAssemblyTicket(StatefulResearchEntry entry) {
        final var itemStack = new ItemStack(ModItems.assemblyTicket);
        itemStack.setHoverName(entry.title());
        final var itemData = itemStack.getOrCreateTag();
        final var recipe = entry.recipe();
        if (recipe != null && !recipe.unlockedRecipes().isEmpty()) {
            itemData.putString("ReplikaEntropieAssemblerResult", recipe.unlockedRecipes().get(0).toString());
        }
        itemData.putInt("ReplikaEntropieAssemblerUsesLeft", 1);
        return itemStack;
    }

    public void openNonogram(Player player, StatefulResearchEntry entry) {
        Balm.getNetworking().openMenu(player, new BalmMenuProvider() {
            @Override
            public Component getDisplayName() {
                return entry.title();
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
                final var researchId = entry.recipe().id();
                final var nonogram = NonogramLoader.getNonogram(entry.recipe().nonogram())
                        .orElseGet(NonogramLoader::createFallback);
                final var nonogramState = Research.getNonogramState(player, researchId)
                        .map(nonogram::ensureState)
                        .orElseGet(nonogram::createState);
                return new NonogramResearchMenu(containerId, inventory, nonogram, nonogramState, researchId);
            }

            @Override
            public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
                final var nonogram = NonogramLoader.getNonogram(entry.recipe().nonogram())
                        .orElseGet(NonogramLoader::createFallback);
                final var nonogramState = Research.getNonogramState(player, entry.recipe().id())
                        .map(nonogram::ensureState)
                        .orElseGet(nonogram::createState);
                new NonogramMenu.Data(nonogram.clues(), nonogramState).write(buf);
            }
        });
    }

    public void updateResearchSlots() {
        int i = scrollOffset * 4;
        for (final var slot : researchSlots) {
            if (i < filteredEntries.size()) {
                final var entry = filteredEntries.get(i);
                slot.setResearchEntry(entry);
                i++;
            } else {
                slot.setResearchEntry(null);
            }
        }
    }

    public void updateCostSlots(@Nullable StatefulResearchEntry statefulEntry) {
        if (statefulEntry == null) {
            dataCostSlot.setCost(0);
            scrapCostSlot.setCost(0);
            biomassCostSlot.setCost(0);
            fragmentsCostSlot.setCost(0);
            return;
        }

        final var recipe = statefulEntry.recipe();
        final var data = recipe != null ? recipe.dataCost() : 0;
        final var scrap = recipe != null ? recipe.scrapCost() : 0;
        final var biomass = recipe != null ? recipe.biomassCost() : 0;
        final var fragments = recipe != null ? recipe.fragmentsCost() : 0;
        dataCostSlot.setCost(statefulEntry.state().requiresPayment() ? data : 0);
        dataCostSlot.setAvailable(Mth.clamp(dataCollected, 0, data));
        scrapCostSlot.setCost(statefulEntry.state().requiresPayment() ? scrap : 0);
        scrapCostSlot.setAvailable(playerInventory.countItem(ModItems.scrap));
        biomassCostSlot.setCost(statefulEntry.state().requiresPayment() ? biomass : 0);
        biomassCostSlot.setAvailable(playerInventory.countItem(ModItems.biomass));
        fragmentsCostSlot.setCost(statefulEntry.state().requiresPayment() ? fragments : 0);
        fragmentsCostSlot.setAvailable(playerInventory.countItem(ModItems.fragments));
    }

    public void setScrollOffset(int scrollOffset) {
        this.scrollOffset = scrollOffset;
        updateResearchSlots();
    }

    public int getEntryCount() {
        return filteredEntries.size();
    }

    public boolean isScrollOffsetDirty() {
        return scrollOffsetDirty;
    }

    public void setScrollOffsetDirty(boolean dirty) {
        scrollOffsetDirty = dirty;
    }

    @Nullable
    public StatefulResearchEntry getClientSelectedResearch() {
        return clientSelectedResearch;
    }

    public int getClientSelectedResearchIndex() {
        return researchEntries.indexOf(clientSelectedResearch);
    }

    public boolean clientCanUnlockSelected() {
        return clientSelectedResearch != null
                && clientSelectedResearch.state() == MenuResearchState.AVAILABLE;
    }

    public boolean clientCanDecryptSelected() {
        return clientSelectedResearch != null
                && clientSelectedResearch.state() == MenuResearchState.IN_PROGRESS;
    }

    public boolean clientCanPrintSelected() {
        if (clientSelectedResearch == null || clientSelectedResearch.state() != MenuResearchState.UNLOCKED) {
            return false;
        }

        final var recipe = clientSelectedResearch.recipe();
        return recipe != null
                && recipe.type() == ResearchRecipe.Type.ASSEMBLER
                && !recipe.unlockedRecipes().isEmpty();
    }

    public boolean clientMissingIngredients() {
        return clientSelectedResearch != null
                && clientSelectedResearch.state() == ResearchMenu.MenuResearchState.MISSING_INGREDIENTS;
    }

    public int getDataCollected() {
        return dataCollected;
    }

    public void setSearchQuery(String query) {
        final var lowercaseQuery = query != null ? query.trim().toLowerCase(Locale.ROOT) : "";
        filteredEntries.clear();
        if (lowercaseQuery.isEmpty()) {
            filteredEntries.addAll(researchEntries);
        } else {
            for (final var entry : researchEntries) {
                final var title = entry.title().getString().toLowerCase(Locale.ROOT);
                if (title.contains(lowercaseQuery)) {
                    filteredEntries.add(entry);
                }
            }
        }

        filteredEntries.sort(currentSorting);
        setScrollOffset(0);
        setScrollOffsetDirty(true);
    }

    public int getFilteredIndexOf(ResourceLocation id) {
        for (int i = 0; i < filteredEntries.size(); i++) {
            final var entry = filteredEntries.get(i);
            if (entry.recipe() != null && entry.recipe().id().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    public static class Provider implements BalmMenuProvider {

        @Override
        public Component getDisplayName() {
            return Component.translatable("container.replikaentropie.sky_scraper");
        }

        @Override
        public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
            return new ResearchMenu(containerId, inventory, createMenuData(player));
        }

        private static ResearchMenu.Data createMenuData(Player player) {
            final var dataCollected = Analyzer.getManager(player).getDataCollected(player);
            final var recipeManager = player.level().getRecipeManager();
            final var researchManager = Research.getManager(player);

            final var entries = recipeManager.getAllRecipesFor(ModRecipes.researchType).stream()
                    .map(recipe -> {
                        final var id = recipe.getId();
                        final var state = researchManager.getResearchState(player, id);
                        if (state == ResearchState.NONE) {
                            if (!researchManager.meetsDependencies(player, recipe.hardDependencies())) {
                                return null;
                            }
                        }

                        final var menuState = switch (state) {
                            case NONE -> {
                                if (!researchManager.meetsDependencies(player, recipe.softDependencies())) {
                                    yield MenuResearchState.MISSING_DEPENDENCIES;
                                }
                                if (!canAfford(player, recipe)) {
                                    yield MenuResearchState.MISSING_INGREDIENTS;
                                }
                                yield MenuResearchState.AVAILABLE;
                            }
                            case IN_PROGRESS -> MenuResearchState.IN_PROGRESS;
                            default -> MenuResearchState.UNLOCKED;
                        };

                        return new ResearchMenu.StatefulResearchEntry(recipe, menuState);
                    })
                    .filter(Objects::nonNull)
                    .toList();
            return new ResearchMenu.Data(entries, dataCollected);
        }

        private static boolean canAfford(Player player, ResearchRecipe recipe) {
            final var dataCollected = Analyzer.getManager(player).getDataCollected(player);
            final var scrap = player.getInventory().countItem(ModItems.scrap);
            final var biomass = player.getInventory().countItem(ModItems.biomass);
            final var fragments = player.getInventory().countItem(ModItems.fragments);
            return dataCollected >= recipe.dataCost()
                    && scrap >= recipe.scrapCost()
                    && biomass >= recipe.biomassCost()
                    && fragments >= recipe.fragmentsCost();
        }

        @Override
        public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
            createMenuData(player).write(buf);
        }
    }

}

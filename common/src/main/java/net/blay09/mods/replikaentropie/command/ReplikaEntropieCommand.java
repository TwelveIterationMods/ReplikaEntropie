package net.blay09.mods.replikaentropie.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.blay09.mods.replikaentropie.core.analyzer.Analyzer;
import net.blay09.mods.replikaentropie.core.nonogram.Nonogram;
import net.blay09.mods.replikaentropie.core.nonogram.NonogramLoader;
import net.blay09.mods.replikaentropie.core.research.Research;
import net.blay09.mods.replikaentropie.core.research.ResearchState;
import net.blay09.mods.replikaentropie.menu.NonogramEditorMenu;
import net.blay09.mods.replikaentropie.menu.NonogramMenu;
import net.blay09.mods.replikaentropie.recipe.ModRecipes;
import net.blay09.mods.replikaentropie.recipe.ResearchRecipe;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.Recipe;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class ReplikaEntropieCommand {

    private static final SimpleCommandExceptionType INVALID_RECIPE = new SimpleCommandExceptionType(Component.translatable("commands.replikaentropie.research.invalidRecipe"));
    private static final SimpleCommandExceptionType INVALID_NONOGRAM = new SimpleCommandExceptionType(Component.translatable("commands.replikaentropie.nonogram.invalidNonogram"));

    // POSTJAM need a sided proxy here because SharedSuggestionProvider does not have level access
    public static final SuggestionProvider<CommandSourceStack> RESEARCH_RECIPES = SuggestionProviders.register(id("research_recipes"), (context, builder) -> SharedSuggestionProvider.suggestResource(context.getSource().getRecipeNames(), builder));
    public static final SuggestionProvider<CommandSourceStack> NONOGRAMS = SuggestionProviders.register(id("nonograms"), (context, builder) -> SharedSuggestionProvider.suggestResource(NonogramLoader.getNonogramIds(), builder));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("replikaentropie")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("scans")
                        .then(Commands.literal("reset")
                                .executes(ReplikaEntropieCommand::resetScans)))
                .then(Commands.literal("events")
                        .then(Commands.literal("reset")
                                .executes(ReplikaEntropieCommand::resetEvents)))
                .then(Commands.literal("research")
                        .then(Commands.literal("unlock")
                                .then(Commands.argument("id", ResourceLocationArgument.id())
                                        .suggests(RESEARCH_RECIPES)
                                        .executes(ctx -> researchUnlock(ctx, ResourceLocationArgument.getRecipe(ctx, "id"))))
                                .then(Commands.literal("all").executes(ReplikaEntropieCommand::researchUnlockAll))
                        )
                        .then(Commands.literal("reset")
                                .then(Commands.argument("id", ResourceLocationArgument.id())
                                        .suggests(RESEARCH_RECIPES)
                                        .executes(ctx -> researchReset(ctx, ResourceLocationArgument.getRecipe(ctx, "id"))))
                                .then(Commands.literal("all").executes(ReplikaEntropieCommand::researchResetAll))
                        )
                )
                .then(Commands.literal("nonogram")
                        .then(Commands.literal("create")
                                .then(Commands.argument("width", IntegerArgumentType.integer(1, 15))
                                        .then(Commands.argument("height", IntegerArgumentType.integer(1, 10))
                                                .then(Commands.argument("id", ResourceLocationArgument.id())
                                                        .executes(ctx -> createNonogram(ctx, ResourceLocationArgument.getId(ctx, "id"), IntegerArgumentType.getInteger(ctx, "width"), IntegerArgumentType.getInteger(ctx, "height")))
                                                ))))
                        .then(Commands.literal("view")
                                .then(Commands.argument("id", ResourceLocationArgument.id())
                                        .suggests(NONOGRAMS)
                                        .executes(ctx -> viewNonogram(ctx, ResourceLocationArgument.getId(ctx, "id")))
                                ))
                        .then(Commands.literal("play")
                                .then(Commands.argument("id", ResourceLocationArgument.id())
                                        .suggests(NONOGRAMS)
                                        .executes(ctx -> playNonogram(ctx, ResourceLocationArgument.getId(ctx, "id")))
                                ))
                        .then(Commands.literal("edit")
                                .then(Commands.argument("id", ResourceLocationArgument.id())
                                        .suggests(NONOGRAMS)
                                        .executes(ctx -> editNonogram(ctx, ResourceLocationArgument.getId(ctx, "id")))))
                )
        );
    }

    private static int resetScans(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final var player = context.getSource().getPlayerOrException();
        Analyzer.resetAllAnalyzed(player);
        return Command.SINGLE_SUCCESS;
    }

    private static int resetEvents(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final var player = context.getSource().getPlayerOrException();
        Analyzer.resetDataMinedEvents(player);
        return Command.SINGLE_SUCCESS;
    }

    private static int researchUnlock(CommandContext<CommandSourceStack> context, Recipe<?> recipe) throws CommandSyntaxException {
        final var player = context.getSource().getPlayerOrException();
        if (!(recipe instanceof ResearchRecipe researchRecipe)) {
            throw INVALID_RECIPE.create();
        }

        Research.updateResearch(player, researchRecipe.id(), ResearchState.UNLOCKED);
        return Command.SINGLE_SUCCESS;
    }

    private static int researchUnlockAll(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final var player = context.getSource().getPlayerOrException();
        final var recipeManager = context.getSource().getLevel().getRecipeManager();
        recipeManager.getAllRecipesFor(ModRecipes.researchType).forEach(r ->
                Research.updateResearch(player, r.getId(), ResearchState.UNLOCKED)
        );
        return Command.SINGLE_SUCCESS;
    }

    private static int researchReset(CommandContext<CommandSourceStack> context, Recipe<?> recipe) throws CommandSyntaxException {
        final var player = context.getSource().getPlayerOrException();
        if (!(recipe instanceof ResearchRecipe researchRecipe)) {
            throw INVALID_RECIPE.create();
        }
        Research.updateResearch(player, researchRecipe.id(), ResearchState.NONE);
        return Command.SINGLE_SUCCESS;
    }

    private static int researchResetAll(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final var player = context.getSource().getPlayerOrException();
        Research.resetAllResearch(player);
        return Command.SINGLE_SUCCESS;
    }

    private static int createNonogram(CommandContext<CommandSourceStack> context, ResourceLocation id, int width, int height) throws CommandSyntaxException {
        final var player = context.getSource().getPlayerOrException();
        final var nonogram = new Nonogram(width, height, new int[width * height]);

        Balm.getNetworking().openMenu(player, new BalmMenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.literal(id.toString());
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
                return new NonogramEditorMenu(containerId, nonogram, nonogram.createState(), id);
            }

            @Override
            public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
                new NonogramMenu.Data(nonogram.clues(), nonogram.createState()).write(buf);
            }
        });

        return Command.SINGLE_SUCCESS;
    }

    private static int viewNonogram(CommandContext<CommandSourceStack> context, ResourceLocation id) throws CommandSyntaxException {
        final var player = context.getSource().getPlayerOrException();
        final var nonogram = NonogramLoader.getNonogram(id).orElseThrow(INVALID_NONOGRAM::create);

        Balm.getNetworking().openMenu(player, new BalmMenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.literal(id.toString());
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
                return new NonogramMenu(containerId, inventory, nonogram, nonogram.createState());
            }

            @Override
            public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
                new NonogramMenu.Data(nonogram.clues(), nonogram.createCompletedState()).write(buf);
            }
        });

        return Command.SINGLE_SUCCESS;
    }

    private static int playNonogram(CommandContext<CommandSourceStack> context, ResourceLocation id) throws CommandSyntaxException {
        final var player = context.getSource().getPlayerOrException();
        final var nonogram = NonogramLoader.getNonogram(id).orElseThrow(INVALID_NONOGRAM::create);

        Balm.getNetworking().openMenu(player, new BalmMenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.literal(id.toString());
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
                return new NonogramMenu(containerId, inventory, nonogram, nonogram.createState());
            }

            @Override
            public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
                new NonogramMenu.Data(nonogram.clues(), nonogram.createState()).write(buf);
            }
        });

        return Command.SINGLE_SUCCESS;
    }

    private static int editNonogram(CommandContext<CommandSourceStack> context, ResourceLocation id) throws CommandSyntaxException {
        final var player = context.getSource().getPlayerOrException();
        final var nonogram = NonogramLoader.getNonogram(id).orElseThrow(INVALID_NONOGRAM::create);

        Balm.getNetworking().openMenu(player, new BalmMenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.literal(id.toString());
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
                return new NonogramEditorMenu(containerId, nonogram, nonogram.createCompletedState(), id);
            }

            @Override
            public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
                new NonogramMenu.Data(nonogram.clues(), nonogram.createCompletedState()).write(buf);
            }
        });

        return Command.SINGLE_SUCCESS;
    }

}


package net.blay09.mods.replikaentropie.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.world.BalmMenuProvider;
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
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class ReplikaEntropieCommand {

    private static final SimpleCommandExceptionType INVALID_RECIPE = new SimpleCommandExceptionType(Component.translatable("commands.replikaentropie.research.invalidRecipe"));
    private static final SimpleCommandExceptionType INVALID_NONOGRAM = new SimpleCommandExceptionType(Component.translatable("commands.replikaentropie.nonogram.invalidNonogram"));

    // POSTJAM need a sided proxy here because SharedSuggestionProvider does not have level access
    public static final SuggestionProvider<CommandSourceStack> NONOGRAMS = SuggestionProviders.register(id("nonograms"), (context, builder) -> SharedSuggestionProvider.suggestResource(NonogramLoader.getNonogramIds(), builder));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("replikaentropie")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.literal("scans")
                        .then(Commands.literal("reset")
                                .executes(ReplikaEntropieCommand::resetScans)))
                .then(Commands.literal("events")
                        .then(Commands.literal("reset")
                                .executes(ReplikaEntropieCommand::resetEvents)))
                .then(Commands.literal("research")
                        .then(Commands.literal("unlock")
                                .then(Commands.argument("id", ResourceKeyArgument.key(Registries.RECIPE))
                                        .executes(ctx -> researchUnlock(ctx, ResourceKeyArgument.getRecipe(ctx, "id"))))
                                .then(Commands.literal("all").executes(ReplikaEntropieCommand::researchUnlockAll))
                        )
                        .then(Commands.literal("reset")
                                .then(Commands.argument("id", IdentifierArgument.id())
                                        .executes(ctx -> researchReset(ctx, ResourceKeyArgument.getRecipe(ctx, "id"))))
                                .then(Commands.literal("all").executes(ReplikaEntropieCommand::researchResetAll))
                        )
                )
                .then(Commands.literal("nonogram")
                        .then(Commands.literal("create")
                                .then(Commands.argument("width", IntegerArgumentType.integer(1, 15))
                                        .then(Commands.argument("height", IntegerArgumentType.integer(1, 10))
                                                .then(Commands.argument("id", IdentifierArgument.id())
                                                        .executes(ctx -> createNonogram(ctx, IdentifierArgument.getId(ctx, "id"), IntegerArgumentType.getInteger(ctx, "width"), IntegerArgumentType.getInteger(ctx, "height")))
                                                ))))
                        .then(Commands.literal("view")
                                .then(Commands.argument("id", IdentifierArgument.id())
                                        .suggests(NONOGRAMS)
                                        .executes(ctx -> viewNonogram(ctx, IdentifierArgument.getId(ctx, "id")))
                                ))
                        .then(Commands.literal("play")
                                .then(Commands.argument("id", IdentifierArgument.id())
                                        .suggests(NONOGRAMS)
                                        .executes(ctx -> playNonogram(ctx, IdentifierArgument.getId(ctx, "id")))
                                ))
                        .then(Commands.literal("edit")
                                .then(Commands.argument("id", IdentifierArgument.id())
                                        .suggests(NONOGRAMS)
                                        .executes(ctx -> editNonogram(ctx, IdentifierArgument.getId(ctx, "id")))))
                )
        );
    }

    private static int resetScans(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final var player = context.getSource().getPlayerOrException();
        Analyzer.resetAllAnalyzed(player);
        context.getSource().sendSuccess(() -> Component.translatable("commands.replikaentropie.scans.reset"), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int resetEvents(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final var player = context.getSource().getPlayerOrException();
        Analyzer.resetDataMinedEvents(player);
        context.getSource().sendSuccess(() -> Component.translatable("commands.replikaentropie.events.reset"), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int researchUnlock(CommandContext<CommandSourceStack> context, RecipeHolder<?> recipe) throws CommandSyntaxException {
        final var player = context.getSource().getPlayerOrException();
        if (!(recipe.value() instanceof ResearchRecipe researchRecipe)) {
            throw INVALID_RECIPE.create();
        }

        final var researchId = getResearchRecipeId(context, researchRecipe);
        Research.updateResearch(player, researchId, ResearchState.UNLOCKED);
        context.getSource().sendSuccess(() -> Component.translatable("commands.replikaentropie.research.unlock", researchId.toString()), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int researchUnlockAll(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final var player = context.getSource().getPlayerOrException();
        final var recipeManager = context.getSource().getLevel().recipeAccess();
        recipeManager.getRecipes().stream()
                .filter(holder -> holder.value().getType() == ModRecipes.research.type())
                .forEach(holder -> Research.updateResearch(player, holder.id().identifier(), ResearchState.UNLOCKED));
        context.getSource().sendSuccess(() -> Component.translatable("commands.replikaentropie.research.unlockAll"), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int researchReset(CommandContext<CommandSourceStack> context, RecipeHolder<?> recipe) throws CommandSyntaxException {
        final var player = context.getSource().getPlayerOrException();
        if (!(recipe.value() instanceof ResearchRecipe researchRecipe)) {
            throw INVALID_RECIPE.create();
        }
        final var researchId = getResearchRecipeId(context, researchRecipe);
        Research.updateResearch(player, researchId, ResearchState.NONE);
        context.getSource().sendSuccess(() -> Component.translatable("commands.replikaentropie.research.reset", researchId.toString()), false);
        return Command.SINGLE_SUCCESS;
    }

    private static Identifier getResearchRecipeId(CommandContext<CommandSourceStack> context, ResearchRecipe researchRecipe) throws CommandSyntaxException {
        return context.getSource().getLevel().recipeAccess().getRecipes().stream()
                .filter(holder -> holder.value() == researchRecipe)
                .findFirst()
                .map(holder -> holder.id().identifier())
                .orElseThrow(INVALID_RECIPE::create);
    }

    private static int researchResetAll(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final var player = context.getSource().getPlayerOrException();
        Research.resetAllResearch(player);
        context.getSource().sendSuccess(() -> Component.translatable("commands.replikaentropie.research.resetAll"), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int createNonogram(CommandContext<CommandSourceStack> context, Identifier id, int width, int height) throws CommandSyntaxException {
        final var player = context.getSource().getPlayerOrException();
        final var nonogram = new Nonogram(width, height, new int[width * height]);

        Balm.networking().openMenu(player, new BalmMenuProvider<NonogramMenu.Data>() {
            @Override
            public Component getDisplayName() {
                return Component.literal(id.toString());
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
                return new NonogramEditorMenu(containerId, nonogram, nonogram.createState(), id);
            }

            @Override
            public NonogramMenu.Data getScreenOpeningData(ServerPlayer player) {
                return new NonogramMenu.Data(nonogram.clues(), nonogram.createState());
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, NonogramMenu.Data> getScreenStreamCodec() {
                return NonogramMenu.Data.STREAM_CODEC;
            }
        });

        context.getSource().sendSuccess(() -> Component.translatable("commands.replikaentropie.nonogram.create", id.toString(), width, height), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int viewNonogram(CommandContext<CommandSourceStack> context, Identifier id) throws CommandSyntaxException {
        final var player = context.getSource().getPlayerOrException();
        final var nonogram = NonogramLoader.getNonogram(id).orElseThrow(INVALID_NONOGRAM::create);

        Balm.networking().openMenu(player, new BalmMenuProvider<NonogramMenu.Data>() {
            @Override
            public Component getDisplayName() {
                return Component.literal(id.toString());
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
                return new NonogramMenu(containerId, inventory, nonogram, nonogram.createState());
            }

            @Override
            public NonogramMenu.Data getScreenOpeningData(ServerPlayer player) {
                return new NonogramMenu.Data(nonogram.clues(), nonogram.createCompletedState());
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, NonogramMenu.Data> getScreenStreamCodec() {
                return NonogramMenu.Data.STREAM_CODEC;
            }
        });

        context.getSource().sendSuccess(() -> Component.translatable("commands.replikaentropie.nonogram.view", id.toString()), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int playNonogram(CommandContext<CommandSourceStack> context, Identifier id) throws CommandSyntaxException {
        final var player = context.getSource().getPlayerOrException();
        final var nonogram = NonogramLoader.getNonogram(id).orElseThrow(INVALID_NONOGRAM::create);

        Balm.networking().openMenu(player, new BalmMenuProvider<NonogramMenu.Data>() {
            @Override
            public Component getDisplayName() {
                return Component.literal(id.toString());
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
                return new NonogramMenu(containerId, inventory, nonogram, nonogram.createState());
            }

            @Override
            public NonogramMenu.Data getScreenOpeningData(ServerPlayer player) {
                return new NonogramMenu.Data(nonogram.clues(), nonogram.createState());
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, NonogramMenu.Data> getScreenStreamCodec() {
                return NonogramMenu.Data.STREAM_CODEC;
            }
        });

        context.getSource().sendSuccess(() -> Component.translatable("commands.replikaentropie.nonogram.play", id.toString()), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int editNonogram(CommandContext<CommandSourceStack> context, Identifier id) throws CommandSyntaxException {
        final var player = context.getSource().getPlayerOrException();
        final var nonogram = NonogramLoader.getNonogram(id).orElseThrow(INVALID_NONOGRAM::create);

        Balm.networking().openMenu(player, new BalmMenuProvider<NonogramMenu.Data>() {
            @Override
            public Component getDisplayName() {
                return Component.literal(id.toString());
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
                return new NonogramEditorMenu(containerId, nonogram, nonogram.createCompletedState(), id);
            }

            @Override
            public NonogramMenu.Data getScreenOpeningData(ServerPlayer player) {
                return new NonogramMenu.Data(nonogram.clues(), nonogram.createCompletedState());
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, NonogramMenu.Data> getScreenStreamCodec() {
                return NonogramMenu.Data.STREAM_CODEC;
            }
        });

        context.getSource().sendSuccess(() -> Component.translatable("commands.replikaentropie.nonogram.edit", id.toString()), false);
        return Command.SINGLE_SUCCESS;
    }

}

package dev.fooble.minesweeper.dialogues;

import dev.fooble.mc.components.ComponentBuilder;
import dev.fooble.mc.dialogue.DialogueContent;
import dev.fooble.mc.dialogue.DialogueEventHandler;
import dev.fooble.mc.dialogue.DialogueHeader;
import dev.fooble.minesweeper.Plugin;
import dev.fooble.minesweeper.game.GameManager;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StartGameDialogue implements DialogueContent {

    private final Plugin plugin;

    public StartGameDialogue(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull Component getTitle() {
        return ComponentBuilder.of("<gradient:#57FF7E:#19C6A3>Minesweeper</gradient>");
    }

    @Override
    public @Nullable DialogueHeader getHeader() {
        return new DialogueHeader(
                ComponentBuilder.of("<white><green>Start</green> a new game of <gradient:#57FF7E:#19C6A3>Minesweeper</gradient>! Use the options below to <aqua>configure</aqua> your game.</white>"),
                null
        );
    }

    @Override
    public @NotNull List<DialogInput> getInputs() {
        return List.of(
                DialogInput.numberRange(
                        "size",
                        ComponentBuilder.of("<yellow>Field Size"),
                        5,
                        30
                ).initial(10f).step(1f).build(),
                DialogInput.numberRange(
                        "mines",
                        ComponentBuilder.of("<color:#ff616e>Amount of Mines</color>"),
                        3,
                        900
                ).initial(20f).step(1f).build()
        );
    }

    @Override
    public @NotNull DialogType getDialogType() {
        return DialogType.confirmation(
                ActionButton.create(ComponentBuilder.of("<green>Confirm"), null, 100, DialogAction.customClick(Key.key("fooble:dialog/minesweeper/confirm"), null)),
                ActionButton.create(ComponentBuilder.of("<red>Cancel"), null, 100, null)
        );
    }

    @Override
    public void registerActions(@NotNull DialogueEventHandler dialogueEventHandler) {
        dialogueEventHandler.register("fooble:dialog/minesweeper/confirm", (player, view) -> {
            final float size = view.getFloat("size");
            final float mines = view.getFloat("mines");

            if(mines >= size * size) {
                player.sendRichMessage("<gradient:#FF5757:#C61919>The number of mines must be less than the total number of cells!</gradient>");
                return;
            }

            try {
                final int sizeInt = (int) size;
                final int minesInt = (int) mines;

                GameManager.startGame(player, sizeInt, minesInt, plugin);
            } catch(NumberFormatException e) {
                player.sendRichMessage("<gradient:#FF5757:#C61919>Invalid input values!</gradient>");
            }
        });
    }
}

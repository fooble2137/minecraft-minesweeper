package dev.fooble.minesweeper.commands;

import dev.fooble.mc.dialogue.Dialogue;
import dev.fooble.minesweeper.Plugin;
import dev.fooble.minesweeper.dialogues.StartGameDialogue;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Stream;

public class MinesweeperCommand extends Command {

    private final Plugin plugin;

    public MinesweeperCommand(Plugin plugin) {
        super("minesweeper");

        setDescription("Start a game of Minesweeper");
        setUsage("/minesweeper <exit>");
        setPermission("minesweeper.use");

        this.plugin = plugin;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String @NotNull [] args) {
        if(!(sender instanceof Player player)) {
            sender.sendRichMessage("<gradient:#FF5757:#C61919>Only a player can run this command!</gradient>");
            return true;
        }

        if(!sender.hasPermission("minesweeper.use")) {
            sender.sendRichMessage("<gradient:#FF5757:#C61919>You do not have permission to use this command.</gradient>");
            return true;
        }

        if(args.length > 1) {
            sender.sendRichMessage("<gradient:#FF5757:#C61919>Incorrect usage! Please use:</gradient> <color:#ff9c9d>/minesweeper <exit></color>");
            return true;
        }

        if(args.length == 0) {
            new Dialogue(new StartGameDialogue(plugin.getGameManager()), plugin).open(player);
            return true;
        }

        if(!args[0].equalsIgnoreCase("exit")) {
            sender.sendRichMessage("<gradient:#FF5757:#C61919>Incorrect usage! Please use:</gradient> <color:#ff9c9d>/minesweeper <exit></color>");
            return true;
        }

        plugin.getGameManager().endGame(player);
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String @NotNull [] args) throws IllegalArgumentException {
        if(args.length == 1) {
            return Stream.of("exit").filter(s -> s.startsWith(args[0].toLowerCase())).toList();
        }

        return List.of();
    }
}
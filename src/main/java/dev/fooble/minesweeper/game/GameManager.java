package dev.fooble.minesweeper.game;

import dev.fooble.minesweeper.Plugin;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class GameManager {

    private static final HashMap<Player, Game> activeGames = new HashMap<>();

    public static void startGame(Player player, int gameSize, int mineCount, Plugin plugin) {
        if(activeGames.containsKey(player)) {
            player.sendRichMessage("<gradient:#FF5757:#C61919>A game is already in progress! Please exit the current game before starting a new one.</gradient>");
            return;
        }

        final Game game = new Game(player, gameSize, mineCount, plugin);
        game.startGame();
        activeGames.put(player, game);
    }

    public static void endGame(Player player) {
        final Game game = activeGames.remove(player);
        if(game != null) {
            game.stopGame();
        } else {
            player.sendRichMessage("<gradient:#FF5757:#C61919>You are not currently in a game.</gradient>");
        }
    }

    public static void endGameSilently(Player player) {
        final Game game = activeGames.remove(player);
        if(game != null) {
            game.stopGame();
        }
    }

    public static void endAllGames() {
        for(Player player : activeGames.keySet()) {
            final Game game = activeGames.get(player);
            if(game != null) {
                game.stopGame();
            }
        }
        activeGames.clear();
    }

    public static Game getGame(Player player) {
        return activeGames.get(player);
    }

    public static boolean isInGame(Player player) {
        return activeGames.containsKey(player);
    }

}
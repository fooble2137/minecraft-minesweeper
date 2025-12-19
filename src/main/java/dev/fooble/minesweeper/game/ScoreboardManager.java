package dev.fooble.minesweeper.game;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ScoreboardManager {

    private static final Map<Player, GameScoreboard> scoreboard = new HashMap<>();

    public static void createScoreboard(Player player, GameManager gameManager) {
        GameScoreboard gameScoreboard = new GameScoreboard(player, gameManager);
        scoreboard.put(player, gameScoreboard);
        gameScoreboard.show();
        gameScoreboard.update();
    }

    public static void removeScoreboard(Player player) {
        if(scoreboard.containsKey(player)) {
            scoreboard.get(player).remove();
            scoreboard.remove(player);
        }
    }

    public static void updateScoreboard(Player player) {
        if(scoreboard.containsKey(player)) {
            scoreboard.get(player).update();
        }
    }

    public static void removeAllScoreboards() {
        final Set<Player> players = scoreboard.keySet();
        for(Player player : players) {
            scoreboard.get(player).remove();
        }
        scoreboard.clear();
    }

}
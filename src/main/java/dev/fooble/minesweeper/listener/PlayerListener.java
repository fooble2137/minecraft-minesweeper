package dev.fooble.minesweeper.listener;

import dev.fooble.minesweeper.game.GameManager;
import dev.fooble.minesweeper.game.ScoreboardManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        ScoreboardManager.removeScoreboard(event.getPlayer());
        GameManager.endGameSilently(event.getPlayer());
    }

}
package dev.fooble.minesweeper;

import dev.fooble.minesweeper.commands.MinesweeperCommand;
import dev.fooble.minesweeper.game.GameManager;
import dev.fooble.minesweeper.game.GameTile;
import dev.fooble.minesweeper.game.ScoreboardManager;
import dev.fooble.minesweeper.listener.InteractListener;
import dev.fooble.minesweeper.listener.PlayerListener;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {

    private final GameManager gameManager = new GameManager(this);

    @Override
    public void onEnable() {
        getServer().getCommandMap().register(
                "minesweeper",
                new MinesweeperCommand(this)
        );

        getServer().getPluginManager().registerEvents(
                new InteractListener(this),
                this
        );
        getServer().getPluginManager().registerEvents(
                new PlayerListener(),
                this
        );
    }

    @Override
    public void onDisable() {
        gameManager.resetGame(true);

        ScoreboardManager.removeAllScoreboards();
    }

    public GameManager getGameManager() {
        return gameManager;
    }
}
package dev.fooble.minesweeper;

import dev.fooble.mc.worlds.impl.PaperWorldManager;
import dev.fooble.minesweeper.commands.MinesweeperCommand;
import dev.fooble.minesweeper.game.GameManager;
import dev.fooble.minesweeper.game.ScoreboardManager;
import dev.fooble.minesweeper.listener.InteractListener;
import dev.fooble.minesweeper.listener.PlayerListener;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class Plugin extends JavaPlugin {

    private final PaperWorldManager worldManager = new PaperWorldManager();

    @Override
    public void onEnable() {
        getServer().getCommandMap().register(
                "minesweeper",
                new MinesweeperCommand(this)
        );

        getServer().getPluginManager().registerEvents(
                new InteractListener(),
                this
        );
        getServer().getPluginManager().registerEvents(
                new PlayerListener(),
                this
        );
    }

    @Override
    public void onDisable() {
        GameManager.endAllGames();

        ScoreboardManager.removeAllScoreboards();
    }

}
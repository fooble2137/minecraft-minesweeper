package dev.fooble.minesweeper.game;

import dev.fooble.mc.scoreboard.PlayerScoreboard;
import org.bukkit.entity.Player;

import java.util.List;

public class GameScoreboard extends PlayerScoreboard {

    private final GameManager gameManager;

    protected GameScoreboard(Player player, GameManager gameManager) {
        super(player);
        this.gameManager = gameManager;
    }

    @Override
    protected String getTitle() {
        return " <gradient:#57FF7E:#19C6A3>Minesweeper</gradient> ";
    }

    @Override
    protected List<String> getLines() {
        int mines = 0;
        int flagsPlaced = 0;
        int gameSize = 0;

        if(gameManager != null) {
            mines = gameManager.getMineCount();
            flagsPlaced = gameManager.getFlagsPlaced();
            gameSize = gameManager.getGameSize();
        }

        return List.of(
                "",
                "<white>Mines in game:</white> <color:#ff616e>" + mines + "</color>",
                "<white>Flags placed:</white> <color:#ffcb3d>" + flagsPlaced + "</color>",
                "<white>Field size:</white> <color:#57ff7e>" + gameSize + "x" + gameSize + "</color>",
                ""
        );
    }
}

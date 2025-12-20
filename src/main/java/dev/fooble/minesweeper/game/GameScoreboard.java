package dev.fooble.minesweeper.game;

import dev.fooble.mc.scoreboard.PlayerScoreboard;
import org.bukkit.entity.Player;

import java.util.List;

public class GameScoreboard extends PlayerScoreboard {

    private final Game game;

    protected GameScoreboard(Player player, Game game) {
        super(player);
        this.game = game;
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

        if(game != null) {
            mines = game.getMineCount();
            flagsPlaced = game.getFlagsPlaced();
            gameSize = game.getGameSize();
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

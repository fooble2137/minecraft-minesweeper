package dev.fooble.minesweeper.game;

import dev.fooble.mc.scoreboard.PlayerScoreboard;
import dev.fooble.minesweeper.game.utils.GameUtils;
import io.papermc.paper.scoreboard.numbers.NumberFormat;
import org.bukkit.entity.Player;

import java.util.List;

public class GameScoreboard extends PlayerScoreboard {

    private final Game game;

    protected GameScoreboard(Player player, Game game) {
        super(player);
        this.game = game;
        this.objective.numberFormat(NumberFormat.blank());
    }

    @Override
    protected String getTitle() {
        return " <gradient:#57FF7E:#19C6A3><b>Minesweeper</b></gradient> ";
    }

    @Override
    protected List<String> getLines() {
        int mines = 0;
        int flagsPlaced = 0;
        int gameSize = 0;
        int tilesRevealed = 0;
        int timeElapsedMs = 0;

        String gameStateText = "<sprite:gui:icon/link> <gradient:#65DB89:#0060b6:#65DB89>Loading...</gradient>";

        if(game != null) {
            mines = game.getMineCount();
            flagsPlaced = game.getFlagsPlaced();
            gameSize = game.getGameSize();
            tilesRevealed = game.getTilesRevealed();
            timeElapsedMs = game.getTimeElapsedMs();

            switch(game.getGameState()) {
                case GENERATING ->
                        gameStateText = "<sprite:gui:icon/link> <gradient:#65DB89:#0060b6:#65DB89>Generating...</gradient>";
                case PLAYING -> gameStateText = "<sprite:gui:icon/search> <gradient:#57FF7E:#19C6A3>Playing</gradient>";
                case LOST ->
                        gameStateText = "<sprite:blocks:block/tnt_side> <gradient:#FF5757:#C61919>You lost!</gradient>";
                case WON ->
                        gameStateText = "<sprite:items:item/firework_rocket> <gradient:#40FF6D:#00A927>You won!</gradient>";
                case RESETTING ->
                        gameStateText = "<sprite:gui:icon/link> <gradient:#65DB89:#0060b6:#65DB89>Resetting...</gradient>";
                default -> gameStateText = "<sprite:gui:icon/link> <gradient:#65DB89:#0060b6:#65DB89>Loading...</gradient>";
            }
        }

        return List.of(
                "",
                "<sprite:map_decorations:red_banner> <color:#ffcb3d>" + flagsPlaced + " flag" + (flagsPlaced != 1 ? "s" : "") + "</color>",
                "<sprite:items:item/iron_shovel> <white>" + tilesRevealed + " tile" + (tilesRevealed != 1 ? "s" : "") + "</white>",
                "<sprite:items:item/clock_07> <yellow>" + GameUtils.formatTime(timeElapsedMs) + "</yellow>",
                "",
                "<sprite:blocks:block/tnt_side> <color:#ff616e>" + mines + " mine" + (mines != 1 ? "s" : "") + "</color>",
                "<sprite:blocks:block/test_block_start> <color:#57ff7e>" + gameSize + "x" + gameSize + " (" + (gameSize * gameSize) + ")</color>",
                "",
                gameStateText,
                ""
        );
    }
}

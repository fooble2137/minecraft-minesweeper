package dev.fooble.minesweeper.game;

import dev.fooble.minesweeper.Plugin;
import dev.fooble.minesweeper.game.utils.GameUtils;
import dev.fooble.minesweeper.game.utils.WinFireworks;
import dev.fooble.minesweeper.game.world.GameWorld;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GameManager {

    private final Plugin plugin;

    @Getter
    private final GameWorld gameWorld;

    @Getter
    private final List<GameTile> tiles = new ArrayList<>();

    @Getter
    private int gameSize = 10;
    @Getter
    private int mineCount = 20;

    @Getter
    private int flagsPlaced = 0;

    @Getter
    private boolean gameOver = false;
    private boolean gameWon = false;

    @Getter
    private Player playingPlayer;

    public GameManager(Plugin plugin) {
        this.plugin = plugin;
        gameWorld = new GameWorld();
    }

    public void startGame(Player player, int gameSize, int mineCount) {
        gameWorld.createWorld();

        if(playingPlayer != null) {
            player.sendRichMessage("<gradient:#FF5757:#C61919>A game is already in progress! Please exit the current game before starting a new one.</gradient>");
            return;
        }

        resetGame(true);

        this.gameSize = gameSize;
        this.mineCount = mineCount;
        this.playingPlayer = player;

        generateMinefield();
        renderMinefield();
        generateMines();
        calculateAdjacentMines();

        gameWorld.teleportPlayerToWorld(player);
        ScoreboardManager.createScoreboard(player, this);
    }

    public void endGame(@NotNull Player player) {
        if(!player.equals(playingPlayer)) {
            player.sendRichMessage("<gradient:#FF5757:#C61919>You are not currently in a Minesweeper game!</gradient>");
            return;
        }

        player.teleport(Bukkit.getWorlds().getFirst().getSpawnLocation());
        player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
        player.sendRichMessage("<gradient:#40FF6D:#00A927>You have exited the Minesweeper game!</gradient>");

        ScoreboardManager.removeScoreboard(player);

        resetGame(true);
    }

    public void resetGame(boolean setAir) {
        for(GameTile tile : tiles) {
            tile.removeTextDisplay();
            tile.setFlag(false, this);

            if(setAir) {
                getGameWorld().getWorld().getBlockAt(
                        tile.getX(),
                        100,
                        tile.getZ()
                ).setType(Material.AIR);
            }
        }

        gameOver = false;
        gameWon = false;
        flagsPlaced = 0;
        gameSize = 10;
        mineCount = 20;
        playingPlayer = null;
    }

    private void generateMinefield() {
        tiles.clear();

        for(int x = 0; x < gameSize; x++) {
            for(int z = 0; z < gameSize; z++) {
                tiles.add(new GameTile(x, z, gameWorld.getWorld()));
            }
        }
    }

    private void renderMinefield() {
        for(int x = 0; x < gameSize; x++) {
            for(int z = 0; z < gameSize; z++) {
                if((x + z) % 2 == 0) {
                    gameWorld.getWorld().getBlockAt(x, 100, z).setType(Material.GREEN_CONCRETE);
                } else {
                    gameWorld.getWorld().getBlockAt(x, 100, z).setType(Material.LIME_CONCRETE);
                }
            }
        }
    }

    private void generateMines() {
        for(int i = 0; i < mineCount; i++) {
            int index;
            do {
                index = (int) (Math.random() * tiles.size());
            } while(tiles.get(index).hasMine());

            tiles.get(index).setMine(true);
        }
    }

    private void calculateAdjacentMines() {
        for(GameTile tile : tiles) {
            int mineCount = 0;
            for(int dx = -1; dx <= 1; dx++) {
                for(int dz = -1; dz <= 1; dz++) {
                    if(dx == 0 && dz == 0) continue;
                    GameTile neighbor = getTileAt(tile.getX() + dx, tile.getZ() + dz);
                    if(neighbor != null && neighbor.hasMine()) {
                        mineCount++;
                    }
                }
            }
            tile.setAdjacentMines(mineCount);
        }
    }

    public void loseGame(Player player) {
        gameOver = true;

        for(GameTile tile : tiles) {
            tile.removeTextDisplay();
            tile.setFlag(false, this);
            if(tile.hasMine()) {
                gameWorld.getWorld().getBlockAt(
                        tile.getX(),
                        100,
                        tile.getZ()
                ).setType(Material.TNT);
            }
        }

        player.sendRichMessage("<gradient:#FF5757:#C61919>Game over! You hit a mine!</gradient>");
    }

    public void checkWinCondition(final Player player) {
        for(final GameTile tile : tiles) {
            if(!tile.hasMine() && !tile.isRevealed()) {
                return;
            }
        }
        gameOver = true;
        gameWon = true;

        for(final GameTile tile : tiles) {
            tile.removeTextDisplay();
            tile.setFlag(false, this);
        }

        player.sendRichMessage("<gradient:#40FF6D:#00A927>Congratulations! You've cleared the minefield successfully!</gradient>");

        WinFireworks.spawnWinFireworks(GameUtils.getBoardCenter(gameWorld.getWorld(), gameSize), plugin);
    }

    public GameTile getTileAt(int x, int z) {
        for(GameTile tile : tiles) {
            if(tile.getX() == x && tile.getZ() == z) {
                return tile;
            }
        }
        return null;
    }

    public void incrementFlagsPlaced() {
        flagsPlaced++;
    }

    public void decrementFlagsPlaced() {
        flagsPlaced--;
    }

}
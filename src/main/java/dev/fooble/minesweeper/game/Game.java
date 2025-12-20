package dev.fooble.minesweeper.game;

import dev.fooble.mc.worlds.api.ManagedWorld;
import dev.fooble.mc.worlds.api.WorldManager;
import dev.fooble.mc.worlds.api.WorldOptions;
import dev.fooble.mc.worlds.api.WorldType;
import dev.fooble.mc.worlds.generators.VoidChunkGenerator;
import dev.fooble.minesweeper.Plugin;
import dev.fooble.minesweeper.game.utils.GameUtils;
import dev.fooble.minesweeper.game.utils.WinFireworks;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private final Plugin plugin;
    private final WorldManager worldManager;

    @Getter
    private final Player player;

    private final List<GameTile> tiles = new ArrayList<>();

    @Getter
    private final int gameSize;
    @Getter
    private final int mineCount;
    @Getter
    @Setter
    private int flagsPlaced = 0;

    @Getter
    private GameState gameState;
    @Getter
    private String worldName;

    public Game(Player player, int gameSize, int mineCount, @NotNull Plugin plugin) {
        this.player = player;
        this.gameSize = gameSize;
        this.mineCount = mineCount;
        this.plugin = plugin;
        this.worldManager = plugin.getWorldManager();
    }

    public void startGame() {
        if(gameState != null) return;
        gameState = GameState.GENERATING;

        createWorld();
        generateMinefield();
        generateMines();
        calculateAdjacentMines();

        player.teleport(new Location(worldManager.getWorld(worldName).getBukkitWorld(), 0.5, 101, 0.5));

        ScoreboardManager.createScoreboard(player, this);

        gameState = GameState.PLAYING;
    }

    public void stopGame() {
        if(gameState == null) return;
        gameState = GameState.RESETTING;

        if(player.isOnline()) player.teleport(Bukkit.getWorlds().getFirst().getSpawnLocation());

        final ManagedWorld world = worldManager.getWorld(worldName);
        if(world != null) {
            worldManager.deleteWorld(worldName);
        }

        ScoreboardManager.removeScoreboard(player);
    }

    public void looseGame() {
        gameState = GameState.LOST;

        for(GameTile tile : tiles) {
            tile.removeTextDisplay();
            tile.setFlag(false);
            if(tile.isMine()) {
                worldManager.getWorld(worldName).getBukkitWorld().getBlockAt(
                        tile.getX(),
                        100,
                        tile.getZ()
                ).setType(Material.TNT);
            }
        }

        player.sendRichMessage("<gradient:#FF5757:#C61919>Game Over! You hit a mine!</gradient><newline><newline><hover:show_text:'<gray><i>Click to exit the game</i></gray>'><click:run_command:'/minesweeper exit'><dark_gray>[<yellow><b>EXIT</b></yellow>]</dark_gray></click></hover>");
    }

    private void winGame() {
        gameState = GameState.WON;

        for(final GameTile tile : tiles) {
            tile.removeTextDisplay();
            tile.setFlag(false);
        }

        player.sendRichMessage("<gradient:#40FF6D:#00A927>Congratulations! You've cleared the minefield successfully!</gradient> <newline><newline><hover:show_text:'<gray><i>Click to exit the game</i></gray>'><click:run_command:'/minesweeper exit'><dark_gray>[<yellow><b>EXIT</b></yellow>]</dark_gray></click></hover>");

        WinFireworks.spawnWinFireworks(GameUtils.getBoardCenter(worldManager.getWorld(worldName).getBukkitWorld(), gameSize), plugin);
    }

    public void checkWinCondition() {
        for(final GameTile tile : tiles) {
            if(!tile.isMine() && !tile.isRevealed()) {
                return;
            }
        }

        winGame();
    }

    private void createWorld() {
        worldName = "temp_" + player.getUniqueId() + "_minesweeper_" + System.currentTimeMillis();
        final ManagedWorld world = worldManager.createWorld(
                worldName,
                WorldOptions.builder()
                        .type(WorldType.TEMPORARY)
                        .environment(World.Environment.NORMAL)
                        .chunkGenerator(new VoidChunkGenerator())
                        .autoSave(false)
                        .allowAnimals(false)
                        .allowMonsters(false)
                        .build()
        );

        final World bukkitWorld = world.getBukkitWorld();
        bukkitWorld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        bukkitWorld.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        bukkitWorld.setTime(6000);
        bukkitWorld.setDifficulty(Difficulty.PEACEFUL);
    }

    private void generateMinefield() {
        tiles.clear();

        for(int x = 0; x < gameSize; x++) {
            for(int z = 0; z < gameSize; z++) {
                tiles.add(new GameTile(x, z, this, plugin));
            }
        }

        renderMinefield();
    }

    private void renderMinefield() {
        for(int x = 0; x < gameSize; x++) {
            for(int z = 0; z < gameSize; z++) {
                if((x + z) % 2 == 0) {
                    worldManager.getWorld(worldName).getBukkitWorld().getBlockAt(x, 100, z).setType(Material.GREEN_CONCRETE);
                } else {
                    worldManager.getWorld(worldName).getBukkitWorld().getBlockAt(x, 100, z).setType(Material.LIME_CONCRETE);
                }
            }
        }
    }

    private void generateMines() {
        for(int i = 0; i < mineCount; i++) {
            int index;
            do {
                index = (int) (Math.random() * tiles.size());
            } while(tiles.get(index).isMine());

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
                    if(neighbor != null && neighbor.isMine()) {
                        mineCount++;
                    }
                }
            }
            tile.setAdjacentMines(mineCount);
        }
    }

    public GameTile getTileAt(int x, int z) {
        for(GameTile tile : tiles) {
            if(tile.getX() == x && tile.getZ() == z) {
                return tile;
            }
        }
        return null;
    }

}
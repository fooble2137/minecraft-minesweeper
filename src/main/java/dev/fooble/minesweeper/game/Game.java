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
    private int flagsPlaced, tilesRevealed = 0;

    @Getter
    @Setter
    private int timeElapsedMs = 0;

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

        ScoreboardManager.updateScoreboard(player);

        createWorld();
        generateMinefield();
        generateMines();
        calculateAdjacentMines();

        player.teleport(new Location(worldManager.getWorld(worldName).getBukkitWorld(), 0.5, 101, 0.5));

        ScoreboardManager.createScoreboard(player, this);

        gameState = GameState.PLAYING;
        startTimer();
        ScoreboardManager.updateScoreboard(player);
    }

    public void stopGame() {
        if(gameState == null) return;
        gameState = GameState.RESETTING;

        ScoreboardManager.updateScoreboard(player);

        if(player.isOnline()) player.teleport(Bukkit.getWorlds().getFirst().getSpawnLocation());

        final ManagedWorld world = worldManager.getWorld(worldName);
        if(world != null) {
            worldManager.deleteWorld(worldName);
        }

        ScoreboardManager.removeScoreboard(player);
    }

    public void restartGame() {
        if(gameState == null) return;
        gameState = GameState.RESETTING;

        ScoreboardManager.updateScoreboard(player);
        GameManager.startGame(player, gameSize, mineCount, plugin);

        final ManagedWorld world = worldManager.getWorld(worldName);
        if(world != null) {
            worldManager.deleteWorld(worldName);
        }
    }

    public void looseGame() {
        gameState = GameState.LOST;

        ScoreboardManager.updateScoreboard(player);

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

        player.playSound(player, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f);

        player.sendRichMessage("<newline><sprite:blocks:block/tnt_side> <gradient:#FF5757:#C61919><b>Game Over!</b> You hit a mine!</gradient> <sprite:blocks:block/tnt_side><newline><newline>" +
                "<hover:show_text:'<aqua><i>Click to exit the game</i></aqua>'><click:run_command:'/minesweeper exit'><dark_gray>[<red><b>EXIT</b></red>]</dark_gray></click></hover> " +
                "<hover:show_text:'<aqua><i>Click to restart the game</i></aqua>'><click:run_command:'/minesweeper restart'><dark_gray>[<yellow><b>RESTART</b></yellow>]</dark_gray></click></hover><newline>");
    }

    private void winGame() {
        gameState = GameState.WON;

        ScoreboardManager.updateScoreboard(player);

        for(final GameTile tile : tiles) {
            tile.removeTextDisplay();
            tile.setFlag(false);
        }

        player.playSound(player, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);

        player.sendRichMessage("<newline><sprite:items:item/firework_rocket> <gradient:#40FF6D:#00A927><b>Congratulations!</b> You cleared the minefield successfully!</gradient> <sprite:items:item/firework_rocket><newline><newline>" +
                "<hover:show_text:'<aqua><i>Click to exit the game</i></aqua>'><click:run_command:'/minesweeper exit'><dark_gray>[<red><b>EXIT</b></red>]</dark_gray></click></hover> " +
                "<hover:show_text:'<aqua><i>Click to restart the game</i></aqua>'><click:run_command:'/minesweeper restart'><dark_gray>[<yellow><b>RESTART</b></yellow>]</dark_gray></click></hover><newline>");

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
        bukkitWorld.setGameRule(GameRules.ADVANCE_TIME, false);
        bukkitWorld.setGameRule(GameRules.ADVANCE_WEATHER, false);
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

    public void regenerateMines() {
        for(GameTile tile : tiles) {
            tile.setMine(false);
            tile.setAdjacentMines(0);
        }

        generateMines();
        calculateAdjacentMines();
    }

    public GameTile getTileAt(int x, int z) {
        for(GameTile tile : tiles) {
            if(tile.getX() == x && tile.getZ() == z) {
                return tile;
            }
        }
        return null;
    }

    public void startTimer() {
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if(gameState != GameState.PLAYING) {
                return;
            }

            timeElapsedMs += 1000;
            ScoreboardManager.updateScoreboard(player);
        }, 20L, 20L);
    }

}
package dev.fooble.minesweeper.game.world;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GameWorld {

    private World minesweeperWorld;

    public void createWorld() {
        final String worldName = "minesweeper_world";

        final World world = Bukkit.getWorld(worldName);
        if(world != null) {
            minesweeperWorld = world;
            return;
        }

        final WorldCreator creator = new WorldCreator(worldName);
        creator.generator(new GameWorldGenerator());
        creator.type(WorldType.FLAT);
        creator.environment(World.Environment.NORMAL);

        minesweeperWorld = creator.createWorld();
        assert minesweeperWorld != null;

        minesweeperWorld.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        minesweeperWorld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        minesweeperWorld.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        minesweeperWorld.setTime(6000);
        minesweeperWorld.setDifficulty(Difficulty.PEACEFUL);
    }

    public void teleportPlayerToWorld(@NotNull Player player) {
        final Location spawn = new Location(minesweeperWorld, 0.5, 101, 0.5);

        player.teleport(spawn);
    }

    public World getWorld() {
        return minesweeperWorld;
    }
}
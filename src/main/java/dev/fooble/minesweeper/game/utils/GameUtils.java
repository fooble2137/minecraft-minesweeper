package dev.fooble.minesweeper.game.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class GameUtils {

    private GameUtils() {
    }

    public static @NotNull Location getBoardCenter(final @NotNull World world, final int gameSize) {
        return new Location(
                world,
                (gameSize - 1) / 2.0,
                101,
                (gameSize - 1) / 2.0
        );
    }
}
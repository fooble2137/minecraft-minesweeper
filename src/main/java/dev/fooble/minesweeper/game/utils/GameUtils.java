package dev.fooble.minesweeper.game.utils;

import org.bukkit.Location;
import org.bukkit.Material;
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

    public static @NotNull Material getNumberMaterial(final int number) {
        if(number < 0 || number > 8) {
            throw new IllegalArgumentException("Number must be between 0 and 8 inclusive.");
        }

        return switch(number) {
            case 0 -> Material.WHITE_WOOL;
            case 1 -> Material.BLUE_WOOL;
            case 2 -> Material.GREEN_WOOL;
            case 3 -> Material.RED_WOOL;
            case 4 -> Material.PURPLE_WOOL;
            case 5 -> Material.BROWN_WOOL;
            case 6 -> Material.CYAN_WOOL;
            case 7 -> Material.BLACK_WOOL;
            case 8 -> Material.GRAY_WOOL;
            default -> throw new IllegalStateException("Unexpected value: " + number);
        };
    }

    public static @NotNull String getNumberColorTag(final int number) {
        if(number < 0 || number > 8) {
            throw new IllegalArgumentException("Number must be between 0 and 8 inclusive.");
        }

        return switch(number) {
            case 0 -> "<white>";
            case 1 -> "<blue>";
            case 2 -> "<green>";
            case 3 -> "<red>";
            case 4 -> "<dark_purple>";
            case 5 -> "<#440e0b>";
            case 6 -> "<dark_aqua>";
            case 7 -> "<black>";
            case 8 -> "<gray>";
            default -> throw new IllegalStateException("Unexpected value: " + number);
        };
    }
}
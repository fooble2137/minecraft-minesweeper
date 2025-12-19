package dev.fooble.minesweeper.game;

import dev.fooble.mc.components.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

public class GameTile {

    private boolean hasFlag = false;
    private boolean isRevealed = false;
    private boolean hasMine = false;
    private int adjacentMines = 0;

    private final int x;
    private final int z;
    private final World gameWorld;

    private TextDisplay textDisplay;

    public GameTile(int x, int z, World gameWorld) {
        this.x = x;
        this.z = z;
        this.gameWorld = gameWorld;
    }

    public void reveal(Player player, GameManager gameManager) {
        if(isRevealed) {
            return;
        }

        setFlag(false, gameManager);
        isRevealed = true;

        if(hasMine) {
            gameWorld.getBlockAt(x, 100, z).setType(Material.TNT);
            gameManager.loseGame(player);
        } else {
            switch(adjacentMines) {
                case 0 -> gameWorld.getBlockAt(x, 100, z).setType(Material.WHITE_WOOL);
                case 1 -> gameWorld.getBlockAt(x, 100, z).setType(Material.BLUE_WOOL);
                case 2 -> gameWorld.getBlockAt(x, 100, z).setType(Material.GREEN_WOOL);
                case 3 -> gameWorld.getBlockAt(x, 100, z).setType(Material.RED_WOOL);
                case 4 -> gameWorld.getBlockAt(x, 100, z).setType(Material.YELLOW_WOOL);
                case 5 -> gameWorld.getBlockAt(x, 100, z).setType(Material.BROWN_WOOL);
                case 6 -> gameWorld.getBlockAt(x, 100, z).setType(Material.CYAN_WOOL);
                case 7 -> gameWorld.getBlockAt(x, 100, z).setType(Material.GRAY_WOOL);
                case 8 -> gameWorld.getBlockAt(x, 100, z).setType(Material.BLACK_WOOL);
            }

            generateTextDisplay();

            if(adjacentMines == 0) {
                for(int dx = -1; dx <= 1; dx++) {
                    for(int dz = -1; dz <= 1; dz++) {
                        if(dx == 0 && dz == 0) continue;

                        final GameTile adjacentTile = gameManager.getTileAt(x + dx, z + dz);
                        if(adjacentTile != null && !adjacentTile.isRevealed()) {
                            adjacentTile.reveal(player, gameManager);
                        }
                    }
                }
            }

            gameManager.checkWinCondition(player);
        }

        ScoreboardManager.updateScoreboard(player);
    }

    private void generateTextDisplay() {
        final Location location = gameWorld
                .getBlockAt(x, 101, z)
                .getLocation()
                .add(0.5, 0.2, 0.75);

        textDisplay = gameWorld.spawn(location, TextDisplay.class);

        String colorTag;
        switch(adjacentMines) {
            case 1 -> colorTag = "<blue>";
            case 2 -> colorTag = "<green>";
            case 3 -> colorTag = "<red>";
            case 4 -> colorTag = "<yellow>";
            case 5 -> colorTag = "<dark_red>";
            case 6 -> colorTag = "<aqua>";
            case 7 -> colorTag = "<dark_gray>";
            case 8 -> colorTag = "<black>";
            default -> colorTag = "<white>";
        }

        textDisplay.text(ComponentBuilder.of("<b>" + colorTag + adjacentMines + "</b>"));

        final Transformation transformation = new Transformation(
                new Vector3f(0, 0, 0),
                new AxisAngle4f(0, 0, 0, 1),
                new Vector3f(2.0f, 2.0f, 2.0f),
                new AxisAngle4f((float) Math.toRadians(-90), 1, 0, 0)
        );

        textDisplay.setTransformation(transformation);

        textDisplay.setBillboard(Display.Billboard.FIXED);
        textDisplay.setSeeThrough(true);
        textDisplay.setShadowed(false);
    }

    public boolean hasFlag() {
        return hasFlag;
    }

    public void setFlag(boolean hasFlag, GameManager gameManager) {
        if(hasFlag == this.hasFlag || isRevealed) {
            return;
        }

        if(hasFlag) {
            gameWorld.getBlockAt(x, 101, z).setType(Material.RED_BANNER);
            gameManager.incrementFlagsPlaced();
        } else {
            gameWorld.getBlockAt(x, 101, z).setType(Material.AIR);
            gameManager.decrementFlagsPlaced();
        }

        this.hasFlag = hasFlag;
    }

    public boolean isRevealed() {
        return isRevealed;
    }

    public boolean hasMine() {
        return hasMine;
    }

    public void setMine(boolean hasMine) {
        this.hasMine = hasMine;
    }

    public int getAdjacentMines() {
        return adjacentMines;
    }

    public void setAdjacentMines(int adjacentMines) {
        this.adjacentMines = adjacentMines;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public void removeTextDisplay() {
        if(textDisplay != null) {
            textDisplay.remove();
        }
    }
}
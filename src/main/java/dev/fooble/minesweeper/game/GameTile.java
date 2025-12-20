package dev.fooble.minesweeper.game;

import dev.fooble.mc.components.ComponentBuilder;
import dev.fooble.minesweeper.Plugin;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

public class GameTile {

    private boolean hasFlag = false;
    @Getter
    private boolean isRevealed = false;
    @Setter
    @Getter
    private boolean isMine = false;
    @Setter
    @Getter
    private int adjacentMines = 0;

    @Getter
    private final int x;
    @Getter
    private final int z;
    private final Game game;
    private final Plugin plugin;

    private TextDisplay textDisplay;

    public GameTile(int x, int z, Game game, Plugin plugin) {
        this.x = x;
        this.z = z;
        this.game = game;
        this.plugin = plugin;
    }

    public void reveal() {
        if(isRevealed) return;

        setFlag(false);
        isRevealed = true;

        final World gameWorld = plugin.getWorldManager().getWorld(game.getWorldName()).getBukkitWorld();

        if(isMine) {
            gameWorld.getBlockAt(x, 100, z).setType(Material.TNT);
            game.looseGame();
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

                        final GameTile adjacentTile = game.getTileAt(x + dx, z + dz);
                        if(adjacentTile != null && !adjacentTile.isRevealed()) {
                            adjacentTile.reveal();
                        }
                    }
                }
            }

            game.checkWinCondition();
        }

        ScoreboardManager.updateScoreboard(game.getPlayer());
    }

    private void generateTextDisplay() {
        final World gameWorld = plugin.getWorldManager().getWorld(game.getWorldName()).getBukkitWorld();

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

    public void setFlag(boolean hasFlag) {
        if(hasFlag == this.hasFlag || isRevealed) return;

        final World gameWorld = plugin.getWorldManager().getWorld(game.getWorldName()).getBukkitWorld();

        if(hasFlag) {
            gameWorld.getBlockAt(x, 101, z).setType(Material.RED_BANNER);
            game.setFlagsPlaced(game.getFlagsPlaced() + 1);
        } else {
            gameWorld.getBlockAt(x, 101, z).setType(Material.AIR);
            if(game.getFlagsPlaced() > 0) game.setFlagsPlaced(game.getFlagsPlaced() - 1);
        }

        this.hasFlag = hasFlag;
    }

    public void removeTextDisplay() {
        if(textDisplay != null) {
            textDisplay.remove();
        }
    }
}
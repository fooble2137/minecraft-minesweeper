package dev.fooble.minesweeper.game;

import dev.fooble.mc.components.ComponentBuilder;
import dev.fooble.minesweeper.Plugin;
import dev.fooble.minesweeper.game.utils.GameUtils;
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
            final Material numberMaterial = GameUtils.getNumberMaterial(adjacentMines);
            gameWorld.getBlockAt(x, 100, z).setType(numberMaterial);

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

        final String colorTag = GameUtils.getNumberColorTag(adjacentMines);
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
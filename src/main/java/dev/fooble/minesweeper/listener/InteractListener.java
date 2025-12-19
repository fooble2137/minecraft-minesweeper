package dev.fooble.minesweeper.listener;

import dev.fooble.minesweeper.Plugin;
import dev.fooble.minesweeper.game.GameTile;
import dev.fooble.minesweeper.game.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

public class InteractListener implements Listener {

    private final Plugin plugin;

    public InteractListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(@NotNull PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        if(!player.getWorld().getName().equals("minesweeper_world")) {
            return;
        }

        event.setCancelled(true);

        if(event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        if(event.getClickedBlock() == null) {
            return;
        }

        if(plugin.getGameManager().isGameOver()) {
            return;
        }

        final GameTile tile = plugin.getGameManager().getTileAt(event.getClickedBlock().getX(), event.getClickedBlock().getZ());
        if(tile == null) {
            return;
        }

        switch(event.getAction()) {
            case RIGHT_CLICK_BLOCK -> {
                // Handle flagging/unflagging the tile
                tile.setFlag(!tile.hasFlag(), plugin.getGameManager());

                ScoreboardManager.updateScoreboard(player);
            }
            case LEFT_CLICK_BLOCK -> {
                // Handle revealing the tile
                tile.reveal(player, plugin.getGameManager());
            }
        }
    }
}
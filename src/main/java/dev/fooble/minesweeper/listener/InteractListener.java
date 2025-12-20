package dev.fooble.minesweeper.listener;

import dev.fooble.minesweeper.game.GameState;
import dev.fooble.minesweeper.game.GameTile;
import dev.fooble.minesweeper.game.ScoreboardManager;
import dev.fooble.minesweeper.game.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

public class InteractListener implements Listener {

    @EventHandler
    public void onInteract(@NotNull PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        if(!GameManager.isInGame(player)) return;

        event.setCancelled(true);

        if(event.getHand() != EquipmentSlot.HAND || event.getClickedBlock() == null) {
            return;
        }

        if(!GameManager.getGame(player).getGameState().equals(GameState.PLAYING)) return;

        final GameTile tile = GameManager.getGame(player).getTileAt(event.getClickedBlock().getX(), event.getClickedBlock().getZ());
        if(tile == null) return;

        switch(event.getAction()) {
            // FLAG
            case RIGHT_CLICK_BLOCK -> {
                tile.setFlag(!tile.hasFlag());

                ScoreboardManager.updateScoreboard(player);
            }
            // REVEAL
            case LEFT_CLICK_BLOCK -> {
                tile.reveal();
            }
        }
    }
}
package dev.fooble.minesweeper.game.utils;

import dev.fooble.minesweeper.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.concurrent.ThreadLocalRandom;

public class WinFireworks {

    private WinFireworks() {
    }

    public static void spawnWinFireworks(final Location center, final Plugin plugin) {
        for(int i = 0; i < 6; i++) {
            final Location location = center.clone().add(
                    ThreadLocalRandom.current().nextDouble(-3, 3),
                    0,
                    ThreadLocalRandom.current().nextDouble(-3, 3)
            );

            final Firework firework = location.getWorld().spawn(location, Firework.class);
            final FireworkMeta meta = firework.getFireworkMeta();

            meta.addEffect(
                    FireworkEffect.builder()
                            .withColor(Color.GREEN, Color.LIME, Color.YELLOW)
                            .withFade(Color.WHITE)
                            .with(FireworkEffect.Type.BALL_LARGE)
                            .trail(true)
                            .flicker(true)
                            .build()
            );

            meta.setPower(1);
            firework.setFireworkMeta(meta);

            Bukkit.getScheduler().runTaskLater(plugin, firework::detonate, 10L + i * 5L);
        }
    }

}
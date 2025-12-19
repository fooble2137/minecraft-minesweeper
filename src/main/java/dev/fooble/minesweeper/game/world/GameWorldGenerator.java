package dev.fooble.minesweeper.game.world;

import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class GameWorldGenerator extends ChunkGenerator {

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull ChunkData generateChunkData(@NotNull World world, @NotNull Random random, int chunkX, int chunkZ, @NotNull BiomeGrid biome) {
        return createChunkData(world);
    }
}
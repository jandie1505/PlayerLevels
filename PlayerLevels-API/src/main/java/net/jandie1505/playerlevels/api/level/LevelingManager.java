package net.jandie1505.playerlevels.api.level;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Manages the Levelers.
 */
public interface LevelingManager {

    /**
     * Returns a cached leveler (player).<br/>
     * Normally, a player has a leveler when the player is online.
     * @param playerUUID player uuid
     * @return leveler (or null if not cached)
     */
    @Nullable LevelPlayer getLeveler(@NotNull UUID playerUUID);

    /**
     * Loads a leveler when it does not exist in cache.<br/>
     * Please note that the returned future is not completed in Bukkit's main thread,
     * which means you have to do new BukkitRunnable() {}.runTask(plugin) to interact with Bukkit stuff like the player.<br/>
     * @param playerUUID player uuid
     * @return future of leveler
     */
    @NotNull CompletableFuture<LevelPlayer> loadLeveler(@NotNull UUID playerUUID);

}

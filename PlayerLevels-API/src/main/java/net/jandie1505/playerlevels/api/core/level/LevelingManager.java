package net.jandie1505.playerlevels.api.core.level;

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
    @Nullable Leveler getLeveler(@NotNull UUID playerUUID);

    /**
     * Loads a leveler when it does not exist in cache.<br/>
     * Please note that the returned future is not completed in Bukkit's main thread,
     * which means you have to do new BukkitRunnable() {}.runTask(plugin) to interact with Bukkit stuff like the player.<br/>
     * @param playerUUID player uuid
     * @return future of leveler
     */
    @NotNull CompletableFuture<Leveler> loadLeveler(@NotNull UUID playerUUID);

    /**
     * Returns the amount of xp for the specified level.
     * @param level level
     * @return xp
     * @throws NullPointerException when the xp formula is not set
     * @throws IllegalArgumentException when the formula is not valid
     * @throws ArithmeticException when the formula contains a division by zero
     */
    double getXPForLevel(int level) throws NullPointerException, IllegalArgumentException, ArithmeticException;

    /**
     * Returns the amount of xp required from the currentLevel to the level.
     * @param currentLevel start
     * @param level target
     * @return required xp
     * @throws NullPointerException when the xp formula is not set
     * @throws IllegalArgumentException when the formula is not valid
     * @throws ArithmeticException when the formula contains a division by zero
     */
    double getXPForNextLevel(int currentLevel, int level) throws NullPointerException, IllegalArgumentException, ArithmeticException;

}

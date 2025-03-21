package net.jandie1505.playerlevels.api.core.level;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Represents the data of a player.<br/>
 * <b>It is not recommended to change values from inside upgrades. This can cause loops in upgrade processing.</b>
 */
public interface LevelerData {

    // ----- LEVEL -----

    /**
     * Returns the leveler's level.
     * @return player level
     */
    int level();

    /**
     * Sets the player's level.
     * @param level new level
     */
    void level(int level);

    // ----- XP -----

    /**
     * Returns the leveler's xp.
     * @return player xp
     */
    double xp();

    /**
     * Sets the player's xp.
     * @param xp new xp
     */
    void xp(double xp);

    // ----- RECEIVED REWARDS -----

    /**
     * Returns an unmodifiable map of the player's received reward entries.
     * @return received rewards
     */
    @NotNull Map<String, ReceivedReward> getReceivedRewards();

    /**
     * Returns a specific received reward entry.
     * @param id reward id
     * @return received reward entry (null if not exist)
     */
    @Nullable ReceivedReward getReceivedReward(@NotNull String id);

    /**
     * Returns a specific received reward entry.<br/>
     * If it does not exist, it creates a new one.
     * @param id reward id
     * @return received reward entry
     */
    @NotNull ReceivedReward getOrCreateReceivedReward(@NotNull String id);

    /**
     * Removes a received reward entry.
     * @param id reward id
     */
    void removeReceivedReward(@NotNull String id);

    // ----- CACHED NAME -----

    /**
     * Returns the cached name of the player.
     * @return cached name
     */
    @Nullable String cachedName();

}

package net.jandie1505.playerlevels.api.level;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Represents the data of a player.<br/>
 * <b>DO NOT CHANGE ANY VALUES FROM INSIDE UPGRADE EXECUTORS OR CONDITIONS!</b>
 */
public interface LevelerData {

    // ----- LEVEL -----

    /**
     * @return player level
     */
    int level();

    /**
     * Sets the player's level and updates the player.<br/>
     * Must not be called from executors or conditions of upgrades.
     * @param level new level
     */
    void level(int level);

    /**
     * Sets the player's level.
     * @param level new level
     * @param call update player (<b>must be false when called from upgrades</b>)
     */
    void level(int level, boolean call);

    // ----- XP -----

    /**
     * @return player xp
     */
    double xp();

    /**
     * Sets the player's xp and updates the player.<br/>
     * Must not be called from executors or conditions of upgrades.
     * @param xp new xp
     */
    void xp(double xp);

    /**
     * Sets the player's xp.
     * @param xp new xp
     * @param call update player (<b>must be false when called from upgrades</b>)
     */
    void xp(double xp, boolean call);

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
     * Returns a specific received reward entry.<br/>
     * If it does not exist, it creates a new one.<br/>
     * If call is set to true, the player is updated only when a new entry is created.
     * @param id reward id
     * @param call update player (<b>must be false when called from upgrades</b>)
     * @return received reward entry
     */
    @NotNull ReceivedReward getOrCreateReceivedReward(@NotNull String id, boolean call);

    /**
     * Removes a received reward entry.<br/>
     * Updates the player when the entry has existed and has not default values.
     * @param id reward id
     */
    void removeReceivedReward(@NotNull String id);

    /**
     * Removes a received reward entry.<br/>
     * If call is set to true, the player is updated only when the entry is has existed and has not default values.
     * @param id reward id
     * @param call update player (<b>must be false when called from upgrades)</b>
     */
    void removeReceivedReward(@NotNull String id, boolean call);

}

package net.jandie1505.playerlevels.api.level;

/**
 * Represents a reward a player has received.<br/>
 * This is an entry that the plugin knows the player has already received the reward,
 * and not the reward itself.<br/>
 * If you are looking for the actual reward, go to {@link net.jandie1505.playerlevels.api.reward.PlayerReward}.
 */
public interface ReceivedReward {

    /**
     * Returns the blocked state of the reward entry.<br/>
     * If a reward is blocked for a player, it will not be applied to that player.
     * @return blocked state
     */
    boolean blocked();

    /**
     * Sets the blocked state of a player.
     * @param blocked blocked state
     * @param process process player (<b>must be false when called from upgrades</b>)
     */
    void blocked(boolean blocked, boolean process);

    /**
     * Sets the blocked state of a player and process the player.<br/>
     * Must not be called from upgrades.
     * @param blocked blocked state
     */
    void blocked(boolean blocked);

    /**
     * Returns the level the player received the reward for.<br/>
     * For milestone rewards, this is always the level the player has unlocked the reward.<br/>
     * For interval rewards, this is the level the player has received the reward for.
     * So if a reward is applied with an interval of 1 (each level), the player is level 50,
     * and the player has received the reward at level 10, the reward is applied for each from 10 to 50.
     * @return level
     */
    int level();

    /**
     * Sets the level the player has received the reward on.
     * @param level level
     * @param process process player (<b>must be false when called from upgrades</b>)
     */
    void level(int level, boolean process);

    /**
     * Sets the level the player has received the reward on and processes the player.<br/>
     * Must not be called from upgrades.
     * @param level level
     */
    void level(int level);

    /**
     * Resets the reward entry to default values.<br/>
     * Since default entries are not written to the database,
     * this is an alternative to deleting the entry.
     * @param call process player (<b>must be false when called from upgrades</b>)
     */
    void reset(boolean call);

    /**
     * Resets the reward entry to default values and processes the player.<br/>
     * Must not be used from upgrades.
     */
    void reset();

    /**
     * Returns true if the reward entry has default values.
     * @return default
     */
    boolean isDefault();

}

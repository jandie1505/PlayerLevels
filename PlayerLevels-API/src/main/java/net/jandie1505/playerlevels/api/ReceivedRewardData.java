package net.jandie1505.playerlevels.api;

/**
 * Represents a reward a player received.
 */
public interface ReceivedRewardData {

    /**
     * Returns he level the player received the reward on.<br/>
     * This is stored for interval rewards.<br/>
     * Setting this to a negative value will mark the reward as received for all levels.
     * The player then won't receive the event on any level anymore.
     * @return level
     */
    int level();

    /**
     * Returns the level<br/>
     * Read {@link ReceivedRewardData#level()} for more information about the level.
     * @param level level
     */
    void level(int level);

}

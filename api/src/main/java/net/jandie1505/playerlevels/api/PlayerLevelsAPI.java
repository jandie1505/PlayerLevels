package net.jandie1505.playerlevels.api;

public interface PlayerLevelsAPI {

    /**
     * Returns the leveling manager.<br/>
     * Responsible for managing players.
     * @return LevelManager
     */
    LevelManager getLevelManager();

    /**
     * Returns the rewards manager.<br/>
     * Responsible for storing rewards.
     * @return RewardManager
     */
    RewardManager getRewardsManager();
}

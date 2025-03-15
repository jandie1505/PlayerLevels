package net.jandie1505.playerlevels.api;

import net.jandie1505.playerlevels.api.level.LevelManager;
import net.jandie1505.playerlevels.api.reward.RewardManager;

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

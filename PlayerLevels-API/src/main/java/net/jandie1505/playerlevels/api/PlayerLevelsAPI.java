package net.jandie1505.playerlevels.api;

import net.jandie1505.playerlevels.api.level.LevelingManager;
import net.jandie1505.playerlevels.api.reward.RewardsManager;

public interface PlayerLevelsAPI {

    /**
     * Returns the leveling manager.<br/>
     * Responsible for managing players.
     * @return LevelManager
     */
    LevelingManager getLevelManager();

    /**
     * Returns the rewards manager.<br/>
     * Responsible for storing rewards.
     * @return RewardManager
     */
    RewardsManager getRewardsManager();

}

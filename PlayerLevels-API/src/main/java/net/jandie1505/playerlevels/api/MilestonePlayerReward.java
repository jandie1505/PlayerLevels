package net.jandie1505.playerlevels.api;

public interface MilestonePlayerReward extends PlayerReward {

    /**
     * Returns the level that is required to get the reward.
     * @return level
     */
    int getLevel();

}

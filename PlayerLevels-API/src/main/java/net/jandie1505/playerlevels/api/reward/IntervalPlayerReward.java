package net.jandie1505.playerlevels.api.reward;

import net.jandie1505.playerlevels.rewards.RewardCondition;

public interface IntervalPlayerReward extends Reward {

    RewardCondition DEFAULT_CONDITION = (reward, player) -> false;

    /**
     * Returns the interval the player will get the reward.
     * @return interval
     */
    int getInterval();

}

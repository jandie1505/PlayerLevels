package net.jandie1505.playerlevels.api.core.reward;

import net.jandie1505.playerlevels.core.rewards.RewardCondition;

/**
 * Represents an interval reward.<br/>
 * Interval rewards are applied each n levels.
 */
public interface IntervalReward extends Reward {

    /**
     * The default condition of an interval reward.
     */
    RewardCondition DEFAULT_CONDITION = (reward, player, level) -> false;

    /**
     * Returns the interval start.
     * @return interval start
     */
    int getStart();

    /**
     * Returns the interval the player will get the reward.
     * @return interval
     */
    int getInterval();

}

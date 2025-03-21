package net.jandie1505.playerlevels.api.core.reward;

import net.jandie1505.playerlevels.core.rewards.IntervalRewardData;
import net.jandie1505.playerlevels.core.rewards.MilestoneRewardData;
import net.jandie1505.playerlevels.core.rewards.RewardConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Manages rewards.
 */
public interface RewardsManager {

    // ----- CREATE REWARDS -----

    /**
     * Creates a new Milestone Reward.<br/>
     * Milestone Rewards are only applied once at the specified level.
     * @param config reward config
     * @param data reward data
     * @return Milestone Reward
     */
    @SuppressWarnings("UnusedReturnValue")
    @NotNull MilestoneReward addMilestoneReward(@NotNull RewardConfig config, @NotNull MilestoneRewardData data);

    /**
     * Creates a new Interval Reward.<br/>
     * Interval Rewards are applied each n levels, while n is the interval.
     * @param config reward config
     * @param data reward data
     * @return Interval Reward
     */
    @SuppressWarnings("UnusedReturnValue")
    @NotNull IntervalReward addIntervalReward(@NotNull RewardConfig config, @NotNull IntervalRewardData data);

    // ----- MANAGE REWARDS -----

    /**
     * Returns an unmodifiable map of all registered rewards.
     * @return rewards map
     */
    @NotNull Map<String, Reward> getRewards();

    /**
     * Removes a reward.
     * @param rewardId reward id
     */
    void removeReward(@NotNull String rewardId);

    /**
     * Returns the reward with the specified id or null if it does not exist.
     * @param rewardId reward id
     * @return reward or null
     */
    @Nullable Reward getReward(@NotNull String rewardId);

    /**
     * Returns all milestone rewards for the specified level.
     * @param level level
     * @return milestone rewards
     */
    @NotNull Map<String, MilestoneReward> getMilestonesForLevel(int level);

}

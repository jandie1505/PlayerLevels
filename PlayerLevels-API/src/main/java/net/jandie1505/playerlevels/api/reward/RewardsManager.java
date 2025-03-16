package net.jandie1505.playerlevels.api.reward;

import net.jandie1505.playerlevels.rewards.IntervalRewardData;
import net.jandie1505.playerlevels.rewards.MilestoneRewardData;
import net.jandie1505.playerlevels.rewards.RewardConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Manages rewards.
 */
public interface RewardsManager {

    // ----- CREATE REWARDS -----

    @SuppressWarnings("UnusedReturnValue")
    @NotNull MilestonePlayerReward addMilestoneReward(@NotNull RewardConfig config, @NotNull MilestoneRewardData data);

    @SuppressWarnings("UnusedReturnValue")
    @NotNull IntervalPlayerReward addIntervalReward(@NotNull RewardConfig config, @NotNull IntervalRewardData data);

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

}

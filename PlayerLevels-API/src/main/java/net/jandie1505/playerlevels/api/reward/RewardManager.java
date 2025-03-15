package net.jandie1505.playerlevels.api.reward;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Manages rewards.
 */
public interface RewardManager {

    /**
     * Returns an unmodifiable map of all registered rewards.
     * @return rewards map
     */
    @NotNull Map<String, PlayerReward> getRewards();

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
    @Nullable PlayerReward getReward(@NotNull String rewardId);

}

package net.jandie1505.playerlevels.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Manages rewards.
 */
public interface RewardManager {

    /**
     * Returns a Builder which can be used to create new rewards.
     * @param rewardId reward id
     * @return reward builder
     */
    @NotNull RewardBuilder create(@NotNull String rewardId);

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

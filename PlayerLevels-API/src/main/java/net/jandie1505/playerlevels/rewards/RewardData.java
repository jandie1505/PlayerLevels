package net.jandie1505.playerlevels.rewards;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The data of a reward.
 * @param executor this will be executed when a reward is applied
 * @param condition this will check if the reward is already applied
 * @param requiresOnlinePlayer this indicates if the reward requires the player to be online to apply the reward
 */
public record RewardData(
        @NotNull RewardExecutor executor,
        @Nullable RewardCondition condition,
        boolean requiresOnlinePlayer
) {
}

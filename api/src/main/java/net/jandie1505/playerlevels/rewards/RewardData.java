package net.jandie1505.playerlevels.rewards;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record RewardData(
        @NotNull RewardExecutor executor,
        @Nullable RewardCondition condition,
        boolean requiresOnlinePlayer
) {
}

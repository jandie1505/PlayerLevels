package net.jandie1505.playerlevels.rewards;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record IntervalRewardData(
        @NotNull RewardExecutor executor,
        @Nullable RewardCondition customCondition,
        boolean requiresPlayerOnline,
        int interval
) {}

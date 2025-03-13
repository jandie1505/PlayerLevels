package net.jandie1505.playerlevels.rewards;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MilestoneReward extends Reward {

    protected MilestoneReward(@NotNull RewardsManager manager, @NotNull String id, @Nullable String serverId, int level, @NotNull RewardExecutor executor, @Nullable RewardCondition condition, boolean requireOnlinePlayer, @NotNull String name, @Nullable String description) {
        super(manager, id, serverId, level, executor, condition, requireOnlinePlayer, name, description);
    }

}

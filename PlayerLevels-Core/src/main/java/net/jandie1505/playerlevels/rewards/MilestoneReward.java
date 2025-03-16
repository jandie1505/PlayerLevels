package net.jandie1505.playerlevels.rewards;

import net.jandie1505.playerlevels.leveler.Leveler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MilestoneReward extends Reward implements net.jandie1505.playerlevels.api.reward.MilestoneReward {
    private final int level;
    @NotNull private final RewardCondition condition;

    protected MilestoneReward(@NotNull RewardsManager manager, @NotNull String id, @Nullable String serverId, int level, @NotNull RewardExecutor executor, @Nullable RewardCondition condition, boolean requireOnlinePlayer, @NotNull String name, @Nullable String description) {
        super(manager, id, serverId, executor, requireOnlinePlayer, name, description);
        this.level = level;
        this.condition = condition != null ? condition : net.jandie1505.playerlevels.api.reward.MilestoneReward.DEFAULT_CONDITION;
    }

    protected MilestoneReward(@NotNull RewardsManager manager, @NotNull RewardConfig config, @NotNull MilestoneRewardData data, int level) {
        this(manager, config.id(), config.serverId(), level, data.executor(), data.condition(), data.requiresOnlinePlayer(), config.name(), config.description());
    }

    @Override
    public boolean checkApplyCondition(@NotNull Leveler leveler) {
        return leveler.getData().level() >= this.level && !this.condition.isApplied(this, leveler);
    }

    @Override
    public int getLevel() {
        return this.level;
    }

    @Override
    public void onApplySuccess(@NotNull Leveler leveler) {
        leveler.getData().getOrCreateReceivedReward(this.getId()).level(leveler.getData().level());
    }

}

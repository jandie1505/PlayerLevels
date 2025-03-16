package net.jandie1505.playerlevels.rewards;

import net.jandie1505.playerlevels.leveler.Leveler;
import net.jandie1505.playerlevels.leveler.ReceivedReward;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class IntervalReward extends Reward implements net.jandie1505.playerlevels.api.reward.IntervalReward {
    private final int interval;
    @NotNull private final RewardCondition customCondition;

    public IntervalReward(@NotNull RewardsManager manager, @NotNull String id, @Nullable String serverId, int interval, @NotNull RewardExecutor executor, @Nullable RewardCondition customCondition, boolean requireOnlinePlayer, @NotNull String name, @Nullable String description) {
        super(manager, id, serverId, executor, requireOnlinePlayer, name, description);
        this.interval = interval;
        this.customCondition = customCondition != null ? customCondition : net.jandie1505.playerlevels.api.reward.IntervalReward.DEFAULT_CONDITION;
    }

    public IntervalReward(@NotNull RewardsManager manager, @NotNull RewardConfig config, @NotNull IntervalRewardData data) {
        this(manager, config.id(), config.serverId(), data.level(), data.executor(), data.customCondition(), data.requiresPlayerOnline(), config.name(), config.description());
    }

    // ----- CONDITIONS -----

    @Override
    public boolean checkApplyCondition(@NotNull Leveler leveler) {

        // Condition is not met when level is not in interval
        if (!this.isInInterval(leveler.getData().level())) {
            return false;
        }

        // Condition is not met when the reward has already been applied for the level
        if (leveler.getData().getOrCreateReceivedReward(this.getId()).level() > leveler.getData().level()) {
            return false;
        }

        // Check if the reward has already been applied with the custom condition
        return !this.customCondition.isApplied(this, leveler);
    }

    @Override
    public void onApplySuccess(@NotNull Leveler leveler) {
        ReceivedReward reward = leveler.getData().getOrCreateReceivedReward(this.getId());
        reward.level(reward.level() + this.interval);
    }

    // ----- INTERVAL -----

    public final int getInterval() {
        return this.interval;
    }

    public boolean isInInterval(int level) {
        return level % this.interval == 0;
    }

    public final List<Integer> getApplyingLevelsUntil(int level) {
        List<Integer> applyingLevels = new ArrayList<>();
        for (int i = 0; i <= level; i++) {
            if (this.isInInterval(i)) applyingLevels.add(i);
        }
        return applyingLevels;
    }

}

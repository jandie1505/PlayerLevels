package net.jandie1505.playerlevels.core.rewards;

import net.jandie1505.playerlevels.core.leveler.Leveler;
import net.jandie1505.playerlevels.core.leveler.ReceivedReward;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class IntervalReward extends Reward implements net.jandie1505.playerlevels.api.core.reward.IntervalReward {
    private final int start;
    private final int interval;
    private final int limit;

    public IntervalReward(@NotNull RewardsManager manager, @NotNull String id, @Nullable String serverId, int start, int interval, int limit, @NotNull RewardExecutor executor, boolean requireOnlinePlayer, @NotNull String name, @Nullable RewardDescriptionProvider descriptionProvider) {
        super(manager, id, serverId, executor, requireOnlinePlayer, limit, name, descriptionProvider);
        this.start = start > 0 ? start : 1;
        this.interval = interval > 0 ? interval : 1;
        this.limit = limit;
    }

    public IntervalReward(@NotNull RewardsManager manager, @NotNull RewardConfig config, @NotNull IntervalRewardData data) {
        this(manager, config.id(), config.serverId(), data.start(), data.interval(), data.limit(), data.executor(), data.requiresPlayerOnline(), config.name(), data.descriptionProvider());
    }

    // ----- CONDITIONS -----

    @Override
    public boolean checkApplyCondition(@NotNull Leveler leveler, int checkedLevel) {

        // Condition is not met when level is not in interval
        if (!this.isInInterval(checkedLevel)) {
            return false;
        }

        // The Condition is not met when the reward has already been applied for the level
        return leveler.getData().getOrCreateReceivedReward(this.getId()).level() < checkedLevel;
    }

    @Override
    public void onApplySuccess(@NotNull Leveler leveler, int checkedLevel) {
        ReceivedReward reward = leveler.getData().getOrCreateReceivedReward(this.getId());
        reward.level(checkedLevel);
    }

    // ----- INTERVAL -----

    public final int getStart() {
        return this.start;
    }

    public final int getInterval() {
        return this.interval;
    }

    public boolean isInInterval(int level) {
        if (level < this.start) return false;
        if (this.limit > 0 && level >= this.limit) return false;
        return (level - this.start) % this.interval == 0;
    }

    public final List<Integer> getApplyingLevelsUntil(int level) {
        List<Integer> applyingLevels = new ArrayList<>();
        for (int i = 0; i <= level; i++) {
            if (this.isInInterval(i)) applyingLevels.add(i);
        }
        return applyingLevels;
    }

}

package net.jandie1505.playerlevels.rewards;

import net.jandie1505.playerlevels.api.IntervalPlayerReward;
import net.jandie1505.playerlevels.leveler.Leveler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class IntervalReward extends Reward {
    private final int interval;
    @NotNull private final RewardCondition customCondition;

    protected IntervalReward(@NotNull RewardsManager manager, @NotNull String id, @Nullable String serverId, int interval, @NotNull RewardExecutor executor, @Nullable RewardCondition customCondition, boolean requireOnlinePlayer, @NotNull String name, @Nullable String description) {
        super(manager, id, serverId, executor, requireOnlinePlayer, name, description);
        this.interval = interval;
        this.customCondition = customCondition != null ? customCondition : IntervalPlayerReward.DEFAULT_CONDITION;
    }

    // ----- CONDITIONS -----

    @Override
    public boolean checkApplyCondition(@NotNull Leveler leveler) {

        // Condition is not met when level is not in interval
        if (!this.isInInterval(leveler.getData().level())) {
            return false;
        }

        // Condition is not met when the reward has already been applied for the level
        if (leveler.getData().getOrCreateReceivedReward(this.getId(), false).level() >= leveler.getData().level()) {
            return false;
        }

        // Check if the reward has already been applied with the custom condition
        return !this.customCondition.isApplied(this, leveler);
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

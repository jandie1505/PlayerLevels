package net.jandie1505.playerlevels.core.rewards;

import net.chaossquad.mclib.storage.DataStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Creates reward data from config.
 */
public interface RewardCreator {

    /**
     * Creates a new milestone reward data from config.<br/>
     * If not implemented, it throws an {@link UnsupportedOperationException}.
     * @param data config
     * @return data
     */
    default @Nullable MilestoneRewardData createMilestoneReward(@NotNull DataStorage data) {
        throw new UnsupportedOperationException("This creator doesn't support milestone rewards");
    }

    /**
     * Creates a new interval reward data from config.
     * @param data config
     * @return data
     */
    default @Nullable IntervalRewardData createIntervalReward(@NotNull DataStorage data) {
        throw new UnsupportedOperationException("This creator doesn't support interval rewards");
    }

}

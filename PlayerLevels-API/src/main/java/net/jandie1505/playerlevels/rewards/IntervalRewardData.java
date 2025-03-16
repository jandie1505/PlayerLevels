package net.jandie1505.playerlevels.rewards;

import net.jandie1505.playerlevels.rewards.types.CommandReward;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The data for an interval reward.<br/>
 * This data is normally created by the reward creator for the specific reward type, like {@link net.jandie1505.playerlevels.rewards.types.CommandReward#createInterval(String, boolean, CommandReward.SenderType, int)}.<br/>
 * It can be used to create events using {@link net.jandie1505.playerlevels.api.reward.RewardsManager#addIntervalReward(RewardConfig, IntervalRewardData)}.
 * @param executor the code that will be executed when the reward is applied
 * @param customCondition a custom condition when the reward counts as applied (apart from the level condition)
 * @param requiresPlayerOnline if the player is required to be online that the reward is applied
 * @param interval the interval the reward is applied to the player
 */
public record IntervalRewardData(
        @NotNull RewardExecutor executor,
        @Nullable RewardCondition customCondition,
        boolean requiresPlayerOnline,
        int interval
) {}

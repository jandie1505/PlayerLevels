package net.jandie1505.playerlevels.core.rewards;

import net.jandie1505.playerlevels.core.rewards.types.CommandReward;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The data for an interval reward.<br/>
 * This data is normally created by the reward creator for the specific reward type, like {@link CommandReward#createInterval(String, boolean, CommandReward.SenderType, Component, int, int, int)}.<br/>
 * It can be used to create events using {@link net.jandie1505.playerlevels.api.core.reward.RewardsManager#addIntervalReward(RewardConfig, IntervalRewardData)}.
 * @param executor the code that will be executed when the reward is applied
 * @param customCondition a custom condition when the reward counts as applied (apart from the level condition)
 * @param descriptionProvider Provides a description for the reward.
 * @param requiresPlayerOnline if the player is required to be online that the reward is applied
 * @param start the level where from which the interval calculation starts
 * @param interval the interval the reward is applied to the player
 * @param limit the maximum level the reward is applied (&lt; 0 to disable)
 */
public record IntervalRewardData(
        @NotNull RewardExecutor executor,
        @Nullable RewardCondition customCondition,
        @Nullable RewardDescriptionProvider descriptionProvider,
        boolean requiresPlayerOnline,
        int start,
        int interval,
        int limit
) {}

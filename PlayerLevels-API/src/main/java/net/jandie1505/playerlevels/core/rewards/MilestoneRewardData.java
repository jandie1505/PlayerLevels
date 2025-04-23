package net.jandie1505.playerlevels.core.rewards;

import net.jandie1505.playerlevels.core.rewards.types.CommandReward;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The data of a milestone reward.<br/>
 * This data is normally created by the reward creator for the specific reward type, like {@link CommandReward#createMilestone(String, boolean, CommandReward.SenderType, Component, int)}.<br/>
 * It can be used to create events using {@link net.jandie1505.playerlevels.api.core.reward.RewardsManager#addMilestoneReward(RewardConfig, MilestoneRewardData)}.
 * @param executor the code that will be executed when the reward is applied
 * @param condition a custom condition when the reward counts as applied (apart from the level condition)
 * @param descriptionProvider Provides a description for the reward.
 * @param requiresOnlinePlayer if the player is required to be online that the reward is applied
 * @param level the level the reward is applied on
 */
public record MilestoneRewardData(
        @NotNull RewardExecutor executor,
        @Nullable RewardCondition condition,
        @Nullable RewardDescriptionProvider descriptionProvider,
        boolean requiresOnlinePlayer,
        int level
) {}

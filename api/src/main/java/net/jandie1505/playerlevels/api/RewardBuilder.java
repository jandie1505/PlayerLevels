package net.jandie1505.playerlevels.api;

import net.jandie1505.playerlevels.rewards.RewardCondition;
import net.jandie1505.playerlevels.rewards.RewardExecutor;
import org.jetbrains.annotations.NotNull;

/**
 * Builds a reward.
 */
public interface RewardBuilder {

    /**
     * {@link PlayerReward#getServerId()}
     */
    RewardBuilder restrictToServer(String serverId);

    /**
     * {@link PlayerReward#getLevel()}<br/>
     * Has to be set to create the reward.
     */
    RewardBuilder onLevel(int level);

    /**
     * The code that that will be executed when the reward is applied.<br/>
     * {@link RewardExecutor#onApply(PlayerReward, LevelPlayer)}<br/>
     * Has to be set to create the reward.
     */
    RewardBuilder withExecutor(@NotNull RewardExecutor executor);

    /**
     * The code that checks if the reward is already applied.<br/>
     * {@link RewardCondition#isApplied(PlayerReward, LevelPlayer)}.<br/>
     * If not set, it will check if the reward is in the applied rewards list.
     */
    RewardBuilder withCondition(@NotNull RewardCondition condition);

    /**
     * {@link PlayerReward#requiresOnlinePlayer()}<br/>
     * If not set, it is set to true.
     */
    RewardBuilder requiresOnlinePlayer(boolean requiresOnlinePlayer);

    /**
     * {@link PlayerReward#getName()}<br/>
     * If not set, the name will be empty.
     */
    RewardBuilder withName(String name);

    /**
     * {@link PlayerReward#getDescription()}<br/>
     * If not set, the description will be empty.
     */
    RewardBuilder withDescription(String description);

    /**
     * {@link PlayerReward#isEnabled()}<br/>
     * If not set it will be set to true.
     */
    RewardBuilder enabledByDefault(boolean enabled);

    /**
     * Registers the new reward to the manager and returns it.
     * @return registered reward
     */
    PlayerReward create();

}

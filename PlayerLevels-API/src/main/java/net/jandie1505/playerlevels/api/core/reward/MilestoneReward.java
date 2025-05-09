package net.jandie1505.playerlevels.api.core.reward;

import net.jandie1505.playerlevels.api.core.level.Leveler;
import net.jandie1505.playerlevels.core.rewards.RewardCondition;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a milestone reward.<br/>
 * This reward is applied once when the player reaches level x.<br/>
 * It can also check if the reward is no longer applied and then re-apply it using the {@link RewardCondition}.
 */
public interface MilestoneReward extends Reward {

    /**
     * The default RewardCondition.<br/>
     * It returns true when the reward is listed as applied in the LevelData.
     */
    RewardCondition DEFAULT_CONDITION = (reward, player, level) -> player.getData().getOrCreateReceivedReward(reward.getId()).level() >= ((MilestoneReward) reward).getLevel();

    /**
     * Returns the level that is required to get the reward.
     * @return level
     */
    int getLevel();

    /**
     * Returns true if the reward has already been applied.
     * @param leveler leveler
     * @return already applied
     */
    boolean isApplied(@NotNull Leveler leveler);

}

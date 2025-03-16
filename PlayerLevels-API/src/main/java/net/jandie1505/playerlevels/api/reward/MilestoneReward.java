package net.jandie1505.playerlevels.api.reward;

import net.jandie1505.playerlevels.api.level.ReceivedReward;
import net.jandie1505.playerlevels.rewards.RewardCondition;

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
    RewardCondition DEFAULT_CONDITION = (reward, player) -> {
        ReceivedReward data = player.getData().getOrCreateReceivedReward(reward.getId());
        return data.blocked() || player.getData().getOrCreateReceivedReward(reward.getId()).level() > 0;
    };

    /**
     * Returns the level that is required to get the reward.
     * @return level
     */
    int getLevel();

}

package net.jandie1505.playerlevels.api.reward;

import net.jandie1505.playerlevels.api.level.ReceivedReward;
import net.jandie1505.playerlevels.rewards.RewardCondition;

public interface MilestonePlayerReward extends PlayerReward {

    /**
     * The default RewardCondition.<br/>
     * It returns true when the reward is listed as applied in the LevelData.
     */
    RewardCondition DEFAULT_CONDITION = (reward, player) -> {
        ReceivedReward data = player.getData().getOrCreateReceivedReward(reward.getId(), false);
        return data.blocked() || player.getData().getOrCreateReceivedReward(reward.getId(), false).level() > 0;
    };

    /**
     * Returns the level that is required to get the reward.
     * @return level
     */
    int getLevel();

}

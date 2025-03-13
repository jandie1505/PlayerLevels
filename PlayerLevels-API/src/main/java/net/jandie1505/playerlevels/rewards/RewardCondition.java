package net.jandie1505.playerlevels.rewards;

import net.jandie1505.playerlevels.api.LevelPlayer;
import net.jandie1505.playerlevels.api.PlayerReward;
import org.jetbrains.annotations.NotNull;

public interface RewardCondition {

    /**
     * The default RewardCondition.<br/>
     * It returns true when the reward is listed as applied in the LevelData.
     */
    RewardCondition DEFAULT = (reward, player) -> player.getData().getOrCreateReceivedReward(reward.getId()).level() >= player.getData().level();

    /**
     * Returns if the reward has been already applied.<br/>
     * The default is when the player has
     * @param player player
     * @return true = reward is already applied
     */
    boolean isApplied(@NotNull PlayerReward reward, @NotNull LevelPlayer player);

}

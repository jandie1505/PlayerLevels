package net.jandie1505.playerlevels.rewards;

import net.jandie1505.playerlevels.api.level.Leveler;
import net.jandie1505.playerlevels.api.reward.PlayerReward;
import org.jetbrains.annotations.NotNull;

public interface RewardExecutor {

    /**
     * This method is called when the reward is applied to the player.<br/>
     * This method should do the things the reward does when it gets applied.<br/>
     * If the upgrade has successfully been applied, this method has to return true. If it failed, it has to return false. This is important!
     * @param reward reward
     * @param player player
     * @return success
     */
    boolean onApply(@NotNull PlayerReward reward, @NotNull Leveler player);

}

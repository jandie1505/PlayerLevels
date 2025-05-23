package net.jandie1505.playerlevels.core.rewards;

import net.jandie1505.playerlevels.api.core.level.Leveler;
import net.jandie1505.playerlevels.api.core.reward.Reward;
import org.jetbrains.annotations.NotNull;

/**
 * This contains the code which is executed when a reward is applied.
 */
public interface RewardExecutor {

    /**
     * This method is called when the reward is applied to the player.<br/>
     * This method should do the things the reward does when it gets applied.<br/>
     * If the upgrade has successfully been applied, this method has to return true. If it failed, it has to return false. This is important!
     * @param reward reward
     * @param player player
     * @param level the level the reward is applied for
     * @return success
     */
    boolean onApply(@NotNull Reward reward, @NotNull Leveler player, int level);

}

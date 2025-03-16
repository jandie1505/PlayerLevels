package net.jandie1505.playerlevels.rewards;

import net.jandie1505.playerlevels.api.level.Leveler;
import net.jandie1505.playerlevels.api.reward.Reward;
import org.jetbrains.annotations.NotNull;

public interface RewardCondition {

    /**
     * Returns if the reward has been already applied.<br/>
     * The default is when the player has
     * @param player player
     * @return true = reward is already applied
     */
    boolean isApplied(@NotNull Reward reward, @NotNull Leveler player);

}

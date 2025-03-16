package net.jandie1505.playerlevels.rewards;

import net.jandie1505.playerlevels.api.level.Leveler;
import net.jandie1505.playerlevels.api.reward.Reward;
import org.jetbrains.annotations.NotNull;

/**
 * A condition when a reward is already applied, which is additional to the level condition integrated in the reward itself.<br/>
 * This can be used for example to do a permission check for a permission reward.
 * If a player has already received the reward, the player will get the reward again when this condition is not fulfilled.
 */
public interface RewardCondition {

    /**
     * Returns if the reward has been already applied.<br/>
     * The default is when the player has.
     * @param reward the reward calling this method
     * @param player the player for which the reward is calling this method
     * @return true = reward is already applied
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    boolean isApplied(@NotNull Reward reward, @NotNull Leveler player);

}

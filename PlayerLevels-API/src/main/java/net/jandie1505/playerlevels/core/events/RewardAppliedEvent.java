package net.jandie1505.playerlevels.core.events;

import net.jandie1505.playerlevels.api.core.level.Leveler;
import net.jandie1505.playerlevels.api.core.reward.Reward;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * This event is fired after the reward has been marked as applied.
 */
public class RewardAppliedEvent extends Event {
    @NotNull private static final HandlerList handlers = new HandlerList();
    @NotNull private final Leveler leveler;
    @NotNull private final Reward reward;
    @NotNull private final Reward.ApplyResult result;

    /**
     * Creates a RewardAppliedEvent.
     * @param leveler the leveler the reward is applied for
     * @param reward the reward that is applied
     * @param result the result of the reward that has been applied
     */
    public RewardAppliedEvent(@NotNull Leveler leveler, @NotNull Reward reward, @NotNull Reward.ApplyResult result) {
        this.leveler = leveler;
        this.reward = reward;
        this.result = result;
    }

    /**
     * Returns the leveler the reward is applied for.
     * @return leveler
     */
    public @NotNull Leveler getLeveler() {
        return leveler;
    }

    /**
     * Returns the reward that is applied.
     * @return reward
     */
    public @NotNull Reward getReward() {
        return reward;
    }

    /**
     * The "outcome" of the reward apply process.
     * @return result
     */
    public @NotNull Reward.ApplyResult getResult() {
        return result;
    }

    /**
     * Returns the HandlerList.
     * @return HandlerList
     */
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Returns the HandlerList.
     * @return HandlerList
     */
    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }

}

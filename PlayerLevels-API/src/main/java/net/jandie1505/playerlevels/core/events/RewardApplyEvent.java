package net.jandie1505.playerlevels.core.events;

import net.jandie1505.playerlevels.api.core.level.Leveler;
import net.jandie1505.playerlevels.api.core.reward.Reward;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * This event is fired when a reward is applied.<br/>
 * The result of this event will control if the event is applied and/or marked as applied.
 */
public class RewardApplyEvent extends Event implements Cancellable {
    @NotNull private static final HandlerList handlers = new HandlerList();
    @NotNull private final Leveler leveler;
    @NotNull private final Reward reward;
    private final int level;
    @NotNull private Reward.ApplyStatus result;

    /**
     * Creates an RewardApplyEvent.
     * @param leveler the leveler the reward is applied for
     * @param reward the reward that is applied
     * @param level the level the reward is applied for
     */
    public RewardApplyEvent(@NotNull Leveler leveler, @NotNull Reward reward, int level) {
        this.leveler = leveler;
        this.reward = reward;
        this.level = level;
        this.result = Reward.ApplyStatus.APPLY;
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
     * Returns the level the reward is applied for.
     * @return level
     */
    public int getLevel() {
        return level;
    }

    /**
     * Returns the current event result.
     * @return result
     */
    public @NotNull Reward.ApplyStatus getResult() {
        return result;
    }

    /**
     * Sets a new event result.
     * @param result result
     */
    public void setResult(@NotNull Reward.ApplyStatus result) {
        this.result = result;
    }

    /**
     * Returns if the event is cancelled.
     * @return cancelled
     * @deprecated Use {@link RewardApplyEvent#getResult()}
     */
    @Override
    @Deprecated
    public boolean isCancelled() {
        return !this.result.isApplied();
    }

    /**
     * Set the event cancelled or not.
     * @param cancelled cancel
     * @deprecated Use {@link RewardApplyEvent#setResult(Reward.ApplyStatus)}
     */
    @Override
    @Deprecated
    public void setCancelled(boolean cancelled) {
        this.result = cancelled ? Reward.ApplyStatus.CANCEL_SKIP : Reward.ApplyStatus.APPLY;
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

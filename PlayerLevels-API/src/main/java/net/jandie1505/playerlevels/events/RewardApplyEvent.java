package net.jandie1505.playerlevels.events;

import net.jandie1505.playerlevels.api.level.Leveler;
import net.jandie1505.playerlevels.api.reward.Reward;
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
    @NotNull private Result result;

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
        this.result = Result.APPLY;
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
    public @NotNull Result getResult() {
        return result;
    }

    /**
     * Sets a new event result.
     * @param result result
     */
    public void setResult(@NotNull Result result) {
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
     * @deprecated Use {@link RewardApplyEvent#setResult(Result)}
     */
    @Override
    @Deprecated
    public void setCancelled(boolean cancelled) {
        this.result = cancelled ? Result.CANCEL_SKIP : Result.APPLY;
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

    /**
     * The event result.
     */
    public enum Result {

        /**
         * The reward is applied normally.<br/>
         * The default value.
         */
        APPLY(true, true),

        /**
         * The reward is applied, but not marked as applied.
         */
        APPLY_SKIP(true, false),

        /**
         * The reward is not applied and not marked as applied.<br/>
         * This behavior is similar to when the player does not meet the requirement for the reward.
         */
        CANCEL_SKIP(false, false),

        /**
         * The reward is not applied, but marked as applied.<br/>
         * This means the player will skip the reward (if the reward uses the default condition).
         */
        CANCEL_MARK_APPLIED(false, true);

        private final boolean applied;
        private final boolean markedAsApplied;

        Result(boolean applied, boolean markedAsApplied) {
            this.applied = applied;
            this.markedAsApplied = markedAsApplied;
        }

        /**
         * Returns true if the reward is applied.
         * @return reward applied
         */
        public boolean isApplied() {
            return applied;
        }

        /**
         * Returns true if the event is marked as applied.
         * @return marked as successful
         */
        public boolean isMarkedAsApplied() {
            return markedAsApplied;
        }

    }

}

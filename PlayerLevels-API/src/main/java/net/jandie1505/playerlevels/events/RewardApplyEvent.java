package net.jandie1505.playerlevels.events;

import net.jandie1505.playerlevels.api.level.Leveler;
import net.jandie1505.playerlevels.api.reward.Reward;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * This event is fired when a reward is applied.<br/>
 * Cancelling it will prevent the reward from getting applied once.
 */
public class RewardApplyEvent extends Event implements Cancellable {
    @NotNull private static final HandlerList handlers = new HandlerList();
    @NotNull private final Leveler leveler;
    @NotNull private final Reward reward;
    private boolean cancelled;

    /**
     * Creates an RewardApplyEvent.
     * @param leveler leveler
     * @param reward reward
     */
    public RewardApplyEvent(@NotNull Leveler leveler, @NotNull Reward reward) {
        this.leveler = leveler;
        this.reward = reward;
    }

    /**
     * Returns the leveler.
     * @return leveler
     */
    public @NotNull Leveler getLeveler() {
        return leveler;
    }

    /**
     * Returns the reward.
     * @return reward
     */
    public @NotNull Reward getReward() {
        return reward;
    }

    /**
     * Returns if the event is cancelled.
     * @return cancelled
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Set the event cancelled or not.
     * @param cancelled cancel
     */
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
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

package net.jandie1505.playerlevels.events;

import net.jandie1505.playerlevels.api.level.Leveler;
import net.jandie1505.playerlevels.api.reward.Reward;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * This event is fired when an upgrade is applied.
 */
public class RewardApplyEvent extends Event implements Cancellable {
    @NotNull private static final HandlerList handlers = new HandlerList();
    @NotNull private final Leveler player;
    @NotNull private final Reward reward;
    private boolean cancelled;

    public RewardApplyEvent(@NotNull Leveler player, @NotNull Reward reward) {
        this.player = player;
        this.reward = reward;
    }

    public @NotNull Leveler getPlayer() {
        return player;
    }

    public @NotNull Reward getReward() {
        return reward;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }

}

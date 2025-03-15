package net.jandie1505.playerlevels.events;

import net.jandie1505.playerlevels.api.level.LevelPlayer;
import net.jandie1505.playerlevels.api.reward.PlayerReward;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * This event is fired when an upgrade is applied.
 */
public class RewardApplyEvent extends Event implements Cancellable {
    @NotNull private static final HandlerList handlers = new HandlerList();
    @NotNull private final LevelPlayer player;
    @NotNull private final PlayerReward reward;
    private boolean cancelled;

    public RewardApplyEvent(@NotNull LevelPlayer player, @NotNull PlayerReward reward) {
        this.player = player;
        this.reward = reward;
    }

    public @NotNull LevelPlayer getPlayer() {
        return player;
    }

    public @NotNull PlayerReward getReward() {
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

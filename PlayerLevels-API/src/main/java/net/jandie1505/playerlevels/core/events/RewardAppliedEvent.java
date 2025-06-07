package net.jandie1505.playerlevels.core.events;

import net.jandie1505.playerlevels.api.core.level.Leveler;
import net.jandie1505.playerlevels.api.core.reward.Reward;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * This event is fired after the reward has been marked as applied.
 */
public class RewardAppliedEvent extends Event {
    @NotNull private static final HandlerList handlers = new HandlerList();
    @NotNull private final Leveler leveler;
    @NotNull private final Reward reward;
    @NotNull private final Reward.ApplyStatus status;
    private int level;

    /**
     * Creates a RewardAppliedEvent.
     * @param leveler the leveler the reward is applied for
     * @param reward the reward that is applied
     * @param status the "outcome" of the reward apply process
     * @param level the level the reward has been applied for
     */
    public RewardAppliedEvent(@NotNull Leveler leveler, @NotNull Reward reward, @NotNull Reward.ApplyStatus status, int level) {
        this.leveler = leveler;
        this.reward = reward;
        this.status = status;
        this.level = level;
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
     * Returns the "outcome" of applying the reward.
     * @return apply status
     */
    @ApiStatus.Experimental
    public @NotNull Reward.ApplyStatus getStatus() {
        return status;
    }

    /**
     * Returns the level the reward has been applied for.
     * @return level
     */
    @ApiStatus.Experimental
    public int getLevel() {
        return level;
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

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
    private final int level;

    /**
     * Creates an RewardAppliedEvent.
     * @param leveler the leveler the reward is applied for
     * @param reward the reward that is applied
     * @param level the level the reward is applied for
     */
    public RewardAppliedEvent(@NotNull Leveler leveler, @NotNull Reward reward, int level) {
        this.leveler = leveler;
        this.reward = reward;
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
     * Returns the level the reward is applied for.
     * @return level
     */
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

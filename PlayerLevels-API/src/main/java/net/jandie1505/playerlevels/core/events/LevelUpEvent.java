package net.jandie1505.playerlevels.core.events;

import net.jandie1505.playerlevels.api.core.level.Leveler;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * An event that is fired when a player levels up.
 */
public class LevelUpEvent extends Event {
    @NotNull private static final HandlerList handlers = new HandlerList();
    @NotNull private final Leveler leveler;
    private final int oldLevel;
    private final int newLevel;

    /**
     * Creates a new LevelUpEvent.
     * @param leveler leveler
     * @param oldLevel old level
     * @param newLevel new level
     */
    public LevelUpEvent(@NotNull Leveler leveler, int oldLevel, int newLevel) {
        this.leveler = leveler;
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
    }

    /**
     * Returns the leveler.
     * @return leveler
     */
    public @NotNull Leveler getLeveler() {
        return leveler;
    }

    /**
     * Returns the old level.
     * @return old level
     */
    public int getOldLevel() {
        return oldLevel;
    }

    /**
     * Returns the new level.
     * @return new level.
     */
    public int getNewLevel() {
        return newLevel;
    }

    /**
     * Returns the HandlerList
     * @return HandlerList
     */
    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Returns the HandlerList
     * @return HandlerList
     */
    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }

}

package net.jandie1505.playerlevels.events;

import net.jandie1505.playerlevels.api.level.Leveler;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class LevelUpEvent extends Event {
    @NotNull private static final HandlerList handlers = new HandlerList();
    @NotNull private final Leveler leveler;
    private int oldLevel;
    private int newLevel;

    public LevelUpEvent(@NotNull Leveler leveler, int oldLevel, int newLevel) {
        this.leveler = leveler;
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
    }

    public @NotNull Leveler getLevelPlayer() {
        return leveler;
    }

    public int getOldLevel() {
        return oldLevel;
    }

    public int getNewLevel() {
        return newLevel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }

}

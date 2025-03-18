package net.jandie1505.playerlevels.api.level;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Manages the top list.
 */
public interface TopListManager {

    /**
     * Returns the cached top list.
     * @return top list
     */
    List<TopListEntry> getTopList();

    /**
     * Regenerates cached top list.<br/>
     * Please note that this might be a time-expensive database operation.
     * @return top list
     */
    CompletableFuture<List<TopListEntry>> updateTopListAsynchronously();

    /**
     * A top list entry.
     * @param playerUUID player uuid
     * @param name the cached name of the player
     * @param level level
     * @param xp xp
     */
    record TopListEntry(@NotNull UUID playerUUID, @Nullable String name, int level, double xp) {}

}

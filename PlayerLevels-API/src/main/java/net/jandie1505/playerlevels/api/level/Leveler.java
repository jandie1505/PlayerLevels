package net.jandie1505.playerlevels.api.level;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface Leveler {

    /**
     * @return player UUID
     */
    @NotNull UUID getPlayerUUID();

    /**
     * Returns the data of the player.<br/>
     * The data stores information like the level, xp or received rewards.
     * @return data
     */
    @NotNull LevelerData getData();

    /**
     * Returns true if the leveler is currently cached.<br/>
     * You should avoid using not cached levelers, specially when the player is online.<br/>
     * Using uncached levelers can cause database conflicts.
     * While the database is protected, only one data can be written to the database.
     * @return if player is cached
     */
    boolean isCached();

}

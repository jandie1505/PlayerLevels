package net.jandie1505.playerlevels.core.database;

import net.jandie1505.playerlevels.api.core.level.TopListManager;
import net.jandie1505.playerlevels.core.leveler.LevelerData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public interface Database {

    // ----- DELETE LEVELER -----

    /**
     * Deletes a leveler from database.
     * @param playerUUID player uuid
     */
    boolean deleteLeveler(@NotNull UUID playerUUID);

    // ----- LEVEL DATA -----

    /**
     * Updates an existing leveler in the database.
     * @param playerUUID player uuid
     * @param data level data
     * @param updateId generated update id
     * @throws DatabaseException if something goes wrong
     */
    void updateLevelDataInDatabase(@NotNull UUID playerUUID, @NotNull LevelerData data, @NotNull String updateId) throws DatabaseException;

    /**
     * Inserts a new leveler into the database.
     * @param playerUUID player uuid
     * @param data level data
     * @param updateId generated update id
     * @throws DatabaseException if something goes wrong
     */
    void insertLevelDataIntoDatabase(@NotNull UUID playerUUID, @NotNull LevelerData data, @NotNull String updateId) throws DatabaseException;

    /**
     * Pulls the LevelData for a player from the database.
     * @param playerUUID player uuid
     * @return result {@link LevelDataPullResult}
     * @throws DatabaseException if the pull was not successful
     */
    LevelDataPullResult getLevelDataFromDatabase(@NotNull UUID playerUUID) throws DatabaseException;

    // ----- SEARCH -----

    /**
     * Returns a list of uuids of players that have the specified name cached.
     * @param playerName player name
     * @return list of player uuids
     */
    @NotNull List<@NotNull UUID> findLevelerByNameSync(@NotNull String playerName);

    // ----- TOPLIST -----

    /**
     * Returns a toplist with the specified length.
     * @param length length
     * @return toplist
     */
    @Nullable List<TopListManager.TopListEntry> getTopList(int length);

    // ----- MANAGE TABLE -----

    /**
     * Sets up the database.<br/>
     * This will be executed every time the LevelingManager is initialized.
     * @return success
     */
    boolean setupDatabase();

    // ----- INNER CLASSES -----

    /**
     * The result of pulling a LevelData from database.
     * @param data level data
     * @param updateId update id
     */
    record LevelDataPullResult(@NotNull LevelerData data, @NotNull String updateId) {};

    /**
     * This exception should be thrown when something goes wrong with database operations.
     */
    class DatabaseException extends Exception {
        public DatabaseException(String message, Throwable cause) {
            super(message, cause);
        }

        public DatabaseException(String message) {
            super(message);
        }
    }

}

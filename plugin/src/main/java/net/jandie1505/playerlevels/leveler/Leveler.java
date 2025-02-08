package net.jandie1505.playerlevels.leveler;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Leveler {
    @NotNull private final UUID playerId;
    @NotNull private final LevelerData data;

    private String updateId;
    private boolean valid;

    public Leveler(@NotNull UUID playerId) {
        this.playerId = playerId;
        this.data = new LevelerData();
        this.valid = true;
    }

    // ----- DATA -----

    /**
     * Returns the player id.
     * @return player id
     */
    public final @NotNull UUID playerId() {
        return this.playerId;
    }

    /**
     * Returns the data.
     * @return data
     */
    public final @NotNull LevelerData data() {
        return this.data;
    }

    // ----- VALIDATION -----

    /**
     * Returns true if the object is valid.
     * @return valid
     */
    public boolean isValid() {
        return this.valid;
    }

    /**
     * Invalidate this Leveler.
     */
    public void invalidate() {
        this.valid = false;
    }

    // ----- DATABASE SYNC -----

    /**
     * Updates the Leveler asynchronously.<br/>
     * @param connection connection
     * @return asynchronous result
     */
    public CompletableFuture<UpdateResult> updateAsync(final @NotNull Connection connection) {
        return CompletableFuture.supplyAsync(() -> this.update(connection));
    }

    /**
     * Updates the Leveler.<br/>
     * Don't use this from the servers main thread.
     * @param connection connection
     * @return result
     */
    private UpdateResult update(Connection connection) {
        if (!this.isValid()) throw new IllegalStateException("Leveler has already been invalidated");

        try {
            LevelerDataPullResult pullResult = this.getDataFromDatabase(connection);

            if (pullResult != null) {

                // Current data is outdated, replace it with database data
                if (!this.updateId.equals(pullResult.updateId())) {
                    this.data.merge(pullResult.data());
                    this.updateId = pullResult.updateId();
                    return UpdateResult.LOCAL_OUTDATED;
                }

                // Remote data is outdated, push changes
                if (!this.data.equals(pullResult.data())) {
                    this.updateDataInDatabase(connection, this.data);
                    return UpdateResult.REMOTE_OUTDATED_AVAIL;
                }


            } else {
                this.insertDataIntoDatabase(connection, this.data);
                return UpdateResult.REMOTE_OUTDATED_MISSING;
            }

            return UpdateResult.UP_TO_DATE;
        } catch (SQLException e) {
            this.invalidate();
            return UpdateResult.ERROR;
        }

    }

    // ----- DATA SERIALIZATION -----

    /**
     * Updates the database record with the locally stored data.<br/>
     * Only works if there is already an entry for this player.<br/>
     * If not, use {@link Leveler#insertDataIntoDatabase(Connection, LevelerData)}<br/>
     * Should only be called from {@link Leveler#update(Connection)}
     * @param c connection
     * @param data data
     * @return {@link PreparedStatement#executeUpdate()} result
     * @throws SQLException exception
     */
    private int updateDataInDatabase(Connection c, LevelerData data) throws SQLException {

        // Generate a new update id to invalidate the cached data on all other instances
        this.updateId = UUID.randomUUID().toString();

        // Update data
        String updateSql = "UPDATE players " +
                "SET " + Keys.LEVEL + " = ?, " + Keys.XP + " = ?, " + Keys.UPDATE_ID + " = ?" +
                "WHERE " + Keys.PLAYER_ID + " = ?";

        PreparedStatement updateStatement = c.prepareStatement(updateSql);

        updateStatement.setInt(1, data.level());
        updateStatement.setDouble(2, data.xp());

        updateStatement.setString(3, this.updateId);
        updateStatement.setString(3, this.playerId.toString());

        return updateStatement.executeUpdate();
    }

    /**
     * Inserts a new database record with the locally stored data.<br/>
     * Only works if the player has no entry.<br/>
     * If not, use {@link Leveler#updateDataInDatabase(Connection, LevelerData)}<br/>
     * Should only be called from {@link Leveler#update(Connection)}
     * @param c connection
     * @param data data
     * @return {@link PreparedStatement#executeUpdate()} result
     * @throws SQLException exception
     */
    private int insertDataIntoDatabase(Connection c, LevelerData data) throws SQLException {

        // Generate a new update id to invalidate the cached data on all other instances
        this.updateId = UUID.randomUUID().toString();

        // Insert data
        String insertSql = "INSERT INTO players (" + Keys.PLAYER_ID + ", " + Keys.LEVEL + ", " + Keys.XP + ", " + Keys.UPDATE_ID + ") " +
                "VALUES (?, ?, ?, ?)";

        PreparedStatement insertStatement = c.prepareStatement(insertSql);
        insertStatement.setString(1, this.playerId.toString());
        insertStatement.setInt(2, this.data.level());
        insertStatement.setDouble(3, this.data.xp());
        insertStatement.setString(4, this.updateId);

        return insertStatement.executeUpdate();
    }

    /**
     * Pulls the current data from the database.
     * @param c connection
     * @return result
     * @throws SQLException exception
     */
    private LevelerDataPullResult getDataFromDatabase(Connection c) throws SQLException {

        String pullSql = "SELECT * FROM " + Keys.LEVEL + " WHERE " + Keys.PLAYER_ID + " = ?";
        PreparedStatement pullStatement = c.prepareStatement(pullSql);
        pullStatement.setString(1, this.playerId.toString());
        ResultSet pullResultSet = pullStatement.executeQuery();

        if (pullResultSet.next()) {
            String updateId = pullResultSet.getString(Keys.UPDATE_ID);

            return new LevelerDataPullResult(
                    new LevelerData(
                            pullResultSet.getInt(Keys.LEVEL),
                            pullResultSet.getDouble(Keys.XP)
                    ),
                    updateId
            );
        } else {
            return null;
        }

    }

    // ----- UTILITY CLASSES -----

    private record LevelerDataPullResult(@NotNull LevelerData data, @NotNull String updateId) {}

    private interface Keys {
        String PLAYER_ID = "player_id";
        String LEVEL = "level";
        String XP = "xp";
        String UPDATE_ID = "update_id";
    }

    public enum UpdateResult {
        LOCAL_OUTDATED(true, false),
        REMOTE_OUTDATED_AVAIL(false, true),
        REMOTE_OUTDATED_MISSING(false, true),
        UP_TO_DATE(false, false),
        ERROR(true, false);

        private final boolean localChanged;
        private final boolean remoteChanged;

        UpdateResult(boolean localChanged, boolean remoteChanged) {
            this.localChanged = localChanged;
            this.remoteChanged = remoteChanged;
        }

        public boolean isLocalChanged() {
            return localChanged;
        }

        public boolean isRemoteChanged() {
            return remoteChanged;
        }

    }

}

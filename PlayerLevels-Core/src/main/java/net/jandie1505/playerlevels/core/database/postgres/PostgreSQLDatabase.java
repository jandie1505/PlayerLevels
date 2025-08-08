package net.jandie1505.playerlevels.core.database.postgres;

import net.jandie1505.playerlevels.api.core.level.TopListManager;
import net.jandie1505.playerlevels.core.database.Database;
import net.jandie1505.playerlevels.core.database.mariadb.MariaDBDatabaseManager;
import net.jandie1505.playerlevels.core.leveler.Leveler;
import net.jandie1505.playerlevels.core.leveler.LevelerData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PostgreSQLDatabase implements Database {
    @NotNull private final PostgreSQLDatabaseManager manager;

    public PostgreSQLDatabase(@NotNull PostgreSQLDatabaseManager manager) {
        this.manager = manager;
    }

    // ----- DELETE -----

    /**
     * Deletes a player from database.
     * @param playerUUID player uuid player uuid
     * @return success
     */
    @Override
    public boolean deleteLeveler(@NotNull UUID playerUUID) {
        String sql = "DELETE FROM playerlevels_players WHERE player_uuid = ?";

        try (Connection connection = this.getConnection();
             PreparedStatement statement = connection != null ? connection.prepareStatement(sql) : null
        ) {

            if (connection == null || statement == null) {
                this.getLogger().warning("Failed to erase player " + playerUUID + ": connection is null");
                return false;
            }

            statement.setObject(1, playerUUID);
            statement.executeUpdate(); // Here, the DELETE is executed!

            return true;
        } catch (SQLException e) {
            this.getLogger().log(Level.WARNING, "Failed to erase player " + playerUUID, e);
            return false;
        }
    }

    // ----- LEVEL DATA -----

    /**
     * Updates the database record with the locally stored data.<br/>
     * Only works if there is already an entry for this player.<br/>
     * If not, use {@link #insertLevelDataIntoDatabase(UUID, LevelerData, String)} <br/>
     * Should only be called from {@link Leveler#sync()}.
     * @return {@link PreparedStatement#executeUpdate()} result
     * @throws SQLException exception
     */
    public void updateLevelDataInDatabase(@NotNull UUID playerUUID, @NotNull LevelerData data, @NotNull String updateId) throws DatabaseException {

        // Generate a new update id to invalidate the cached data on all other instances
        //this.updateId = UUID.randomUUID().toString(); // TODO: Move to Leveler

        try (Connection c = this.getConnection()) {

            if (c == null) {
                throw new DatabaseException("Failed to connect to MariaDB database");
            }

            // Update data
            String updateSql = "UPDATE playerlevels_players SET data = ?::JSONB, update_id = ? WHERE player_uuid = ?";

            try (PreparedStatement updateStatement = c.prepareStatement(updateSql)) {

                updateStatement.setString(1, data.toJSON().toString());
                updateStatement.setString(2, updateId);
                updateStatement.setObject(3, playerUUID);

                updateStatement.executeUpdate();
                return;
            }

        } catch (SQLException | JSONException e) {
            throw new DatabaseException("Failed to update LevelData in database", e);
        }

    }

    /**
     * Inserts a new database record with the locally stored data.<br/>
     * Only works if the player has no entry.<br/>
     * If not, use {@link #updateLevelDataInDatabase(UUID, LevelerData, String)}.<br/>
     * Should only be called from {@link Leveler#sync()}.
     * @return {@link PreparedStatement#executeUpdate()} result
     * @throws SQLException exception
     */
    public void insertLevelDataIntoDatabase(@NotNull UUID playerUUID, @NotNull LevelerData data, @NotNull String updateId) throws DatabaseException {

        // Generate a new update id to invalidate the cached data on all other instances
        //this.updateId = UUID.randomUUID().toString(); TODO: Move to Leveler

        try (Connection c = this.getConnection()) {

            if (c == null) {
                throw new DatabaseException("Failed to connect to MariaDB database");
            }

            // Insert data
            String insertSql = "INSERT INTO playerlevels_players (player_uuid, data, update_id) VALUES (?, ?::JSONB, ?)";

            try (PreparedStatement insertStatement = c.prepareStatement(insertSql)) {

                insertStatement.setObject(1, playerUUID);
                insertStatement.setString(2, data.toJSON().toString());
                insertStatement.setString(3, updateId);

                insertStatement.executeUpdate();
                return;
            }

        } catch (SQLException | JSONException e) {
            throw new DatabaseException("Failed tu insert LevelData into database", e);
        }

    }

    /**
     * Pulls the current data from the database.
     * @return result
     * @throws JSONException when data json is invalid
     */
    public LevelDataPullResult getLevelDataFromDatabase(@NotNull UUID playerUUID) throws DatabaseException {

        try (Connection c = this.getConnection()) {

            if (c == null) {
                throw new DatabaseException("Failed to connect to MariaDB database");
            }

            String pullSql = "SELECT * FROM playerlevels_players WHERE player_uuid = ?";

            try (PreparedStatement pullStatement = c.prepareStatement(pullSql)) {

                pullStatement.setObject(1, playerUUID);

                ResultSet pullResultSet = pullStatement.executeQuery();

                if (pullResultSet.next()) {
                    String updateId = pullResultSet.getString("update_id");

                    return new LevelDataPullResult(
                            LevelerData.fromJSON(new JSONObject(pullResultSet.getString("data"))),
                            updateId
                    );
                } else {
                    return null;
                }

            }

        } catch (SQLException | JSONException e) {
            throw new DatabaseException("Failed to pull LevelData from database", e);
        }

    }

    // ----- SEARCH -----

    /**
     * Searches for levelers with the specified player name.
     * @param playerName player name
     * @return list of uuids that have the specified name cached
     */
    public @NotNull List<@NotNull UUID> findLevelerByNameSync(@NotNull String playerName) {
        String sql = "SELECT player_uuid FROM playerlevels_players WHERE data->>'name' = ?";

        try (
                Connection connection = this.getConnection();
                PreparedStatement statement = connection != null ? connection.prepareStatement(sql) : null;
        ) {

            if (connection == null || statement == null) {
                this.getLogger().log(Level.WARNING, "Failed to find player " + playerName + ": connection is null");
                return List.of();
            }

            statement.setString(1, playerName);

            try (ResultSet rs = statement.executeQuery()) {
                List<@NotNull UUID> list = new ArrayList<>();

                while (rs.next()) {
                    list.add((UUID) rs.getObject("player_uuid"));
                }

                return list;
            } catch (SQLException | IllegalArgumentException e) {
                this.getLogger().log(Level.WARNING, "Failed to find player " + playerName + ": ", e);
                return List.of();
            }

        } catch (SQLException e) {
            this.getLogger().log(Level.SEVERE, "Failed to find leveler by player name " + playerName, e);
            return List.of();
        }
    }

    // ----- TOP LIST -----

    /**
     * Creates a toplist with the specified length.
     * @param until toplist length
     * @return toplist
     */
    public @Nullable List<TopListManager.TopListEntry> getTopList(int until) {

        String sql = "SELECT player_uuid, data, (data->>'level')::INT as level FROM playerlevels_players ORDER BY level DESC LIMIT ?";

        try (Connection connection = this.getConnection();
             PreparedStatement statement = connection != null ? connection.prepareStatement(sql) : null;
        ) {

            if (connection == null || statement == null) {
                this.getLogger().warning("Failed to get top list: connection is null");
                return null;
            }

            statement.setInt(1, until);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<TopListManager.TopListEntry> topList = new ArrayList<>();
                while (resultSet.next()) {
                    JSONObject json = new JSONObject(resultSet.getString("data"));
                    topList.add(new TopListManager.TopListEntry((UUID) resultSet.getObject("player_uuid"), json.optString("name", null), json.getInt("level"), json.getDouble("xp")));
                }
                return topList;
            } catch (Exception e) {
                return null;
            }

        } catch (SQLException e) {
            return null;
        }

    }

    // ----- MANAGE TABLE -----

    /**
     * Creates the plugin's table.
     * @return success
     */
    public boolean setupDatabase() {

        try (Connection connection = this.getConnection()) {

            if (connection == null) {
                this.getLogger().log(Level.WARNING, "Failed to create table: connection is null");
                return false;
            }

            String sql = "CREATE TABLE IF NOT EXISTS playerlevels_players (" +
                    "player_uuid UUID PRIMARY KEY," +
                    "data JSONB NOT NULL," +
                    "update_id VARCHAR(36) NOT NULL," +
                    "last_updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP" +
                    ");" +
                    "CREATE INDEX IF NOT EXISTS idx_playerlevels_players_level ON playerlevels_players (((data ->> 'level')::INT ));" +
                    "CREATE INDEX IF NOT EXISTS idx_playerlevels_players_cached_name ON playerlevels_players ((data ->> 'name'));" +
                    "CREATE OR REPLACE FUNCTION playerlevels_players_update_last_updated() RETURNS TRIGGER AS $$" +
                    "BEGIN " +
                    "NEW.last_updated = CURRENT_TIMESTAMP;" +
                    "RETURN NEW;" +
                    "END;" +
                    "$$ LANGUAGE plpgsql;" +
                    "CREATE TRIGGER trg_playerlevels_players_update_last_updated BEFORE INSERT OR UPDATE ON playerlevels_players FOR EACH ROW EXECUTE FUNCTION playerlevels_players_update_last_updated();";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.executeUpdate();
                return true;
            } catch (SQLException e) {
                this.getLogger().log(Level.WARNING, "Failed to create table", e);
                return false;
            }

        } catch (SQLException e) {
            this.getLogger().log(Level.WARNING, "Failed to create table", e);
            return false;
        }

    }

    // ----- UTILITIES -----

    private Connection getConnection() throws SQLException {
        return this.manager.getConnection();
    }

    private Logger getLogger() {
        return this.manager.getPlugin().getLogger();
    }

}

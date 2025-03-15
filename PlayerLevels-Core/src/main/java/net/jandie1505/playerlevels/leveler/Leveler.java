package net.jandie1505.playerlevels.leveler;

import net.jandie1505.playerlevels.events.LevelUpEvent;
import net.jandie1505.playerlevels.database.DatabaseSource;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public final class Leveler implements net.jandie1505.playerlevels.api.level.Leveler {
    @NotNull private final LevelingManager manager;
    @NotNull private final UUID playerUUID;
    @NotNull private final DatabaseSource databaseSource;
    @NotNull private final AtomicBoolean databaseUpdateInProgress;
    @NotNull private final AtomicBoolean manageValuesInProgress;
    @NotNull private final LevelerData data;
    @NotNull private String updateId;

    public Leveler(@NotNull LevelingManager manager, @NotNull UUID playerUUID, @NotNull DatabaseSource databaseSource) {
        this.manager = manager;
        this.playerUUID = playerUUID;
        this.databaseSource = databaseSource;
        this.databaseUpdateInProgress = new AtomicBoolean(false);
        this.manageValuesInProgress = new AtomicBoolean(false);
        this.data = new LevelerData(data -> new BukkitRunnable() {
            @Override
            public void run() {
                Leveler.this.process(); // TODO: I don't know if this can stay like it is or if it should be called directly without the async task
            }
        }.runTaskAsynchronously(this.manager.getPlugin()));
        this.updateId = UUID.randomUUID().toString();
    }

    // ----- GET VALUES -----

    public @NotNull UUID getPlayerUUID() {
        return this.playerUUID;
    }

    public @NotNull LevelerData getData() {
        return data;
    }

    public @NotNull String getUpdateId() {
        return updateId;
    }

    public boolean isCached() {
        return this.manager.getLeveler(this.playerUUID) == this;
    }

    // ----- MANAGE VALUES -----

    /**
     * Processes the leveler asynchronously.
     */
    public void processAsynchronously() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Leveler.this.process();
            }
        }.runTaskAsynchronously(this.manager.getPlugin());
    }

    /**
     * Processes the leveler.<br/>
     * This means running the leveling process and applying rewards.
     */
    public void process() {

        // Prevent executing this more than once at the same time
        if (!this.manageValuesInProgress.compareAndSet(false, true)) {
            throw new IllegalStateException("Process task of Leveler " + this.playerUUID + " has been called while still in progress. This can be caused by unsupported API usage.");
        }

        try {
            this.levelUp();
            this.manager.getPlugin().getRewardsManager().processPlayer(this);
        } catch (Exception e) {
            this.manager.getPlugin().getLogger().log(Level.SEVERE, "Manage values task of " + this.playerUUID + " threw an exception", e);
        }

        this.manageValuesInProgress.set(false);
    }

    /**
     * Level up a player when the required amount of xp for the new level has been reached.
     */
    private void levelUp() {
        final int levelAtStart = this.data.level();
        final double xpAtStart = this.data.xp();

        int level = levelAtStart;
        double xp = xpAtStart;
        double requiredXP = this.manager.getXPForNextLevel(level, level + 1);
        int maxIterations = 100;
        while (maxIterations-- > 0 && xp >= requiredXP) {
            level++;
            xp -= requiredXP;
            requiredXP = this.manager.getXPForNextLevel(level, level + 1);
        }

        this.data.level(level, false);
        this.data.xp(xp, false);

        final int levelToPush = level;

        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getServer().getPluginManager().callEvent(new LevelUpEvent(Leveler.this, levelAtStart, levelToPush));
            }
        }.runTask(this.manager.getPlugin());

    }

    // ----- DATABASE SYNC -----

    /**
     * Synchronizes the Leveler asynchronously with the database.<br/>
     * The recommended way for updating.
     * @return future result
     */
    public CompletableFuture<UpdateResult> syncAsynchronously() {
        CompletableFuture<UpdateResult> future = new CompletableFuture<>();
        new BukkitRunnable() {
            @Override
            public void run() {
                UpdateResult result = Leveler.this.sync();
                future.complete(result);
            }
        }.runTaskAsynchronously(this.manager.getPlugin());
        return future;
    }

    /**
     * Synchronizes the Leveler with the database.<br/>
     * This is a blocking method.
     * Don't use this from the servers main thread.
     * @return result
     */
    public @NotNull UpdateResult sync() {

        // Prevent executing this more than once at the same time
        if (!this.databaseUpdateInProgress.compareAndSet(false, true)) {
            this.manager.getPlugin().getLogger().warning("Sync task of Leveler " + this.playerUUID + " has been called while still in progress. This can be caused by unsupported API usage.");
            return UpdateResult.ALREADY_IN_PROGRESS;
        }

        try (Connection connection = this.databaseSource.getConnection()) {
            if (connection == null) {
                System.out.println("Error: No database connection available");
                return UpdateResult.ERROR;
            }

            LevelDataPullResult pullResult = this.getDataFromDatabase(connection);

            if (pullResult != null) {
                // Current data is outdated, replace it with database data
                if (!this.updateId.equals(pullResult.updateId())) {
                    this.data.merge(pullResult.data(), true);
                    this.updateId = pullResult.updateId();
                    System.out.println("Local outdated");
                    return UpdateResult.LOCAL_OUTDATED;
                }

                // Remote data is outdated, push changes
                if (!this.data.equals(pullResult.data())) {
                    this.updateDataInDatabase(connection);
                    System.out.println("Remote outdated");
                    return UpdateResult.REMOTE_OUTDATED_AVAIL;
                }

            } else {
                this.insertDataIntoDatabase(connection);
                System.out.println("Remote not avail");
                return UpdateResult.REMOTE_OUTDATED_MISSING;
            }

            return UpdateResult.UP_TO_DATE;
        } catch (SQLException | JSONException e) {
            System.out.println("Exception: ");
            e.printStackTrace();
            return UpdateResult.ERROR;
        } finally {
            this.databaseUpdateInProgress.set(false);
        }
    }

    // ----- DATA SERIALIZATION -----

    /**
     * Updates the database record with the locally stored data.<br/>
     * Only works if there is already an entry for this player.<br/>
     * If not, use {@link Leveler#insertDataIntoDatabase(Connection)} <br/>
     * Should only be called from {@link Leveler#sync()}.
     * @param c connection
     * @return {@link PreparedStatement#executeUpdate()} result
     * @throws SQLException exception
     */
    private int updateDataInDatabase(Connection c) throws SQLException {
        // Generate a new update id to invalidate the cached data on all other instances
        this.updateId = UUID.randomUUID().toString();

        // Update data
        String updateSql = "UPDATE playerlevels_players SET data = ?, update_id = ? WHERE player_uuid = ?";

        try (PreparedStatement updateStatement = c.prepareStatement(updateSql)) {

            updateStatement.setString(1, this.data.toJSON().toString());
            updateStatement.setString(2, this.updateId);
            updateStatement.setString(3, this.playerUUID.toString());

            return updateStatement.executeUpdate();
        }
    }

    /**
     * Inserts a new database record with the locally stored data.<br/>
     * Only works if the player has no entry.<br/>
     * If not, use {@link Leveler#updateDataInDatabase(Connection)}.<br/>
     * Should only be called from {@link Leveler#sync()}.
     * @param c connection
     * @return {@link PreparedStatement#executeUpdate()} result
     * @throws SQLException exception
     */
    private int insertDataIntoDatabase(Connection c) throws SQLException {

        // Generate a new update id to invalidate the cached data on all other instances
        this.updateId = UUID.randomUUID().toString();

        // Insert data
        String insertSql = "INSERT INTO playerlevels_players (player_uuid, data, update_id) VALUES (?, ?, ?)";

        try (PreparedStatement insertStatement = c.prepareStatement(insertSql)) {

            insertStatement.setString(1, this.playerUUID.toString());
            insertStatement.setString(2, this.data.toJSON().toString());
            insertStatement.setString(3, this.updateId);

            return insertStatement.executeUpdate();
        }
    }

    /**
     * Pulls the current data from the database.
     * @param c connection
     * @return result
     * @throws SQLException exception
     * @throws JSONException when data json is invalid
     */
    private LevelDataPullResult getDataFromDatabase(Connection c) throws SQLException, JSONException {

        String pullSql = "SELECT * FROM playerlevels_players WHERE player_uuid = ?";

        try (PreparedStatement pullStatement = c.prepareStatement(pullSql)) {

            pullStatement.setString(1, this.playerUUID.toString());

            ResultSet pullResultSet = pullStatement.executeQuery();

            if (pullResultSet.next()) {
                String updateId = pullResultSet.getString("update_id");

                return new LevelDataPullResult(
                        LevelerData.fromJSON(new JSONObject(pullResultSet.getString("data")), null),
                        updateId
                );
            } else {
                return null;
            }

        }
    }

    // ----- INNER CLASSES -----

    private record LevelDataPullResult(@NotNull LevelerData data, @NotNull String updateId) {}

    public enum UpdateResult {
        LOCAL_OUTDATED(true, false, false),
        REMOTE_OUTDATED_AVAIL(false, true, false),
        REMOTE_OUTDATED_MISSING(false, true, false),
        UP_TO_DATE(false, false, false),
        ERROR(true, false, true),
        ALREADY_IN_PROGRESS(false, false, true);

        private final boolean localChanged;
        private final boolean remoteChanged;
        private final boolean fail;

        UpdateResult(boolean localChanged, boolean remoteChanged, boolean fail) {
            this.localChanged = localChanged;
            this.remoteChanged = remoteChanged;
            this.fail = fail;
        }

        public boolean isLocalChanged() {
            return localChanged;
        }

        public boolean isRemoteChanged() {
            return remoteChanged;
        }

        public boolean isFail() {
            return fail;
        }

    }

}

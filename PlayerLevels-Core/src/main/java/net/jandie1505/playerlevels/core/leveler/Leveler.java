package net.jandie1505.playerlevels.core.leveler;

import net.jandie1505.playerlevels.core.constants.ConfigKeys;
import net.jandie1505.playerlevels.core.database.DatabaseSource;
import net.jandie1505.playerlevels.core.events.LevelUpEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public final class Leveler implements net.jandie1505.playerlevels.api.core.level.Leveler {
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
        this.data = new LevelerData();
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

        if (this.manageValuesInProgress.get()) {
            throw new IllegalStateException("Process task of Leveler " + this.playerUUID + " has been called while still in progress (async call). This can be caused by unsupported API usage.");
        }

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
            if (this.manager.getPlugin().config().optBoolean(ConfigKeys.REMOVE_NON_EXISTENT_REWARD_ENTRIES, false)) this.cleanupNotExistingRewards();
            this.updateCachedName();
        } catch (Exception e) {
            this.manager.getPlugin().getLogger().log(Level.SEVERE, "Manage values task of " + this.playerUUID + " threw an exception", e);
        }

        this.manageValuesInProgress.set(false);
    }

    /**
     * Level up a player when the required amount of xp for the new level has been reached.
     * @return LevelUpEvent or null if the player has not leveled up
     */
    private @Nullable LevelUpEvent levelUp() {
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

        if (level == levelAtStart) return null;

        this.data.level(level);
        this.data.xp(xp);

        final LevelUpEvent event = new LevelUpEvent(Leveler.this, levelAtStart, level);

        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getServer().getPluginManager().callEvent(event);
            }
        }.runTask(this.manager.getPlugin());

        return event;
    }

    /**
     * Removes reward entries for rewards that do not exist.
     */
    private void cleanupNotExistingRewards() {

        Iterator<String> i = this.getData().internalReceivedRewards().keySet().iterator();
        while (i.hasNext()) {
            String key = i.next();
            if (this.manager.getPlugin().getRewardsManager().getReward(key) != null) continue;
            i.remove();
        }

    }

    /**
     * Updates the cached name of the player.
     */
    private void updateCachedName() {
        Player player = Bukkit.getPlayer(this.playerUUID);
        if (player == null) return;
        this.data.cachedName(player.getName());
    }

    // ----- DATABASE SYNC -----

    /**
     * Synchronizes the Leveler asynchronously with the database.<br/>
     * The recommended way for updating.
     * @return future result
     */
    public CompletableFuture<SyncResult> syncAsynchronously() {

        if (this.databaseUpdateInProgress.get()) {
            this.manager.getPlugin().getLogger().warning("Sync task of Leveler " + this.playerUUID + " has been called while still in progress (async call). This can be caused by unsupported API usage.");
        }

        CompletableFuture<SyncResult> future = new CompletableFuture<>();
        new BukkitRunnable() {
            @Override
            public void run() {
                SyncResult result = Leveler.this.sync();
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
    public @NotNull net.jandie1505.playerlevels.api.core.level.Leveler.SyncResult sync() {

        // Prevent executing this more than once at the same time
        if (!this.databaseUpdateInProgress.compareAndSet(false, true)) {
            this.manager.getPlugin().getLogger().warning("Sync task of Leveler " + this.playerUUID + " has been called while still in progress. This can be caused by unsupported API usage.");
            return SyncResult.ALREADY_IN_PROGRESS;
        }

        try (Connection connection = this.databaseSource.getConnection()) {
            if (connection == null) {
                System.out.println("Error: No database connection available");
                return SyncResult.ERROR;
            }

            LevelDataPullResult pullResult = this.getDataFromDatabase(connection);

            if (pullResult != null) {
                // Current data is outdated, replace it with database data
                if (!this.updateId.equals(pullResult.updateId())) {
                    this.data.merge(pullResult.data());
                    this.updateId = pullResult.updateId();
                    System.out.println("Local outdated");
                    return SyncResult.LOCAL_OUTDATED;
                }

                // Remote data is outdated, push changes
                if (!this.data.equals(pullResult.data())) {
                    this.updateDataInDatabase(connection);
                    System.out.println("Remote outdated");
                    return SyncResult.REMOTE_OUTDATED_AVAIL;
                }

            } else {

                if (this.getData().isDefault()) {
                    System.out.println("Remote not avail, only default values");
                    return SyncResult.REMOTE_MISSING_DEFAULT;
                }

                this.insertDataIntoDatabase(connection);
                System.out.println("Remote not avail");
                return SyncResult.REMOTE_OUTDATED_MISSING;
            }

            return SyncResult.UP_TO_DATE;
        } catch (SQLException | JSONException e) {
            System.out.println("Exception: ");
            e.printStackTrace();
            return SyncResult.ERROR;
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
                        LevelerData.fromJSON(new JSONObject(pullResultSet.getString("data"))),
                        updateId
                );
            } else {
                return null;
            }

        }
    }

    // ----- INNER CLASSES -----

    private record LevelDataPullResult(@NotNull LevelerData data, @NotNull String updateId) {}

}

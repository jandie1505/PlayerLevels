package net.jandie1505.playerlevels.leveler;

import net.jandie1505.playerlevels.PlayerLevels;
import net.jandie1505.playerlevels.api.LevelManager;
import net.jandie1505.playerlevels.api.LevelPlayer;
import net.jandie1505.playerlevels.database.DatabaseSource;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public class LevelingManager implements LevelManager, Listener {
    @NotNull private final PlayerLevels plugin;
    @NotNull private final DatabaseSource databaseSource;
    @NotNull private final ConcurrentHashMap<UUID, Leveler> cachedLevelers;
    @NotNull private final AtomicBoolean cachingTaskActive;

    public LevelingManager(@NotNull PlayerLevels plugin, @NotNull DatabaseSource databaseSource) {
        this.plugin = plugin;
        this.databaseSource = databaseSource;
        this.cachedLevelers = new ConcurrentHashMap<>();
        this.cachingTaskActive = new AtomicBoolean(false);

        this.createTable();
    }

    // ----- MANAGE -----

    /**
     * Returns a cached leveler.
     * @param playerUUID player uuid
     * @return leveler if cached, else null
     */
    @Override
    public @Nullable Leveler getLeveler(@NotNull UUID playerUUID) {
        Leveler leveler = this.cachedLevelers.get(playerUUID);
        if (leveler == null) return null;
        if (!leveler.isValid()) return null;
        return leveler;
    }

    /**
     * Loads a leveler either from cache or from database.
     * @param playerUUID player uuid
     * @param update update even when cached
     * @return future of leveler
     */
    public @NotNull CompletableFuture<Leveler> loadLeveler(@NotNull UUID playerUUID, boolean update) {

        Leveler leveler = this.cachedLevelers.get(playerUUID);
        if (leveler != null && leveler.isValid()) {

            // Update the leveler when required, if not just return it
            if (update) {
                CompletableFuture<Leveler> future = new CompletableFuture<>();
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        leveler.update();
                        future.complete(leveler);
                    }
                }.runTaskAsynchronously(this.plugin);
                return future;
            } else {
                return CompletableFuture.completedFuture(leveler);
            }
        }

        // Create a new leveler when not cached
        CompletableFuture<Leveler> future = new CompletableFuture<>();
        new BukkitRunnable() {
            @Override
            public void run() {
                Leveler leveler = new Leveler(playerUUID, databaseSource, runnable -> new BukkitRunnable() {
                    @Override
                    public void run() {
                        runnable.run();
                    }
                }.runTaskAsynchronously(plugin));
                leveler.update();
                cachedLevelers.put(playerUUID, leveler);
                future.complete(leveler);
            }
        }.runTaskAsynchronously(this.plugin);

        return future;
    }

    /**
     * Calls {@link LevelingManager#loadLeveler(UUID, boolean)} with update set to false.
     * @param playerUUID player uuid
     * @return future of level manager
     */
    @Override
    public @NotNull CompletableFuture<LevelPlayer> loadLeveler(@NotNull UUID playerUUID) {
        return this.loadLeveler(playerUUID, false).thenApply(leveler -> leveler);
    }

    /**
     * Returns the cache.
     * @return cache
     */
    public Map<UUID, Leveler> getCache() {
        return cachedLevelers;
    }

    /**
     * Drops the cache for all levelers.
     * @param update sync with database before dropping
     */
    public void dropCaches(boolean update) {
        Iterator<Map.Entry<UUID, Leveler>> iterator = this.cachedLevelers.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, Leveler> entry = iterator.next();
            iterator.remove();
            if (update) entry.getValue().updateAsync();
        }
    }

    /**
     * Drops the cache for the specified leveler.
     * @param playerUUID player uuid
     * @param update sync with database before dropping
     */
    public void dropCache(@NotNull UUID playerUUID, boolean update) {
        Leveler leveler = this.cachedLevelers.remove(playerUUID);
        if (leveler == null) return;
        if (update) leveler.updateAsync();
    }

    /**
     * Erases the player from the database and from the cache.
     * @param playerUUID player uuid
     * @return success
     */
    public boolean erasePlayer(@NotNull UUID playerUUID) {

        Connection connection = this.databaseSource.getConnection();
        if (connection == null) {
            this.plugin.getLogger().warning("Failed to erase player " + playerUUID + ": connection is null");
            return false;
        }

        try {
            String sql = "DELETE FROM playerlevels_players WHERE player_uuid = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, playerUUID.toString());

            this.cachedLevelers.remove(playerUUID);
            return true;
        } catch (SQLException e) {
            this.plugin.getLogger().log(Level.WARNING, "Failed to erase player " + playerUUID, e);
            return false;
        }
    }

    // ----- TASKS -----

    public void updateCacheTask() {
        if (this.cachingTaskActive.get()) return;
        this.cachingTaskActive.set(true);

        Iterator<Map.Entry<UUID, Leveler>> iterator = this.cachedLevelers.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, Leveler> entry = iterator.next();

            if (!entry.getValue().isValid()) {
                iterator.remove();
                continue;
            }

            Player player = this.plugin.getServer().getPlayer(entry.getKey());
            if (player == null) {
                iterator.remove();
                entry.getValue().updateAsync();
                continue;
            }

            entry.getValue().updateAsync();
        }

        this.cachingTaskActive.set(false);
    }

    // ----- EVENTS -----

    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        this.loadLeveler(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        Leveler leveler = this.cachedLevelers.remove(event.getPlayer().getUniqueId());
        leveler.updateAsync();
    }

    // ----- UTILITIES -----

    public void createTable() {

        Connection connection = this.databaseSource.getConnection();
        if (connection == null) {
            this.plugin.getLogger().log(Level.WARNING, "Failed to create table: connection is null");
            return;
        }

        try {
            String sql = "CREATE TABLE IF NOT EXISTS playerlevels_players (" +
                    "player_uuid VARCHAR(36) NOT NULL PRIMARY KEY," +
                    "data LONGTEXT NOT NULL," +
                    "update_id VARCHAR(36) NOT NULL," +
                    "last_updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP" +
                    ")";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.executeUpdate();
        } catch (SQLException e) {
            this.plugin.getLogger().log(Level.WARNING, "Failed to create table", e);
        }
    }

}

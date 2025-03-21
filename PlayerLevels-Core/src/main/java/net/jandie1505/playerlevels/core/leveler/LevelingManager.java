package net.jandie1505.playerlevels.core.leveler;

import net.jandie1505.playerlevels.core.PlayerLevels;
import net.jandie1505.playerlevels.core.constants.ConfigKeys;
import net.jandie1505.playerlevels.core.database.DatabaseSource;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class LevelingManager implements net.jandie1505.playerlevels.api.core.level.LevelingManager, Listener {
    @NotNull private final PlayerLevels plugin;
    @NotNull private final DatabaseSource databaseSource;
    @NotNull private final ConcurrentHashMap<UUID, Leveler> cachedLevelers;

    public LevelingManager(@NotNull PlayerLevels plugin, @NotNull DatabaseSource databaseSource) {
        this.plugin = plugin;
        this.databaseSource = databaseSource;
        this.cachedLevelers = new ConcurrentHashMap<>();

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
        if (leveler != null) {

            // Update the leveler when required, if not just return it
            if (update) {
                CompletableFuture<Leveler> future = new CompletableFuture<>();
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        leveler.sync();
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
                Leveler leveler = new Leveler(LevelingManager.this, playerUUID, databaseSource);
                leveler.sync();
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
    public @NotNull CompletableFuture<net.jandie1505.playerlevels.api.core.level.Leveler> loadLeveler(@NotNull UUID playerUUID) {
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
            if (update) entry.getValue().syncAsynchronously();
        }
    }

    /**
     * Drops the cache for the specified leveler.
     * @param playerUUID player uuid
     * @param update sync with database before dropping
     */
    public boolean dropCache(@NotNull UUID playerUUID, boolean update) {
        Leveler leveler = this.cachedLevelers.remove(playerUUID);
        if (leveler == null) return false;
        if (update) leveler.syncAsynchronously();
        return true;
    }

    /**
     * Erases the player from the database and from the cache.
     * @param playerUUID player uuid
     * @return success
     */
    public boolean erasePlayer(@NotNull UUID playerUUID) {
        String sql = "DELETE FROM playerlevels_players WHERE player_uuid = ?";

        try (Connection connection = this.databaseSource.getConnection();
             PreparedStatement statement = connection != null ? connection.prepareStatement(sql) : null
        ) {

            if (connection == null || statement == null) {
                this.plugin.getLogger().warning("Failed to erase player " + playerUUID + ": connection is null");
                return false;
            }

            statement.setString(1, playerUUID.toString());
            statement.executeUpdate(); // Here, the DELETE is executed!

            this.cachedLevelers.remove(playerUUID);
            return true;

        } catch (SQLException e) {
            this.plugin.getLogger().log(Level.WARNING, "Failed to erase player " + playerUUID, e);
            return false;
        }
    }

    // ----- FIND LEVELERS -----

    public @NotNull CompletableFuture<@NotNull List<@NotNull UUID>> findLevelerByName(@NotNull String name) {
        CompletableFuture<List<@NotNull UUID>> future = new CompletableFuture<>();

        new BukkitRunnable() {
            @Override
            public void run() {
                future.complete(findLevelerByNameSync(name));
            }
        }.runTaskAsynchronously(this.plugin);
        return future;
    }

    private @NotNull List<@NotNull UUID> findLevelerByNameSync(@NotNull String playerName) {

        String sql = "SELECT player_uuid FROM playerlevels_players WHERE cached_name = ?";

        try (
                Connection connection = this.databaseSource.getConnection();
                PreparedStatement statement = connection != null ? connection.prepareStatement(sql) : null;
        ) {

            if (connection == null || statement == null) {
                this.plugin.getLogger().log(Level.WARNING, "Failed to find player " + playerName + ": connection is null");
                return List.of();
            }

            statement.setString(1, playerName);

            try (ResultSet rs = statement.executeQuery()) {
                List<@NotNull UUID> list = new ArrayList<>();

                while (rs.next()) {
                    list.add(UUID.fromString(rs.getString("player_uuid")));
                }

                return list;
            } catch (SQLException | IllegalArgumentException e) {
                this.plugin.getLogger().log(Level.WARNING, "Failed to find player " + playerName + ": ", e);
                return List.of();
            }

        } catch (SQLException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to find leveler by player name " + playerName, e);
            return List.of();
        }

    }

    // ----- TASKS -----

    public void updateCacheAsync() {
        new BukkitRunnable() {
            @Override
            public void run() {
                LevelingManager.this.updateCache();
            }
        }.runTaskAsynchronously(this.plugin);
    }

    public void updateCache() {

        Iterator<Map.Entry<UUID, Leveler>> iterator = this.cachedLevelers.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, Leveler> entry = iterator.next();

            Player player = this.plugin.getServer().getPlayer(entry.getKey());
            if (player == null) {
                iterator.remove();
                entry.getValue().sync();
                continue;
            }

            entry.getValue().sync();
        }

    }

    // ----- EVENTS -----

    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        this.loadLeveler(event.getPlayer().getUniqueId(), true).thenAccept(Leveler::process);
    }

    @EventHandler
    public void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        Leveler leveler = this.cachedLevelers.remove(event.getPlayer().getUniqueId());
        leveler.syncAsynchronously();
    }

    // ----- UTILITIES -----

    public void createTable() {

        Connection connection = this.databaseSource.getConnection();
        if (connection == null) {
            this.plugin.getLogger().log(Level.WARNING, "Failed to create table: connection is null");
            return;
        }

        String sql = "CREATE TABLE IF NOT EXISTS playerlevels_players (" +
                "player_uuid VARCHAR(36) NOT NULL PRIMARY KEY," +
                "data LONGTEXT NOT NULL," +
                "update_id VARCHAR(36) NOT NULL," +
                "last_updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                "level INT GENERATED ALWAYS AS (JSON_UNQUOTE(JSON_EXTRACT(data, '$.level'))) PERSISTENT," +
                "cached_name VARCHAR(64) GENERATED ALWAYS AS (JSON_UNQUOTE(JSON_EXTRACT(data, '$.name'))) PERSISTENT," +
                "INDEX idx_level (level)," +
                "INDEX idx_cached_name (cached_name)," +
                "CONSTRAINT chk_json_valid CHECK (JSON_VALID(data))"+
                ")";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            this.plugin.getLogger().log(Level.WARNING, "Failed to create table", e);
        }
    }

    public double getXPForLevel(int level) throws NullPointerException, IllegalArgumentException, ArithmeticException {

        String formula = this.plugin.config().optString(ConfigKeys.XP_FORMULA, null);
        if (formula == null) throw new NullPointerException("XP formula is not set");

        Expression expression = new ExpressionBuilder(formula)
                .variables("level")
                .build()
                .setVariable("level", level);

        return (int) expression.evaluate();
    }

    public double getXPForNextLevel(int currentLevel, int level) throws NullPointerException, IllegalArgumentException, ArithmeticException {
        double currentXP = getXPForLevel(currentLevel);
        double nextXP = this.getXPForLevel(level);
        return nextXP - currentXP;
    }

    // ----- OTHER -----


    public @NotNull PlayerLevels getPlugin() {
        return plugin;
    }
}

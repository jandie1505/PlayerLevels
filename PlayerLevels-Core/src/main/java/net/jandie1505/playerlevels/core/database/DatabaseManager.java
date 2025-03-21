package net.jandie1505.playerlevels.core.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.jandie1505.playerlevels.core.PlayerLevels;
import net.jandie1505.playerlevels.core.constants.ConfigKeys;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

public class DatabaseManager implements DatabaseSource {
    @NotNull private final PlayerLevels plugin;
    @Nullable private HikariDataSource dataSource;

    public DatabaseManager(@NotNull PlayerLevels plugin) {
        this.plugin = plugin;
        this.dataSource = null;
    }

    public boolean setupDatabase() {
        this.shutdownDatabase();

        String host = this.plugin.config().optString(ConfigKeys.DATABASE_HOST, null);
        int port = this.plugin.config().optInt(ConfigKeys.DATABASE_PORT, 0);
        String user = this.plugin.config().optString(ConfigKeys.DATABASE_USER, null);
        String password = this.plugin.config().optString(ConfigKeys.DATABASE_PASSWORD, null);
        String database = this.plugin.config().optString(ConfigKeys.DATABASE_NAME, null);

        if (host == null || user == null || password == null || database == null) {
            this.plugin.getLogger().warning("Failed to setup database: Config options missing");
            return false;
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mariadb://" + host + ":" + port + "/" + database + "?socketTimeout=30000");
        config.setUsername(user);
        config.setPassword(password);
        config.setDriverClassName("org.mariadb.jdbc.Driver");

        try {
            this.dataSource = new HikariDataSource(config);
            this.plugin.getLogger().info("Database connection initialized");
            return true;
        } catch (Exception e) {
            this.plugin.getLogger().log(Level.WARNING, "Failed to initialize database connection", e);
            return false;
        }
    }

    public void shutdownDatabase() {
        if (this.dataSource != null) {
            try {
                this.dataSource.close();
            } catch (Exception e) {
                this.plugin.getLogger().log(Level.WARNING, "Failed to close database connection", e);
            }
        }

        this.dataSource = null;
        this.plugin.getLogger().info("Database closed");
    }

    public @Nullable HikariDataSource getDataSource() {
        return dataSource;
    }

    @Override
    public Connection getConnection() {
        try {
            return this.dataSource != null ? this.dataSource.getConnection() : null;
        } catch (SQLException e) {
            return null;
        }
    }

    public @NotNull PlayerLevels getPlugin() {
        return plugin;
    }

}

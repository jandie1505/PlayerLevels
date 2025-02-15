package net.jandie1505.playerlevels.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager implements DatabaseSource {
    @Nullable private HikariDataSource dataSource;

    public DatabaseManager() {
        this.dataSource = new HikariDataSource();
    }

    public void setupDatabase() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/playerlevels");
        config.setUsername("root");
        config.setPassword("root");
        this.dataSource = new HikariDataSource(config);
    }

    public void shutdownDatabase() {
        if (this.dataSource != null) {
            this.dataSource.close();
        }

        this.dataSource = null;
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
}

package net.jandie1505.playerlevels.leveler;

import net.jandie1505.playerlevels.database.DatabaseSource;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LevelingManager {
    @NotNull private final ConcurrentHashMap<UUID, Leveler> levelers;
    @NotNull private final DatabaseSource databaseSource;

    public LevelingManager(@NotNull DatabaseSource databaseSource) {
        levelers = new ConcurrentHashMap<>();
        this.databaseSource = databaseSource;
    }

    // ----- TASKS -----

    // ----- INIT -----

    private void createTable() throws SQLException {

        Connection db = this.databaseSource.getConnection();
        if (db == null) return;

        String sql = "CREATE TABLE IF NOT EXISTS players (" +
                "uuid CHAR(36) PRIMARY KEY, " +
                "level INT NOT NULL DEFAULT 0, " +
                "xp DECIMAL(10, 2) NOT NULL DEFAULT 0, " +
                "update_id CHAR(36) NOT NULL DEFAULT '', " +
                "last_update TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
                "INDEX idx_level (level), " +
                "INDEX idx_xp (xp)" +
                ");";

        db.createStatement().executeUpdate(sql);

    }

}

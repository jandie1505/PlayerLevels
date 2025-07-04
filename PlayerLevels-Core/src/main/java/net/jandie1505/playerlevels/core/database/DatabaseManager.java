package net.jandie1505.playerlevels.core.database;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;

public interface DatabaseManager {
    boolean setupDatabase();
    void shutdownDatabase();
    Connection getConnection();
    @NotNull Database getDatabase();
}

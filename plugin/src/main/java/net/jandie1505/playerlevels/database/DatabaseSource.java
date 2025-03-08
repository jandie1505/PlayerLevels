package net.jandie1505.playerlevels.database;

import java.sql.Connection;

public interface DatabaseSource {
    Connection getConnection();
}

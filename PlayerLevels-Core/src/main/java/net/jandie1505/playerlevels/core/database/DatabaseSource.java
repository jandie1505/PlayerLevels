package net.jandie1505.playerlevels.core.database;

import java.sql.Connection;

public interface DatabaseSource {
    Connection getConnection();
}

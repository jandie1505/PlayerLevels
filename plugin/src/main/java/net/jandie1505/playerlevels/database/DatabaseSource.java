package net.jandie1505.playerlevels.database;

import javax.sql.DataSource;
import java.sql.Connection;

public interface DatabaseSource {
    Connection getConnection();
}

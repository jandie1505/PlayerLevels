package net.jandie1505.playerlevels.constants;

import net.chaossquad.mclib.storage.DataStorage;

public interface DefaultConfigValues {

    static DataStorage get() {
        DataStorage config = new DataStorage();

        config.set(ConfigKeys.DATABASE_HOST, "127.0.0.1");
        config.set(ConfigKeys.DATABASE_PORT, 3306);
        config.set(ConfigKeys.DATABASE_USER, "playerlevels");
        config.set(ConfigKeys.DATABASE_PASSWORD, "insert_a_secure_password_here");
        config.set(ConfigKeys.DATABASE_NAME, "playerlevels");
        config.set(ConfigKeys.XP_FORMULA, "level * 10");
        config.set(ConfigKeys.SERVER_ID, "");

        return config;
    }

}

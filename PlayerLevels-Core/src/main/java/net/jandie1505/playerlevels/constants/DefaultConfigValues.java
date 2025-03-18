package net.jandie1505.playerlevels.constants;

import net.chaossquad.mclib.storage.DataStorage;
import org.jetbrains.annotations.NotNull;

public interface DefaultConfigValues {

    static @NotNull DataStorage getConfig() {
        DataStorage config = new DataStorage();

        config.set(ConfigKeys.DATABASE_HOST, "127.0.0.1");
        config.set(ConfigKeys.DATABASE_PORT, 3306);
        config.set(ConfigKeys.DATABASE_USER, "playerlevels");
        config.set(ConfigKeys.DATABASE_PASSWORD, "insert_a_secure_password_here");
        config.set(ConfigKeys.DATABASE_NAME, "playerlevels");
        config.set(ConfigKeys.XP_FORMULA, "level * 10");
        config.set(ConfigKeys.SERVER_ID, "");
        config.set(ConfigKeys.TOP_LIST_ENABLED, true);
        config.set(ConfigKeys.TOP_LIST_LENGTH, 10);

        return config;
    }

    static @NotNull DataStorage getRewards() {
        DataStorage config = new DataStorage();

        config.set("test_reward.name", "Test Reward");
        config.set("test_reward.description", "This is a test reward");
        config.set("test_reward.type", "command");
        config.set("test_reward.apply_type", "milestone");
        config.set("test_reward.level", 10);
        config.set("test_reward.sender_type", "console");
        config.set("test_reward.command", "say {reward_name} has been unlocked by {player_name} on level {player_reward_level}");

        return config;
    }

}

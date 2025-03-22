package net.jandie1505.playerlevels.core.constants;

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
        config.set(ConfigKeys.PLAYER_COMMANDS_LOAD_PLAYERS, true);
        config.set(ConfigKeys.PLAYER_COMMANDS_DATABASE_RATE_LIMIT, 10);

        return config;
    }

    static @NotNull DataStorage getMessages() {
        DataStorage messages = new DataStorage();

        messages.set(MessageKeys.GENERAL_NO_PERMISSION, "<red>You don't have permission to use this command");
        messages.set(MessageKeys.GENERAL_UNKNOWN_SUBCOMMAND, "<red>Unknown subcommand");

        messages.set(MessageKeys.INFO_OWN, """
                        <gold>----- YOUR LEVELING PROGRESS -----
                        Your level: <leveler:level>
                        Total XP: <leveler:total_xp_formatted>
                        Progress: <leveler:xp_formatted>/<leveler:xp_to_next_level_formatted> XP (<leveler:xp_remaining_formatted> XP remaining)
                        Next milestones: <level:next_milestones>"""
        );
        messages.set(MessageKeys.INFO_OTHERS, "<green><leveler:name> is on level <leveler:level> and has <leveler:total_xp_formatted> XP");

        messages.set(MessageKeys.TOPLIST_TITLE, "<gold>Level Leaderboard (Page <page>):");
        messages.set(MessageKeys.TOPLIST_ENTRY, "<aqua><entry:place>. <yellow><entry:name><reset> - <gold><entry:level>⭐ (<entry:xp_formatted> XP)");

        messages.set(MessageKeys.MILESTONE_LIST_TITLE, "<gold><bold>Milestones (page <page>/<max_pages>):");
        messages.set(MessageKeys.MILESTONE_LIST_ENTRY, """
                <gold>▪ <reward:name>
                <gray>└ <italic><reward:description></italic>
                <gray>└ <reward_leveler:milestone_unlock_status>"""
        );
        messages.set(MessageKeys.MILESTONE_LIST_EMPTY, "<gold>No rewards");

        messages.set(MessageKeys.PLACEHOLDER_REWARD_UNLOCKED, "<green>\uD83D\uDD13 Unlocked</green>");
        messages.set(MessageKeys.PLACEHOLDER_REWARD_LOCKED, "<red>\uD83D\uDD12 Unlocks at level <reward:level></red>");
        messages.set(MessageKeys.PLACEHOLDER_REWARD_UNKNOWN_UNLOCK_STATUS, "<yellow>Unlock status unknown");

        return messages;
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

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
                <newline><gold>━━━ ✦ Your Leveling Progress ✦ ━━━
                <gray>➥ Level: <green><leveler:level>
                <gray>➥ Total XP: <aqua><leveler:total_xp_formatted>
                <gray>➥ Fortschritt: <yellow><leveler:xp_formatted>/<leveler:xp_to_next_level_formatted> XP
                <gray>   Only <red><leveler:xp_remaining_formatted> XP <gray>to the next level!
                <gray>➥ Next milestones: <gold><level:next_milestones><newline>"""
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

        messages.set(MessageKeys.ANNOUNCEMENT_LEVELUP_SELF, """
                
                <green>⬆ Congratulations! You just reached level <leveler:level>!<interval_rewards_list><newline>""");
        messages.set(MessageKeys.ANNOUNCEMENT_LEVELUP_OTHERS, "<newline><yellow>⬆ <player:display_name> <reset><yellow>just leveled up to <leveler:level>!<newline>");
        messages.set(MessageKeys.ANNOUNCEMENT_LEVELUP_REWARD_LIST_TITLE, "<newline><newline><gray>You received the following rewards:");
        messages.set(MessageKeys.ANNOUNCEMENT_LEVELUP_REWARD_LIST_ENTRY, "<gray>➥ <green><reward:name>");
        messages.set(MessageKeys.ANNOUNCEMENT_MILESTONE_UNLOCKED_SELF, """
                <newline><green><bold>✔ You have unlocked a new milestone!<reset>
                <gray>➥ <bold><reward:name>
                <gray>➥ <italic><reward:description>
                <gray>➥ Received at level <green><level><newline>"""
        );
        messages.set(MessageKeys.ANNOUNCEMENT_MILESTONE_UNLOCKED_OTHERS, """
                <newline><yellow><player:display_name> <reset><yellow>has unlocked a new milestone!
                <gray>➥ <bold><reward:name>
                <gray>➥ Unlocked at level <green><reward:level><newline>"""
        );
        messages.set(MessageKeys.ANNOUNCEMENT_INTERVAL_REWARD_UNLOCKED, "");

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

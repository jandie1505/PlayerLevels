package net.jandie1505.playerlevels.placeholderapi_expansion;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.jandie1505.playerlevels.api.core.level.Leveler;
import net.jandie1505.playerlevels.api.core.level.TopListManager;
import net.jandie1505.playerlevels.core.PlayerLevelsAPIProvider;
import net.jandie1505.playerlevels.core.messages.Formatters;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class PlayerLevelsPAPIExpansion extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "playerlevels";
    }

    @Override
    public @NotNull String getAuthor() {
        return "jandie1505";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {

        try {
            Class.forName("net.jandie1505.playerlevels.api.core.PlayerLevelsAPI");
        } catch (ClassNotFoundException e) {
            return "plugin_not_found";
        }

        if (params.startsWith("toplist_")) {
            params = params.substring("toplist_".length());
            String[] split = params.split("_", 2);
            if (split.length != 2) return "toplist_invalid_format";

            int position;

            try {
                position = Integer.parseInt(split[0]);
            } catch (NumberFormatException e) {
                return "toplist_invalid_position";
            }

            TopListManager tlm = PlayerLevelsAPIProvider.getApi().getTopListManager();
            List<TopListManager.TopListEntry> tl = tlm.getTopList();
            TopListManager.TopListEntry entry;
            if (position >= tl.size()) {
                return "---";
            }
            entry = tl.get(position);

            String type = split[1];
            return switch (type) {
                case "level" -> String.valueOf(entry.level());
                case "xp" -> String.valueOf(entry.xp());
                case "xp_formatted" -> Formatters.formatXP(entry.xp());
                case "total_xp" -> String.valueOf(getXPForLevel(entry.level()) + entry.xp());
                case "total_xp_formatted" -> Formatters.formatXP(getXPForLevel(entry.level()) + entry.xp());
                case "uuid" -> entry.playerUUID().toString();
                case "name" -> entry.name() != null ? entry.name() : "???";
                case "level_next" -> String.valueOf(entry.level() + 1);
                case "xp_current_level" -> String.valueOf(getXPForLevel(entry.level()));
                case "xp_current_level_formatted" -> Formatters.formatXP(getXPForLevel(entry.level()));
                case "xp_next_level" -> String.valueOf(getXPForLevel(entry.level() + 1));
                case "xp_next_level_formatted" -> Formatters.formatXP(getXPForLevel(entry.level() + 1));
                case "xp_to_next_level" -> String.valueOf(getXPForNextLevel(entry.level(), entry.level()) + 1);
                case "xp_to_next_level_formatted" -> Formatters.formatXP(getXPForNextLevel(entry.level(), entry.level() + 1));
                case "xp_remaining" -> String.valueOf(getXPForNextLevel(entry.level(), entry.level() + 1) - entry.xp());
                case "xp_remaining_formatted" -> Formatters.formatXP(getXPForNextLevel(entry.level(), entry.level() + 1) - entry.xp());
                default -> "toplist_invalid_type";
            };
        }

        @Nullable Leveler leveler = player != null ? PlayerLevelsAPIProvider.getApi().getLevelManager().getLeveler(player.getUniqueId()) : null;

        if (leveler != null) {
            return switch (params.toLowerCase()) {
                case "level" -> String.valueOf(leveler.getData().level());
                case "xp" -> String.valueOf(leveler.getData().xp());
                case "xp_formatted" -> Formatters.formatXP(leveler.getData().xp());
                case "total_xp" -> String.valueOf(getXPForLevel(leveler.getData().level()) + leveler.getData().xp());
                case "total_xp_formatted" -> Formatters.formatXP(getXPForLevel(leveler.getData().level()) + leveler.getData().xp());
                case "cached_name" -> Objects.requireNonNullElse(leveler.getData().cachedName(), "?");
                case "uuid" -> leveler.getPlayerUUID().toString();
                case "level_next" -> String.valueOf(leveler.getData().level() + 1);
                case "xp_current_level" -> String.valueOf(getXPForLevel(leveler.getData().level()));
                case "xp_current_level_formatted" -> Formatters.formatXP(getXPForLevel(leveler.getData().level()));
                case "xp_next_level" -> String.valueOf(getXPForLevel(leveler.getData().level() + 1));
                case "xp_next_level_formatted" -> Formatters.formatXP(getXPForLevel(leveler.getData().level() + 1));
                case "xp_to_next_level" -> String.valueOf(getXPForNextLevel(leveler.getData().level(), leveler.getData().level()) + 1);
                case "xp_to_next_level_formatted" -> Formatters.formatXP(getXPForNextLevel(leveler.getData().level(), leveler.getData().level() + 1));
                case "xp_remaining" -> String.valueOf(getXPForNextLevel(leveler.getData().level(), leveler.getData().level() + 1) - leveler.getData().xp());
                case "xp_remaining_formatted" -> Formatters.formatXP(getXPForNextLevel(leveler.getData().level(), leveler.getData().level() + 1) - leveler.getData().xp());
                case "xp_progress_bar" -> {
                    double xp = leveler.getData().xp();
                    double requiredXP = getXPForNextLevel(leveler.getData().level(), leveler.getData().level() + 1);
                    if (requiredXP == 0) requiredXP = 1;
                    double percentage = xp / requiredXP;
                    yield createProgressBar(percentage);
                }
                default -> "invalid_placeholder";
            };
        } else {
            return "invalid_placeholder";
        }
    }

    private static double getXPForLevel(int level) {
        try {
            return PlayerLevelsAPIProvider.getApi().getLevelManager().getXPForLevel(level);
        } catch (Exception e) {
            return 0;
        }
    }

    private static double getXPForNextLevel(int currentLevel, int level) {
        try {
            return PlayerLevelsAPIProvider.getApi().getLevelManager().getXPForNextLevel(currentLevel, level);
        } catch (Exception e) {
            return 0;
        }
    }

    private static String createProgressBar(double progress) {
        int filledCount = (int) Math.round(10.0 * progress);
        String bar = "";

        for (int i = 0; i < 10; i++) {
            bar = bar + (i < filledCount ? "§a■" : "§7■");
        }

        return bar;
    }

}

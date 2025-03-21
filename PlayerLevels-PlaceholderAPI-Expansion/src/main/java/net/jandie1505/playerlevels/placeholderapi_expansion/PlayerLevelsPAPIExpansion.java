package net.jandie1505.playerlevels.placeholderapi_expansion;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.jandie1505.playerlevels.PlayerLevelsAPIProvider;
import net.jandie1505.playerlevels.api.level.Leveler;
import net.jandie1505.playerlevels.api.level.TopListManager;
import net.jandie1505.playerlevels.messages.Formatters;
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
            Class.forName("net.jandie1505.playerlevels.api.PlayerLevelsAPI");
        } catch (ClassNotFoundException e) {
            return "plugin_not_found";
        }

        if (params.startsWith("toplist_")) {
            params = params.substring("toplist_".length());
            String[] split = params.split("_");
            if (split.length != 2) return "toplist_invalid_format";

            String type = split[0];
            int position;

            try {
                position = Integer.parseInt(split[1]);
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

            return switch (type) {
                case "level" -> String.valueOf(entry.level());
                case "xp" -> String.valueOf(entry.xp());
                case "xp_formatted" -> Formatters.formatXP(entry.xp());
                case "total_xp" -> String.valueOf(getXPForLevel(entry.level()) + entry.xp());
                case "total_xp_formatted" -> Formatters.formatXP(getXPForLevel(entry.level()) + entry.xp());
                case "uuid" -> entry.playerUUID().toString();
                case "name" -> entry.name();
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

}

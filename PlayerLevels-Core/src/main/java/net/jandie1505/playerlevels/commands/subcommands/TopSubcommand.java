package net.jandie1505.playerlevels.commands.subcommands;

import com.destroystokyo.paper.profile.PlayerProfile;
import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.playerlevels.PlayerLevels;
import net.jandie1505.playerlevels.api.level.TopListManager;
import net.jandie1505.playerlevels.constants.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TopSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final PlayerLevels plugin;

    public TopSubcommand(@NotNull PlayerLevels plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (!Permissions.hasPermission(sender, Permissions.TOPLIST)) {
            return true;
        }

        List<TopListManager.TopListEntry> toplist = TopSubcommand.this.plugin.getTopListManager().getTopList();
        if (toplist.isEmpty()) {
            sender.sendRichMessage("<red>The toplist is currently not available");
            return true;
        }

        String message = "<gold>Level Leaderboard:";

        for (int i = 0; i < toplist.size(); i++) {
            TopListManager.TopListEntry entry = toplist.get(i);

            double xp;

            try {
                xp = this.plugin.getLevelManager().getXPForLevel(entry.level()) + entry.xp();
            } catch (Exception e) {
                xp = -1;
            }

            message = message + "\n<aqua>" + (i + 1) + ". <yellow>" + entry.name() + "<reset><gray> - <gold>" + entry.level() + "⭐ (" + (xp >= 0 ? TopSubcommand.this.formatXP(xp) : "?") + " XP)<reset>";

        }

        sender.sendRichMessage(message);

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return List.of();
    }

    private String formatXP(double xp) {
        // Wenn XP kleiner als 1000 ist, gib einfach den Wert aus
        if (xp < 1000) {
            return String.format("%.0f", xp);
        }

        // Falls die Zahl Tausende überschreitet, teile sie durch 1000 und füge "K" hinzu
        if (xp < 1000000) {
            return String.format("%.1fK", xp / 1000);
        }

        // Wenn die Zahl Millionen überschreitet, teile sie durch 1000000 und füge "M" hinzu
        if (xp < 1000000000) {
            return String.format("%.1fM", xp / 1000000);
        }

        // Falls Milliarden überschritten werden, teile sie durch 1000000000 und füge "B" hinzu
        if (xp < 1000000000000L) {
            return String.format("%.1fB", xp / 1000000000);
        }

        // Optional: Falls noch größere Zahlen existieren, könnte man auch "T" für Billionen hinzufügen
        return String.format("%.1fT", xp / 1000000000000L);
    }

}

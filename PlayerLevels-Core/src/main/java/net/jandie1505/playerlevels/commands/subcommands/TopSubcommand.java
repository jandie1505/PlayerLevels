package net.jandie1505.playerlevels.commands.subcommands;

import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.playerlevels.PlayerLevels;
import net.jandie1505.playerlevels.api.level.TopListManager;
import net.jandie1505.playerlevels.constants.MessageKeys;
import net.jandie1505.playerlevels.constants.Permissions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TopSubcommand implements TabCompletingCommandExecutor {
    private static final int pageSize = 10;
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

        int page = 0;
        if (args.length > 0) {
            try {
                page = Integer.parseInt(args[0]) - 1;
            } catch (IllegalArgumentException e) {
                // don't care, show first page
            }
        }

        if (page < 0) page = 0;

        int start = page * pageSize;
        int end = page * pageSize + pageSize;

        if (start >= toplist.size()) {
            sender.sendRichMessage("<red>This page does not exist");
            return true;
        }

        Component message = Component.empty()
                .append(MiniMessage.miniMessage().deserialize(
                        this.plugin.messages().optString(MessageKeys.TOPLIST_TITLE, ""),
                        TagResolver.resolver("page", Tag.inserting(Component.text(page + 1)))
                ));

        for (int i = start; i < toplist.size() && i < end; i++) {
            TopListManager.TopListEntry entry = toplist.get(i);

            double xp;

            try {
                xp = this.plugin.getLevelManager().getXPForLevel(entry.level()) + entry.xp();
            } catch (Exception e) {
                xp = -1;
            }

            message = message
                    .appendNewline()
                    .append(MiniMessage.miniMessage().deserialize(
                            this.plugin.messages().optString(MessageKeys.TOPLIST_ENTRY, ""),
                            this.tagResolver(entry)
                    ));
        }

        sender.sendMessage(message);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (args.length == 1) {
            int maxPages = (int) Math.ceil((double) this.plugin.getTopListManager().getTopList().size() / (pageSize == 0 ? 1 : pageSize));
            List<String> complete = new ArrayList<>();
            for (int i = 0; i < maxPages; i++) {
                complete.add(String.valueOf(i + 1));
            }
            return complete;
        }

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

    private TagResolver tagResolver(@NotNull TopListManager.TopListEntry entry) {
        return TagResolver.resolver("entry", (argumentQueue, context) -> {
            final String arg = argumentQueue.popOr("entry" + " tag requires an argument").value();

            Component placeholder;

            try {

                switch (arg) {
                    case "level" -> placeholder = Component.text(entry.level());
                    case "xp" -> placeholder = Component.text(entry.xp());
                    case "xp_formatted" -> placeholder = Component.text(this.formatXP(entry.xp()));
                    case "total_xp" -> placeholder = Component.text(this.plugin.getLevelManager().getXPForLevel(entry.level()) + entry.xp());
                    case "total_xp_formatted" -> placeholder = Component.text(this.formatXP(this.plugin.getLevelManager().getXPForLevel(entry.level()) + entry.xp()));
                    case "name" -> placeholder = Component.text(entry.name() != null ? entry.name() : "???");
                    case "uuid" -> placeholder = Component.text(entry.playerUUID().toString());
                    default -> placeholder = Component.empty();
                }

            } catch (Exception e) {
                placeholder = Component.empty();
            }

            return Tag.inserting(placeholder);
        });
    }

}

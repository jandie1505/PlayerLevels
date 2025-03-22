package net.jandie1505.playerlevels.core.commands.subcommands.debug;

import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.playerlevels.core.PlayerLevels;
import net.jandie1505.playerlevels.core.commands.subcommands.utils.OptionParser;
import net.jandie1505.playerlevels.core.constants.MessageKeys;
import net.jandie1505.playerlevels.core.constants.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class DebugReloadSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final PlayerLevels plugin;

    public DebugReloadSubcommand(@NotNull PlayerLevels plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] a) {

        if (!Permissions.hasPermission(sender)) {
            sender.sendRichMessage(this.plugin.messages().optString(MessageKeys.GENERAL_NO_PERMISSION, ""));
            return true;
        }

        OptionParser.Result args = OptionParser.parse(a);

        if (args.args().length < 1) {
            sender.sendRichMessage("<red>Usage: /levels debug reload (config|messages> [--clear=(true|false)|--merge-defaults=(true|false)]");
        }

        boolean clearConfig = Boolean.parseBoolean(args.options().getOrDefault("clear", "true"));
        boolean mergeDefaults = Boolean.parseBoolean(args.options().getOrDefault("merge-defaults", "true"));

        switch (args.args()[0].toLowerCase()) {
            case "config" -> {
                boolean result = this.plugin.reloadConfig(clearConfig, mergeDefaults);
                sender.sendRichMessage(result ? "<green>Config reloaded" : "<red>Failed to reload config");
            }
            case "messages" -> {
                boolean result = this.plugin.reloadMessages(clearConfig, mergeDefaults);
                sender.sendRichMessage(result ? "<green>Messages reloaded" : "<red>Failed to reload messages");
            }
            default -> sender.sendRichMessage("<red>Configuration not found");
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return switch (args.length) {
            case 1 -> List.of("config", "messages");
            case 2, 3 -> OptionParser.complete(
                    sender,
                    OptionParser.parse(args),
                    Set.of(),
                    Map.of("clear", (sender1, args1) -> List.of("true", "false"), "merge-defaults", (sender2, args2) -> List.of("true", "false"))
            );
            default -> List.of();
        };
    }

    public @NotNull PlayerLevels getPlugin() {
        return plugin;
    }
}

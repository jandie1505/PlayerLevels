package net.jandie1505.playerlevels.core.commands.subcommands.debug;

import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.chaossquad.mclib.commands.DataStorageEditorCommand;
import net.jandie1505.playerlevels.core.PlayerLevels;
import net.jandie1505.playerlevels.core.constants.ConfigKeys;
import net.jandie1505.playerlevels.core.constants.MessageKeys;
import net.jandie1505.playerlevels.core.constants.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class DebugConfigSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final PlayerLevels plugin;

    public DebugConfigSubcommand(@NotNull PlayerLevels plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (!Permissions.hasPermission(sender)) {
            sender.sendRichMessage(this.plugin.messages().optString(MessageKeys.GENERAL_NO_PERMISSION, ""));
            return true;
        }

        return DataStorageEditorCommand.onCommand(this.plugin.config(), sender, label, args, Set.of(ConfigKeys.DATABASE_PASSWORD));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (!Permissions.hasPermission(sender, Permissions.DEBUG)) {
            sender.sendRichMessage(this.plugin.messages().optString(MessageKeys.GENERAL_NO_PERMISSION, ""));
            return List.of();
        }

        return DataStorageEditorCommand.onTabComplete(this.plugin.config(), sender, args);
    }

    public @NotNull PlayerLevels getPlugin() {
        return plugin;
    }

}

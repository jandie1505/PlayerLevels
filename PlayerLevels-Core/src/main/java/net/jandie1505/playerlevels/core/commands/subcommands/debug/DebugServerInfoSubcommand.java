package net.jandie1505.playerlevels.core.commands.subcommands.debug;

import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.playerlevels.core.PlayerLevels;
import net.jandie1505.playerlevels.core.constants.MessageKeys;
import net.jandie1505.playerlevels.core.constants.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DebugServerInfoSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final PlayerLevels plugin;

    public DebugServerInfoSubcommand(@NotNull PlayerLevels plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (!Permissions.hasPermission(sender)) {
            sender.sendRichMessage(this.plugin.messages().optString(MessageKeys.GENERAL_NO_PERMISSION, ""));
            return true;
        }

        sender.sendRichMessage("<gray>Server ID: " + this.plugin.getServerId());

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return List.of();
    }

    public @NotNull PlayerLevels getPlugin() {
        return plugin;
    }

}

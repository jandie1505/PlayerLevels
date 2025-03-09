package net.jandie1505.playerlevels.commands.subcommands.debug;

import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.playerlevels.PlayerLevels;
import net.jandie1505.playerlevels.constants.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ServerInfoSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final PlayerLevels plugin;

    public ServerInfoSubcommand(@NotNull PlayerLevels plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (!Permissions.hasPermission(sender, Permissions.DEBUG)) {
            sender.sendRichMessage("<red>No permission");
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

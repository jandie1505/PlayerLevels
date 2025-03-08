package net.jandie1505.playerlevels.commands.subcommands;

import net.chaossquad.mclib.command.SubcommandCommand;
import net.chaossquad.mclib.command.SubcommandEntry;
import net.jandie1505.playerlevels.PlayerLevels;
import net.jandie1505.playerlevels.commands.subcommands.debug.CacheSubCommand;
import net.jandie1505.playerlevels.commands.subcommands.debug.DatabaseSubcommand;
import net.jandie1505.playerlevels.commands.subcommands.debug.ServerInfoSubcommand;
import net.jandie1505.playerlevels.constants.Permissions;
import org.jetbrains.annotations.NotNull;

public class DebugSubcommand extends SubcommandCommand {
    @NotNull private final PlayerLevels plugin;

    public DebugSubcommand(@NotNull PlayerLevels plugin) {
        super(plugin, sender -> Permissions.hasPermission(sender, Permissions.DEBUG));
        this.plugin = plugin;

        this.addSubcommand("cache", SubcommandEntry.of(new CacheSubCommand(this.plugin)));
        this.addSubcommand("database", SubcommandEntry.of(new DatabaseSubcommand(this.plugin)));
        this.addSubcommand("server-info", SubcommandEntry.of(new ServerInfoSubcommand(this.plugin)));
    }

}

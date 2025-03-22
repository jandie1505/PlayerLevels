package net.jandie1505.playerlevels.core.commands.subcommands;

import net.chaossquad.mclib.command.SubcommandCommand;
import net.chaossquad.mclib.command.SubcommandEntry;
import net.jandie1505.playerlevels.core.PlayerLevels;
import net.jandie1505.playerlevels.core.commands.subcommands.debug.*;
import net.jandie1505.playerlevels.core.constants.Permissions;
import org.jetbrains.annotations.NotNull;

public class DebugSubcommand extends SubcommandCommand {
    @NotNull private final PlayerLevels plugin;

    public DebugSubcommand(@NotNull PlayerLevels plugin) {
        super(plugin, Permissions::hasPermission);
        this.plugin = plugin;

        this.addSubcommand("cache", SubcommandEntry.of(new DebugCacheSubcommand(this.plugin)));
        this.addSubcommand("config", SubcommandEntry.of(new DebugConfigSubcommand(this.plugin)));
        this.addSubcommand("messages", SubcommandEntry.of(new DebugMessagesSubcommand(this.plugin)));
        this.addSubcommand("database", SubcommandEntry.of(new DebugDatabaseSubcommand(this.plugin)));
        this.addSubcommand("server-info", SubcommandEntry.of(new DebugServerInfoSubcommand(this.plugin)));
        this.addSubcommand("reload", SubcommandEntry.of(new DebugReloadSubcommand(this.plugin)));
    }

}

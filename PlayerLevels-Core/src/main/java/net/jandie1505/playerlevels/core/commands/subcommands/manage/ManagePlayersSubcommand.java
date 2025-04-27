package net.jandie1505.playerlevels.core.commands.subcommands.manage;

import net.chaossquad.mclib.command.SubcommandCommand;
import net.chaossquad.mclib.command.SubcommandEntry;
import net.jandie1505.playerlevels.core.PlayerLevels;
import net.jandie1505.playerlevels.core.commands.subcommands.manage.manage_players.*;
import net.jandie1505.playerlevels.core.constants.Permissions;
import org.jetbrains.annotations.NotNull;

public class ManagePlayersSubcommand extends SubcommandCommand {
    @NotNull private final PlayerLevels plugin;

    public ManagePlayersSubcommand(@NotNull PlayerLevels plugin) {
        super(plugin, sender -> Permissions.hasPermission(sender, Permissions.MANAGE_PLAYERS));
        this.plugin = plugin;

        this.addSubcommand("info", SubcommandEntry.of(new ManagePlayersInfoSubcommand(plugin)));
        this.addSubcommand("level", SubcommandEntry.of(new ManagePlayersLevelSubcommand(this.plugin)));
        this.addSubcommand("xp", SubcommandEntry.of(new ManagePlayersXPSubcommand(this.plugin)));
        this.addSubcommand("rewards", SubcommandEntry.of(new ManagePlayersRewardsSubcommand(this.plugin)));
        this.addSubcommand("cached-name", SubcommandEntry.of(new ManagePlayersCachedNameSubcommand(this.plugin)));
        this.addSubcommand("sync", SubcommandEntry.of(new ManagePlayersSyncSubcommand(this.plugin)));
        this.addSubcommand("process", SubcommandEntry.of(new ManagePlayersProcessSubcommand(this.plugin)));
        this.addSubcommand("erase", SubcommandEntry.of(new ManagePlayersEraseSubcommand(this.plugin)));
    }

}

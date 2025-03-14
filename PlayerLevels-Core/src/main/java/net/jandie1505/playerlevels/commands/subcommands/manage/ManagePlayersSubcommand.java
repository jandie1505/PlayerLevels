package net.jandie1505.playerlevels.commands.subcommands.manage;

import net.chaossquad.mclib.command.SubcommandCommand;
import net.chaossquad.mclib.command.SubcommandEntry;
import net.jandie1505.playerlevels.PlayerLevels;
import net.jandie1505.playerlevels.commands.subcommands.manage.manage_players.EraseSubcommand;
import net.jandie1505.playerlevels.commands.subcommands.manage.manage_players.InfoSubcommand;
import net.jandie1505.playerlevels.commands.subcommands.manage.manage_players.LevelSubcommand;
import net.jandie1505.playerlevels.commands.subcommands.manage.manage_players.RewardsManageSubcommand;
import net.jandie1505.playerlevels.commands.subcommands.manage.manage_players.XPSubcommand;
import net.jandie1505.playerlevels.constants.Permissions;
import org.jetbrains.annotations.NotNull;

public class ManagePlayersSubcommand extends SubcommandCommand {
    @NotNull private final PlayerLevels plugin;

    public ManagePlayersSubcommand(@NotNull PlayerLevels plugin) {
        super(plugin, sender -> Permissions.hasPermission(sender, Permissions.MANAGE_PLAYERS));
        this.plugin = plugin;

        this.addSubcommand("info", SubcommandEntry.of(new InfoSubcommand(plugin)));
        this.addSubcommand("level", SubcommandEntry.of(new LevelSubcommand(this.plugin)));
        this.addSubcommand("xp", SubcommandEntry.of(new XPSubcommand(this.plugin)));
        this.addSubcommand("rewards", SubcommandEntry.of(new RewardsManageSubcommand(this.plugin)));
        this.addSubcommand("erase", SubcommandEntry.of(new EraseSubcommand(this.plugin)));
    }

}

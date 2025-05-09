package net.jandie1505.playerlevels.core.commands.subcommands;

import net.chaossquad.mclib.command.SubcommandCommand;
import net.chaossquad.mclib.command.SubcommandEntry;
import net.jandie1505.playerlevels.core.PlayerLevels;
import net.jandie1505.playerlevels.core.commands.subcommands.manage.ManagePlayersSubcommand;
import net.jandie1505.playerlevels.core.commands.subcommands.manage.ManageRewardsSubcommand;
import net.jandie1505.playerlevels.core.constants.Permissions;
import org.jetbrains.annotations.NotNull;

public class ManageSubcommand extends SubcommandCommand {
    @NotNull private final PlayerLevels plugin;

    public ManageSubcommand(@NotNull PlayerLevels plugin) {
        super(plugin);
        this.plugin = plugin;

        this.addSubcommand("players", SubcommandEntry.of(new ManagePlayersSubcommand(plugin), sender -> Permissions.hasPermission(sender, Permissions.MANAGE_PLAYERS)));
        this.addSubcommand("rewards", SubcommandEntry.of(new ManageRewardsSubcommand(plugin), sender -> Permissions.hasPermission(sender, Permissions.MANAGE_REWARDS)));
    }

}

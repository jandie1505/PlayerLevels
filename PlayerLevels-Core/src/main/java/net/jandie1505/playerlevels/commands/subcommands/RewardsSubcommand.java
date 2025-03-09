package net.jandie1505.playerlevels.commands.subcommands;

import net.chaossquad.mclib.command.SubcommandCommand;
import net.chaossquad.mclib.command.SubcommandEntry;
import net.jandie1505.playerlevels.PlayerLevels;
import net.jandie1505.playerlevels.commands.subcommands.rewards.RewardsInfoSubcommand;
import net.jandie1505.playerlevels.commands.subcommands.rewards.RewardsListSubcommand;
import net.jandie1505.playerlevels.constants.Permissions;
import org.jetbrains.annotations.NotNull;

public class RewardsSubcommand extends SubcommandCommand {
    @NotNull private final PlayerLevels plugin;

    public RewardsSubcommand(@NotNull PlayerLevels plugin) {
        super(plugin, sender -> Permissions.hasPermission(sender, Permissions.MANAGE_REWARDS));
        this.plugin = plugin;

        this.addSubcommand("info", SubcommandEntry.of(new RewardsInfoSubcommand(this.plugin)));
        this.addSubcommand("list", SubcommandEntry.of(new RewardsListSubcommand(this.plugin)));
    }

}

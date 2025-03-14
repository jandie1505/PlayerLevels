package net.jandie1505.playerlevels.commands.subcommands.manage;

import net.chaossquad.mclib.command.SubcommandCommand;
import net.chaossquad.mclib.command.SubcommandEntry;
import net.jandie1505.playerlevels.PlayerLevels;
import net.jandie1505.playerlevels.commands.subcommands.manage.manage_rewards.RewardsInfoSubcommand;
import net.jandie1505.playerlevels.commands.subcommands.manage.manage_rewards.RewardsListSubcommand;
import net.jandie1505.playerlevels.constants.Permissions;
import org.jetbrains.annotations.NotNull;

public class ManageRewardsSubcommand extends SubcommandCommand {
    @NotNull private final PlayerLevels plugin;

    public ManageRewardsSubcommand(@NotNull PlayerLevels plugin) {
        super(plugin, sender -> Permissions.hasPermission(sender, Permissions.MANAGE_REWARDS));
        this.plugin = plugin;

        this.addSubcommand("info", SubcommandEntry.of(new RewardsInfoSubcommand(this.plugin)));
        this.addSubcommand("list", SubcommandEntry.of(new RewardsListSubcommand(this.plugin)));
    }

}

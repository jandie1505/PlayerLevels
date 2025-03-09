package net.jandie1505.playerlevels.commands.subcommands;

import net.chaossquad.mclib.command.SubcommandCommand;
import net.jandie1505.playerlevels.constants.Permissions;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class RewardsSubcommand extends SubcommandCommand {

    public RewardsSubcommand(@NotNull Plugin plugin) {
        super(plugin, sender -> Permissions.hasPermission(sender, Permissions.MANAGE_REWARDS));


    }

}

package net.jandie1505.playerlevels.core.constants;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public interface Permissions {
    String ADMIN = "playerlevels.admin";
    String DEBUG = "playerlevels.debug";
    String MANAGE_PLAYERS = "playerlevels.manage_players";
    String MANAGE_REWARDS = "playerlevels.manage_rewards";
    String TOPLIST = "playerlevels.toplist";
    String COMMAND_REWARDS = "playerlevels.command.rewards";
    String COMMAND_INFO_VIEW_OTHERS = "playerlevels.command.info.view_other";

    /**
     * Returns if the specified sender has at least one of the specified permissions.<br/>
     * This means, if the sender has one of the specified permissions (or more), this will return true.
     * @param sender sender
     * @param permission permissions
     * @return true = has permission
     */
    static boolean hasPermission(@NotNull CommandSender sender, @NotNull String... permission) {
        if (sender == Bukkit.getConsoleSender() || sender.hasPermission(ADMIN)) return true;

        for (String s : permission) {
            if (sender.hasPermission(s)) return true;
        }

        return false;
    }

}

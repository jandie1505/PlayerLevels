package net.jandie1505.playerlevels.constants;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public interface Permissions {
    String ADMIN = "playerlevels.admin";
    String DEBUG = "playerlevels.debug";
    String MANAGE_PLAYERS = "playerlevels.manage_players";
    String USE = "playerlevels.use";

    static boolean hasPermission(@NotNull CommandSender sender, @NotNull String... permission) {
        if (sender == Bukkit.getConsoleSender() || sender.hasPermission(ADMIN)) return true;

        for (String s : permission) {
            if (sender.hasPermission(s)) return true;
        }

        return false;
    }

}

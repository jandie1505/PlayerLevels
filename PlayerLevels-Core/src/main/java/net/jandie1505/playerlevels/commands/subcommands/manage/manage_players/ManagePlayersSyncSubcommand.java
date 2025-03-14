package net.jandie1505.playerlevels.commands.subcommands.manage.manage_players;

import net.chaossquad.mclib.PlayerUtils;
import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.playerlevels.PlayerLevels;
import net.jandie1505.playerlevels.constants.Permissions;
import net.jandie1505.playerlevels.leveler.Leveler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class ManagePlayersSyncSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final PlayerLevels plugin;

    public ManagePlayersSyncSubcommand(@NotNull PlayerLevels plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String @NotNull [] args) {

        if (!Permissions.hasPermission(sender, Permissions.MANAGE_PLAYERS)) {
            sender.sendRichMessage("<red>No permission");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(Component.text("Usage: /playerlevels manage players sync <player>", NamedTextColor.RED));
            return true;
        }

        UUID playerUUID = PlayerUtils.getPlayerUUIDFromString(args[0]);
        if (playerUUID == null) {
            sender.sendMessage(Component.text("Player not found", NamedTextColor.RED));
            return true;
        }

        Leveler leveler = this.plugin.getLevelManager().getLeveler(playerUUID);

        if (leveler != null) {
            leveler.updateAsync().thenAccept(result -> new BukkitRunnable() {
                @Override
                public void run() {

                    switch (result) {
                        case ERROR -> sender.sendRichMessage("<red>Sync: Failed");
                        case ALREADY_IN_PROGRESS -> sender.sendRichMessage("<red>Sync: Already in progress");
                        case UP_TO_DATE -> sender.sendRichMessage("<green>Sync: Already up to date");
                        case LOCAL_OUTDATED -> sender.sendRichMessage("<green>Sync: Pulled changes from database");
                        case REMOTE_OUTDATED_AVAIL -> sender.sendRichMessage("<green>Sync: Pushed changes to database");
                        case REMOTE_OUTDATED_MISSING -> sender.sendRichMessage("<green>Sync: Pushed new player to database");
                        default -> sender.sendRichMessage("<red>Sync: Unknown result");
                    }

                }
            }.runTask(this.plugin));
        } else {

            this.plugin.getLevelManager().loadLeveler(playerUUID, true).thenAccept(l -> new BukkitRunnable() {
                @Override
                public void run() {
                    sender.sendRichMessage("<green>Sync: Pulled uncached player from database");
                }
            }.runTask(this.plugin));

        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String @NotNull [] args) {

        if (args.length == 1) {
            return List.copyOf(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
        }

        return List.of();
    }

    public @NotNull PlayerLevels getPlugin() {
        return plugin;
    }

}

package net.jandie1505.playerlevels.commands.subcommands.manage.manage_players;

import net.chaossquad.mclib.PlayerUtils;
import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.playerlevels.PlayerLevels;
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

public class EraseSubcommand implements TabCompletingCommandExecutor {

    @NotNull
    private final PlayerLevels plugin;

    public EraseSubcommand(@NotNull PlayerLevels plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (args.length < 1) {
            sender.sendMessage(Component.text("Usage: /playerlevels manage erase <player>", NamedTextColor.RED));
            return true;
        }

        UUID playerUUID = PlayerUtils.getPlayerUUIDFromString(args[0]);
        if (playerUUID == null) {
            sender.sendRichMessage("<red>Player not found");
            return true;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                boolean result = EraseSubcommand.this.plugin.getLevelManager().erasePlayer(playerUUID);

                new BukkitRunnable() {
                    @Override
                    public void run() {

                        if (result) {
                            sender.sendRichMessage("<green>Player erased successfully from database");
                        } else {
                            sender.sendRichMessage("<red>Failed to erase player from database");
                        }

                    }
                }.runTask(EraseSubcommand.this.plugin);
            }
        }.runTaskAsynchronously(this.plugin);

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 1) return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        return List.of();
    }

}

package net.jandie1505.playerlevels.commands;

import net.chaossquad.mclib.PlayerUtils;
import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.playerlevels.PlayerLevels;
import net.jandie1505.playerlevels.api.LevelPlayer;
import net.jandie1505.playerlevels.leveler.Leveler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class InfoSubCommand implements TabCompletingCommandExecutor {
    @NotNull private final PlayerLevels plugin;

    public InfoSubCommand(@NotNull PlayerLevels plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (args.length < 1) {
            sender.sendMessage(Component.text("You need to specify a player", NamedTextColor.RED));
            return true;
        }

        UUID playerUUID = PlayerUtils.getPlayerUUIDFromString(args[0]);

        CompletableFuture<LevelPlayer> leveler = this.plugin.getLevelManager().loadLeveler(playerUUID);
        leveler.thenAccept(levelPlayer -> new BukkitRunnable() {
            @Override
            public void run() {

                if (levelPlayer == null) {
                    sender.sendMessage(Component.text("Player not found", NamedTextColor.RED));
                    return;
                }

                sender.sendMessage(Component.empty()
                        .append(Component.text("UUID: " + levelPlayer.getPlayerUUID(), NamedTextColor.GRAY))
                        .append(Component.text("Level: " + levelPlayer.getData().level(), NamedTextColor.GRAY))
                        .append(Component.text("XP: " + levelPlayer.getData().xp(), NamedTextColor.GRAY))
                );
            }
        }.runTask(plugin));

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return List.of();
    }
}

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
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class LevelSubCommand implements TabCompletingCommandExecutor {
    @NotNull private final PlayerLevels plugin;

    public LevelSubCommand(@NotNull PlayerLevels plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (args.length < 2) {
            sender.sendMessage(Component.text("Usage: /playerlevels level <player> (get|set <level>)", NamedTextColor.RED));
            return true;
        }

        UUID playerUUID = PlayerUtils.getPlayerUUIDFromString(args[0]);
        if (playerUUID == null) {
            sender.sendMessage(Component.text("Player not found", NamedTextColor.RED));
            return true;
        }

        CompletableFuture<LevelPlayer> playerId = this.plugin.getLevelManager().loadLeveler(playerUUID);
        playerId.thenAccept(levelPlayer -> new BukkitRunnable() {
            @Override
            public void run() {

                if (levelPlayer == null) {
                    sender.sendMessage(Component.text("Player not found", NamedTextColor.RED));
                    return;
                }

                switch (args[1].toLowerCase()) {
                    case "get" -> sender.sendMessage(Component.text("Level: " + levelPlayer.getData().level(), NamedTextColor.GRAY));
                    case "set" -> {

                        if (args.length > 2) {
                            levelPlayer.getData().level(Integer.parseInt(args[2]));
                            sender.sendMessage(Component.text("Updated level to " + levelPlayer.getData().level(), NamedTextColor.GRAY));

                            Leveler leveler = (Leveler) levelPlayer;
                            leveler.updateAsync();
                        } else {
                            sender.sendMessage(Component.text("You need to specify a level", NamedTextColor.RED));
                        }

                    }
                }

            }
        }.runTask(plugin));

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return List.of();
    }
}

package net.jandie1505.playerlevels.commands.subcommands.manage;

import net.chaossquad.mclib.PlayerUtils;
import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.playerlevels.PlayerLevels;
import net.jandie1505.playerlevels.leveler.Leveler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class XPSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final PlayerLevels plugin;

    public XPSubcommand(@NotNull PlayerLevels plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (args.length < 2) {
            sender.sendMessage(Component.text("Usage: /playerlevels xp (get <player>|set <player> <xp>|give <player> <xp>|take <player> <xp>)", NamedTextColor.RED));
            return true;
        }

        UUID playerUUID = PlayerUtils.getPlayerUUIDFromString(args[1]);
        if (playerUUID == null) {
            sender.sendMessage(Component.text("Player not found", NamedTextColor.RED));
            return true;
        }

        CompletableFuture<Leveler> playerId = this.plugin.getLevelManager().loadLeveler(playerUUID, true);
        playerId.thenAccept(leveler -> new BukkitRunnable() {
            @Override
            public void run() {

                try {

                    if (leveler == null) {
                        sender.sendMessage(Component.text("Player not found", NamedTextColor.RED));
                        return;
                    }

                    switch (args[0].toLowerCase()) {
                        case "get" -> sender.sendMessage(Component.text("XP: " + leveler.getData().xp(), NamedTextColor.GRAY));
                        case "set" -> {

                            if (args.length > 2) {
                                double value = Double.parseDouble(args[2]);
                                if (value < 0) value = 0;

                                leveler.getData().xp(value);
                                sender.sendMessage(Component.text("Updated xp to " + leveler.getData().xp(), NamedTextColor.GRAY));
                                leveler.updateAsync();
                            } else {
                                sender.sendMessage(Component.text("You need to specify an amount of xp", NamedTextColor.RED));
                            }

                        }
                        case "give" -> {

                            if (args.length > 2) {
                                double value = leveler.getData().xp() + Double.parseDouble(args[2]);
                                if (value < 0) value = 0;

                                leveler.getData().xp(value);
                                sender.sendMessage(Component.text("Updated xp to " + leveler.getData().xp(), NamedTextColor.GRAY));
                                leveler.updateAsync();
                            } else {
                                sender.sendMessage(Component.text("You need to specify an amount of xp", NamedTextColor.RED));
                            }

                        }
                        case "take" -> {

                            if (args.length > 2) {
                                double value = leveler.getData().xp() - Double.parseDouble(args[2]);
                                if (value < 0) value = 0;

                                leveler.getData().xp(value);
                                sender.sendMessage(Component.text("Updated xp to " + leveler.getData().xp(), NamedTextColor.GRAY));
                                leveler.updateAsync();
                            } else {
                                sender.sendMessage(Component.text("You need to specify an amount of xp", NamedTextColor.RED));
                            }

                        }
                    }

                } catch (IllegalArgumentException e) {
                    sender.sendMessage(Component.text("Illegal argument", NamedTextColor.RED));
                }

            }
        }.runTaskLater(plugin, 1));

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return switch (args.length) {
            case 1 -> List.of("get", "set", "give", "take");
            case 2 -> this.plugin.getServer().getOnlinePlayers().stream().map(Player::getName).toList();
            case 3 -> {

                switch (args[0]) {
                    case "set" -> {
                        yield List.of("0", "100", "200", "300", "400", "500", "600", "700", "800", "900", "1000");
                    }
                    case "give", "take" -> {
                        yield List.of("10", "50", "100", "200", "300", "400", "500", "600", "700", "800", "900", "1000");
                    }
                    default -> {
                        yield List.of();
                    }
                }

            }
            default -> List.of();
        };
    }
}

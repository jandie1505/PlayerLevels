package net.jandie1505.playerlevels.commands.subcommands.manage.manage_players;

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

public class ManagePlayersLevelSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final PlayerLevels plugin;

    public ManagePlayersLevelSubcommand(@NotNull PlayerLevels plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (args.length < 2) {
            sender.sendMessage(Component.text("Usage: /playerlevels level (get <player>|set <level> <level>)", NamedTextColor.RED));
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

                if (leveler == null) {
                    sender.sendMessage(Component.text("Player not found", NamedTextColor.RED));
                    return;
                }

                switch (args[0].toLowerCase()) {
                    case "get" -> sender.sendMessage(Component.text("Level: " + leveler.getData().level(), NamedTextColor.GRAY));
                    case "set" -> {

                        if (args.length > 2) {
                            int level = Integer.parseInt(args[2]);
                            if (level < 0) {
                                sender.sendMessage(Component.text("Level must be a positive (or zero) integer", NamedTextColor.RED));
                                return;
                            }

                            leveler.getData().level(level);
                            sender.sendMessage(Component.text("Updated level to " + leveler.getData().level(), NamedTextColor.GRAY));
                            leveler.updateAsync();
                        } else {
                            sender.sendMessage(Component.text("You need to specify a level", NamedTextColor.RED));
                        }

                    }
                }

            }
        }.runTaskLater(plugin, 1));

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return switch (args.length) {
            case 1 -> List.of("get", "set");
            case 2 -> this.plugin.getServer().getOnlinePlayers().stream().map(Player::getName).toList();
            case 3 -> {

                if (args[0].equalsIgnoreCase("set")) {
                    yield List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "20", "30", "40", "50", "60", "70", "80", "90", "100");
                }

                yield List.of();
            }
            default -> List.of();
        };
    }
}

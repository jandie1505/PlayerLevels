package net.jandie1505.playerlevels.commands.subcommands.debug;

import com.zaxxer.hikari.HikariDataSource;
import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.playerlevels.PlayerLevels;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DatabaseSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final PlayerLevels plugin;

    public DatabaseSubcommand(@NotNull PlayerLevels plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (sender != Bukkit.getConsoleSender()) {
            sender.sendRichMessage("<red>No permission");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(Component.text("Usage: /playerlevels database (connect|disconnect|info)", NamedTextColor.RED));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "connect" -> {
                boolean success = this.plugin.getDatabaseManager().setupDatabase();

                if (success) {
                    sender.sendMessage(Component.text("Successfully connected to the database", NamedTextColor.GREEN));
                } else {
                    sender.sendMessage(Component.text("Failed to connect to the database", NamedTextColor.RED));
                }
            }
            case "disconnect" -> {
                this.plugin.getDatabaseManager().shutdownDatabase();
                sender.sendMessage(Component.text("Successfully disconnected from the database", NamedTextColor.GREEN));
            }
            case "info" -> {

                HikariDataSource source = this.plugin.getDatabaseManager().getDataSource();

                if (source == null) {
                    sender.sendMessage(Component.text("Not connected", NamedTextColor.RED));
                    return true;
                }

                sender.sendMessage(Component.empty()
                        .append(Component.text("URL: " + source.getJdbcUrl(), NamedTextColor.GRAY)).appendNewline()
                        .append(Component.text("Username: " + source.getUsername(), NamedTextColor.GRAY))
                );

            }
            default -> sender.sendMessage(Component.text("Run command without arguments for help", NamedTextColor.RED));
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (args.length == 1) {
            return List.of("connect", "disconnect", "info");
        }

        return List.of();
    }

    public @NotNull PlayerLevels getPlugin() {
        return plugin;
    }

}

package net.jandie1505.playerlevels.commands;

import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.playerlevels.PlayerLevels;
import net.jandie1505.playerlevels.leveler.Leveler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CacheSubCommand implements TabCompletingCommandExecutor {
    @NotNull private final PlayerLevels plugin;

    public CacheSubCommand(@NotNull PlayerLevels plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        sender.sendMessage(Component.text("Cached players:", NamedTextColor.GRAY));

        for (Map.Entry<UUID, Leveler> entry : this.plugin.getLevelManager().getCache().entrySet()) {
            sender.sendMessage(Component.text(entry.getKey().toString() + ": " + entry.getValue().getPlayerUUID() + " " + entry.getValue().getData().level() + " " + entry.getValue().getData().xp(), NamedTextColor.GRAY));
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return List.of();
    }
}

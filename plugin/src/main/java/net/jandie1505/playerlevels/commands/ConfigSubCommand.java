package net.jandie1505.playerlevels.commands;

import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.chaossquad.mclib.commands.DataStorageEditorCommand;
import net.jandie1505.playerlevels.PlayerLevels;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ConfigSubCommand implements TabCompletingCommandExecutor {
    @NotNull private final PlayerLevels plugin;

    public ConfigSubCommand(@NotNull PlayerLevels plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return DataStorageEditorCommand.onCommand(this.plugin.config(), sender, label, args);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return DataStorageEditorCommand.onTabComplete(this.plugin.config(), sender, args);
    }

    public @NotNull PlayerLevels getPlugin() {
        return plugin;
    }

}

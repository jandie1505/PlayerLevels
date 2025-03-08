package net.jandie1505.playerlevels.commands;

import net.chaossquad.mclib.command.SubcommandCommand;

import net.chaossquad.mclib.command.SubcommandEntry;
import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.playerlevels.PlayerLevels;
import net.jandie1505.playerlevels.leveler.Leveler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.List;
import java.util.logging.Level;

public class PlayerLevelsCommand extends SubcommandCommand {
    @NotNull private final PlayerLevels plugin;

    public PlayerLevelsCommand(@NotNull PlayerLevels plugin) {
        super(plugin);
        this.plugin = plugin;

        this.addSubcommand("help", SubcommandEntry.of(new TabCompletingCommandExecutor() {
            @Override
            public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
                PlayerLevelsCommand.super.onExecutionWithoutSubcommand(sender, command, label);
                return true;
            }

            @Override
            public @NotNull List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
                return List.of();
            }
        }));

        this.addSubcommand("config", SubcommandEntry.of(new ConfigSubCommand(this.plugin)));
        this.addSubcommand("cache", SubcommandEntry.of(new CacheSubCommand(this.plugin)));
        this.addSubcommand("manage", SubcommandEntry.of(new ManageSubcommand(this.plugin)));
        this.addSubcommand("database", SubcommandEntry.of(new DatabaseSubcommand(this.plugin)));
    }

    @Override
    protected void onExecutionWithoutSubcommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label) {

        if (!(sender instanceof Player player)) {
            super.onExecutionWithoutSubcommand(sender, cmd, label);
            return;
        }

        Leveler leveler = this.plugin.getLevelManager().getLeveler(player.getUniqueId());
        if (leveler == null) {
            super.onExecutionWithoutSubcommand(sender, cmd, label);
            return;
        }

        double totalXP;
        try {
            totalXP = this.plugin.getLevelManager().getXPForLevel(leveler.getData().level());
        } catch (Exception e) {
            totalXP = -1;
            this.plugin.getLogger().log(Level.WARNING, "Failed to calculate xp using the XP formula", e);
        }

        String formattedXP = new DecimalFormat("#,###.00").format(totalXP) + " XP";
        String formattedTotalXP = totalXP > 0 ? " (" + new DecimalFormat("#,###.00").format(totalXP) + " XP)" : "";

        sender.sendRichMessage("<green>Your level is " + leveler.getData().level() + " and you have " + formattedXP + formattedTotalXP);
    }

}

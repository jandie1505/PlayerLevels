package net.jandie1505.playerlevels.core.commands;

import net.chaossquad.mclib.command.SubcommandCommand;
import net.chaossquad.mclib.command.SubcommandEntry;
import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.playerlevels.core.PlayerLevels;
import net.jandie1505.playerlevels.core.commands.subcommands.*;
import net.jandie1505.playerlevels.core.constants.MessageKeys;
import net.jandie1505.playerlevels.core.constants.Permissions;
import net.jandie1505.playerlevels.core.leveler.Leveler;
import net.jandie1505.playerlevels.core.messages.TagResolvers;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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

        this.addSubcommand("manage", SubcommandEntry.of(new ManageSubcommand(this.plugin), sender -> Permissions.hasPermission(sender, Permissions.MANAGE_PLAYERS, Permissions.MANAGE_REWARDS)));
        this.addSubcommand("debug", SubcommandEntry.of(new DebugSubcommand(this.plugin), Permissions::hasPermission));
        this.addSubcommand("info", SubcommandEntry.of(new InfoSubcommand(this.plugin)));
        this.addSubcommand("top", SubcommandEntry.of(new TopSubcommand(this.plugin), sender -> Permissions.hasPermission(sender, Permissions.TOPLIST)));
        this.addSubcommand("milestones", SubcommandEntry.of(new MilestonesSubcommand(this.plugin), sender -> Permissions.hasPermission(sender, Permissions.COMMAND_REWARDS)));
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

        sender.sendRichMessage(this.plugin.messages().optString(MessageKeys.INFO_OWN, ""), TagResolvers.leveler("leveler", leveler), TagResolvers.level("level", leveler.getData().level()));
    }

    @Override
    protected void onExecutionWithUnknownSubcommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        sender.sendRichMessage(this.plugin.messages().optString(MessageKeys.GENERAL_UNKNOWN_SUBCOMMAND, ""));
    }

    @Override
    protected void onExecutionWithoutPermission(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args, @Nullable String subcommand) {
        sender.sendRichMessage(this.plugin.messages().optString(MessageKeys.GENERAL_NO_PERMISSION, ""));
    }

}

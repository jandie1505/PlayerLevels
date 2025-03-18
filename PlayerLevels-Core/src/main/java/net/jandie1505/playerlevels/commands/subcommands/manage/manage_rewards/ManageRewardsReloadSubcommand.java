package net.jandie1505.playerlevels.commands.subcommands.manage.manage_rewards;

import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.playerlevels.PlayerLevels;
import net.jandie1505.playerlevels.commands.subcommands.utils.OptionParser;
import net.jandie1505.playerlevels.constants.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ManageRewardsReloadSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final PlayerLevels plugin;

    public ManageRewardsReloadSubcommand(@NotNull PlayerLevels plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] a) {

        if (!Permissions.hasPermission(sender, Permissions.MANAGE_REWARDS)) {
            sender.sendRichMessage("<red>No permission");
            return true;
        }

        OptionParser.Result args = OptionParser.parse(a);

        this.plugin.getRewardsRegistry().createRewardsFromConfig(!args.hasOption("update-only"));
        sender.sendRichMessage("<green>Rewards have been reloaded successfully");

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return OptionParser.complete(sender, OptionParser.parse(args), Set.of("update-only"), Map.of());
    }

    public @NotNull PlayerLevels getPlugin() {
        return plugin;
    }

}

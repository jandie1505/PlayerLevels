package net.jandie1505.playerlevels.commands.subcommands.rewards;

import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.playerlevels.PlayerLevels;
import net.jandie1505.playerlevels.commands.subcommands.utils.OptionParser;
import net.jandie1505.playerlevels.constants.Permissions;
import net.jandie1505.playerlevels.rewards.Reward;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RewardsInfoSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final PlayerLevels plugin;

    public RewardsInfoSubcommand(@NotNull PlayerLevels plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] a) {

        if (!Permissions.hasPermission(sender, Permissions.MANAGE_REWARDS)) {
            sender.sendRichMessage("<red>No permission");
            return true;
        }

        OptionParser.Result args = OptionParser.parse(a);
        boolean unformatted = args.hasOption("unformatted");

        if (args.args().length < 1) {
            sender.sendRichMessage("<red>Usage: /levels rewards info <id>");
            return true;
        }

        Reward reward = this.plugin.getRewardsManager().getReward(args.args()[0]);
        if (reward == null) {
            sender.sendRichMessage("<red>Reward not found");
            return true;
        }

        if (unformatted) {
            sender.sendMessage(reward.getId() + " " + reward.getLevel() + " " + reward.getServerId() + " " + reward.isEnabled() + reward.getDescription());
        } else {
            sender.sendRichMessage("<gold>----- Reward " + reward.getId() + " -----");
            sender.sendRichMessage("<gold>Name: " + reward.getName());
            sender.sendRichMessage("<gold>Level: " + reward.getLevel());
            sender.sendRichMessage("<gold>ServerId: " + reward.getServerId());
            sender.sendRichMessage("<gold>Enabled: " + reward.isEnabled());
            sender.sendRichMessage("<gold>Description: " + reward.getDescription());
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 1) return this.plugin.getRewardsManager().getRewardsInternal().values().stream().map(Reward::getId).toList();
        return List.of();
    }

    public @NotNull PlayerLevels getPlugin() {
        return plugin;
    }

}

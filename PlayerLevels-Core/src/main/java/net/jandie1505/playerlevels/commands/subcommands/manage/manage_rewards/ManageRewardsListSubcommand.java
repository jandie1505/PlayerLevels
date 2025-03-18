package net.jandie1505.playerlevels.commands.subcommands.manage.manage_rewards;

import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.playerlevels.PlayerLevels;
import net.jandie1505.playerlevels.commands.subcommands.utils.OptionParser;
import net.jandie1505.playerlevels.constants.Permissions;
import net.jandie1505.playerlevels.rewards.MilestoneReward;
import net.jandie1505.playerlevels.rewards.Reward;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ManageRewardsListSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final PlayerLevels plugin;

    public ManageRewardsListSubcommand(@NotNull PlayerLevels plugin) {
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

        if (unformatted) {
            for (Reward reward : this.plugin.getRewardsManager().getRewardsInternal().values()) {
                sender.sendMessage(reward.getId() + " " + reward.getClass().getSimpleName() + " " + reward.getServerId() + " " + reward.isEnabled());
            }
        } else {
            sender.sendRichMessage("<gold>Rewards:");
            for (Reward reward : this.plugin.getRewardsManager().getRewardsInternal().values()) {
                sender.sendRichMessage("<gold>" + reward.getId() + ": " + reward.getClass().getSimpleName() + " " + (reward.getServerId() != null ? reward.getServerId() + " " : "all ") + reward.isEnabled());
            }
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return List.of();
    }

    public @NotNull PlayerLevels getPlugin() {
        return plugin;
    }

}

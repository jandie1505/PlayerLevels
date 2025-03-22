package net.jandie1505.playerlevels.core.commands.subcommands.manage.manage_rewards;

import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.playerlevels.core.PlayerLevels;
import net.jandie1505.playerlevels.core.commands.subcommands.utils.OptionParser;
import net.jandie1505.playerlevels.core.constants.MessageKeys;
import net.jandie1505.playerlevels.core.constants.Permissions;
import net.jandie1505.playerlevels.core.rewards.Reward;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ManageRewardsEnableSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final PlayerLevels plugin;

    public ManageRewardsEnableSubcommand(@NotNull PlayerLevels plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] a) {

        if (!Permissions.hasPermission(sender, Permissions.MANAGE_REWARDS)) {
            sender.sendRichMessage(this.plugin.messages().optString(MessageKeys.GENERAL_NO_PERMISSION, ""));
            return true;
        }

        OptionParser.Result args = OptionParser.parse(a);

        if (args.args().length < 2) {
            sender.sendRichMessage("<red>Usage: /levels rewards enable <id> (true|false)");
            return true;
        }

        Reward reward = this.plugin.getRewardsManager().getReward(args.args()[0]);
        if (reward == null) {
            sender.sendRichMessage("<red>Reward not found");
            return true;
        }

        reward.setEnabled(Boolean.parseBoolean(args.args()[1]));
        sender.sendRichMessage("<green>Reward " + (reward.isEnabled() ? "enabled" : "disabled"));

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 1) return this.plugin.getRewardsManager().getRewardsInternal().values().stream().map(Reward::getId).toList();
        if (args.length == 2) return List.of("false", "true");
        return List.of();
    }

    public @NotNull PlayerLevels getPlugin() {
        return plugin;
    }

}

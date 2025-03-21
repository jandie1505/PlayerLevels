package net.jandie1505.playerlevels.commands.subcommands;

import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.playerlevels.PlayerLevels;
import net.jandie1505.playerlevels.commands.subcommands.utils.OptionParser;
import net.jandie1505.playerlevels.constants.MessageKeys;
import net.jandie1505.playerlevels.constants.Permissions;
import net.jandie1505.playerlevels.leveler.Leveler;
import net.jandie1505.playerlevels.messages.InternalTagResolvers;
import net.jandie1505.playerlevels.messages.TagResolvers;
import net.jandie1505.playerlevels.rewards.MilestoneReward;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MilestonesSubcommand implements TabCompletingCommandExecutor {
    private static final int pageSize = 10;
    @NotNull private final PlayerLevels plugin;

    public MilestonesSubcommand(@NotNull PlayerLevels plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] a) {

        if (!Permissions.hasPermission(sender, Permissions.COMMAND_REWARDS)) {
            sender.sendRichMessage("<red>No permission");
            return true;
        }

        OptionParser.Result args = OptionParser.parse(a);

        Leveler leveler = (sender instanceof Player player) ? this.plugin.getLevelManager().getLeveler(player.getUniqueId()) : null;

        int page = 0;

        if (args.args().length > 0) {
            try {
                page = Integer.parseInt(args.args()[0]) - 1;
            } catch (IllegalArgumentException e) {
                sender.sendRichMessage("<red>Invalid page");
                return true;
            }
        }

        if (page < 0) {
            sender.sendRichMessage("<red>Invalid page");
            return true;
        }

        List<MilestoneReward> rewards = this.plugin.getRewardsManager().getRewardsInternal().values().stream()
                .filter(reward -> reward instanceof MilestoneReward)
                .map(reward -> (MilestoneReward) reward)
                .sorted(Comparator.comparing(MilestoneReward::getLevel))
                .toList();

        if (rewards.isEmpty()) {
            sender.sendRichMessage(this.plugin.messages().optString(MessageKeys.MILESTONE_LIST_EMPTY, ""));
            return true;
        }

        List<List<MilestoneReward>> pages = this.createPages(rewards, pageSize);

        if (page >= pages.size()) {
            sender.sendRichMessage("<red>Invalid page");
            return true;
        }

        List<MilestoneReward> pageRewards = pages.get(page);

        Component message = Component.empty()
                .append(MiniMessage.miniMessage().deserialize(
                        this.plugin.messages().optString(MessageKeys.MILESTONE_LIST_TITLE, ""),
                        TagResolver.resolver("page", Tag.inserting(Component.text(page + 1))),
                        TagResolver.resolver("max_pages", Tag.inserting(Component.text(pages.size())))
                ));

        message = message.append(this.createRewardsList(pageRewards, leveler));
        sender.sendMessage(message);

        return true;
    }

    private Component createRewardsList(Collection<MilestoneReward> rewards, @Nullable Leveler leveler) {
        Component message = Component.empty();

        if (rewards.isEmpty()) {
            return Component.empty();
        }

        for (MilestoneReward ri : rewards) {
            if (!(ri instanceof MilestoneReward reward)) continue;

            message = message.appendNewline().append(MiniMessage.miniMessage().deserialize(
                    this.plugin.messages().optString(MessageKeys.MILESTONE_LIST_ENTRY, ""),
                    leveler != null ? TagResolvers.leveler("leveler", leveler) : TagResolver.empty(),
                    TagResolvers.reward("reward", reward),
                    InternalTagResolvers.rewardLeveler(this.plugin, "reward_leveler", reward, leveler)
            ));
        }

        return message;
    }

    private @NotNull List<List<MilestoneReward>> createPages(List<MilestoneReward> rewards, int pageSize) {
        List<List<MilestoneReward>> result = new ArrayList<>();

        int amount = 0;
        List<MilestoneReward> currentPage = new ArrayList<>();

        Iterator<MilestoneReward> i = rewards.iterator();
        while (i.hasNext()) {
            MilestoneReward reward = i.next();

            currentPage.add(reward);
            amount += 1;

            if (amount >= pageSize || !i.hasNext()) {
                amount = 0;
                result.add(currentPage);
                currentPage = new ArrayList<>();
            }

        }

        return result;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (args.length == 1) {
            int maxPages = (int) Math.ceil((double) this.plugin.getRewardsManager().getRewardsInternal().values().stream().filter(reward -> reward instanceof MilestoneReward).count() / (pageSize == 0 ? 1 : pageSize));
            List<String> complete = new ArrayList<>();
            for (int i = 0; i < maxPages; i++) {
                complete.add(String.valueOf(i + 1));
            }
            return complete;
        }

        return List.of();
    }

    public @NotNull PlayerLevels getPlugin() {
        return plugin;
    }

}

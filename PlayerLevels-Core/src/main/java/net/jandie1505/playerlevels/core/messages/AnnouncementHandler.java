package net.jandie1505.playerlevels.core.messages;

import net.jandie1505.playerlevels.core.PlayerLevels;
import net.jandie1505.playerlevels.core.constants.MessageKeys;
import net.jandie1505.playerlevels.core.events.LevelUpEvent;
import net.jandie1505.playerlevels.core.events.RewardAppliedEvent;
import net.jandie1505.playerlevels.core.rewards.IntervalReward;
import net.jandie1505.playerlevels.core.rewards.MilestoneReward;
import net.jandie1505.playerlevels.core.rewards.Reward;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AnnouncementHandler implements Listener {
    @NotNull private final PlayerLevels plugin;

    public AnnouncementHandler(@NotNull PlayerLevels plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLevelUp(@NotNull LevelUpEvent event) {
        Player player = Bukkit.getPlayer(event.getLeveler().getPlayerUUID());
        if (player == null) return;

        String ownMessage = this.plugin.messages().optString(MessageKeys.ANNOUNCEMENT_LEVELUP_SELF, null);
        if (ownMessage != null) {
            player.sendRichMessage(
                    ownMessage,
                    TagResolvers.leveler("leveler", event.getLeveler()),
                    TagResolvers.player("player", player),
                    TagResolver.resolver("old_level", Tag.inserting(Component.text(event.getOldLevel()))),
                    TagResolver.resolver("new_level", Tag.inserting(Component.text(event.getNewLevel()))),
                    this.intervalRewardsListOnLevelUp("interval_rewards_list", event)
            );
        }

        String othersMessage = this.plugin.messages().optString(MessageKeys.ANNOUNCEMENT_LEVELUP_OTHERS, null);
        if (othersMessage != null) {
            for (Player otherPlayer : Bukkit.getOnlinePlayers().stream().filter(p -> p != player).toList()) {
                otherPlayer.sendRichMessage(
                        othersMessage,
                        TagResolvers.leveler("leveler", event.getLeveler()),
                        TagResolvers.player("player", player),
                        TagResolver.resolver("old_level", Tag.inserting(Component.text(event.getOldLevel()))),
                        TagResolver.resolver("new_level", Tag.inserting(Component.text(event.getNewLevel())))
                );
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRewardApplied(RewardAppliedEvent event) {
        Player player = Bukkit.getPlayer(event.getLeveler().getPlayerUUID());
        if (player == null) return;

        if (event.getReward() instanceof MilestoneReward milestone) {

            String ownMessage = this.plugin.messages().optString(MessageKeys.ANNOUNCEMENT_MILESTONE_UNLOCKED_SELF, null);
            if (ownMessage != null) {
                player.sendRichMessage(
                        ownMessage,
                        TagResolvers.leveler("leveler", event.getLeveler()),
                        TagResolvers.player("player", player),
                        TagResolvers.reward("reward", milestone, new TagResolvers.RewardContext(event.getLevel())),
                        TagResolver.resolver("level", Tag.inserting(Component.text(event.getLevel())))
                );
            }

            String othersMessage = this.plugin.messages().optString(MessageKeys.ANNOUNCEMENT_MILESTONE_UNLOCKED_OTHERS, null);
            if (othersMessage != null) {
                for (Player otherPlayer : Bukkit.getOnlinePlayers().stream().filter(p -> p != player).toList()) {
                    otherPlayer.sendRichMessage(
                            othersMessage,
                            TagResolvers.leveler("leveler", event.getLeveler()),
                            TagResolvers.player("player", player),
                            TagResolvers.reward("reward", milestone, new TagResolvers.RewardContext(event.getLevel())),
                            TagResolver.resolver("level", Tag.inserting(Component.text(event.getLevel())))
                    );
                }
            }

        } else if (event.getReward() instanceof IntervalReward reward) {
            String ownMessage = this.plugin.messages().optString(MessageKeys.ANNOUNCEMENT_INTERVAL_REWARD_UNLOCKED, null);
            if (ownMessage != null) {
                player.sendRichMessage(
                        ownMessage,
                        TagResolvers.leveler("leveler", event.getLeveler()),
                        TagResolvers.player("player", player),
                        TagResolvers.reward("reward", reward, new TagResolvers.RewardContext(event.getLevel())),
                        TagResolver.resolver("level", Tag.inserting(Component.text(event.getLevel())))
                );
            }
        }

    }

    private TagResolver intervalRewardsListOnLevelUp(@Subst("interval_rewards_list") String tagName, @NotNull LevelUpEvent event) {
        List<IntervalReward> rewards = this.plugin.getRewardsManager().getRewardsInternal().values().stream()
                .filter(Reward::isEnabled)
                .filter(reward -> reward instanceof IntervalReward)
                .map(reward -> (IntervalReward) reward)
                .filter(reward -> reward.isInInterval(event.getNewLevel()))
                .toList();

        if (rewards.isEmpty()) return TagResolver.resolver(tagName, Tag.inserting(Component.empty()));

        Component text = Component.empty().append(MiniMessage.miniMessage().deserialize(this.plugin.messages().optString(MessageKeys.ANNOUNCEMENT_LEVELUP_REWARD_LIST_TITLE, "")));

        for (Reward reward : rewards) {
            text = text
                    .appendNewline()
                    .append(
                            MiniMessage.miniMessage().deserialize(this.plugin.messages().optString(MessageKeys.ANNOUNCEMENT_LEVELUP_REWARD_LIST_ENTRY, ""),
                                    TagResolvers.reward("reward", reward, new TagResolvers.RewardContext(event.getNewLevel())))
                    );
        }

        return TagResolver.resolver(tagName, Tag.inserting(text));
    }

    public @NotNull PlayerLevels getPlugin() {
        return plugin;
    }

}

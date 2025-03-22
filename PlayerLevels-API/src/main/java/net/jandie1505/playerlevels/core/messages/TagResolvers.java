package net.jandie1505.playerlevels.core.messages;

import net.jandie1505.playerlevels.api.core.level.Leveler;
import net.jandie1505.playerlevels.api.core.level.ReceivedReward;
import net.jandie1505.playerlevels.api.core.reward.IntervalReward;
import net.jandie1505.playerlevels.api.core.reward.MilestoneReward;
import net.jandie1505.playerlevels.api.core.reward.Reward;
import net.jandie1505.playerlevels.core.PlayerLevelsAPIProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

/**
 * Contains {@link TagResolver}s for the MiniMessage format.
 */
public final class TagResolvers {

    private TagResolvers() {}

    /**
     * Returns a tag resolver for the specified leveler.
     * @param tagName the tag name: &lt;TAG_NAME:field&gt;.
     * @param leveler leveler
     * @return tag resolver
     */
    public static TagResolver leveler(@Subst("leveler") @NotNull String tagName, @NotNull Leveler leveler) {
        return TagResolver.resolver(tagName, (argumentQueue, context) -> {
            final String arg = argumentQueue.popOr(tagName + " tag (leveler resolver) requires an argument").value();

            Component placeholder;

            try {

                if (arg.startsWith("reward_")) {
                    String rewardString = arg.substring("reward_".length());
                    String[] rewardStringSplit = rewardString.split("_", 1);
                    if (rewardStringSplit.length < 2) {
                        return Tag.inserting(Component.empty());
                    }

                    String name = rewardStringSplit[0];
                    String value = rewardStringSplit[1];

                    ReceivedReward reward = leveler.getData().getReceivedReward(name);
                    if (reward == null) {
                        return Tag.inserting(Component.empty());
                    }

                    switch (value) {
                        case "blocked" -> placeholder = Component.text(reward.blocked());
                        case "level" -> placeholder = Component.text(reward.level());
                        case "default" -> placeholder = Component.text(reward.isDefault());
                        default -> placeholder = Component.empty();
                    }

                    return Tag.inserting(placeholder);
                }

                switch (arg) {
                    case "level" -> placeholder = Component.text(leveler.getData().level());
                    case "xp" -> placeholder = Component.text(leveler.getData().xp());
                    case "xp_formatted" -> placeholder = Component.text(Formatters.formatXP(leveler.getData().xp()));
                    case "total_xp" -> placeholder = Component.text(getXPForLevel(leveler.getData().level()) + leveler.getData().xp());
                    case "total_xp_formatted" -> placeholder = Component.text(Formatters.formatXP(getXPForLevel(leveler.getData().level()) + leveler.getData().xp()));
                    case "cached_name" -> placeholder = Component.text(Objects.requireNonNullElse(leveler.getData().cachedName(), "?"));
                    case "rewards" -> placeholder = Component.text(leveler.getData().getReceivedRewards().toString());
                    case "uuid" -> placeholder = Component.text(leveler.getPlayerUUID().toString());
                    case "cached" -> placeholder = Component.text(leveler.isCached());
                    case "data_string" -> placeholder = Component.text(leveler.getData().toString());
                    case "name" -> {
                        String name = Bukkit.getOfflinePlayer(leveler.getPlayerUUID()).getName();
                        placeholder = name != null ? Component.text(name) : Component.text("???");
                    }
                    case "level_next" -> placeholder = Component.text(leveler.getData().level() + 1);
                    case "xp_current_level" -> placeholder = Component.text(getXPForLevel(leveler.getData().level()));
                    case "xp_current_level_formatted" -> placeholder = Component.text(Formatters.formatXP(getXPForLevel(leveler.getData().level())));
                    case "xp_next_level" -> placeholder = Component.text(getXPForLevel(leveler.getData().level() + 1));
                    case "xp_next_level_formatted" -> placeholder = Component.text(Formatters.formatXP(getXPForLevel(leveler.getData().level() + 1)));
                    case "xp_to_next_level" -> placeholder = Component.text(getXPForNextLevel(leveler.getData().level(), leveler.getData().level() + 1));
                    case "xp_to_next_level_formatted" -> placeholder = Component.text(Formatters.formatXP(getXPForNextLevel(leveler.getData().level(), leveler.getData().level() + 1)));
                    case "xp_remaining" -> placeholder = Component.text(getXPForNextLevel(leveler.getData().level(), leveler.getData().level() + 1) - leveler.getData().xp());
                    case "xp_remaining_formatted" -> placeholder = Component.text(Formatters.formatXP(getXPForNextLevel(leveler.getData().level(), leveler.getData().level() + 1) - leveler.getData().xp()));
                    default -> placeholder = Component.empty();
                }

                return Tag.inserting(placeholder);
            } catch (Exception e) {
                return Tag.inserting(Component.empty());
            }

        });
    }

    /**
     * Resolves tags for a specified level.
     * @param tagName tag name
     * @param level level
     * @return tag resolver
     */
    public static TagResolver level(@Subst("level") @NotNull String tagName, int level) {
        return TagResolver.resolver(tagName, (argumentQueue, context) -> {
            final String arg = argumentQueue.popOr(tagName + " tag (level resolver) requires an argument").value();

            Component placeholder;

            try {

                switch (arg) {
                    case "milestones" -> {
                        Map<String, MilestoneReward> milestones = PlayerLevelsAPIProvider.getApi().getRewardsManager().getMilestonesForLevel(level);

                        String value = "";
                        for (MilestoneReward milestone : milestones.values()) {
                            value = value + milestone.getName() + ", ";
                        }
                        value = value.substring(0, value.length() - 2);
                        placeholder = Component.text(value.isEmpty() ? "---" : value);
                    }
                    case "next_milestones" -> {
                        Map<String, MilestoneReward> milestones = PlayerLevelsAPIProvider.getApi().getRewardsManager().getMilestonesForLevel(level + 1);

                        String value = "";
                        for (MilestoneReward milestone : milestones.values()) {
                            value = value + milestone.getName() + ", ";
                        }
                        value = value.substring(0, value.length() - 2);
                        placeholder = Component.text(value.isEmpty() ? "---" : value);
                    }
                    case "level" -> placeholder = Component.text(level);
                    case "next_level" -> placeholder = Component.text(level + 1);
                    case "xp_current_level" -> placeholder = Component.text(getXPForLevel(level));
                    case "xp_current_level_formatted" -> placeholder = Component.text(Formatters.formatXP(getXPForLevel(level)));
                    case "xp_next_level" -> placeholder = Component.text(getXPForLevel(level + 1));
                    case "xp_next_level_formatted" -> placeholder = Component.text(Formatters.formatXP(getXPForLevel(level + 1)));
                    case "xp_to_next_level" -> placeholder = Component.text(getXPForNextLevel(level, level + 1));
                    case "xp_to_next_level_formatted" -> placeholder = Component.text(Formatters.formatXP(getXPForNextLevel(level, level + 1)));
                    default -> placeholder = Component.empty();
                }

                return Tag.inserting(placeholder);
            } catch (Exception e) {
                return Tag.inserting(Component.empty());
            }

        });
    }

    /**
     * Resolves tags about rewards.
     * @param tagName tag name
     * @param reward reward
     * @return resolver
     */
    public static TagResolver reward(@Subst("reward") @NotNull String tagName, @NotNull Reward reward) {
        return TagResolver.resolver(tagName, (argumentQueue, context) -> {
            final String arg = argumentQueue.popOr(tagName + " tag (reward resolver) requires an argument").value();

            Component placeholder;

            try {

                switch (arg) {
                    case "id" -> placeholder = Component.text(reward.getId());
                    case "type" -> placeholder = Component.text(reward.getClass().getSimpleName());
                    case "name" -> placeholder = Component.text(reward.getName());
                    case "description" -> placeholder = Component.text(reward.getDescription());
                    case "limit" -> placeholder = Component.text(reward.getLimit());
                    case "enabled" -> placeholder = Component.text(reward.isEnabled());
                    case "requiresOnlinePlayer" -> placeholder = Component.text(reward.requiresOnlinePlayer());
                    case "serverId" -> {
                        String serverId = reward.getServerId();
                        placeholder = Component.text(serverId == null ? "_global_" : serverId);
                    }
                    case "level" -> {
                        if (reward instanceof MilestoneReward milestone) {
                            placeholder = Component.text(milestone.getLevel());
                        } else {
                            placeholder = Component.empty();
                        }

                    }
                    case "interval" -> {
                        if (reward instanceof IntervalReward intervalReward) {
                            placeholder = Component.text(intervalReward.getInterval());
                        } else {
                            placeholder = Component.empty();
                        }
                    }
                    case "start" -> {
                        if (reward instanceof IntervalReward intervalReward) {
                            placeholder = Component.text(intervalReward.getStart());
                        } else {
                            placeholder = Component.empty();
                        }
                    }
                    default -> placeholder = Component.empty();
                }

                return Tag.inserting(placeholder);
            } catch (Exception e) {
                return Tag.inserting(Component.empty());
            }

        });
    }

    /**
     * Resolves tags for a specified level.
     * @param tagName tag name
     * @param player player
     * @return tag resolver
     */
    public static TagResolver player(@Subst("player") @NotNull String tagName, @NotNull Player player) {
        return TagResolver.resolver(tagName, (argumentQueue, context) -> {
            final String arg = argumentQueue.popOr(tagName + " tag (level resolver) requires an argument").value();

            Component placeholder;

            try {

                switch (arg) {
                    case "uuid" -> placeholder = Component.text(player.getUniqueId().toString());
                    case "name" -> placeholder = Component.text(player.getName());
                    case "display_name" -> placeholder = player.displayName();
                    case "player_list_name" -> placeholder = player.playerListName();
                    default -> placeholder = Component.empty();
                }

                return Tag.inserting(placeholder);
            } catch (Exception e) {
                return Tag.inserting(Component.empty());
            }

        });
    }

    private static double getXPForLevel(int level) {
        try {
            return PlayerLevelsAPIProvider.getApi().getLevelManager().getXPForLevel(level);
        } catch (Exception e) {
            return 0;
        }
    }

    private static double getXPForNextLevel(int currentLevel, int level) {
        try {
            return PlayerLevelsAPIProvider.getApi().getLevelManager().getXPForNextLevel(currentLevel, level);
        } catch (Exception e) {
            return 0;
        }
    }

}

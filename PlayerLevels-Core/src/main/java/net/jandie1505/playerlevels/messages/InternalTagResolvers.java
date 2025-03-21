package net.jandie1505.playerlevels.messages;

import net.jandie1505.playerlevels.PlayerLevels;
import net.jandie1505.playerlevels.api.level.Leveler;
import net.jandie1505.playerlevels.api.reward.MilestoneReward;
import net.jandie1505.playerlevels.constants.MessageKeys;
import net.jandie1505.playerlevels.rewards.Reward;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class InternalTagResolvers {

    private InternalTagResolvers() {}

    public static TagResolver rewardLeveler(@NotNull PlayerLevels plugin, @Subst("reward_leveler") @NotNull String tagName, @NotNull Reward reward, @Nullable Leveler leveler) {
        return TagResolver.resolver(tagName, (argumentQueue, context) -> {
            final String arg = argumentQueue.popOr(tagName + " tag (reward leveler resolver) requires an argument").value();

            Component placeholder;

            try {

                switch (arg) {
                    case "milestone_unlock_status" -> {
                        if (reward instanceof MilestoneReward milestone) {
                            if (leveler != null) {
                                if (milestone.isApplied(leveler)) {
                                    placeholder = MiniMessage.miniMessage().deserialize(
                                            plugin.messages().optString(MessageKeys.PLACEHOLDER_REWARD_UNLOCKED, ""),
                                            TagResolvers.reward("reward", reward),
                                            TagResolvers.leveler("leveler", leveler)
                                    );
                                } else {
                                    placeholder = MiniMessage.miniMessage().deserialize(
                                            plugin.messages().optString(MessageKeys.PLACEHOLDER_REWARD_LOCKED, ""),
                                            TagResolvers.reward("reward", reward),
                                            TagResolvers.leveler("leveler", leveler)
                                    );
                                }
                            } else {
                                placeholder = MiniMessage.miniMessage().deserialize(
                                        plugin.messages().optString(MessageKeys.PLACEHOLDER_REWARD_UNKNOWN_UNLOCK_STATUS, ""),
                                        TagResolvers.reward("reward", reward)
                                );
                            }
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

}

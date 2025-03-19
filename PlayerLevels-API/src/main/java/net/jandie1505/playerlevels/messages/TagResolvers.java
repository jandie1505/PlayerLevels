package net.jandie1505.playerlevels.messages;

import net.jandie1505.playerlevels.PlayerLevelsAPIProvider;
import net.jandie1505.playerlevels.api.level.Leveler;
import net.jandie1505.playerlevels.api.level.ReceivedReward;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;

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

}

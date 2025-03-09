package net.jandie1505.playerlevels.rewards;

import org.jetbrains.annotations.NotNull;

/**
 * The configuration for creating a reward.
 * @param id reward id
 * @param serverId server id
 * @param level level
 * @param name name
 * @param description description
 */
public record RewardConfig(
        @NotNull String id,
        @NotNull String serverId,
        int level,
        @NotNull String name,
        @NotNull String description
) {
}

package net.jandie1505.playerlevels.core.rewards;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The configuration for creating a reward.
 * @param id reward id
 * @param serverId server id
 * @param name name
 */
public record RewardConfig(
        @NotNull String id,
        @Nullable String serverId,
        @NotNull String name
) {
}

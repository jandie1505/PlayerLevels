package net.jandie1505.playerlevels.rewards;

import org.jetbrains.annotations.NotNull;

public record RewardConfig(
        @NotNull String id,
        @NotNull String serverId,
        int level,
        @NotNull String name,
        @NotNull String description
) {
}

package net.jandie1505.playerlevels.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface TemplateRewardCreator {

    /**
     * Registers a command reward.
     * @param id reward id
     * @param level required level
     * @param serverId server id
     * @param command command to execute
     * @param name reward name
     * @param description reward description
     * @param requireOnlinePlayer if the player needs to be online
     * @return reward
     */
    PlayerReward createCommandReward(
            @NotNull String id,
            int level,
            @Nullable String serverId,
            @NotNull String command,
            @NotNull String name,
            @Nullable String description,
            boolean requireOnlinePlayer
    );

}

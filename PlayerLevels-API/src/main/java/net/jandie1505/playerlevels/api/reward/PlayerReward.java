package net.jandie1505.playerlevels.api.reward;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A reward a player will receive for reaching a specific level.
 */
public interface PlayerReward {

    /**
     * This is the unique id of the upgrade.
     * @return upgrade id
     */
    @NotNull String getId();

    /**
     * This is the server id where the upgrade has to be applied.<br/>
     * If the value is null, the upgrade can be applied on all servers.
     * @return server id
     */
    @Nullable String getServerId();

    /**
     * Returns if the player needs to be online that the reward can be applied.
     * @return true = the player must be online
     */
    boolean requiresOnlinePlayer();

    /**
     * Returns if the upgrade is enabled.
     * @return enabled
     */
    boolean isEnabled();

    /**
     * Enables/disables the upgrade.<br/>
     * This will prevent the upgrade from applying to players.
     * @param enabled enable
     */
    void setEnabled(boolean enabled);

    /**
     * Name of the reward.
     * @return name
     */
    @NotNull String getName();

    /**
     * Description of the reward.
     * @return description
     */
    @NotNull String getDescription();

}

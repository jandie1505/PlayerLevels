package net.jandie1505.playerlevels.api.core;

import net.jandie1505.playerlevels.api.core.level.LevelingManager;
import net.jandie1505.playerlevels.api.core.level.TopListManager;
import net.jandie1505.playerlevels.api.core.reward.RewardsManager;
import net.jandie1505.playerlevels.api.core.reward.RewardsRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The main API interface of the PlayerLevels plugin.
 */
public interface PlayerLevelsAPI {

    /**
     * Returns the leveling manager.<br/>
     * Responsible for managing players.
     * @return LevelManager
     */
    LevelingManager getLevelManager();

    /**
     * Returns the rewards manager.<br/>
     * Responsible for storing rewards.
     * @return RewardManager
     */
    RewardsManager getRewardsManager();

    /**
     * Returns the rewards registry.<br/>
     * Responsible for storing reward creators which are used to create rewards from config.
     * @return RewardRegistry
     */
    RewardsRegistry getRewardsRegistry();

    /**
     * Returns the toplist manager.<br/>
     * The toplist manager handles the toplist.<br/>
     * This list contains the top n (configurable) players, for displaying purposes.
     * @return TopListManager
     */
    TopListManager getTopListManager();

    /**
     * Returns the server id currently set for the plugin.
     * @return server id
     */
    @NotNull String getServerId();

    /**
     * Returns the server id override.
     * @return server id override
     */
    @Nullable String getServerIdOverride();

    /**
     * Set a server id override
     * @param serverId server id
     */
    void setServerIdOverride(@Nullable String serverId);

}

package net.jandie1505.playerlevels.api.core.reward;

import net.jandie1505.playerlevels.core.rewards.RewardCreator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Contains {@link RewardCreator}s for creating rewards using the config.
 */
public interface RewardsRegistry {

    /**
     * Get all registered creators.
     * @return map of creators
     */
    @NotNull Map<String, RewardCreator> getCreators();

    /**
     * Get a creator.
     * @param type type
     * @return  creator (null if not exist)
     */
    @Nullable RewardCreator getCreator(@NotNull String type);

    /**
     * Register a new creator.
     * @param type type
     * @param creator creator
     */
    void registerCreator(@NotNull String type, @NotNull RewardCreator creator);

    /**
     * Unregister a new creator.
     * @param type type
     */
    void unregisterCreator(@NotNull String type);

}

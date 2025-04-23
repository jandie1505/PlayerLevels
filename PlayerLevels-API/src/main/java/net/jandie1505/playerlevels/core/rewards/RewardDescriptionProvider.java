package net.jandie1505.playerlevels.core.rewards;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

/**
 * Provides a description for a reward.<br/>
 * This is used instead of a static value to allow the reward to display dynamic information.
 */
public interface RewardDescriptionProvider {

    /**
     * Returns the description.
     * @param level checked level (negative value for not provided)
     * @return description
     */
    @Nullable Component getDescription(int level);

}

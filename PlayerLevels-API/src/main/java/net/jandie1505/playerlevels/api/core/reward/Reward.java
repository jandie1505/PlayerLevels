package net.jandie1505.playerlevels.api.core.reward;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A reward a player will receive for reaching a specific level.
 */
public interface Reward {

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
     * Returns the level limit.<br/>
     * Levels after this limit are not checked for this reward.<br/>
     * That means, if the reward unlocks on level 50, but the limit is set to 49, the reward will not be applied<br/>
     * This is useful for limiting IntervalRewards, for example, you get a reward each level, but only until level 50.
     * @return limit
     */
    int getLimit();

    /**
     * Name of the reward.
     * @return name
     */
    @NotNull String getName();

    /**
     * Returns the description.<br/>
     * A description can be dependent on the level of a player.
     * @param level level (-1 if not provided)
     * @return description
     */
    @ApiStatus.Experimental
    @NotNull Component getDescription(int level);

    /**
     * Description of the reward.
     * @return description
     */
    @ApiStatus.Experimental
    @NotNull Component getDescription();

    // ----- ENABLE/DISABLE -----

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

    // ----- INNER CLASSES -----

    /**
     * The "outcome" when a reward has been applied.<br/>
     * Can be modified for rewards in {@link net.jandie1505.playerlevels.core.events.RewardApplyEvent}.
     */
    enum ApplyStatus {

        /**
         * The reward is applied normally.<br/>
         * The default value.
         */
        APPLY(true, true),

        /**
         * The reward is applied, but not marked as applied.
         */
        APPLY_SKIP(true, false),

        /**
         * The reward is not applied and not marked as applied.<br/>
         * This behavior is similar to when the player does not meet the requirement for the reward.
         */
        CANCEL_SKIP(false, false),

        /**
         * The reward is not applied, but marked as applied.<br/>
         * This means the player will skip the reward (if the reward uses the default condition).
         */
        CANCEL_MARK_APPLIED(false, true);

        private final boolean applied;
        private final boolean markedAsApplied;

        ApplyStatus(boolean applied, boolean markedAsApplied) {
            this.applied = applied;
            this.markedAsApplied = markedAsApplied;
        }

        /**
         * Returns true if the reward is applied.
         * @return reward applied
         */
        public boolean isApplied() {
            return applied;
        }

        /**
         * Returns true if the event is marked as applied.
         * @return marked as successful
         */
        public boolean isMarkedAsApplied() {
            return markedAsApplied;
        }

    }

    /**
     * The status of an applied reward.
     * @param status The status of the applied event.
     * @param level The level the reward has been applied for.
     */
    record ApplyResult(@NotNull ApplyStatus status, int level) {}

}

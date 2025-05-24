package net.jandie1505.playerlevels.core.rewards;

import net.jandie1505.playerlevels.core.events.RewardAppliedEvent;
import net.jandie1505.playerlevels.core.events.RewardApplyEvent;
import net.jandie1505.playerlevels.core.leveler.Leveler;
import net.jandie1505.playerlevels.core.leveler.ReceivedReward;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;

public abstract class Reward implements net.jandie1505.playerlevels.api.core.reward.Reward {
    @NotNull private final RewardsManager manager;
    @NotNull private final String id;
    @Nullable private final String serverId;
    @NotNull private final RewardExecutor executor;
    private final boolean requireOnlinePlayer;
    private final int limit;
    @NotNull private final String name;
    @NotNull private final RewardDescriptionProvider descriptionProvider;
    private boolean enabled;

    protected Reward(
            @NotNull RewardsManager manager,
            @NotNull String id,
            @Nullable String serverId,
            @NotNull RewardExecutor executor,
            boolean requireOnlinePlayer,
            int limit,
            @NotNull String name,
            @Nullable RewardDescriptionProvider descriptionProvider
    ) {
        this.manager = manager;
        this.id = id;
        this.serverId = serverId;
        this.executor = executor;
        this.requireOnlinePlayer = requireOnlinePlayer;
        this.limit = limit;
        this.name = name;
        this.descriptionProvider = descriptionProvider != null ? descriptionProvider : (level) -> Component.empty();
        this.enabled = true;
    }

    // ----- REWARD APPLY -----

    /**
     * Applies the event if all conditions are met.
     * @param leveler leveler
     */
    @SuppressWarnings("UnusedReturnValue")
    public final void apply(@NotNull Leveler leveler) {
        if (!this.isApplicable(leveler)) return; // Check conditions

        new BukkitRunnable() {
            @Override
            public void run() {
                Reward.this.applyLevels(leveler);
            }
        }.runTask(this.manager.getPlugin());

    }

    /**
     * Apply the reward.<br/>
     * Should only be called from {@link #apply(Leveler)} from the Bukkit main thread.
     * @param leveler leveler
     */
    private void applyLevels(@NotNull Leveler leveler) {

        int levelerLevel = leveler.getData().level();
        for (int level = 1; level <= levelerLevel && (this.limit < 0 || level <= this.limit); level++) {
            boolean continueLoop = this.applyLevel(leveler, level);
            if (!continueLoop) break;
        }

    }

    /**
     * Apply a single level of the reward.<br/>
     * Should only be called from {@link #applyLevels(Leveler)}.
     * @param leveler leveler
     * @param level level to apply
     * @return continue loop
     */
    private boolean applyLevel(@NotNull Leveler leveler, int level) {

        // PRECONDITIONS

        // Check for general apply conditions
        // If the general apply condition fails, the reward can't be applied for any level, so we can return here.
        if (!this.isApplicable(leveler)) {
            return false;
        }

        // Continue with next level when current level does not apply.
        // This ensures that the event is applied for all levels that fulfill the condition between level 1 and the player's level.
        if (!this.checkApplyCondition(leveler, level)) {
            return true;
        }

        // APPLY REWARD

        // Call apply event before applying the reward
        RewardApplyEvent event = new RewardApplyEvent(leveler, this, level);
        Bukkit.getPluginManager().callEvent(event);

        final ApplyStatus status = event.getResult();

        if (status.isApplied()) {

            // The event is applied when the event result is either APPLY or APPLY_SKIP

            boolean success;

            // Apply event and catch errors
            try {
                success = this.executor.onApply(this, leveler, level);
            } catch (Exception e) {
                this.getManager().getPlugin().getLogger().log(Level.WARNING, "Exception while applying reward " + this.id + " to player " + leveler.getPlayerUUID(), e);
                return false;
            } catch (Throwable throwable) {
                this.enabled = false;
                this.getManager().getPlugin().getLogger().log(Level.SEVERE,
                        "A throwable which is not an exception has been thrown in onApply from reward " + this.id + " to player " + leveler.getPlayerUUID() + " " +
                                "The reward has been disabled for safety reasons. DO NOT IGNORE THIS!",
                        throwable
                );
                return false;
            }

            // Do not mark the event as applied when it was unsuccessful
            if (!success) {
                this.getManager().getPlugin().getLogger().log(Level.WARNING, "Failed to apply reward " + this.id + " to player " + leveler.getPlayerUUID() + ": Executor returned failure");
                return false;
            }

        }

        // RESULT

        ApplyResult result = new ApplyResult(status, level);

        // SUCCESS

        if (status.isMarkedAsApplied()) {

            // The reward is marked successful when the event result is APPLY or CANCEL_MARK_APPLIED

            this.onApplySuccess(leveler, level);

            // Call applied event
            Bukkit.getPluginManager().callEvent(new RewardAppliedEvent(leveler, this, result));

        }

        // RETURN RESULT

        return true;
    }

    // ----- REWARD APPLICABLE -----

    /**
     * Checks if the reward can be applied.
     * @param leveler leveler
     * @return applicable
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public final boolean isApplicable(@NotNull Leveler leveler) {
        if (!this.enabled) return false; // Not applicable when reward disabled
        if (leveler.getData().getOrCreateReceivedReward(this.id).blocked()) return false; // Not applicable when blocked
        if (this.requireOnlinePlayer && Bukkit.getPlayer(leveler.getPlayerUUID()) == null) return false; // not applicable when player required online but is offline
        if (this.serverId != null && !this.serverId.equals(this.manager.getPlugin().getServerId())) return false; // Wrong server
        return true;
    }

    // ----- ABSTRACT -----

    /**
     * The condition if the reward should be applied or not.
     * @param leveler leveler
     * @return true = reward should be applied
     */
    public abstract boolean checkApplyCondition(@NotNull Leveler leveler, int checkedLevel);

    /**
     * Is called after the reward has been successfully applied.<br/>
     * <b>This has to be overridden by the super class. If not, the reward will be blocked.</b>
     * @param leveler leveler
     */
    @ApiStatus.OverrideOnly
    public void onApplySuccess(@NotNull Leveler leveler, int checkedLevel) {
        ReceivedReward reward = leveler.getData().getOrCreateReceivedReward(Reward.this.id);
        reward.blocked(true);
        reward.level(leveler.getData().level());
    }

    // ----- GETTER -----

    public final @NotNull RewardsManager getManager() {
        return manager;
    }

    public final @NotNull String getId() {
        return id;
    }

    public final @Nullable String getServerId() {
        return serverId;
    }

    public final boolean requiresOnlinePlayer() {
        return requireOnlinePlayer;
    }

    public final int getLimit() {
        return limit;
    }

    public final @NotNull String getName() {
        return name;
    }

    /**
     * Returns the description.<br/>
     * A description can be dependent on the level of a player.
     * @param level level (-1 if not provided)
     * @return description
     */
    public final @NotNull Component getDescription(int level) {
        try {
            Component description = this.descriptionProvider.getDescription(level);
            return description != null ? description : Component.empty();
        } catch (Exception e) {
            Reward.this.getManager().getPlugin().getLogger().log(Level.WARNING, "Exception while getting description of reward " + Reward.this.id + " for level " + level, e);
            return Component.empty();
        } catch (Throwable throwable) {
            Reward.this.enabled = false;
            Reward.this.getManager().getPlugin().getLogger().log(Level.SEVERE,
                    "A throwable which is not an exception has been thrown in getDescription from reward " + Reward.this.id + " for level " + level + " " +
                            "The reward has been disabled for safety reasons. DO NOT IGNORE THIS!",
                    throwable
            );
            return Component.empty();
        }
    }

    public final @NotNull Component getDescription(@NotNull Leveler leveler) {
        return this.getDescription(leveler.getData().level());
    }

    public final @NotNull Component getDescription() {
        return this.getDescription(-1);
    }

    public final boolean isEnabled() {
        return enabled;
    }

    public final void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}

package net.jandie1505.playerlevels.core.rewards;

import net.jandie1505.playerlevels.core.events.RewardAppliedEvent;
import net.jandie1505.playerlevels.core.events.RewardApplyEvent;
import net.jandie1505.playerlevels.core.leveler.Leveler;
import net.jandie1505.playerlevels.core.leveler.ReceivedReward;
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
    @NotNull private final String description;
    private boolean enabled;

    protected Reward(
            @NotNull RewardsManager manager,
            @NotNull String id,
            @Nullable String serverId,
            @NotNull RewardExecutor executor,
            boolean requireOnlinePlayer,
            int limit,
            @NotNull String name,
            @Nullable String description
    ) {
        this.manager = manager;
        this.id = id;
        this.serverId = serverId;
        this.executor = executor;
        this.requireOnlinePlayer = requireOnlinePlayer;
        this.limit = limit;
        this.name = name;
        this.description = description != null ? description : "";
        this.enabled = true;
    }

    // ----- REWARD -----

    /**
     * Applies the event if all conditions are met.
     * @param leveler leveler
     * @return success
     */
    @SuppressWarnings("UnusedReturnValue")
    public final void apply(@NotNull Leveler leveler) {
        if (!this.isApplicable(leveler)) return; // Check conditions

        new BukkitRunnable() {
            @Override
            public void run() {

                int levelerLevel = leveler.getData().level();
                for (int level = 1; level <= levelerLevel && (Reward.this.limit < 0 || level <= Reward.this.limit); level++) {

                    // PRECONDITIONS

                    // Check for general apply conditions
                    // If the general apply condition fails, the reward can't be applied for any level, so we can return here.
                    if (!Reward.this.isApplicable(leveler)) {
                        return;
                    }

                    // Continue with next level when current level does not apply.
                    // This ensures that the event is applied for all levels that fulfill the condition between level 1 and the player's level.
                    if (!Reward.this.checkApplyCondition(leveler, level)) {
                        continue;
                    }

                    // APPLY REWARD

                    // Call apply event before applying the reward
                    RewardApplyEvent event = new RewardApplyEvent(leveler, Reward.this, level);
                    Bukkit.getPluginManager().callEvent(event);

                    if (event.getResult().isApplied()) {

                        // The event is applied when the event result is either APPLY or APPLY_SKIP

                        boolean success;

                        // Apply event and catch errors
                        try {
                            success = Reward.this.executor.onApply(Reward.this, leveler, level);
                        } catch (Exception e) {
                            Reward.this.getManager().getPlugin().getLogger().log(Level.WARNING, "Exception while applying reward " + Reward.this.id + " to player " + leveler.getPlayerUUID(), e);
                            return;
                        }

                        // Do not mark the event as applied when it was unsuccessful
                        if (!success) {
                            Reward.this.getManager().getPlugin().getLogger().log(Level.WARNING, "Failed to apply reward " + Reward.this.id + " to player " + leveler.getPlayerUUID() + ": Executor returned failure");
                            return;
                        }

                    }

                    // SUCCESS

                    if (event.getResult().isMarkedAsApplied()) {

                        // The reward is marked successful when the event result is APPLY or CANCEL_MARK_APPLIED

                        Reward.this.onApplySuccess(leveler, level);

                        // Call applied event
                        Bukkit.getPluginManager().callEvent(new RewardAppliedEvent(leveler, Reward.this, level));

                    }

                }

            }
        }.runTask(this.manager.getPlugin());

    }

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

    public final @NotNull String getDescription() {
        return description;
    }

    public final boolean isEnabled() {
        return enabled;
    }

    public final void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}

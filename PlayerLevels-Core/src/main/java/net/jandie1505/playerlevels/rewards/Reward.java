package net.jandie1505.playerlevels.rewards;

import net.jandie1505.playerlevels.api.PlayerReward;
import net.jandie1505.playerlevels.events.RewardApplyEvent;
import net.jandie1505.playerlevels.leveler.Leveler;
import net.jandie1505.playerlevels.leveler.ReceivedReward;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class Reward implements PlayerReward {
    @NotNull private final RewardsManager manager;
    @NotNull private final String id;
    @Nullable private final String serverId;
    private final int level;
    @NotNull private final RewardExecutor executor;
    @NotNull private final RewardCondition condition;
    private final boolean requireOnlinePlayer;
    @NotNull private final String name;
    @NotNull private final String description;
    private boolean enabled;

    protected Reward(
            @NotNull RewardsManager manager,
            @NotNull String id,
            @Nullable String serverId,
            int level,
            @NotNull RewardExecutor executor,
            @Nullable RewardCondition condition,
            boolean requireOnlinePlayer,
            @NotNull String name,
            @Nullable String description
    ) {
        this.manager = manager;
        this.id = id;
        this.serverId = serverId;
        this.level = level;
        this.executor = executor;
        this.condition = condition != null ? condition : RewardCondition.DEFAULT;
        this.requireOnlinePlayer = requireOnlinePlayer;
        this.name = name;
        this.description = description != null ? description : "";
        this.enabled = true;
    }

    protected Reward(@NotNull RewardsManager manager, @NotNull RewardConfig config, @NotNull RewardData data) {
        this(manager, config.id(), config.serverId(), config.level(), data.executor(), data.condition(), data.requiresOnlinePlayer(), config.name(), config.description());
    }

    // ----- REWARD -----

    /**
     * Applies the event if all conditions are met.
     * @param leveler leveler
     * @return success
     */
    @SuppressWarnings("UnusedReturnValue")
    public final CompletableFuture<Boolean> apply(@NotNull Leveler leveler) {
        if (!this.isApplicable(leveler)) return CompletableFuture.completedFuture(false); // Check conditions

        CompletableFuture<Boolean> future = new CompletableFuture<>();

        new BukkitRunnable() {
            @Override
            public void run() {

                // Fire event and don't apply the reward when it's cancelled
                RewardApplyEvent event = new RewardApplyEvent(leveler, Reward.this);
                Bukkit.getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    future.complete(false);
                    return;
                }

                boolean success;

                // Apply event and catch errors
                try {
                    success = Reward.this.executor.onApply(Reward.this, leveler);
                } catch (Exception e) {
                    Reward.this.getManager().getPlugin().getLogger().log(Level.WARNING, "Exception while applying reward " + Reward.this.id + " to player " + leveler.getPlayerUUID(), e);
                    future.complete(false);
                    return;
                }

                // Do not mark the event as applied when it was unsuccessful
                if (!success) {
                    Reward.this.getManager().getPlugin().getLogger().log(Level.WARNING, "Failed to apply reward " + Reward.this.id + " to player " + leveler.getPlayerUUID() + ": Executor returned null");
                    future.complete(false);
                    return;
                }

                leveler.getData().getOrCreateReceivedReward(Reward.this.id, false); // Mark as applied

            }
        }.runTask(this.manager.getPlugin());

        return future;
    }

    /**
     * Checks if the reward can be applied.
     * @param leveler leveler
     * @return applicable
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public final boolean isApplicable(@NotNull Leveler leveler) {
        if (!this.enabled) return false;
        if (leveler.getData().level() < this.level) return false;
        if (this.requireOnlinePlayer && Bukkit.getPlayer(leveler.getPlayerUUID()) == null) return false;
        if (this.serverId != null && !this.serverId.equals(this.manager.getPlugin().getServerId())) return false; // Wrong server
        return !this.condition.isApplied(this, leveler); // Already applied
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

    public final int getLevel() {
        return level;
    }

    public final boolean requiresOnlinePlayer() {
        return requireOnlinePlayer;
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

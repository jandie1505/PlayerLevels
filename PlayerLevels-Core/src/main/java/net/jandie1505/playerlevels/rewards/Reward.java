package net.jandie1505.playerlevels.rewards;

import net.jandie1505.playerlevels.api.PlayerReward;
import net.jandie1505.playerlevels.events.RewardApplyEvent;
import net.jandie1505.playerlevels.leveler.Leveler;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public abstract class Reward implements PlayerReward {
    @NotNull private final RewardsManager manager;
    @NotNull private final String id;
    @Nullable private final String serverId;
    @NotNull private final RewardExecutor executor;
    private final boolean requireOnlinePlayer;
    @NotNull private final String name;
    @NotNull private final String description;
    private boolean enabled;

    protected Reward(
            @NotNull RewardsManager manager,
            @NotNull String id,
            @Nullable String serverId,
            @NotNull RewardExecutor executor,
            boolean requireOnlinePlayer,
            @NotNull String name,
            @Nullable String description
    ) {
        this.manager = manager;
        this.id = id;
        this.serverId = serverId;
        this.executor = executor;
        this.requireOnlinePlayer = requireOnlinePlayer;
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

                boolean failure = false;

                int maxIterations = 100;
                while (maxIterations-- > 0) {

                    boolean success;

                    // Apply event and catch errors
                    try {
                        success = Reward.this.executor.onApply(Reward.this, leveler);
                    } catch (Exception e) {
                        Reward.this.getManager().getPlugin().getLogger().log(Level.WARNING, "Exception while applying reward " + Reward.this.id + " to player " + leveler.getPlayerUUID(), e);
                        future.complete(false);
                        failure = true;
                        break;
                    }

                    // Do not mark the event as applied when it was unsuccessful
                    if (!success) {
                        Reward.this.getManager().getPlugin().getLogger().log(Level.WARNING, "Failed to apply reward " + Reward.this.id + " to player " + leveler.getPlayerUUID() + ": Executor returned null");
                        future.complete(false);
                        failure = true;
                        break;
                    }

                    Reward.this.onApplySuccess(leveler);

                    if (!Reward.this.isApplicable(leveler)) {
                        break;
                    }

                }

                if (maxIterations <= 0) {
                    Reward.this.getManager().getPlugin().getLogger().warning(
                            "Reward " + Reward.this.id + " has exceeded the maximum number of iterations\n"
                    );
                    future.complete(false);
                    return;
                }

                if (failure) {
                    future.complete(false);
                    Reward.this.getManager().getPlugin().getLogger().warning("Reward " + Reward.this.id + " could not be applied to player " + leveler.getPlayerUUID() + ". Check for warnings/errors before this log message.");
                    return;
                }

                future.complete(true);
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
        if (!this.enabled) return false; // Not applicable when reward disabled
        if (leveler.getData().getOrCreateReceivedReward(this.id, false).blocked()) return false; // Not applicable when blocked
        if (this.requireOnlinePlayer && Bukkit.getPlayer(leveler.getPlayerUUID()) == null) return false; // not applicable when player required online but is offline
        if (this.serverId != null && !this.serverId.equals(this.manager.getPlugin().getServerId())) return false; // Wrong server
        return this.checkApplyCondition(leveler); // Applicable when apply condition of subclass is successful
    }

    // ----- ABSTRACT -----

    public abstract boolean checkApplyCondition(@NotNull Leveler leveler);

    /**
     * Is called after the reward has been successfully applied.
     * @param leveler leveler
     */
    public void onApplySuccess(@NotNull Leveler leveler) {
        leveler.getData().getOrCreateReceivedReward(Reward.this.id, false);
        //ReceivedReward reward = leveler.getData().getOrCreateReceivedReward(Reward.this.id, false);
        //reward.level(reward.level(), false); TODO
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

package net.jandie1505.playerlevels.rewards;

import net.jandie1505.playerlevels.PlayerLevels;
import net.jandie1505.playerlevels.api.PlayerReward;
import net.jandie1505.playerlevels.api.RewardManager;
import net.jandie1505.playerlevels.leveler.Leveler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class RewardsManager implements RewardManager {
    @NotNull private final PlayerLevels plugin;
    @NotNull private final Map<String, Reward> rewards;

    public RewardsManager(@NotNull PlayerLevels plugin) {
        this.plugin = plugin;
        this.rewards = new HashMap<>();
    }

    // ----- PROCESS PLAYER -----

    /**
     * Give the player all the rewards for which they fulfills the condition.
     * @param leveler leveller
     */
    public void processPlayer(@NotNull Leveler leveler) {

        for (Map.Entry<String, Reward> entry : this.rewards.entrySet()) {

            try {
                if (!entry.getValue().isApplicable(leveler)) continue;
                entry.getValue().apply(leveler);
            } catch (Exception e) {
                this.plugin.getLogger().log(Level.SEVERE, "Failed to check and apply reward " + entry.getKey() + " for player " + leveler.getPlayerUUID(), e);
            }

        }

    }

    // ----- CREATE REWARDS -----

    @SuppressWarnings("UnusedReturnValue")
    public @NotNull MilestoneReward addMilestoneReward(@NotNull RewardConfig config, @NotNull MilestoneRewardData data) {
        MilestoneReward reward = new MilestoneReward(this, config, data, data.level());
        this.addReward(reward);
        return reward;
    }

    @SuppressWarnings("UnusedReturnValue")
    public @NotNull IntervalReward addIntervalReward(@NotNull RewardConfig config, @NotNull IntervalRewardData data) {
        IntervalReward reward = new IntervalReward(this, config, data, data.level());
        this.addReward(reward);
        return reward;
    }

    // ----- MANAGE REWARDS -----

    public final @NotNull Map<String, PlayerReward> getRewards() {
        return Map.copyOf(this.rewards);
    }

    public final void addReward(@NotNull Reward reward) {
        if (this.rewards.containsKey(reward.getId())) throw new IllegalArgumentException("Reward with id " + reward.getId() + " already exists");
        this.rewards.put(reward.getId(), reward);
    }

    public final void removeReward(@NotNull String rewardId) {
        this.rewards.remove(rewardId);
    }

    public final Reward getReward(@NotNull String rewardId) {
        return this.rewards.get(rewardId);
    }

    public final @NotNull Map<String, Reward> getRewardsInternal() {
        return this.rewards;
    }

    // ----- OTHER -----

    public final @NotNull PlayerLevels getPlugin() {
        return plugin;
    }

}

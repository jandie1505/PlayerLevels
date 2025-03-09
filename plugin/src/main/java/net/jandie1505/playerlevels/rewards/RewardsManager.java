package net.jandie1505.playerlevels.rewards;

import net.jandie1505.playerlevels.PlayerLevels;
import net.jandie1505.playerlevels.api.PlayerReward;
import net.jandie1505.playerlevels.api.RewardManager;
import net.jandie1505.playerlevels.leveler.Leveler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

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

        for (Reward reward : this.rewards.values()) {
            if (!reward.isApplicable(leveler)) continue;
            reward.apply(leveler);
        }

    }

    // ----- CREATE REWARDS -----

    public @NotNull Reward addReward(@NotNull RewardConfig rewardConfig, @NotNull RewardData rewardData) {
        Reward reward = new Reward(this, rewardConfig, rewardData);
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

package net.jandie1505.playerlevels.rewards;

import net.jandie1505.playerlevels.rewards.types.CommandReward;
import net.jandie1505.playerlevels.rewards.types.LuckPermsPermissionReward;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class RewardsRegistry implements net.jandie1505.playerlevels.api.reward.RewardsRegistry {
    @NotNull private final Map<String, RewardCreator> creators;

    public RewardsRegistry() {
        this.creators = new HashMap<>();

        this.creators.put("command", new CommandReward.Creator());
        this.creators.put("luckperms", new LuckPermsPermissionReward.Creator());
    }

    // ----- MANAGE CREATORS -----

    public @NotNull Map<String, RewardCreator> getCreators() {
        return Map.copyOf(this.creators);
    }

    public @Nullable RewardCreator getCreator(@NotNull String type) {
        return this.creators.get(type);
    }

    public void registerCreator(@NotNull String type, @NotNull RewardCreator creator) {
        if (this.creators.containsKey(type)) throw new IllegalArgumentException("Type " + type + " already exists");
        this.creators.put(type, creator);
    }

    public void unregisterCreator(@NotNull String type) {
        this.creators.remove(type);
    }

}

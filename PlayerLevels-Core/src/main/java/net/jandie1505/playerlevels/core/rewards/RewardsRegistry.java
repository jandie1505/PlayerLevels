package net.jandie1505.playerlevels.core.rewards;

import net.chaossquad.mclib.storage.DSSerializer;
import net.chaossquad.mclib.storage.DataStorage;
import net.jandie1505.playerlevels.core.PlayerLevels;
import net.jandie1505.playerlevels.core.constants.DefaultConfigValues;
import net.jandie1505.playerlevels.core.rewards.types.CommandReward;
import net.jandie1505.playerlevels.core.rewards.types.LuckPermsPermissionReward;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

public final class RewardsRegistry implements net.jandie1505.playerlevels.api.core.reward.RewardsRegistry {
    @NotNull private final PlayerLevels plugin;
    @NotNull private final Map<String, RewardCreator> creators;

    public RewardsRegistry(@NotNull PlayerLevels plugin) {
        this.plugin = plugin;
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

    // ----- CREATE REWARDS FROM CONFIG -----

    public void createRewardsFromConfig(boolean clearExisting) {
        DataStorage config = new DataStorage();

        if (clearExisting) this.plugin.getRewardsManager().getRewardsInternal().clear();

        try {

            DataStorage loadedStorage = DSSerializer.loadConfig(new File(this.plugin.getDataFolder(), "rewards.yml"));
            if (loadedStorage != null) {
                config.merge(loadedStorage);
                this.plugin.getLogger().info("Rewards config loaded successfully");
            } else {
                config.merge(DefaultConfigValues.getRewards());
                DSSerializer.saveConfig(config, new File(this.plugin.getDataFolder(), "rewards.yml"));
                this.plugin.getLogger().info("Rewards config created successfully");
            }

        } catch (Exception e) {
            this.plugin.getLogger().log(Level.WARNING, "Failed to load rewards config.", e);
        }

        for (Map.Entry<String, DataStorage> entry : config.getSections().entrySet()) {
            String id = entry.getKey();
            DataStorage section = entry.getValue();

            String type = section.optString("type", null);
            if (type == null) {
                this.plugin.getLogger().warning("No reward type specified for " + id);
                continue;
            }

            RewardCreator creator = this.getCreator(type);
            if (creator == null) {
                this.plugin.getLogger().warning("Could not find creator for id " + id);
                continue;
            }

            RewardConfig rewardConfig = new RewardConfig(
                    id,
                    section.optString("server_id", null),
                    Objects.requireNonNull(section.optString("name", "")),
                    Objects.requireNonNull(section.optString("description", ""))
            );

            try {

                switch (section.optString("apply_type", null)) {
                    case "milestone" -> {
                        MilestoneRewardData data = creator.createMilestoneReward(section);

                        if (data == null) {
                            this.plugin.getLogger().warning("Creator of milestone reward " + id + " returned null. This can be caused by configuration errors.");
                            continue;
                        }

                        this.plugin.getRewardsManager().removeReward(id);
                        this.plugin.getRewardsManager().addMilestoneReward(rewardConfig, data);

                    }
                    case "interval" -> {
                        IntervalRewardData data = creator.createIntervalReward(section);

                        if (data == null) {
                            this.plugin.getLogger().warning("Creator of interval reward " + id + " returned null. This can be caused by configuration errors.");
                            continue;
                        }

                        this.plugin.getRewardsManager().removeReward(id);
                        this.plugin.getRewardsManager().addIntervalReward(rewardConfig, data);
                    }
                    case null, default -> {
                        this.plugin.getLogger().warning("Unknown reward type " + section.optString("type", "null"));
                        continue;
                    }
                }

            } catch (Exception e) {
                this.plugin.getLogger().log(Level.WARNING, "Exception while creating reward from config for id " + id, e);
                continue;
            }

        }

    }

    // ----- OTHER -----

    public @NotNull PlayerLevels getPlugin() {
        return this.plugin;
    }

}

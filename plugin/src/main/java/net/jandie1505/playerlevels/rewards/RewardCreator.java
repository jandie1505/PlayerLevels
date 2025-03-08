package net.jandie1505.playerlevels.rewards;

import net.jandie1505.playerlevels.api.RewardBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RewardCreator implements RewardBuilder {
    @NotNull private final RewardsManager manager;
    @NotNull private final String id;
    private String serverId;
    private int level;
    private RewardExecutor executor;
    private RewardCondition condition;
    private boolean requiresOnlinePlayer;
    private String name;
    private String description;
    private boolean enabled;

    public RewardCreator(@NotNull RewardsManager manager, @NotNull String id) {
        this.manager = manager;
        this.id = id;
        this.serverId = null;
        this.level = -1;
        this.executor = null;
        this.condition = null;
        this.requiresOnlinePlayer = true;
        this.name = null;
        this.description = null;
        this.enabled = true;
    }

    public RewardCreator restrictToServer(String serverId) {
        this.serverId = serverId;
        return this;
    }

    public RewardCreator onLevel(int level) {
        this.level = level;
        return this;
    }

    public RewardCreator withExecutor(@NotNull RewardExecutor executor) {
        this.executor = executor;
        return this;
    }

    public RewardCreator withCondition(@NotNull RewardCondition condition) {
        this.condition = condition;
        return this;
    }

    public RewardCreator requiresOnlinePlayer(boolean requiresOnlinePlayer) {
        this.requiresOnlinePlayer = requiresOnlinePlayer;
        return this;
    }

    public RewardCreator withName(@Nullable String name) {
        this.name = name;
        return this;
    }

    public RewardCreator withDescription(@Nullable String description) {
        this.description = description;
        return this;
    }

    public RewardCreator enabledByDefault(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public Reward create() {
        if (level < 0) throw new IllegalArgumentException("Level must be a positive integer");
        if (executor == null) throw new IllegalArgumentException("No executor provided");
        if (condition == null) condition = RewardCondition.DEFAULT;

        Reward reward = new Reward(this.manager, this.id, this.serverId, this.level, this.executor, this.condition, this.requiresOnlinePlayer, this.name, this.description);
        if (this.manager.getReward(this.id) != null) throw new IllegalArgumentException("Reward with id " + this.id + " already exists");
        reward.setEnabled(this.enabled);
        this.manager.addReward(reward);
        return reward;
    }

}

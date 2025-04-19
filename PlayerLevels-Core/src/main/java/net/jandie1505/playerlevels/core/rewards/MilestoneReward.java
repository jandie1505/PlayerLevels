package net.jandie1505.playerlevels.core.rewards;

import net.jandie1505.playerlevels.core.leveler.Leveler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;

public class MilestoneReward extends Reward implements net.jandie1505.playerlevels.api.core.reward.MilestoneReward {
    private final int level;
    @NotNull private final RewardCondition condition;

    protected MilestoneReward(@NotNull RewardsManager manager, @NotNull String id, @Nullable String serverId, int level, @NotNull RewardExecutor executor, @Nullable RewardCondition condition, boolean requireOnlinePlayer, @NotNull String name, @Nullable String description) {
        super(manager, id, serverId, executor, requireOnlinePlayer, level, name, description);
        this.level = level;
        this.condition = condition != null ? condition : net.jandie1505.playerlevels.api.core.reward.MilestoneReward.DEFAULT_CONDITION;
    }

    protected MilestoneReward(@NotNull RewardsManager manager, @NotNull RewardConfig config, @NotNull MilestoneRewardData data) {
        this(manager, config.id(), config.serverId(), data.level(), data.executor(), data.condition(), data.requiresOnlinePlayer(), config.name(), config.description());
    }

    @Override
    public boolean checkApplyCondition(@NotNull Leveler leveler, int checkedLevel) {

        // If the checked level is the required level, the reward will is applied.
        // This does two things.
        // First, it limits a success of the apply condition to one level (the required level of this reward).
        // This is the case because the apply method of the Reward checks for all levels until the player's level.
        // Second, it checks if the player meets level requirement for this reward, since only levels from 1 to the player's level are checked.
        if (checkedLevel != this.level) {
            return false;
        }

        // Check if the reward has already been applied with the custom condition
        try {
            return !this.condition.isApplied(this, leveler, checkedLevel);
        } catch (Exception e) {
            MilestoneReward.this.getManager().getPlugin().getLogger().log(Level.WARNING, "Exception while applying reward " + MilestoneReward.this.getId() + " to player " + leveler.getPlayerUUID(), e);
            return false;
        } catch (Throwable t) {
            MilestoneReward.this.setEnabled(false);
            MilestoneReward.this.getManager().getPlugin().getLogger().log(Level.SEVERE,
                    "A throwable which is not an exception has been thrown in isApplied from reward " + MilestoneReward.this.getId() + " to player " + leveler.getPlayerUUID() + " " +
                            "The reward has been disabled for safety reasons. DO NOT IGNORE THIS!",
                    t
            );
            return false;
        }
    }

    /**
     * Returns true if the reward has already been applied.
     * @param leveler leveler
     * @return already applied
     */
    public boolean isApplied(@NotNull Leveler leveler) {
        return this.condition.isApplied(this, leveler, this.level);
    }

    /**
     * Returns true if the reward has already been applied.
     * @param leveler leveler
     * @return already applied
     */
    public boolean isApplied(@NotNull net.jandie1505.playerlevels.api.core.level.Leveler leveler) {
        return this.isApplied((Leveler) leveler);
    }

    @Override
    public int getLevel() {
        return this.level;
    }

    @Override
    public void onApplySuccess(@NotNull Leveler leveler, int checkedLevel) {
        leveler.getData().getOrCreateReceivedReward(this.getId()).level(leveler.getData().level());
    }

}

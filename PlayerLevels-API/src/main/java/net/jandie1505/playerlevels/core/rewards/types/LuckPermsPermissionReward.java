package net.jandie1505.playerlevels.core.rewards.types;

import net.chaossquad.mclib.storage.DataStorage;
import net.jandie1505.playerlevels.api.core.level.Leveler;
import net.jandie1505.playerlevels.api.core.reward.Reward;
import net.jandie1505.playerlevels.core.rewards.*;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A reward that gives a permission to a player when it is applied.<br/>
 * It also checks if the permission is still applied, even when the reward is already applied.<br/>
 * If the permission has been removed, it is re-applied.
 */
public class LuckPermsPermissionReward implements RewardExecutor, RewardCondition {
    @NotNull private final String permission;

    /**
     * Creates a LuckPermsPermissionReward.
     * @param permission permission to check for
     */
    private LuckPermsPermissionReward(@NotNull String permission) {
        this.permission = permission;
    }

    @Override
    public boolean isApplied(@NotNull Reward reward, @NotNull Leveler player, int checkedLevel) {

        User user = LuckPermsProvider.get().getUserManager().getUser(player.getPlayerUUID());
        if (user == null) return false;

        return user.getNodes().stream().anyMatch(node -> node.getKey().equalsIgnoreCase(this.permission));
    }

    @Override
    public boolean onApply(@NotNull Reward reward, @NotNull Leveler player, int level) {

        User user = LuckPermsProvider.get().getUserManager().getUser(player.getPlayerUUID());
        if (user == null) return false;

        user.data().add(Node.builder(this.permission).build());
        return true;
    }

    /**
     * Returns the permission.
     * @return permission
     */
    public @NotNull String getPermission() {
        return permission;
    }

    /**
     * Creates a new milestone LuckPermsPermissionReward.
     * @param permission permission to check for
     * @param level level to check for
     * @return data of created reward
     */
    public static MilestoneRewardData createMilestoneRewardData(String permission, int level) {
        LuckPermsPermissionReward reward = new LuckPermsPermissionReward(permission);
        return new MilestoneRewardData(reward, reward, true, level);
    }

    /**
     * Creates a new interval LuckPermsPermissionReward.
     * @param permission permission to check for
     * @param start start
     * @param interval interval
     * @param limit limit
     * @return data of created reward
     * @deprecated Useless as an interval reward. Use {@link LuckPermsPermissionReward#createMilestoneRewardData(String, int)} instead.
     */
    @Deprecated
    public static IntervalRewardData createIntervalRewardData(String permission, int start, int interval, int limit) {
        LuckPermsPermissionReward reward = new LuckPermsPermissionReward(permission);
        return new IntervalRewardData(reward, reward, true, start, interval, limit);
    }

    /**
     * Creates a new LuckPermsPermissionReward from config.
     */
    public static class Creator implements RewardCreator {

        /**
         * Creates the creator.
         */
        public Creator() {}

        @Override
        public @Nullable MilestoneRewardData createMilestoneReward(@NotNull DataStorage data) {

            String permission = data.optString("permission", null);
            if (permission == null) throw new NullPointerException("permission is null");

            return createMilestoneRewardData(
                    permission,
                    data.optInt("level", 0)
            );
        }

        @Override
        @Deprecated
        public @Nullable IntervalRewardData createIntervalReward(@NotNull DataStorage data) {

            String permission = data.optString("permission", null);
            if (permission == null) throw new NullPointerException("permission is null");

            return createIntervalRewardData(
                    permission,
                    data.optInt("start", 1),
                    data.optInt("interval", 1),
                    data.optInt("limit", 1)
            );
        }
    }

}

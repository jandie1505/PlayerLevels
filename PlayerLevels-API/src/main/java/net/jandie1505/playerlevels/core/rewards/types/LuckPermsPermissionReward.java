package net.jandie1505.playerlevels.core.rewards.types;

import net.chaossquad.mclib.storage.DataStorage;
import net.jandie1505.playerlevels.api.core.level.Leveler;
import net.jandie1505.playerlevels.api.core.reward.Reward;
import net.jandie1505.playerlevels.core.rewards.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
public class LuckPermsPermissionReward implements RewardExecutor, RewardCondition, RewardDescriptionProvider {
    @NotNull private final String permission;
    @Nullable private final Component description;

    /**
     * Creates a LuckPermsPermissionReward.
     * @param permission permission to check for
     */
    private LuckPermsPermissionReward(@NotNull String permission, @Nullable Component description) {
        this.permission = permission;
        this.description = description;
    }

    @Override
    public boolean isApplied(@NotNull Reward reward, @NotNull Leveler player, int checkedLevel) {

        try {
            Class.forName("net.luckperms.api.LuckPermsProvider");
        } catch (ClassNotFoundException e) {
            return false;
        }

        User user = LuckPermsProvider.get().getUserManager().getUser(player.getPlayerUUID());
        if (user == null) return false;

        return user.getNodes().stream().anyMatch(node -> node.getKey().equalsIgnoreCase(this.permission));
    }

    @Override
    public boolean onApply(@NotNull Reward reward, @NotNull Leveler player, int level) {

        try {
            Class.forName("net.luckperms.api.LuckPermsProvider");
        } catch (ClassNotFoundException e) {
            return false;
        }

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

    @Override
    public @Nullable Component getDescription(int level) {
        return this.description;
    }

    /**
     * Creates a new milestone LuckPermsPermissionReward.
     * @param description reward description
     * @param permission permission to check for
     * @param level level to check for
     * @return data of created reward
     */
    public static MilestoneRewardData createMilestoneRewardData(@NotNull String permission, @Nullable Component description, int level) {
        LuckPermsPermissionReward reward = new LuckPermsPermissionReward(permission, description);
        return new MilestoneRewardData(reward, reward, reward, true, level);
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

            String description = data.optString("description", null);

            return createMilestoneRewardData(
                    permission,
                    description != null ? MiniMessage.miniMessage().deserialize(description) : Component.empty(),
                    data.optInt("level", 0)
            );
        }
    }

}

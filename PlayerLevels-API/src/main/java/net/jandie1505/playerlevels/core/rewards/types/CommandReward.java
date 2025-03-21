package net.jandie1505.playerlevels.core.rewards.types;

import net.chaossquad.mclib.storage.DataStorage;
import net.jandie1505.playerlevels.api.core.level.Leveler;
import net.jandie1505.playerlevels.api.core.reward.IntervalReward;
import net.jandie1505.playerlevels.api.core.reward.MilestoneReward;
import net.jandie1505.playerlevels.api.core.reward.Reward;
import net.jandie1505.playerlevels.core.rewards.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This is a reward that executes a command when it is applied.<br/>
 * You can get the data using {@link CommandReward#createInterval(String, boolean, SenderType, int, int, int)}.
 */
public class CommandReward implements RewardExecutor {
    @NotNull private final String command;
    @NotNull private final SenderType senderType;

    private CommandReward(@NotNull String command, @NotNull SenderType senderType) {
        this.command = command;
        this.senderType = senderType;
    }

    @Override
    public boolean onApply(@NotNull Reward reward, @NotNull Leveler player, int level) {

        String cmd = this.command;

        // PLACEHOLDERS

        cmd = cmd.replace("{player_uuid}", player.getPlayerUUID().toString());
        cmd = cmd.replace("{player_level}", String.valueOf(player.getData().level()));
        cmd = cmd.replace("{player_xp}", String.valueOf(player.getData().xp()));

        cmd = cmd.replace("{player_reward_blocked}", String.valueOf(player.getData().getOrCreateReceivedReward(reward.getId()).blocked()));
        cmd = cmd.replace("{player_reward_level}", String.valueOf(level));
        cmd = cmd.replace("{player_reward_level_stored}", String.valueOf(player.getData().getOrCreateReceivedReward(reward.getId()).level()));

        cmd = cmd.replace("{reward_id}", reward.getId());
        cmd = cmd.replace("{reward_name}", reward.getName());
        cmd = cmd.replace("{reward_requires_online_player}", String.valueOf(reward.requiresOnlinePlayer()));
        cmd = cmd.replace("{reward_command}", this.command);

        if (reward instanceof MilestoneReward r) {
            cmd = cmd.replace("{reward_level}", String.valueOf(r.getLevel()));
        }

        if (reward instanceof IntervalReward r) {
            cmd = cmd.replace("{reward_interval_start}", String.valueOf(r));
            cmd = cmd.replace("{reward_interval}", String.valueOf(r.getInterval()));
        }

        Player bukkitPlayer = Bukkit.getPlayer(player.getPlayerUUID());
        cmd = cmd.replace("{player_name}", bukkitPlayer != null ? bukkitPlayer.getName() : " ");

        String serverId = reward.getServerId();
        cmd = cmd.replace("{reward_server_id}", serverId != null ? serverId : " ");

        // Prevents changing the command structure
        String description = reward.getDescription();
        cmd = cmd.replace("{reward_description}", description.isEmpty() ? " " : description);

        // RUN COMMAND

        switch (this.senderType) {
            case CONSOLE -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                return true;
            }
            case PLAYER -> {
                if (bukkitPlayer == null) return false;
                Bukkit.dispatchCommand(bukkitPlayer, cmd);
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    /**
     * The sender type of the command.<br/>
     * Either the player itself or the console.<br/>
     * Support for custom command senders is planned.
     */
    public enum SenderType {

        /**
         * Execute the command as the console.
         */
        CONSOLE,

        /**
         * Execute the command as the player.
         */
        PLAYER;

    }

    // ----- CREATE -----

    /**
     * Creates a milestone reward data for this command reward.<br/>
     * The reward can be registered at {@link net.jandie1505.playerlevels.api.core.reward.RewardsManager#addMilestoneReward(RewardConfig, MilestoneRewardData)}.
     * @param command the command that should be executed
     * @param requiresOnlinePlayer if the command requires the player to be online
     * @param senderType {@link SenderType}
     * @param level the level the reward should be applied
     * @return milestone reward data
     */
    public static MilestoneRewardData createMilestone(
            @NotNull String command,
            boolean requiresOnlinePlayer,
            @NotNull SenderType senderType,
            int level
    ) {
        return new MilestoneRewardData(new CommandReward(command, senderType), null, requiresOnlinePlayer, level);
    }

    /**
     * Creates an interval reward data for this command reward.<br/>
     * The reward data can be registered at {@link net.jandie1505.playerlevels.api.core.reward.RewardsManager#addIntervalReward(RewardConfig, IntervalRewardData)}.
     * @param command the command that should be executed
     * @param requiresOnlinePlayer if the command requires the player to be online
     * @param senderType {@link SenderType}
     * @param start interval start
     * @param interval interval in levels the player should get the reward applied
     * @param limit the reward will not be applied for levels higher/equal than this
     * @return interval reward data
     */
    public static IntervalRewardData createInterval(
            @NotNull String command,
            boolean requiresOnlinePlayer,
            SenderType senderType,
            int start,
            int interval,
            int limit
    ) {
        return new IntervalRewardData(new CommandReward(command, senderType), null, requiresOnlinePlayer, start, interval, limit);
    }

    // ----- CREATOR -----

    /**
     * Creates a new CommandReward from config.
     */
    public static class Creator implements RewardCreator {

        /**
         * Creates the creator.
         */
        public Creator() {}

        @Override
        public @Nullable MilestoneRewardData createMilestoneReward(@NotNull DataStorage data) {

            String command = data.optString("command", null);
            if (command == null) throw new IllegalArgumentException("command is null");

            return createMilestone(
                    command,
                    data.optBoolean("requires_online_player", true),
                    SenderType.valueOf(data.optString("sender_type", "").toUpperCase()),
                    data.optInt("level", 1)
            );
        }

        @Override
        public @Nullable IntervalRewardData createIntervalReward(@NotNull DataStorage data) {

            String command = data.optString("command", null);
            if (command == null) throw new IllegalArgumentException("command is null");

            return createInterval(
                    command,
                    data.optBoolean("requires_online_player", true),
                    SenderType.valueOf(data.optString("sender_type", "").toUpperCase()),
                    data.optInt("start", 1),
                    data.optInt("interval", 1),
                    data.optInt("limit", 1)
            );
        }
    }

}

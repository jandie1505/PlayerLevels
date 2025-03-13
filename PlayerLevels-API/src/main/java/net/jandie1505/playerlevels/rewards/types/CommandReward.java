package net.jandie1505.playerlevels.rewards.types;

import net.jandie1505.playerlevels.api.LevelPlayer;
import net.jandie1505.playerlevels.api.MilestonePlayerReward;
import net.jandie1505.playerlevels.api.PlayerReward;
import net.jandie1505.playerlevels.rewards.RewardCondition;
import net.jandie1505.playerlevels.rewards.RewardData;
import net.jandie1505.playerlevels.rewards.RewardExecutor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandReward implements RewardExecutor {
    @NotNull private final String command;
    @NotNull private final SenderType senderType;

    private CommandReward(@NotNull String command, @NotNull SenderType senderType) {
        this.command = command;
        this.senderType = senderType;
    }

    @Override
    public boolean onApply(@NotNull PlayerReward reward, @NotNull LevelPlayer player) {

        String cmd = this.command;

        // PLACEHOLDERS

        cmd = cmd.replace("<player_uuid>", player.getPlayerUUID().toString());
        cmd = cmd.replace("<reward_id>", reward.getId());
        if (reward instanceof MilestonePlayerReward r) cmd = cmd.replace("<reward_level>", String.valueOf(r.getLevel()));
        cmd = cmd.replace("<reward_name>", reward.getName());
        cmd = cmd.replace("<reward_requires_online_player>", String.valueOf(reward.requiresOnlinePlayer()));
        cmd = cmd.replace("<reward_command>", this.command);

        Player bukkitPlayer = Bukkit.getPlayer(player.getPlayerUUID());
        cmd = cmd.replace("<player_name>", bukkitPlayer != null ? bukkitPlayer.getName() : " ");

        String serverId = reward.getServerId();
        cmd = cmd.replace("<reward_server_id>", serverId != null ? serverId : " ");

        // Prevents changing the command structure
        String description = reward.getDescription();
        cmd = cmd.replace("<reward_description>", description.isEmpty() ? " " : description);

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

    public enum SenderType {
        CONSOLE,
        PLAYER;
    }

    // ----- CREATE -----

    public static RewardData create(
            @NotNull String command,
            boolean requiresOnlinePlayer,
            @NotNull SenderType senderType
    ) {
        return new RewardData(new CommandReward(command, senderType), MilestonePlayerReward.DEFAULT_CONDITION, requiresOnlinePlayer);
    }

}

package net.jandie1505.playerlevels.rewards;

import net.jandie1505.playerlevels.api.TemplateRewardCreator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TemplateRewardsCreator implements TemplateRewardCreator {
    @NotNull private final RewardsManager manager;
    
    public TemplateRewardsCreator(@NotNull RewardsManager manager) {
        this.manager = manager;
    }
    
    public @NotNull Reward createCommandReward(
            @NotNull String id,
            int level,
            @Nullable String serverId,
            @NotNull String command,
            @NotNull String name,
            @Nullable String description,
            boolean requireOnlinePlayer
    ) {

        RewardExecutor executor = (reward, player) -> {
            String cmd = command;
            cmd = cmd.replace("<player_uuid>", player.getPlayerUUID().toString());
            cmd = cmd.replace("<player_level>", String.valueOf(player.getData().level()));
            cmd = cmd.replace("<xp>", String.valueOf(player.getData().xp()));
            cmd = cmd.replace("<reward_level>", String.valueOf(level));
            cmd = cmd.replace("<reward_name>", String.valueOf(player.getData().xp()));
            cmd = cmd.replace("<reward_server_id>", serverId);
            cmd = cmd.replace("<reward_command>", command);
            cmd = cmd.replace("<reward_name>", name);
            cmd = cmd.replace("<reward_description>", description != null ? description : "");
            cmd = cmd.replace("<reward_require_online_player>", String.valueOf(requireOnlinePlayer));

            Player bukkitPlayer = Bukkit.getPlayer(player.getPlayerUUID());
            cmd = cmd.replace("<player_name>", bukkitPlayer != null ? bukkitPlayer.getName() : " ");

            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
            return true;
        };

        Reward reward = new Reward(this.manager, id, serverId, level, executor, RewardCondition.DEFAULT, requireOnlinePlayer, name, description);
        this.manager.addReward(reward);
        return reward;
    }
    
}

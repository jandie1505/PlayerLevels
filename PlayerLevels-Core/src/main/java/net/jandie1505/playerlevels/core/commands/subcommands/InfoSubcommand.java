package net.jandie1505.playerlevels.core.commands.subcommands;

import com.destroystokyo.paper.profile.PlayerProfile;
import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.playerlevels.core.PlayerLevels;
import net.jandie1505.playerlevels.core.commands.subcommands.utils.OptionParser;
import net.jandie1505.playerlevels.core.constants.ConfigKeys;
import net.jandie1505.playerlevels.core.constants.MessageKeys;
import net.jandie1505.playerlevels.core.constants.Permissions;
import net.jandie1505.playerlevels.core.leveler.Leveler;
import net.jandie1505.playerlevels.core.messages.TagResolvers;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InfoSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final PlayerLevels plugin;
    @NotNull private final ConcurrentHashMap<UUID, Long> rateLimiter;

    public InfoSubcommand(@NotNull PlayerLevels plugin) {
        this.plugin = plugin;
        this.rateLimiter = new ConcurrentHashMap<>();

        new BukkitRunnable() {
            @Override
            public void run() {
                InfoSubcommand.this.rateLimiter.entrySet().removeIf(entry -> Instant.now().getEpochSecond() > entry.getValue());
            }
        }.runTaskTimerAsynchronously(plugin, 30*20L, 30*20L);
    }

    // ----- COMMAND -----

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] a) {
        OptionParser.Result args = OptionParser.parse(a);

        if (args.args().length > 0) {
            this.otherPlayerInfo(sender, args);
        } else {
            this.ownPlayerInfo(sender, args);
        }

        return true;
    }

    // ----- OWN PLAYER INFO -----

    private void ownPlayerInfo(@NotNull CommandSender sender, @NotNull OptionParser.Result args) {

        if (!(sender instanceof Player player)) {
            sender.sendRichMessage("<red>You need to specify or be a player");
            return;
        }

        Leveler leveler = this.plugin.getLevelManager().getLeveler(player.getUniqueId());
        if (leveler == null) {
            sender.sendRichMessage("<red>There was an error loading your data. You currently can't view your level.");
            return;
        }

        sender.sendRichMessage(this.plugin.messages().optString(MessageKeys.INFO_OWN, ""), TagResolvers.leveler("leveler", leveler), TagResolvers.level("level", leveler.getData().level()));
    }

    // ----- OTHER PLAYER INFO -----

    private void otherPlayerInfo(@NotNull CommandSender sender, @NotNull OptionParser.Result args) {

        if (!Permissions.hasPermission(sender, Permissions.COMMAND_INFO_VIEW_OTHERS)) {
            sender.sendRichMessage("<red>You can't see the level of other players");
            return;
        }

        boolean loadLevelers = this.plugin.config().optBoolean(ConfigKeys.PLAYER_COMMANDS_LOAD_PLAYERS, false);
        int rateLimit = this.plugin.config().optInt(ConfigKeys.PLAYER_COMMANDS_DATABASE_RATE_LIMIT, 10);

        if (sender instanceof Player player && loadLevelers && rateLimit > 0) {
            Long time = this.rateLimiter.get(player.getUniqueId());

            if (time != null && Instant.now().getEpochSecond() < time) {
                sender.sendRichMessage("<red>Please wait a few seconds until you use this command again.");
                return;
            }

            this.rateLimiter.put(player.getUniqueId(), Instant.now().getEpochSecond() + rateLimit);
        }

        try {
            this.otherPlayerLoadLeveler(sender, UUID.fromString(args.args()[0]), loadLevelers);
        } catch (IllegalArgumentException e) {

            PlayerProfile profile = Bukkit.createProfile(args.args()[0]);
            if (profile.getId() != null) {
                this.otherPlayerLoadLeveler(sender, profile.getId(), loadLevelers);
            } else {
                this.plugin.getLevelManager().findLevelerByName(args.args()[0]).thenAccept(list -> this.otherPlayerLoadLeveler(sender, list.isEmpty() ? null : list.getFirst(), loadLevelers));
            }
        }
    }

    private void otherPlayerLoadLeveler(@NotNull CommandSender sender, @Nullable UUID uuid, boolean loadLevelers) {

        if (uuid == null) {
            this.otherPlayerLevelerRetrieved(sender, null);
            return;
        }

        if (loadLevelers) {
            this.plugin.getLevelManager().loadLeveler(uuid, false).thenAccept(leveler -> this.otherPlayerLevelerRetrieved(sender, leveler));
        } else {
            this.otherPlayerLevelerRetrieved(sender, this.plugin.getLevelManager().getLeveler(uuid));
        }

    }

    private void otherPlayerLevelerRetrieved(@NotNull CommandSender sender, @Nullable Leveler leveler) {
        new BukkitRunnable() {
            @Override
            public void run() {

                if (leveler == null) {
                    sender.sendRichMessage("<red>Player not found");
                    return;
                }

                sender.sendRichMessage(InfoSubcommand.this.plugin.messages().optString(MessageKeys.INFO_OTHERS, ""), TagResolvers.leveler("leveler", leveler));
            }
        }.runTask(this.plugin);
    }

    // ----- TAB COMPLETER -----

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (args.length == 1 && Permissions.hasPermission(sender, Permissions.COMMAND_INFO_VIEW_OTHERS)) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        }

        return List.of();
    }

    // ----- OTHER -----

    public @NotNull PlayerLevels getPlugin() {
        return plugin;
    }

}

package net.jandie1505.playerlevels.commands.subcommands;

import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.playerlevels.PlayerLevels;
import net.jandie1505.playerlevels.commands.subcommands.utils.OptionParser;
import net.jandie1505.playerlevels.constants.ConfigKeys;
import net.jandie1505.playerlevels.constants.MessageKeys;
import net.jandie1505.playerlevels.constants.Permissions;
import net.jandie1505.playerlevels.leveler.Leveler;
import net.jandie1505.playerlevels.messages.TagResolvers;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class InfoSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final PlayerLevels plugin;

    public InfoSubcommand(@NotNull PlayerLevels plugin) {
        this.plugin = plugin;
    }

    // ----- COMMAND -----

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] a) {

        if (!Permissions.hasPermission(sender, Permissions.USE)) {
            sender.sendRichMessage("<red>No permission");
            return true;
        }

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

        sender.sendRichMessage(this.plugin.messages().optString(MessageKeys.INFO_OWN, ""), TagResolvers.leveler("leveler", leveler));
    }

    // ----- OTHER PLAYER INFO -----

    private void otherPlayerInfo(@NotNull CommandSender sender, @NotNull OptionParser.Result args) {

        if (!Permissions.hasPermission(sender, Permissions.VIEW_OTHER)) {
            sender.sendRichMessage("<red>You can't see the level of other players");
            return;
        }

        UUID playerId;

        try {
            playerId = UUID.fromString(args.args()[0]);
        } catch (IllegalArgumentException e) {

            Player player = Bukkit.getPlayer(args.args()[0]);
            if (player != null) {
                playerId = player.getUniqueId();
            } else {
                playerId = null;
            }

        }

        if (playerId == null) {
            sender.sendRichMessage("<red>Player has not been found");
            return;
        }

        if (this.plugin.config().optBoolean(ConfigKeys.PLAYER_COMMANDS_LOAD_PLAYERS, false)) {
            this.plugin.getLevelManager().loadLeveler(playerId, false).thenAccept(leveler -> this.otherPlayerLevelerRetrieved(sender, leveler));
        } else {

            Leveler leveler = this.plugin.getLevelManager().getLeveler(playerId);
            if (leveler == null) {
                sender.sendRichMessage("<red>Player not found");
                return;
            }

            this.otherPlayerLevelerRetrieved(sender, leveler);
        }

    }

    private void otherPlayerLevelerRetrieved(@NotNull CommandSender sender, @NotNull Leveler leveler) {
        new BukkitRunnable() {
            @Override
            public void run() {
                sender.sendRichMessage(InfoSubcommand.this.plugin.messages().optString(MessageKeys.INFO_OTHERS, ""), TagResolvers.leveler("leveler", leveler));
            }
        }.runTask(this.plugin);
    }

    // ----- TAB COMPLETER -----

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return List.of();
    }

    // ----- OTHER -----

    public @NotNull PlayerLevels getPlugin() {
        return plugin;
    }

}

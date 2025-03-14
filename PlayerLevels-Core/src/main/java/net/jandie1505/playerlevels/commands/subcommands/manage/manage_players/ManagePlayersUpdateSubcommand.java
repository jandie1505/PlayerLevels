package net.jandie1505.playerlevels.commands.subcommands.manage.manage_players;

import net.chaossquad.mclib.PlayerUtils;
import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.playerlevels.PlayerLevels;
import net.jandie1505.playerlevels.commands.subcommands.utils.OptionParser;
import net.jandie1505.playerlevels.constants.Permissions;
import net.jandie1505.playerlevels.leveler.Leveler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ManagePlayersUpdateSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final PlayerLevels plugin;

    public ManagePlayersUpdateSubcommand(@NotNull PlayerLevels plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String lavel, @NotNull String @NotNull [] a) {

        if (!Permissions.hasPermission(sender, Permissions.MANAGE_PLAYERS)) {
            sender.sendRichMessage("<red>No permission");
            return true;
        }

        OptionParser.Result args = OptionParser.parse(a);

        if (args.args().length < 1) {
            sender.sendRichMessage("<red>Usage: /levels manage players update [--use-cache] <player>");
            return true;
        }

        UUID playerUUID = PlayerUtils.getPlayerUUIDFromString(args.args()[0]);
        if (playerUUID == null) {
            sender.sendMessage(Component.text("Player not found", NamedTextColor.RED));
            return true;
        }

        boolean cache = args.hasOption("use-cache");

        if (cache) {
            Leveler leveler = this.plugin.getLevelManager().getLeveler(playerUUID);

            if (leveler == null) {
                sender.sendRichMessage("<red>Leveler not cached");
                return true;
            }

            this.resultCallSync(sender, leveler);

        } else {
            this.loadLevelerWay(sender, playerUUID);
        }

        return true;
    }

    private void loadLevelerWay(@NotNull final CommandSender sender, @NotNull final UUID playerUUID) {
        this.plugin.getLevelManager().loadLeveler(playerUUID, true).thenAccept(leveler -> new BukkitRunnable() {
            @Override
            public void run() {
                ManagePlayersUpdateSubcommand.this.resultCallSync(sender, leveler);
            }
        }.runTask(this.plugin));
    }

    private void resultCallSync(@NotNull CommandSender sender, @NotNull Leveler leveler) {
        leveler.manageValuesAsync();
        sender.sendRichMessage("<green>Scheduled updating player values");
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String lavel, @NotNull String @NotNull [] args) {

        if (args.length == 1) {
            List<String> result = new ArrayList<>();
            result.add("--use-cache");
            result.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
            return result;
        }

        if (args.length == 2 && args[0].equals("--use-cache")) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        }

        return List.of();
    }

    public @NotNull PlayerLevels getPlugin() {
        return plugin;
    }

}

package net.jandie1505.playerlevels.commands.subcommands.manage.manage_players;

import net.jandie1505.playerlevels.PlayerLevels;
import net.jandie1505.playerlevels.commands.subcommands.utils.OptionParser;
import net.jandie1505.playerlevels.constants.Permissions;
import net.jandie1505.playerlevels.leveler.Leveler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ManagePlayersUpdateSubcommand extends ManagePlayersLevelerTemplateSubcommand {

    public ManagePlayersUpdateSubcommand(@NotNull PlayerLevels plugin) {
        super(plugin);
    }

    @Override
    protected Result onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, OptionParser.@NotNull Result args, @NotNull Leveler leveler) {
        leveler.manageValuesAsync();
        sender.sendRichMessage("<green>Scheduled updating player values");
        return new Result(true);
    }

    @Override
    protected boolean hasPermission(@NotNull CommandSender sender) {
        return Permissions.hasPermission(sender, Permissions.MANAGE_PLAYERS);
    }

    @Override
    protected void onInvalidSyntax(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, OptionParser.@NotNull Result args) {
        sender.sendRichMessage("<red>Usage: /levels manage players update <player>");
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String lavel, @NotNull String @NotNull [] args) {

        return switch (args.length) {
            case 1 -> Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
            case 2, 3 -> OptionParser.complete(sender, OptionParser.parse(args), Set.of("use-cache"), Map.of("push", (sender1, args1) -> List.of("false", "true")));
            default -> List.of();
        };

    }

}

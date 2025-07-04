package net.jandie1505.playerlevels.core.commands.subcommands.manage.manage_players;

import net.jandie1505.playerlevels.core.PlayerLevels;
import net.jandie1505.playerlevels.core.commands.subcommands.utils.OptionParser;
import net.jandie1505.playerlevels.core.constants.Permissions;
import net.jandie1505.playerlevels.core.leveler.Leveler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ManagePlayersProcessSubcommand extends ManagePlayersLevelerTemplateSubcommand {

    public ManagePlayersProcessSubcommand(@NotNull PlayerLevels plugin) {
        super(plugin);
    }

    @Override
    protected Result onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, OptionParser.@NotNull Result args, @NotNull Leveler leveler) {
        args.options().remove("no-process");
        sender.sendRichMessage("<green>Scheduled processing player values");
        return new Result(true, true);
    }

    @Override
    protected boolean hasPermission(@NotNull CommandSender sender) {
        return Permissions.hasPermission(sender, Permissions.MANAGE_PLAYERS);
    }

    @Override
    protected void onInvalidSyntax(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, OptionParser.@NotNull Result args) {
        sender.sendRichMessage("<red>Usage: /levels manage players process <player> [--use-cache|--push=(true|false)]");
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

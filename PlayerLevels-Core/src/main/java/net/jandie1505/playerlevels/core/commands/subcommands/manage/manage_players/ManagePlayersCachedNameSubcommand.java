package net.jandie1505.playerlevels.core.commands.subcommands.manage.manage_players;

import net.jandie1505.playerlevels.core.PlayerLevels;
import net.jandie1505.playerlevels.core.commands.subcommands.utils.OptionParser;
import net.jandie1505.playerlevels.core.constants.Permissions;
import net.jandie1505.playerlevels.core.leveler.Leveler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ManagePlayersCachedNameSubcommand extends ManagePlayersLevelerTemplateSubcommand {

    public ManagePlayersCachedNameSubcommand(@NotNull PlayerLevels plugin) {
        super(plugin);
    }

    @Override
    public Result onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, @NotNull final OptionParser.Result args, @NotNull final Leveler leveler) {

        if (args.args().length < 2) {
            this.onInvalidSyntax(sender, command, label, args);
            return Result.doNothing();
        }

        try {

            switch (args.args()[1].toLowerCase()) {
                case "get" -> {
                    sender.sendMessage(Component.text("Cached Name: " + leveler.getData().cachedName(), NamedTextColor.GRAY));
                    return Result.doNothing();
                }
                case "clear" -> {
                    leveler.getData().cachedName(null);
                    sender.sendMessage(Component.text("Cleared cached name", NamedTextColor.GRAY));
                    return new Result(true, false);
                }
                default -> {
                    this.onInvalidSyntax(sender, command, label, args);
                    return Result.doNothing();
                }
            }

        } catch (IllegalArgumentException e) {
            sender.sendRichMessage("<red>Illegal argument: " + e.getMessage());
            return Result.doNothing();
        }

    }

    @Override
    protected boolean hasPermission(@NotNull CommandSender sender) {
        return Permissions.hasPermission(sender, Permissions.MANAGE_PLAYERS);
    }

    @Override
    protected void onInvalidSyntax(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, OptionParser.@NotNull Result args) {
        sender.sendMessage(Component.text("Usage: /playerlevels manage players cached-name <player> (get|clear) [--use-cache|--push=(true|false)|--no-process]", NamedTextColor.RED));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return switch (args.length) {
            case 1 -> this.getPlugin().getServer().getOnlinePlayers().stream().map(Player::getName).toList();
            case 2 -> List.of("get", "clear");
            case 3, 4, 5 -> OptionParser.complete(sender, OptionParser.parse(args), Set.of("use-cache", "no-process"), Map.of("push", (sender1, args1) -> List.of("false", "true")));
            default -> List.of();
        };
    }
}

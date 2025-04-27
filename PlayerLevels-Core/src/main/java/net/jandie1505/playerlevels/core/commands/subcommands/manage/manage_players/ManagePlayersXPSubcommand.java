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

public class ManagePlayersXPSubcommand extends ManagePlayersLevelerTemplateSubcommand {

    public ManagePlayersXPSubcommand(@NotNull PlayerLevels plugin) {
        super(plugin);
    }

    @Override
    protected Result onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, OptionParser.@NotNull Result args, @NotNull Leveler leveler) {

        if (args.args().length < 2) {
            this.onInvalidSyntax(sender, command, label, args);
            return new Result(false);
        }

        try {

            switch (args.args()[1].toLowerCase()) {
                case "get" -> {
                    sender.sendMessage(Component.text("XP: " + leveler.getData().xp(), NamedTextColor.GRAY));
                    return new Result(false);
                }
                case "set" -> {

                    if (args.args().length > 2) {
                        double value = Double.parseDouble(args.args()[2]);
                        if (value < 0) value = 0;

                        leveler.getData().xp(value);
                        if (!args.hasOption("no-process")) leveler.processAsynchronously();
                        sender.sendMessage(Component.text("Updated xp to " + leveler.getData().xp(), NamedTextColor.GRAY));
                        return new Result(true);
                    } else {
                        sender.sendMessage(Component.text("You need to specify an amount of xp", NamedTextColor.RED));
                        return new Result(false);
                    }

                }
                case "give" -> {

                    if (args.args().length > 2) {
                        double value = leveler.getData().xp() + Double.parseDouble(args.args()[2]);
                        if (value < 0) value = 0;

                        leveler.getData().xp(value);
                        if (!args.hasOption("no-process")) leveler.processAsynchronously();
                        sender.sendMessage(Component.text("Updated xp to " + leveler.getData().xp(), NamedTextColor.GRAY));
                        return new Result(true);
                    } else {
                        sender.sendMessage(Component.text("You need to specify an amount of xp", NamedTextColor.RED));
                        return new Result(false);
                    }

                }
                case "take" -> {

                    if (args.args().length > 2) {
                        double value = leveler.getData().xp() - Double.parseDouble(args.args()[2]);
                        if (value < 0) value = 0;

                        leveler.getData().xp(value);
                        if (!args.hasOption("no-process")) leveler.processAsynchronously();
                        sender.sendMessage(Component.text("Updated xp to " + leveler.getData().xp(), NamedTextColor.GRAY));
                        return new Result(true);
                    } else {
                        sender.sendMessage(Component.text("You need to specify an amount of xp", NamedTextColor.RED));
                        return new Result(false);
                    }

                }
                default -> {
                    this.onInvalidSyntax(sender, command, label, args);
                    return new Result(false);
                }
            }

        } catch (IllegalArgumentException e) {
            sender.sendMessage(Component.text("Illegal argument", NamedTextColor.RED));
            return new Result(false);
        }

    }

    @Override
    protected boolean hasPermission(@NotNull CommandSender sender) {
        return Permissions.hasPermission(sender, Permissions.MANAGE_PLAYERS);
    }

    @Override
    protected void onInvalidSyntax(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, OptionParser.@NotNull Result args) {
        sender.sendMessage(Component.text("Usage: /playerlevels xp (get <player>|set <player> <xp>|give <player> <xp>|take <player> <xp>) [--use-cache|--push=(true|false)|--no-process]", NamedTextColor.RED));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return switch (args.length) {
            case 1 -> this.getPlugin().getServer().getOnlinePlayers().stream().map(Player::getName).toList();
            case 2 -> List.of("get", "set", "give", "take");
            case 3 -> {

                switch (args[1]) {
                    case "get" -> {
                        yield OptionParser.complete(sender, OptionParser.parse(args), Set.of("use-cache"), Map.of());
                    }
                    case "set" -> {
                        yield List.of("0", "100", "200", "300", "400", "500", "600", "700", "800", "900", "1000");
                    }
                    case "give", "take" -> {
                        yield List.of("10", "50", "100", "200", "300", "400", "500", "600", "700", "800", "900", "1000");
                    }
                    default -> {
                        yield List.of();
                    }
                }

            }
            case 4, 5, 6 -> {

                switch (args[1]) {
                    case "set", "give", "take" -> {
                        yield OptionParser.complete(sender, OptionParser.parse(args), Set.of("use-cache", "no-process"), Map.of("push", (sender1, args1) -> List.of("false", "true")));
                    }
                    default -> {
                        yield List.of();
                    }
                }

            }
            default -> List.of();
        };
    }
}

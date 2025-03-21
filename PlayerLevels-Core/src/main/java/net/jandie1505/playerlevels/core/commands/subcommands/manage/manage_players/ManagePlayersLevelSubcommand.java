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

public class ManagePlayersLevelSubcommand extends ManagePlayersLevelerTemplateSubcommand {

    public ManagePlayersLevelSubcommand(@NotNull PlayerLevels plugin) {
        super(plugin);
    }

    @Override
    public Result onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, @NotNull final OptionParser.Result args, @NotNull final Leveler leveler) {

        if (args.args().length < 2) {
            this.onInvalidSyntax(sender, command, label, args);
            return new Result(false);
        }

        try {

            switch (args.args()[1].toLowerCase()) {
                case "get" -> {
                    sender.sendMessage(Component.text("Level: " + leveler.getData().level(), NamedTextColor.GRAY));
                    return new Result(false);
                }
                case "set" -> {

                    if (args.args().length > 2) {
                        int level = Integer.parseInt(args.args()[2]);
                        if (level < 1) {
                            sender.sendMessage(Component.text("Level must be a positive (or zero) integer", NamedTextColor.RED));
                            return new Result(false);
                        }

                        leveler.getData().level(level);
                        if (!args.hasOption("no-update")) leveler.processAsynchronously();
                        sender.sendMessage(Component.text("Updated level to " + leveler.getData().level(), NamedTextColor.GRAY));
                        return new Result(true);
                    } else {
                        sender.sendMessage(Component.text("You need to specify a level", NamedTextColor.RED));
                        return new Result(false);
                    }

                }
                default -> {
                    this.onInvalidSyntax(sender, command, label, args);
                    return new Result(false);
                }
            }

        } catch (IllegalArgumentException e) {
            sender.sendRichMessage("<red>Illegal argument: " + e.getMessage());
            return new Result(false);
        }

    }

    @Override
    protected boolean hasPermission(@NotNull CommandSender sender) {
        return Permissions.hasPermission(sender, Permissions.MANAGE_PLAYERS);
    }

    @Override
    protected void onInvalidSyntax(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, OptionParser.@NotNull Result args) {
        sender.sendMessage(Component.text("Usage: /playerlevels manage players level (get <player>|set <level> <level>) [--use-cache|--push=(true|false)|--no-update]", NamedTextColor.RED));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return switch (args.length) {
            case 1 -> this.getPlugin().getServer().getOnlinePlayers().stream().map(Player::getName).toList();
            case 2 -> List.of("get", "set");
            case 3 -> {

                if (args[1].equalsIgnoreCase("set")) {
                    yield List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "20", "30", "40", "50", "60", "70", "80", "90", "100");
                } else {
                    yield OptionParser.complete(sender, OptionParser.parse(args), Set.of("use-cache"), Map.of());
                }

            }
            case 4, 5, 6 -> OptionParser.complete(sender, OptionParser.parse(args), Set.of("use-cache", "no-update"), Map.of("push", (sender1, args1) -> List.of("false", "true")));
            default -> List.of();
        };
    }
}

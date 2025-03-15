package net.jandie1505.playerlevels.commands.subcommands.manage.manage_players;

import net.jandie1505.playerlevels.PlayerLevels;
import net.jandie1505.playerlevels.commands.subcommands.utils.OptionParser;
import net.jandie1505.playerlevels.constants.Permissions;
import net.jandie1505.playerlevels.leveler.Leveler;
import net.jandie1505.playerlevels.leveler.ReceivedReward;
import net.jandie1505.playerlevels.rewards.Reward;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ManagePlayersRewardsSubcommand extends ManagePlayersLevelerTemplateSubcommand {

    public ManagePlayersRewardsSubcommand(@NotNull PlayerLevels plugin) {
        super(plugin);
    }

    @Override
    protected Result onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, OptionParser.@NotNull Result args, @NotNull Leveler leveler) {

        switch (args.args()[1].toLowerCase()) {
            case "list" -> {

                sender.sendRichMessage("<gold>Levels:");
                for (Map.Entry<String, ReceivedReward> entry : leveler.getData().internalReceivedRewards().entrySet()) {
                    sender.sendRichMessage("<gold>" + entry.getKey() + ": blocked=" + entry.getValue().blocked() + " level=" + entry.getValue().level() + " default=" + entry.getValue().isDefault());
                }

                return new Result(false);
            }
            case "get" -> {

                if (args.args().length < 3) {
                    sender.sendRichMessage("<red>You need to specify a reward entry id");
                    return new Result(false);
                }

                String id = args.args()[2];

                ReceivedReward receivedReward = leveler.getData().getOrCreateReceivedReward(id, false);

                String message = ("<gold>----- PLAYER REWARD INFO -----\n" +
                        "<gold>ID: " + id + "\n" +
                        "<gold>Reward Entry:" + "\n" +
                        "<gold>- Blocked: " + receivedReward.blocked() + "\n" +
                        "<gold>- Level: " + receivedReward.level() + "\n" +
                        "<gold>- Default: " + receivedReward.isDefault()
                );

                Reward reward = this.getPlugin().getRewardsManager().getReward(id);
                if (reward != null) {
                    message = (message + "\n" +
                            "<gold>Reward data:\n" +
                            "<gold>- Name: " + reward.getName() + "\n" +
                            "<gold>- Type: " + reward.getClass().getSimpleName() + "\n" +
                            "<gold>- Server ID: " + reward.getServerId() + "\n" +
                            "<gold>- Enabled: " + reward.isEnabled()
                    );
                } else {
                    message = message + "\n<gold>Reward of this entry does not exist";
                }

                sender.sendRichMessage(message);
                return new Result(false);
            }
            case "delete" -> {

                if (args.args().length < 3) {
                    sender.sendRichMessage("<red>You need to specify a reward entry id");
                    return new Result(false);
                }

                String id = args.args()[2];

                ReceivedReward receivedReward = leveler.getData().getReceivedReward(id);
                leveler.getData().removeReceivedReward(id, !args.hasOption("no-update"));

                if (receivedReward != null) {
                    sender.sendRichMessage("<green>Successfully deleted reward entry id");
                } else {
                    sender.sendRichMessage("<red>Reward entry does not exist");
                }

                return new Result(true);
            }
            case "reset" -> {

                if (args.args().length < 3) {
                    sender.sendRichMessage("<red>You need to specify a reward entry id");
                    return new Result(false);
                }

                String id = args.args()[2];

                ReceivedReward receivedReward = leveler.getData().getReceivedReward(id);
                if (receivedReward == null) {
                    sender.sendRichMessage("<red>Reward entry does not exist");
                    return new Result(false);
                }

                receivedReward.reset(!args.hasOption("no-update"));
                sender.sendRichMessage("<green>Successfully reset reward entry");
                return new Result(true);
            }
            case "set" -> {

                if (args.args().length < 5) {
                    sender.sendRichMessage("<red>You need to specify a reward entry id, field and value");
                    return new Result(false);
                }

                String id = args.args()[2];

                ReceivedReward receivedReward = leveler.getData().getOrCreateReceivedReward(id, false);

                try {

                    switch (args.args()[3]) {
                        case "blocked" -> {
                            receivedReward.blocked(Boolean.parseBoolean(args.args()[4]), !args.hasOption("no-update"));
                            sender.sendRichMessage("<green>Successfully updated reward entry");
                            return new Result(true);
                        }
                        case "level" -> {
                            receivedReward.level(Integer.parseInt(args.args()[4]), !args.hasOption("no-update"));
                            sender.sendRichMessage("<green>Successfully updated reward entry");
                            return new Result(true);
                        }
                        default -> sender.sendRichMessage("<red>Unknown field");
                    }

                } catch (IllegalArgumentException e) {
                    sender.sendRichMessage("<red>Illegal argument");
                }

                return new Result(false);
            }
            default -> {
                sender.sendRichMessage("<red>Unknown subcommand");
                return new Result(false);
            }
        }

    }

    @Override
    protected boolean hasPermission(@NotNull CommandSender sender) {
        return Permissions.hasPermission(sender, Permissions.MANAGE_PLAYERS);
    }

    @Override
    protected void onInvalidSyntax(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, OptionParser.@NotNull Result args) {
        sender.sendRichMessage("<red>Usage: /levels manage players rewards <player> (list|get <reward>|set <reward> (blocked|level) <value>|reset <reward>|delete <reward>) [--use-cache|--push=(true|false)|--no-update]");
    }

    // ----- TAB COMPLETER -----

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        return switch (args.length) {
            case 1 -> List.copyOf(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
            case 2 -> List.of("list", "get", "delete", "reset", "set");
            case 3 -> {

                switch (args[1].toLowerCase()) {
                    case "list" -> {
                        yield OptionParser.complete(sender, OptionParser.parse(args), Set.of("use-cache"), Map.of());
                    }
                    case "get", "set", "delete", "reset" -> {
                        yield List.copyOf(this.getPlugin().getRewardsManager().getRewards().keySet());
                    }
                }

                yield List.of();
            }
            case 4 -> {

                switch (args[1].toLowerCase()) {
                    case "get" -> {
                        yield OptionParser.complete(sender, OptionParser.parse(args), Set.of("use-cache"), Map.of());
                    }
                    case "delete", "reset" -> {
                        yield OptionParser.complete(sender, OptionParser.parse(args), Set.of("use-cache", "no-update"), Map.of("push", (sender1, args1) -> List.of("false", "true")));
                    }
                    case "set" -> {
                        yield List.of("blocked", "level");
                    }
                    default -> {
                        yield List.of();
                    }
                }

            }
            case 5 -> {

                switch (args[1].toLowerCase()) {
                    case "delete", "reset" -> {
                        yield OptionParser.complete(sender, OptionParser.parse(args), Set.of("use-cache", "no-update"), Map.of("push", (sender1, args1) -> List.of("false", "true")));
                    }
                    case "set" -> {

                        switch (args[3].toLowerCase()) {
                            case "blocked" -> {
                                yield List.of("false", "true");
                            }
                            case "level" -> {
                                yield List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "20", "30", "40", "50", "60", "70", "80", "90", "100");
                            }
                            default -> {
                                yield List.of();
                            }
                        }

                    }
                    default -> {
                        yield List.of();
                    }
                }

            }
            case 6, 7 -> {

                if (args[1].equalsIgnoreCase("set")) {
                    yield OptionParser.complete(sender, OptionParser.parse(args), Set.of("use-cache", "no-update"), Map.of("push", (sender1, args1) -> List.of("false", "true")));
                }

                yield List.of();
            }
            default -> List.of();
        };
    }

}

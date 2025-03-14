package net.jandie1505.playerlevels.commands.subcommands.manage.manage_players;

import net.chaossquad.mclib.PlayerUtils;
import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.playerlevels.PlayerLevels;
import net.jandie1505.playerlevels.commands.subcommands.utils.OptionParser;
import net.jandie1505.playerlevels.constants.Permissions;
import net.jandie1505.playerlevels.leveler.Leveler;
import net.jandie1505.playerlevels.leveler.ReceivedReward;
import net.jandie1505.playerlevels.rewards.Reward;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RewardsManageSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final PlayerLevels plugin;

    public RewardsManageSubcommand(@NotNull PlayerLevels plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] a) {

        if (!Permissions.hasPermission(sender, Permissions.MANAGE_PLAYERS)) {
            sender.sendRichMessage("<red>No permission");
            return true;
        }

        OptionParser.Result args = OptionParser.parse(a);

        if (args.args().length < 1) {
            sender.sendRichMessage("<red>Usage: /levels manage rewards [--use-cache] <player>");
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

            this.command(sender, leveler, args);

        } else {
            this.loadLevelerWay(sender, playerUUID, args);
        }

        return true;
    }

    private void loadLevelerWay(@NotNull final CommandSender sender, @NotNull final UUID playerUUID, @NotNull final OptionParser.Result args) {
        this.plugin.getLevelManager().loadLeveler(playerUUID, true).thenAccept(leveler -> new BukkitRunnable() {
            @Override
            public void run() {
                RewardsManageSubcommand.this.command(sender, leveler, args);
            }
        }.runTask(this.plugin));
    }

    private void command(@NotNull CommandSender sender, @NotNull Leveler leveler, OptionParser.Result args) {

        if (args.args().length < 2) {
            sender.sendRichMessage("<red>Usage: ...");
            return;
        }

        switch (args.args()[1].toLowerCase()) {
            case "list" -> {

                sender.sendRichMessage("<gold>Levels:");
                for (Map.Entry<String, ReceivedReward> entry : leveler.getData().internalReceivedRewards().entrySet()) {
                    sender.sendRichMessage("<gold>" + entry.getKey() + ": blocked=" + entry.getValue().blocked() + " level=" + entry.getValue().level() + " default=" + entry.getValue().isDefault());
                }

            }
            case "get" -> {

                if (args.args().length < 3) {
                    sender.sendRichMessage("<red>You need to specify a reward entry id");
                    return;
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

                Reward reward = this.plugin.getRewardsManager().getReward(id);
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

            }
            case "delete" -> {

                if (args.args().length < 3) {
                    sender.sendRichMessage("<red>You need to specify a reward entry id");
                    return;
                }

                String id = args.args()[2];

                ReceivedReward receivedReward = leveler.getData().getReceivedReward(id);
                leveler.getData().removeReceivedReward(id, !args.hasOption("no-update"));

                if (receivedReward != null) {
                    sender.sendRichMessage("<green>Successfully deleted reward entry id");
                } else {
                    sender.sendRichMessage("<red>Reward entry does not exist");
                }

            }
            case "reset" -> {

                if (args.args().length < 3) {
                    sender.sendRichMessage("<red>You need to specify a reward entry id");
                    return;
                }

                String id = args.args()[2];

                ReceivedReward receivedReward = leveler.getData().getReceivedReward(id);
                if (receivedReward == null) {
                    sender.sendRichMessage("<red>Reward entry does not exist");
                    return;
                }

                receivedReward.reset(!args.hasOption("no-update"));
                sender.sendRichMessage("<green>Successfully reset reward entry");

            }
            case "set" -> {

                if (args.args().length < 5) {
                    sender.sendRichMessage("<red>You need to specify a reward entry id, field and value");
                    return;
                }

                String id = args.args()[2];

                ReceivedReward receivedReward = leveler.getData().getOrCreateReceivedReward(id, false);

                try {

                    switch (args.args()[3]) {
                        case "blocked" -> {
                            receivedReward.blocked(Boolean.parseBoolean(args.args()[4]), !args.hasOption("no-update"));
                            sender.sendRichMessage("<green>Successfully updated reward entry");
                        }
                        case "level" -> {
                            receivedReward.level(Integer.parseInt(args.args()[4]), !args.hasOption("no-update"));
                            sender.sendRichMessage("<green>Successfully updated reward entry");
                        }
                        default -> sender.sendRichMessage("<red>Unknown field");
                    }

                } catch (IllegalArgumentException e) {
                    sender.sendRichMessage("<red>Illegal argument");
                }

            }
            default -> sender.sendRichMessage("<red>Unknown subcommand");
        }

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
                        yield List.of("--use-cache");
                    }
                    case "get", "set", "delete", "reset" -> {
                        yield List.copyOf(this.plugin.getRewardsManager().getRewards().keySet());
                    }
                }

                yield List.of();
            }
            case 4 -> {

                switch (args[1].toLowerCase()) {
                    case "get" -> {
                        yield List.of("--use-cache");
                    }
                    case "delete", "reset" -> {
                        yield List.of("--use-cache", "--no-update");
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

                        switch (args[3].toLowerCase()) {
                            case "--use-cache" -> {
                                yield List.of("--no-update");
                            }
                            case "--no-update" -> {
                                yield List.of("--use-cache");
                            }
                            default -> {
                                yield List.of();
                            }
                        }

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
            case 6 -> {

                if (args[1].equalsIgnoreCase("set")) {
                    yield List.of("--use-cache", "--no-update");
                }

                yield List.of();
            }
            case 7 -> {

                if (args[1].equalsIgnoreCase("set")) {

                    switch (args[5].toLowerCase()) {
                        case "--use-cache" -> {
                            yield List.of("--no-update");
                        }
                        case "--no-update" -> {
                            yield List.of("--use-cache");
                        }
                        default -> {
                            yield List.of();
                        }
                    }

                }

                yield List.of();
            }
            default -> List.of();
        };
    }

    public @NotNull PlayerLevels getPlugin() {
        return plugin;
    }

}

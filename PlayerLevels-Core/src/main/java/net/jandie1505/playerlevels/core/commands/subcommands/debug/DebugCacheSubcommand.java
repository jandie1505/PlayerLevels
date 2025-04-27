package net.jandie1505.playerlevels.core.commands.subcommands.debug;

import net.chaossquad.mclib.PlayerUtils;
import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.playerlevels.core.PlayerLevels;
import net.jandie1505.playerlevels.core.commands.subcommands.utils.OptionParser;
import net.jandie1505.playerlevels.core.constants.MessageKeys;
import net.jandie1505.playerlevels.core.constants.Permissions;
import net.jandie1505.playerlevels.core.leveler.Leveler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DebugCacheSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final PlayerLevels plugin;

    public DebugCacheSubcommand(@NotNull PlayerLevels plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] a) {

        if (!Permissions.hasPermission(sender)) {
            sender.sendRichMessage(this.plugin.messages().optString(MessageKeys.GENERAL_NO_PERMISSION, ""));
            return true;
        }

        OptionParser.Result args = OptionParser.parse(a);

        if (args.args().length < 1) {
            sender.sendRichMessage("<red>Usage: /levels debug cache (list|refresh|drop <player> [--no-process|--confirm]|drop-all [--no-process|--confirm])");
            return true;
        }

        switch (args.args()[0]) {
            case "list" -> {

                sender.sendRichMessage("<gray>Cached players:");

                for (Map.Entry<UUID, Leveler> entry : this.plugin.getLevelManager().getCache().entrySet()) {
                    Player player = Bukkit.getPlayer(entry.getKey());
                    String name = player != null ? player.getName() + " (" + entry.getKey() + ") " : entry.getKey().toString();

                    sender.sendRichMessage("<gray>" + name + " " + entry.getValue().getData().level() + " " + entry.getValue().getData().xp());
                }

            }
            case "refresh" -> {
                this.plugin.getLevelManager().updateCacheAsync();
                sender.sendRichMessage("<green>Scheduled cache refresh");
            }
            case "drop" -> {

                if (args.args().length > 1) {

                    UUID playerUUID = PlayerUtils.getPlayerUUIDFromString(args.args()[1]);
                    if (playerUUID == null) {
                        sender.sendRichMessage("<red>Player not found");
                        return true;
                    }

                    boolean update = !Boolean.parseBoolean(args.options().get("no-process"));

                    if (args.hasOption("confirm")) {

                        boolean success = this.plugin.getLevelManager().dropCache(playerUUID, update);

                        if (success) {
                            sender.sendRichMessage("<green>Successfully dropped cache for " + playerUUID);
                        } else {
                            sender.sendRichMessage("<red>Player not cached");
                        }

                    } else {

                        Component message = Component.empty()
                                .append(Component.text("Do you really want to drop the cache for " + playerUUID + "?", NamedTextColor.RED)).appendNewline()
                                .append(Component.text("Dropping the cache without refreshing it can cause issues!", NamedTextColor.RED)).appendNewline();

                        if (update) {
                            message = message.append(Component.text("[CONFIRM]", NamedTextColor.DARK_RED)
                                    .clickEvent(ClickEvent.runCommand("/levels debug cache drop " + playerUUID + " --confirm"))
                                    .hoverEvent(Component.text("Click to confirm", NamedTextColor.GRAY))
                            );
                        } else {
                            message = message.append(Component.text("[CONFIRM]", NamedTextColor.DARK_RED)
                                    .clickEvent(ClickEvent.runCommand("/levels debug cache drop --no-process " + playerUUID + " --confirm"))
                                    .hoverEvent(Component.text("Click to confirm", NamedTextColor.GRAY))
                            );
                        }

                        sender.sendMessage(message);
                    }

                } else {
                    sender.sendRichMessage("<red>Usage: /levels debug cache drop <player>");
                }

            }
            case "drop-all" -> {

                boolean update = !Boolean.parseBoolean(args.options().get("no-process"));

                if (args.hasOption("confirm")) {
                    this.plugin.getLevelManager().dropCaches(update);
                    sender.sendRichMessage("<green>Successfully dropped cache");
                } else {

                    Component message = Component.empty()
                            .append(Component.text("Do you really want to drop the entire cache?", NamedTextColor.RED)).appendNewline()
                            .append(Component.text("Dropping the cache without refreshing it can cause issues!", NamedTextColor.RED)).appendNewline();

                    if (update) {
                        message = message.append(Component.text("[CONFIRM]", NamedTextColor.DARK_RED)
                                .clickEvent(ClickEvent.runCommand("/levels debug cache drop-all --confirm"))
                                .hoverEvent(Component.text("Click to confirm", NamedTextColor.GRAY))
                        );
                    } else {
                        message = message.append(Component.text("[CONFIRM]", NamedTextColor.DARK_RED)
                                .clickEvent(ClickEvent.runCommand("/levels debug cache drop --no-process --confirm"))
                                .hoverEvent(Component.text("Click to confirm", NamedTextColor.GRAY))
                        );
                    }

                    sender.sendMessage(message);
                }

            }
            default -> sender.sendRichMessage("<red>Run command without arguments for help");
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return switch(args.length) {
            case 1 -> List.of("list", "refresh", "drop", "drop-all");
            case 2 -> {

                switch (args[0]) {
                    case "drop" -> {
                        List<String> list = new ArrayList<>();
                        list.add("--no-process");
                        list.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
                        yield list;
                    }
                    case "drop-all" -> {
                        yield List.of("--no-process");
                    }
                    default -> {
                        yield List.of();
                    }
                }

            }
            case 3 -> {

                if (args[0].equalsIgnoreCase("drop")) {
                    if (args[1].equalsIgnoreCase("--no-process")) {
                        yield Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
                    } else {
                        yield List.of("--no-process");
                    }
                } else {
                    yield List.of();
                }

            }
            default -> List.of();
        };
    }
}

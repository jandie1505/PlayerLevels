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

public class ManagePlayersInfoSubcommand extends ManagePlayersLevelerTemplateSubcommand {

    public ManagePlayersInfoSubcommand(@NotNull PlayerLevels plugin) {
        super(plugin);
    }

    @Override
    protected Result onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, OptionParser.@NotNull Result args, @NotNull Leveler leveler) {
        sender.sendRichMessage("<gray>Leveling Info:");
        sender.sendRichMessage("<gray>UUID: " + leveler.getPlayerUUID());
        sender.sendRichMessage("<gray>Level: " + leveler.getData().level());
        sender.sendRichMessage("<gray>XP: " + leveler.getData().xp());
        sender.sendRichMessage("<gray>Reward entries:" + leveler.getData().internalReceivedRewards().size());
        return Result.doNothing();
    }

    @Override
    protected boolean hasPermission(@NotNull CommandSender sender) {
        return Permissions.hasPermission(sender, Permissions.MANAGE_PLAYERS);
    }

    @Override
    protected void onInvalidSyntax(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, OptionParser.@NotNull Result args) {
        sender.sendRichMessage("<red>Usage: /levels manage players info <player> [--use-cache]");
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (args.length > 1) {
            return OptionParser.complete(sender, OptionParser.parse(args), Set.of("use-cache"), Map.of());
        }

        return switch (args.length) {
            case 1 -> Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
            default -> List.of();
        };
    }

}

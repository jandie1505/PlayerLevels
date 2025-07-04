package net.jandie1505.playerlevels.core.commands.subcommands.manage.manage_players;

import net.chaossquad.mclib.PlayerUtils;
import net.chaossquad.mclib.command.TabCompletingCommandExecutor;
import net.jandie1505.playerlevels.core.PlayerLevels;
import net.jandie1505.playerlevels.core.commands.subcommands.utils.OptionParser;
import net.jandie1505.playerlevels.core.leveler.Leveler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Template command for simple leveler-requiring management commands
 */
public abstract class ManagePlayersLevelerTemplateSubcommand implements TabCompletingCommandExecutor {
    @NotNull private final PlayerLevels plugin;

    public ManagePlayersLevelerTemplateSubcommand(@NotNull PlayerLevels plugin) {
        this.plugin = plugin;
    }

    // ----- COMMAND -----

    @Override
    public final boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] a) {

        if (!this.hasPermission(sender)) {
            this.onNoPermission(sender, command, label);
            return true;
        }

        OptionParser.Result args = OptionParser.parse(a);

        if (args.args().length < 1) {
            this.onInvalidSyntax(sender, command, label, args);
            return true;
        }

        UUID playerUUID = PlayerUtils.getPlayerUUIDFromString(args.args()[0]);
        if (playerUUID == null) {
            this.onInvalidPlayerUUID(sender, command, label, args);
            return true;
        }

        boolean useCache = args.hasOption("use-cache");
        Boolean pushToDatabase = this.checkPushOptions(args);

        if (useCache) {

            if (pushToDatabase != null) {
                sender.sendRichMessage("<red>You can't combine --use-cache with --push");
                return true;
            }

            Leveler leveler = this.plugin.getLevelManager().getLeveler(playerUUID);

            if (leveler == null) {
                this.onLevelerNotCached(sender, command, label, args);
                return true;
            }

            Result result = this.onCommand(sender, command, label, args, leveler);
            if (result == null) result = Result.doNothing();

            if (result.processDataBeforeSync()) {
                leveler.processAsynchronously();
            }

            // Here is no if (result.syncWithDatabase()) {...} because here, the cache is used.
            // So the data should not be pushed.

        } else {
            this.loadLevelerWay(sender, command, label, args, playerUUID, pushToDatabase);
        }

        return true;
    }

    private void loadLevelerWay(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, @NotNull final OptionParser.Result args, @NotNull final UUID playerUUID, @Nullable Boolean pushToDatabase) {
        this.plugin.getLevelManager().loadLeveler(playerUUID, true).thenAccept(leveler -> new BukkitRunnable() {
            @Override
            public void run() {
                Result result = ManagePlayersLevelerTemplateSubcommand.this.onCommand(sender, command, label, args, leveler);
                if (result == null) result = Result.doNothing();

                final Result finalResult = result;
                new BukkitRunnable() {
                    @Override
                    public void run() {

                        // Process the leveler before the push if the command has it enabled in its result. This can be disabled by the user by specifying --no-process.
                        if (finalResult.processDataBeforeSync() && !args.hasOption("no-process")) {
                            ManagePlayersLevelerTemplateSubcommand.this.processPlayer(sender, command, label, args, leveler);
                        }

                        // Push to database if enabled (use --push=<value> if available, else use command default which is the value it returns as result)
                        if (pushToDatabase != null ? pushToDatabase : finalResult.syncWithDatabase()) {
                            ManagePlayersLevelerTemplateSubcommand.this.pushToDatabase(sender, command, label, args, leveler);
                        }

                    }
                }.runTaskAsynchronously(ManagePlayersLevelerTemplateSubcommand.this.plugin);
            }
        }.runTask(this.plugin));
    }

    /**
     * Processes the player and sends a message.<br/>
     * Should be called asynchronously.
     */
    private void processPlayer(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, @NotNull final OptionParser.Result args, @NotNull final Leveler leveler) {
        leveler.process();

        new BukkitRunnable() {
            @Override
            public void run() {
                ManagePlayersLevelerTemplateSubcommand.this.onProcessFinished(sender, command, label, args, leveler);
            }
        }.runTask(this.plugin);
    }

    private void pushToDatabase(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, @NotNull final OptionParser.Result args, @NotNull final Leveler leveler) {
        leveler.syncAsynchronously().thenAccept(result -> new BukkitRunnable() {
            @Override
            public void run() {

                if (result.isRemoteChanged()) {
                    ManagePlayersLevelerTemplateSubcommand.this.onDatabasePushSuccess(sender, command, label, args, leveler);
                } else if (result.isFail() || result.isLocalChanged()) {
                    ManagePlayersLevelerTemplateSubcommand.this.onDatabasePushFailed(sender, command, label, args, leveler);
                }

            }
        }.runTask(this.plugin));
    }

    private @Nullable Boolean checkPushOptions(@NotNull OptionParser.Result args) {
        String option = args.options().get("push");
        if (option == null || option.isEmpty()) return null;
        return Boolean.parseBoolean(option);
    }

    protected abstract @Nullable Result onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, @NotNull final OptionParser.Result args, @NotNull final Leveler leveler);

    protected abstract boolean hasPermission(@NotNull CommandSender sender);

    @ApiStatus.OverrideOnly
    protected void onNoPermission(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label) {
        sender.sendRichMessage("<red>You don't have permission to use this command");
    }

    @ApiStatus.OverrideOnly
    protected void onInvalidSyntax(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, @NotNull final OptionParser.Result args) {
        sender.sendRichMessage("<red>Invalid syntax");
    }

    @ApiStatus.OverrideOnly
    protected void onInvalidPlayerUUID(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, @NotNull final OptionParser.Result args) {
        sender.sendRichMessage("<red>Player not found");
    }

    @ApiStatus.OverrideOnly
    protected void onLevelerNotCached(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, @NotNull final OptionParser.Result args) {
        sender.sendRichMessage("<red>Leveler is not cached");
    }

    @ApiStatus.OverrideOnly
    protected void onDatabasePushSuccess(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, @NotNull final OptionParser.Result args, @NotNull final Leveler leveler) {
        sender.sendRichMessage("<green>Pushed changes to database successfully");
    }

    @ApiStatus.OverrideOnly
    protected void onDatabasePushFailed(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, @NotNull final OptionParser.Result args, @NotNull final Leveler leveler) {
        sender.sendRichMessage("<red>Failed to push changes to the database");
    }

    @ApiStatus.OverrideOnly
    protected void onProcessFinished(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, @NotNull final OptionParser.Result args, @NotNull final Leveler leveler) {
        sender.sendRichMessage("<green>Leveler has been processed");
    }

    // ----- TAB COMPLETION -----

    @Override
    @ApiStatus.OverrideOnly
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return List.of();
    }

    // ----- PLUGIN -----

    public final @NotNull PlayerLevels getPlugin() {
        return plugin;
    }

    // ----- COMMAND RESULT -----

    /**
     * The result a onCommand method of this template command returns.<br/>
     * This controls if a leveler is processed, synced or both after the command has been executed.
     * @param syncWithDatabase if the leveler is synced with the database
     * @param processDataBeforeSync if the data is processed before it is synced with the database
     */
    public record Result(boolean syncWithDatabase, boolean processDataBeforeSync) {

        public static @NotNull Result doNothing() {
            return new Result(false, false);
        }

    }

}

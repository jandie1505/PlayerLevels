package net.jandie1505.playerlevels;

import net.chaossquad.mclib.command.SubcommandCommand;
import net.chaossquad.mclib.command.SubcommandEntry;
import net.chaossquad.mclib.storage.DSSerializer;
import net.chaossquad.mclib.storage.DataStorage;
import net.jandie1505.playerlevels.api.PlayerLevelsAPI;
import net.jandie1505.playerlevels.commands.*;
import net.jandie1505.playerlevels.constants.DefaultConfigValues;
import net.jandie1505.playerlevels.database.DatabaseManager;
import net.jandie1505.playerlevels.leveler.LevelingManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.logging.Level;

public class PlayerLevels extends JavaPlugin implements PlayerLevelsAPI {
    @NotNull private final DataStorage config;
    private LevelingManager levelingManager;
    private DatabaseManager databaseManager;
    private SubcommandCommand command;

    public PlayerLevels() {
        this.config = new DataStorage();
    }

    // ----- ENABLE -----

    @Override
    public void onEnable() {

        this.config.clear();
        this.config.merge(DefaultConfigValues.get());

        try {

            DataStorage loadedStorage = DSSerializer.loadConfig(new File(this.getDataFolder(), "config.yml"));
            if (loadedStorage != null) {
                this.config.merge(loadedStorage);
                this.getLogger().info("Config loaded successfully");
            } else {
                DSSerializer.saveConfig(this.config, new File(this.getDataFolder(), "config.yml"));
                this.getLogger().info("Config created successfully");
            }

        } catch (Exception e) {
            this.getLogger().log(Level.WARNING, "Failed to load config. Using default values.", e);
        }

        this.databaseManager = new DatabaseManager(this);
        this.databaseManager.setupDatabase();
        this.levelingManager = new LevelingManager(this, this.databaseManager);

        this.command = new SubcommandCommand(this);
        this.command.addSubcommand("config", SubcommandEntry.of(new ConfigSubCommand(this)));
        this.command.addSubcommand("cache", SubcommandEntry.of(new CacheSubCommand(this)));
        this.command.addSubcommand("info", SubcommandEntry.of(new InfoSubCommand(this)));
        this.command.addSubcommand("level", SubcommandEntry.of(new LevelSubCommand(this)));
        this.command.addSubcommand("database", SubcommandEntry.of(new DatabaseSubcommand(this)));

        this.getCommand("playerlevels").setExecutor(this.command);
        this.getCommand("playerlevels").setTabCompleter(this.command);

        new BukkitRunnable() {
            @Override
            public void run() {
                PlayerLevels.this.levelingManager.updateCacheTask();
            }
        }.runTaskTimer(this, 20, 10*60*20);
        this.getServer().getPluginManager().registerEvents(this.levelingManager, this);
    }

    @Override
    public void onDisable() {
        this.levelingManager = null;
        this.databaseManager.shutdownDatabase();
        this.databaseManager = null;
        this.config.clear();
    }

    // ----- OTHER -----

    public final @NotNull DataStorage config() {
        return config;
    }

    @Override
    public LevelingManager getLevelManager() {
        return this.levelingManager;
    }

    public DatabaseManager getDatabaseManager() {
        return this.databaseManager;
    }

}

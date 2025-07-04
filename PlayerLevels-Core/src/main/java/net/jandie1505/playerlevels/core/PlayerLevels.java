package net.jandie1505.playerlevels.core;

import net.chaossquad.mclib.storage.DSSerializer;
import net.chaossquad.mclib.storage.DataStorage;
import net.jandie1505.playerlevels.api.core.PlayerLevelsAPI;
import net.jandie1505.playerlevels.core.commands.PlayerLevelsCommand;
import net.jandie1505.playerlevels.core.constants.ConfigKeys;
import net.jandie1505.playerlevels.core.constants.DefaultConfigValues;
import net.jandie1505.playerlevels.core.database.mariadb.MariaDBDatabaseManager;
import net.jandie1505.playerlevels.core.leveler.Leveler;
import net.jandie1505.playerlevels.core.leveler.LevelingManager;
import net.jandie1505.playerlevels.core.leveler.TopListManager;
import net.jandie1505.playerlevels.core.messages.AnnouncementHandler;
import net.jandie1505.playerlevels.core.rewards.RewardsManager;
import net.jandie1505.playerlevels.core.rewards.RewardsRegistry;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Iterator;
import java.util.Objects;
import java.util.logging.Level;

public class PlayerLevels extends JavaPlugin implements PlayerLevelsAPI {
    @NotNull private final DataStorage config;
    @NotNull private final DataStorage messages;
    private MariaDBDatabaseManager databaseManager;
    private LevelingManager levelingManager;
    private RewardsManager rewardsManager;
    private TopListManager topListManager;
    private RewardsRegistry rewardsRegistry;
    private PlayerLevelsCommand command;
    @Nullable private String serverIdOverride;

    public PlayerLevels() {
        this.config = new DataStorage();
        this.messages = new DataStorage();
    }

    // ----- ENABLE -----

    @Override
    public void onEnable() {

        this.reloadConfig(true, true);
        this.reloadMessages(true, true);

        this.databaseManager = new MariaDBDatabaseManager(this);
        this.databaseManager.setupDatabase();
        this.levelingManager = new LevelingManager(this, this.databaseManager.getDatabase());
        this.rewardsRegistry = new RewardsRegistry(this);
        this.rewardsManager = new RewardsManager(this);
        this.topListManager = new TopListManager(this, this.databaseManager.getDatabase());
        this.command = new PlayerLevelsCommand(this);
        this.serverIdOverride = null;

        this.getCommand("playerlevels").setExecutor(this.command);
        this.getCommand("playerlevels").setTabCompleter(this.command);

        new BukkitRunnable() {
            @Override
            public void run() {
                PlayerLevels.this.levelingManager.updateCache();
            }
        }.runTaskTimerAsynchronously(this, 20, 10*60*20);
        this.getServer().getPluginManager().registerEvents(this.levelingManager, this);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (PlayerLevels.this.config().optBoolean(ConfigKeys.TOP_LIST_ENABLED, false)) PlayerLevels.this.topListManager.updateTopList();
            }
        }.runTaskTimerAsynchronously(this, 20, 60*60*20);

        this.getServer().getPluginManager().registerEvents(new AnnouncementHandler(this), this);

        PlayerLevelsAPIProvider.setApi(this);

        try {
            this.rewardsRegistry.createRewardsFromConfig(true);
        } catch (Exception e) {
            this.getLogger().log(Level.SEVERE, "Failed to load rewards from config", e);
        }
    }

    @Override
    public void onDisable() {

        try {

            Iterator<Leveler> i = this.levelingManager.getCache().values().iterator();
            while (i.hasNext()) {
                Leveler leveler = i.next();
                leveler.sync();
                i.remove();
            }

        } catch (Exception e) {
            this.getLogger().log(Level.WARNING, "Failed to sync levelers on plugin disable", e);
        }

        this.levelingManager = null;
        this.rewardsManager = null;
        this.rewardsRegistry = null;
        this.databaseManager.shutdownDatabase();
        this.databaseManager = null;
        this.config.clear();
        this.messages.clear();
        PlayerLevelsAPIProvider.setApi(null);
    }

    // ----- CONFIG -----

    public boolean reloadConfig(boolean clear, boolean mergeDefaults) {

        if (clear) this.config.clear();
        if (mergeDefaults) this.config.merge(DefaultConfigValues.getConfig());

        try {

            DataStorage loadedStorage = DSSerializer.loadConfig(new File(this.getDataFolder(), "config.yml"));
            if (loadedStorage != null) {
                this.config.merge(loadedStorage);
                this.getLogger().info("Config loaded successfully");
                return true;
            } else {
                DSSerializer.saveConfig(this.config, new File(this.getDataFolder(), "config.yml"));
                this.getLogger().info("Config created successfully");
                return true;
            }

        } catch (Exception e) {
            this.getLogger().log(Level.WARNING, "Failed to load config. Using default values.", e);
            return false;
        }
    }

    public boolean reloadMessages(boolean clear, boolean mergeDefaults) {

        if (clear) this.messages.clear();
        if (mergeDefaults) this.messages.merge(DefaultConfigValues.getMessages());

        try {

            DataStorage loadedStorage = DSSerializer.loadConfig(new File(this.getDataFolder(), "messages.yml"));
            if (loadedStorage != null) {
                this.messages.merge(loadedStorage);
                this.getLogger().info("Messages config loaded successfully");
                return true;
            } else {
                DSSerializer.saveConfig(this.messages, new File(this.getDataFolder(), "messages.yml"));
                this.getLogger().info("Messages config created successfully");
                return true;
            }

        } catch (Exception e) {
            this.getLogger().log(Level.WARNING, "Failed to load messages config. Using default values.", e);
            return false;
        }
    }

    // ----- OTHER -----

    public final @NotNull DataStorage config() {
        return config;
    }

    public final @NotNull DataStorage messages() {
        return messages;
    }

    @Override
    public LevelingManager getLevelManager() {
        return this.levelingManager;
    }

    @Override
    public RewardsManager getRewardsManager() {
        return this.rewardsManager;
    }

    @Override
    public RewardsRegistry getRewardsRegistry() {
        return this.rewardsRegistry;
    }

    public TopListManager getTopListManager() {
        return this.topListManager;
    }

    public MariaDBDatabaseManager getDatabaseManager() {
        return this.databaseManager;
    }

    public @NotNull String getServerId() {

        if (this.serverIdOverride != null) return this.serverIdOverride;

        String serverId = System.getProperty(this.getName() + ".server-id");
        if (serverId != null) return serverId;

        serverId = System.getenv(this.getName() + ".server-id");
        if (serverId != null) return serverId;

        return Objects.requireNonNullElse(this.config.optString(ConfigKeys.SERVER_ID, ""), "");
    }

    public @Nullable String getServerIdOverride() {
        return this.serverIdOverride;
    }

    public void setServerIdOverride(@Nullable String serverId) {
        this.serverIdOverride = serverId;
    }

}

package net.jandie1505.playerlevels;

import net.chaossquad.mclib.storage.DSSerializer;
import net.chaossquad.mclib.storage.DataStorage;
import net.jandie1505.playerlevels.api.PlayerLevelsAPI;
import net.jandie1505.playerlevels.commands.PlayerLevelsCommand;
import net.jandie1505.playerlevels.constants.ConfigKeys;
import net.jandie1505.playerlevels.constants.DefaultConfigValues;
import net.jandie1505.playerlevels.database.DatabaseManager;
import net.jandie1505.playerlevels.leveler.Leveler;
import net.jandie1505.playerlevels.leveler.LevelingManager;
import net.jandie1505.playerlevels.leveler.TopListManager;
import net.jandie1505.playerlevels.rewards.RewardConfig;
import net.jandie1505.playerlevels.rewards.RewardsManager;
import net.jandie1505.playerlevels.rewards.RewardsRegistry;
import net.jandie1505.playerlevels.rewards.types.CommandReward;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import javax.swing.text.html.parser.Entity;
import java.io.File;
import java.util.Iterator;
import java.util.Objects;
import java.util.logging.Level;

public class PlayerLevels extends JavaPlugin implements PlayerLevelsAPI {
    @NotNull private final DataStorage config;
    private DatabaseManager databaseManager;
    private LevelingManager levelingManager;
    private RewardsManager rewardsManager;
    private TopListManager topListManager;
    private RewardsRegistry rewardsRegistry;
    private PlayerLevelsCommand command;

    public PlayerLevels() {
        this.config = new DataStorage();
    }

    // ----- ENABLE -----

    @Override
    public void onEnable() {

        this.config.clear();
        this.config.merge(DefaultConfigValues.getConfig());

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
        this.rewardsRegistry = new RewardsRegistry(this);
        this.rewardsManager = new RewardsManager(this);
        this.topListManager = new TopListManager(this, this.databaseManager);
        this.command = new PlayerLevelsCommand(this);

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

        PlayerLevelsAPIProvider.setApi(this);

        try {
            this.rewardsRegistry.createRewardsFromConfig(true);
        } catch (Exception e) {
            this.getLogger().log(Level.SEVERE, "Failed to load rewards from config", e);
        }

        this.getRewardsManager().addMilestoneReward(
                new RewardConfig("test_milestone", null, "Test Reward", null),
                CommandReward.createMilestone("say {player_name} has unlocked {reward_name} on level {player_reward_level}", true, CommandReward.SenderType.CONSOLE, 50)
        );

        this.getRewardsManager().addIntervalReward(
                new RewardConfig("test_interval", null, "Interval Test Reward", null),
                CommandReward.createInterval("say {player_name} has unlocked {reward_name} on level {player_reward_level}", true, CommandReward.SenderType.CONSOLE, 1, 2, -1)
        );
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
        PlayerLevelsAPIProvider.setApi(null);
    }

    // ----- OTHER -----

    public final @NotNull DataStorage config() {
        return config;
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

    public DatabaseManager getDatabaseManager() {
        return this.databaseManager;
    }

    public @NotNull String getServerId() {

        String serverId = System.getProperty(this.getName() + ".server-id");
        if (serverId != null) return serverId;

        serverId = System.getenv(this.getName() + ".server-id");
        if (serverId != null) return serverId;

        return Objects.requireNonNullElse(this.config.optString(ConfigKeys.SERVER_ID, ""), "");
    }

}

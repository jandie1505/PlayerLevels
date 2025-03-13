package net.jandie1505.playerlevels;

import net.chaossquad.mclib.storage.DSSerializer;
import net.chaossquad.mclib.storage.DataStorage;
import net.jandie1505.playerlevels.api.PlayerLevelsAPI;
import net.jandie1505.playerlevels.commands.PlayerLevelsCommand;
import net.jandie1505.playerlevels.constants.ConfigKeys;
import net.jandie1505.playerlevels.constants.DefaultConfigValues;
import net.jandie1505.playerlevels.database.DatabaseManager;
import net.jandie1505.playerlevels.leveler.LevelingManager;
import net.jandie1505.playerlevels.rewards.IntervalReward;
import net.jandie1505.playerlevels.rewards.RewardConfig;
import net.jandie1505.playerlevels.rewards.RewardsManager;
import net.jandie1505.playerlevels.rewards.types.CommandReward;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;
import java.util.logging.Level;

public class PlayerLevels extends JavaPlugin implements PlayerLevelsAPI {
    @NotNull private final DataStorage config;
    private LevelingManager levelingManager;
    private RewardsManager rewardsManager;
    private DatabaseManager databaseManager;
    private PlayerLevelsCommand command;

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
        this.rewardsManager = new RewardsManager(this);

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

        PlayerLevelsAPIProvider.setApi(this);

        this.getRewardsManager().addReward(
                new RewardConfig("test", null, 50, "Test Reward", null),
                CommandReward.create("say <player_name> has unlocked <reward_name>", true, CommandReward.SenderType.CONSOLE)
        );

        this.getRewardsManager().addReward(
                new RewardConfig("test2", null, 50, "Test Reward 2", null),
                CommandReward.create("say <player_name> has unlocked <reward_name>", true, CommandReward.SenderType.CONSOLE)
        );

        this.getRewardsManager().addReward(new IntervalReward(this.getRewardsManager(), "test3", null, 1, (reward, player) -> {
            Bukkit.broadcast(Component.text(player.getPlayerUUID() + " has received the interval reward " + reward.getName() + " with id " + reward.getId()));
            return true;
        }, null, true, "Interval Test", "Test"));
    }

    @Override
    public void onDisable() {
        this.levelingManager = null;
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

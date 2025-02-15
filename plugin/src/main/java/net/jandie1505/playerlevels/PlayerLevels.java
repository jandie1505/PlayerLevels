package net.jandie1505.playerlevels;

import net.chaossquad.mclib.storage.DataStorage;
import net.jandie1505.playerlevels.api.PlayerLevelsAPI;
import net.jandie1505.playerlevels.database.DatabaseManager;
import net.jandie1505.playerlevels.leveler.LevelingManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class PlayerLevels extends JavaPlugin implements PlayerLevelsAPI {
    @NotNull private final DataStorage config;
    private LevelingManager levelingManager;
    private DatabaseManager databaseManager;

    public PlayerLevels() {
        this.config = new DataStorage();
    }

    // ----- ENABLE -----

    @Override
    public void onEnable() {
        this.databaseManager = new DatabaseManager();
        this.levelingManager = new LevelingManager(this, this.databaseManager);
    }


    // ----- EVENTS -----

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

    }

    // ----- OTHER -----

    public final @NotNull DataStorage config() {
        return config;
    }

    @Override
    public LevelingManager getLevelManager() {
        return this.levelingManager;
    }
}

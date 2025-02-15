package net.jandie1505.playerlevels.leveler;

import net.jandie1505.playerlevels.PlayerLevels;
import net.jandie1505.playerlevels.api.LevelManager;
import net.jandie1505.playerlevels.api.LevelPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class LevelingManager implements LevelManager, Listener {
    @NotNull private final PlayerLevels plugin;
    @NotNull private ConcurrentHashMap<UUID, Leveler> cachedLevelers;
    private final AtomicBoolean cachingTaskActive;

    public LevelingManager(@NotNull PlayerLevels plugin) {
        this.plugin = plugin;
        this.cachedLevelers = new ConcurrentHashMap<>();
        this.cachingTaskActive = new AtomicBoolean(false);
    }

    // ----- MANAGE -----

    @Override
    public @Nullable Leveler getLeveler(@NotNull UUID playerUUID) {
        Leveler leveler = this.cachedLevelers.get(playerUUID);
        if (leveler == null) return null;
        if (!leveler.isValid()) return null;
        return leveler;
    }

    @Override
    public @NotNull CompletableFuture<LevelPlayer> loadLeveler(@NotNull UUID playerUUID) {

        Leveler leveler = this.cachedLevelers.get(playerUUID);
        if (leveler != null && leveler.isValid()) {
            return CompletableFuture.completedFuture(leveler);
        }

        CompletableFuture<LevelPlayer> future = new CompletableFuture<>();
        new BukkitRunnable() {
            @Override
            public void run() {
                Leveler leveler = new Leveler(playerUUID);
                leveler.update();
                cachedLevelers.put(playerUUID, leveler);
                future.complete(leveler);
            }
        }.runTaskAsynchronously(this.plugin);

        return future;
    }

    // ----- TASKS -----

    public void updateCacheTask() {
        if (this.cachingTaskActive.get()) return;
        this.cachingTaskActive.set(true);

        Iterator<Map.Entry<UUID, Leveler>> iterator = this.cachedLevelers.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, Leveler> entry = iterator.next();

            if (!entry.getValue().isValid()) {
                iterator.remove();
                continue;
            }

            Player player = this.plugin.getServer().getPlayer(entry.getKey());
            if (player == null) {
                iterator.remove();
                continue;
            }

            entry.getValue().updateAsync();
        }

        this.cachingTaskActive.set(false);
    }

    // ----- EVENTS -----

    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {

    }

    @EventHandler
    public void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        this.cachedLevelers.remove(event.getPlayer().getUniqueId());
    }

}

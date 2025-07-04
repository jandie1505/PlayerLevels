package net.jandie1505.playerlevels.core.leveler;

import net.jandie1505.playerlevels.core.constants.ConfigKeys;
import net.jandie1505.playerlevels.core.database.Database;
import net.jandie1505.playerlevels.core.events.LevelUpEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public final class Leveler implements net.jandie1505.playerlevels.api.core.level.Leveler {
    @NotNull private final LevelingManager manager;
    @NotNull private final UUID playerUUID;
    @NotNull private final Database db;
    @NotNull private final AtomicBoolean databaseUpdateInProgress;
    @NotNull private final AtomicBoolean manageValuesInProgress;
    @NotNull private final LevelerData data;
    @NotNull private String updateId;

    public Leveler(@NotNull LevelingManager manager, @NotNull UUID playerUUID, @NotNull Database db) {
        this.manager = manager;
        this.playerUUID = playerUUID;
        this.db = db;
        this.databaseUpdateInProgress = new AtomicBoolean(false);
        this.manageValuesInProgress = new AtomicBoolean(false);
        this.data = new LevelerData();
        this.updateId = UUID.randomUUID().toString();
    }

    // ----- GET VALUES -----

    public @NotNull UUID getPlayerUUID() {
        return this.playerUUID;
    }

    public @NotNull LevelerData getData() {
        return data;
    }

    public @NotNull String getUpdateId() {
        return updateId;
    }

    public boolean isCached() {
        return this.manager.getLeveler(this.playerUUID) == this;
    }

    // ----- COMBINED METHODS -----

    /**
     * First calls {@link #sync()}, then {@link #process()}.<br/>
     * Exists for {@link #syncAndProcessAsynchronously()}.
     */
    public void syncAndProcess() {
        this.sync();
        this.process();
    }

    /**
     * First calls {@link #process()}, then {@link #sync()}.<br/>
     * Exists for {@link #processAndSyncAsynchronously()}.
     */
    public void processAndSync() {
        this.process();
        this.sync();
    }

    /**
     * Syncs the leveler with the database, then processes the updated data.
     */
    public void syncAndProcessAsynchronously() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Leveler.this.syncAndProcess();
            }
        }.runTaskAsynchronously(this.manager.getPlugin());
    }

    /**
     * First processes the data of the leveler, then syncs it with the database.
     */
    public void processAndSyncAsynchronously() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Leveler.this.processAndSync();
            }
        }.runTaskAsynchronously(this.manager.getPlugin());
    }

    // ----- MANAGE VALUES -----

    /**
     * Processes the leveler asynchronously.
     */
    public void processAsynchronously() {

        if (this.manageValuesInProgress.get()) {
            throw new IllegalStateException("Process task of Leveler " + this.playerUUID + " has been called while still in progress (async call). This can be caused by unsupported API usage.");
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                Leveler.this.process();
            }
        }.runTaskAsynchronously(this.manager.getPlugin());
    }

    /**
     * Processes the leveler.<br/>
     * This means running the leveling process and applying rewards.
     */
    public void process() {

        // Prevent executing this more than once at the same time
        if (!this.manageValuesInProgress.compareAndSet(false, true)) {
            throw new IllegalStateException("Process task of Leveler " + this.playerUUID + " has been called while still in progress. This can be caused by unsupported API usage.");
        }

        try {
            this.levelUp();
            this.manager.getPlugin().getRewardsManager().processPlayer(this);
            if (this.manager.getPlugin().config().optBoolean(ConfigKeys.REMOVE_NON_EXISTENT_REWARD_ENTRIES, false)) this.cleanupNotExistingRewards();
            this.updateCachedName();
        } catch (Exception e) {
            this.manager.getPlugin().getLogger().log(Level.SEVERE, "Manage values task of " + this.playerUUID + " threw an exception", e);
        }

        this.manageValuesInProgress.set(false);
    }

    /**
     * Level up a player when the required amount of xp for the new level has been reached.
     * @return LevelUpEvent or null if the player has not leveled up
     */
    private @Nullable LevelUpEvent levelUp() {
        final int levelAtStart = this.data.level();
        final double xpAtStart = this.data.xp();

        int level = levelAtStart;
        double xp = xpAtStart;
        double requiredXP = this.manager.getXPForNextLevel(level, level + 1);
        int maxIterations = 100;
        while (maxIterations-- > 0 && xp >= requiredXP) {
            level++;
            xp -= requiredXP;
            requiredXP = this.manager.getXPForNextLevel(level, level + 1);
        }

        if (level == levelAtStart) return null;

        this.data.level(level);
        this.data.xp(xp);

        final LevelUpEvent event = new LevelUpEvent(Leveler.this, levelAtStart, level);

        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getServer().getPluginManager().callEvent(event);
            }
        }.runTask(this.manager.getPlugin());

        return event;
    }

    /**
     * Removes reward entries for rewards that do not exist.
     */
    private void cleanupNotExistingRewards() {

        Iterator<String> i = this.getData().internalReceivedRewards().keySet().iterator();
        while (i.hasNext()) {
            String key = i.next();
            if (this.manager.getPlugin().getRewardsManager().getReward(key) != null) continue;
            i.remove();
        }

    }

    /**
     * Updates the cached name of the player.
     */
    private void updateCachedName() {
        Player player = Bukkit.getPlayer(this.playerUUID);
        if (player == null) return;
        this.data.cachedName(player.getName());
    }

    // ----- DATABASE SYNC -----

    /**
     * Synchronizes the Leveler asynchronously with the database.<br/>
     * The recommended way for updating.
     * @return future result
     */
    public CompletableFuture<SyncResult> syncAsynchronously() {

        if (this.databaseUpdateInProgress.get()) {
            this.manager.getPlugin().getLogger().warning("Sync task of Leveler " + this.playerUUID + " has been called while still in progress (async call). This can be caused by unsupported API usage.");
        }

        CompletableFuture<SyncResult> future = new CompletableFuture<>();
        new BukkitRunnable() {
            @Override
            public void run() {
                SyncResult result = Leveler.this.sync();
                future.complete(result);
            }
        }.runTaskAsynchronously(this.manager.getPlugin());
        return future;
    }

    /**
     * Synchronizes the Leveler with the database.<br/>
     * This is a blocking method.
     * Don't use this from the servers main thread.
     * @return result
     */
    public @NotNull net.jandie1505.playerlevels.api.core.level.Leveler.SyncResult sync() {

        // Prevent executing this more than once at the same time
        if (!this.databaseUpdateInProgress.compareAndSet(false, true)) {
            this.manager.getPlugin().getLogger().warning("Sync task of Leveler " + this.playerUUID + " has been called while still in progress. This can be caused by unsupported API usage.");
            return SyncResult.ALREADY_IN_PROGRESS;
        }

        try {

            Database.LevelDataPullResult pullResult = this.db.getLevelDataFromDatabase(this.playerUUID);

            if (pullResult != null) {
                // Current data is outdated, replace it with database data
                if (!this.updateId.equals(pullResult.updateId())) {
                    this.data.merge(pullResult.data());
                    this.updateId = pullResult.updateId();
                    System.out.println("Local outdated");
                    return SyncResult.LOCAL_OUTDATED;
                }

                // Remote data is outdated, push changes
                if (!this.data.equals(pullResult.data())) {
                    this.db.updateLevelDataInDatabase(this.playerUUID, this.data, this.updateId);
                    System.out.println("Remote outdated");
                    return SyncResult.REMOTE_OUTDATED_AVAIL;
                }

            } else {

                if (this.getData().isDefault()) {
                    System.out.println("Remote not avail, only default values");
                    return SyncResult.REMOTE_MISSING_DEFAULT;
                }

                this.db.insertLevelDataIntoDatabase(this.playerUUID, this.data, this.updateId);
                System.out.println("Remote not avail");
                return SyncResult.REMOTE_OUTDATED_MISSING;
            }

            return SyncResult.UP_TO_DATE;
        } catch (Exception e) {
            this.manager.getPlugin().getLogger().log(Level.WARNING, "Exception syncing leveler data", e);
            return SyncResult.ERROR;
        } finally {
            this.databaseUpdateInProgress.set(false);
        }
    }

}

package net.jandie1505.playerlevels.api.core.level;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * A leveling player.<br/>
 * Each player has a leveler, which handles the leveling for that player.
 */
public interface Leveler {

    // ----- VALUES -----

    /**
     * Returns the player's uuid.
     * @return player UUID
     */
    @NotNull UUID getPlayerUUID();

    /**
     * Returns the data of the player.<br/>
     * The data stores information like the level, xp or received rewards.
     * @return data
     */
    @NotNull LevelerData getData();

    /**
     * Returns true if the leveler is currently cached.<br/>
     * You should avoid using not cached levelers, specially when the player is online.<br/>
     * Using uncached levelers can cause database conflicts.
     * While the database is protected, only one data can be written to the database.
     * @return if player is cached
     */
    boolean isCached();

    // ----- TASKS -----

    /**
     * Processes the Leveler.<br/>
     * This means doing the levelling process and applying rewards.
     */
    void processAsynchronously();

    /**
     * Synchronizes the leveler with the database.<br/>
     * The result returns what has been done.
     * @return result
     * @throws IllegalStateException if an update is already in progress
     */
    CompletableFuture<SyncResult> syncAsynchronously();

    // ----- INNER CLASSES -----

    /**
     * The result of the database sync.
     */
    enum SyncResult {

        /**
         * Local leveler is outdated.<br/>
         * Pull from database.
         */
        LOCAL_OUTDATED(true, false, false),

        /**
         * Remote is outdated, but leveler is already available.<br/>
         * Update existing leveler entry in the database.
         */
        REMOTE_OUTDATED_AVAIL(false, true, false),

        /**
         * The leveler is not in the database and has been pushed to the database.<br/>
         * Create new leveler entry in the database.
         */
        REMOTE_OUTDATED_MISSING(false, true, false),

        /**
         * The leveler is not in the database and has not been pushed because it only consists of default values.
         */
        REMOTE_MISSING_DEFAULT(false, false, false),

        /**
         * Leveler is up-to-date with the database.<br/>
         * Nothing has to be done.
         */
        UP_TO_DATE(false, false, false),

        /**
         * An error occurred while synchronizing.<br/>
         * The error has been logged.
         */
        ERROR(true, false, true),

        /**
         * The sync task is already in progress.<br/>
         * It can not run multiple times at the same time.
         */
        ALREADY_IN_PROGRESS(false, false, true);

        private final boolean localChanged;
        private final boolean remoteChanged;
        private final boolean fail;

        SyncResult(boolean localChanged, boolean remoteChanged, boolean fail) {
            this.localChanged = localChanged;
            this.remoteChanged = remoteChanged;
            this.fail = fail;
        }

        /**
         * Returns true if the local Leveler has been changed.
         * @return local changed
         */
        public boolean isLocalChanged() {
            return localChanged;
        }

        /**
         * Returns true if the remote Leveler entry has been changed.
         * @return remote changed
         */
        public boolean isRemoteChanged() {
            return remoteChanged;
        }

        /**
         * Returns true if the sync task has failed.
         * @return sync failed
         */
        public boolean isFail() {
            return fail;
        }

    }

}

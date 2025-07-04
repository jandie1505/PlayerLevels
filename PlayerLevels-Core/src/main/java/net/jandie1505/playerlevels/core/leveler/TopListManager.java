package net.jandie1505.playerlevels.core.leveler;

import net.jandie1505.playerlevels.core.PlayerLevels;
import net.jandie1505.playerlevels.core.constants.ConfigKeys;
import net.jandie1505.playerlevels.core.database.Database;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TopListManager implements net.jandie1505.playerlevels.api.core.level.TopListManager {
    @NotNull private final PlayerLevels plugin;
    @NotNull private final Database database;
    @NotNull private final List<TopListEntry> cachedTopList;

    public TopListManager(@NotNull PlayerLevels plugin, @NotNull Database database) {
        this.plugin = plugin;
        this.database = database;
        this.cachedTopList = new ArrayList<>();
    }

    public List<TopListEntry> getTopList() {
        return Collections.unmodifiableList(this.cachedTopList);
    }

    public CompletableFuture<List<TopListEntry>> updateTopListAsynchronously() {
        CompletableFuture<List<TopListEntry>> future = new CompletableFuture<>();
        new BukkitRunnable() {
            @Override
            public void run() {
                List<TopListEntry> result =  TopListManager.this.updateTopList();
                future.complete(result);
            }
        }.runTaskAsynchronously(plugin);
        return future;
    }

    public List<TopListEntry> updateTopList() {

        int until = this.plugin.config().optInt(ConfigKeys.TOP_LIST_LENGTH, 10);

        List<TopListEntry> topList = this.database.getTopList(until);
        if (topList == null) {
            this.cachedTopList.clear();
            return this.cachedTopList;
        }

        this.cachedTopList.clear();
        this.cachedTopList.addAll(topList);
        return this.cachedTopList;
    }

}

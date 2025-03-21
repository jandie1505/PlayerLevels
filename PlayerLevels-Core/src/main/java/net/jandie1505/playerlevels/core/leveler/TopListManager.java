package net.jandie1505.playerlevels.core.leveler;

import net.jandie1505.playerlevels.core.PlayerLevels;
import net.jandie1505.playerlevels.core.constants.ConfigKeys;
import net.jandie1505.playerlevels.core.database.DatabaseSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class TopListManager implements net.jandie1505.playerlevels.api.core.level.TopListManager {
    @NotNull private final PlayerLevels plugin;
    @NotNull private final DatabaseSource databaseSource;
    @NotNull private final List<TopListEntry> cachedTopList;

    public TopListManager(@NotNull PlayerLevels plugin, @NotNull DatabaseSource databaseSource) {
        this.plugin = plugin;
        this.databaseSource = databaseSource;
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

        String sql = "SELECT player_uuid, data, level FROM playerlevels_players ORDER BY level DESC LIMIT ?";

        try (Connection connection = this.databaseSource.getConnection();
             PreparedStatement statement = connection != null ? connection.prepareStatement(sql) : null;
        ) {

            if (connection == null || statement == null) {
                this.plugin.getLogger().warning("Failed to get top list: connection is null");
                this.cachedTopList.clear();
                return this.cachedTopList;
            }

            statement.setInt(1, until);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<TopListEntry> topList = new ArrayList<>();
                while (resultSet.next()) {
                    JSONObject json = new JSONObject(resultSet.getString("data"));
                    topList.add(new TopListEntry(UUID.fromString(resultSet.getString("player_uuid")), json.optString("name", null), json.getInt("level"), json.getDouble("xp")));
                }
                this.cachedTopList.clear();
                this.cachedTopList.addAll(topList);
                return this.cachedTopList;
            } catch (Exception e) {
                this.cachedTopList.clear();
                return this.cachedTopList;
            }

        } catch (SQLException e) {
            this.cachedTopList.clear();
            return this.cachedTopList;
        }

    }

}

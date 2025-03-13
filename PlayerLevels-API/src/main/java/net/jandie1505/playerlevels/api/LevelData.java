package net.jandie1505.playerlevels.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface LevelData {
    int level();
    void level(int level);
    double xp();
    void xp(double xp);
    @NotNull Map<String, ReceivedRewardData> getReceivedRewards();
    @Nullable ReceivedRewardData getReceivedReward(String id);
    @NotNull ReceivedRewardData getOrCreateReceivedReward(String id);
    void removeReceivedReward(String id);
}

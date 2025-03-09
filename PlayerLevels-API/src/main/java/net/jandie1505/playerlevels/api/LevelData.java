package net.jandie1505.playerlevels.api;

import java.util.Set;

public interface LevelData {
    int level();
    void level(int level);
    double xp();
    void xp(double xp);
    Set<String> receivedRewards();
}

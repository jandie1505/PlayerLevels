package net.jandie1505.playerlevels.rewards;

import net.jandie1505.playerlevels.api.PlayerLevelsAPI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class Reward {
    @NotNull private final PlayerLevelsAPI api;

    public Reward(@NotNull PlayerLevelsAPI api) {
        this.api = api;
    }

    public final void reward(@NotNull Player player) {
        if (this.hasReward(player)) return;
        this.applyReward(player);
    }

    protected void applyReward(@NotNull Player player) {}

    public boolean hasReward(@NotNull Player player) {
        return false;
    }

}

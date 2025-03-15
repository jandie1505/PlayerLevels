package net.jandie1505.playerlevels.api.level;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface LevelPlayer {
    @NotNull UUID getPlayerUUID();
    @NotNull LevelerData getData();
}

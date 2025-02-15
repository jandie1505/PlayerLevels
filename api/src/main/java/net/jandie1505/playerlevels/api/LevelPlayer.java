package net.jandie1505.playerlevels.api;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface LevelPlayer {
    @NotNull UUID getPlayerUUID();
    @NotNull LevelData getData();
}

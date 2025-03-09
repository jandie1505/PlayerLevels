package net.jandie1505.playerlevels.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface LevelManager {
    @Nullable LevelPlayer getLeveler(@NotNull UUID playerUUID);
    @NotNull CompletableFuture<LevelPlayer> loadLeveler(@NotNull UUID playerUUID);
}

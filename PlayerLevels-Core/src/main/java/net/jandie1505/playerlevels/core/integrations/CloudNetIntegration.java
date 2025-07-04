package net.jandie1505.playerlevels.core.integrations;

import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.wrapper.configuration.WrapperConfiguration;
import net.jandie1505.playerlevels.core.PlayerLevels;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;

public final class CloudNetIntegration {

    private CloudNetIntegration() {}

    public static @Nullable String getTaskId(@NotNull PlayerLevels plugin) {
        if (!Bukkit.getPluginManager().isPluginEnabled("CloudNet-Bridge")) return null;

        try {
            Class.forName("eu.cloudnetservice.driver.inject.InjectionLayer");
            Class.forName("eu.cloudnetservice.wrapper.configuration.WrapperConfiguration");

            WrapperConfiguration wrapperConfiguration = InjectionLayer.ext().instance(WrapperConfiguration.class);
            return wrapperConfiguration.serviceConfiguration().serviceId().taskName();
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Exception while getting CloudNet task id", e);
            return null;
        }

    }

}

package net.jandie1505.playerlevels.cloudnet;

import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.wrapper.configuration.WrapperConfiguration;
import net.jandie1505.playerlevels.api.core.PlayerLevelsAPI;
import net.jandie1505.playerlevels.core.PlayerLevelsAPIProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class PlayerLevelsCloudNet extends JavaPlugin {

    @Override
    public void onEnable() {
        PlayerLevelsAPI api = PlayerLevelsAPIProvider.getApi();
        if (api == null) return;

        try {
            Class.forName("eu.cloudnetservice.driver.inject.InjectionLayer");
            Class.forName("eu.cloudnetservice.wrapper.configuration.WrapperConfiguration");

            WrapperConfiguration wrapperConfiguration = InjectionLayer.ext().instance(WrapperConfiguration.class);
            String taskId = wrapperConfiguration.serviceConfiguration().serviceId().taskName();

            api.setServerIdOverride(taskId);
            this.getLogger().info("Successfully set PlayerLevels server id to " + taskId);
        } catch (ClassNotFoundException e) {
            this.getLogger().log(Level.SEVERE, "Failed to set CloudNet API", e);
        } catch (Exception e) {
            this.getLogger().log(Level.SEVERE, "Exception while setting server id", e);
        }
    }

}

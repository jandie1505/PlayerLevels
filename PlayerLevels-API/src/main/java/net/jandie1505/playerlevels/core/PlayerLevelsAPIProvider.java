package net.jandie1505.playerlevels.core;

import net.jandie1505.playerlevels.api.core.PlayerLevelsAPI;

/**
 * Class for getting the api.
 */
public class PlayerLevelsAPIProvider {
    private static PlayerLevelsAPI api;

    private PlayerLevelsAPIProvider() {}

    /**
     * Returns the API.
     * @return api
     */
    public static PlayerLevelsAPI getApi() {
        return api;
    }

    static void setApi(PlayerLevelsAPI api) {
        PlayerLevelsAPIProvider.api = api;
    }

}

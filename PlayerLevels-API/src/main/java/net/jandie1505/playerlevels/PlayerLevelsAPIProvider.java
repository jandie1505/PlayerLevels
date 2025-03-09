package net.jandie1505.playerlevels;

import net.jandie1505.playerlevels.api.PlayerLevelsAPI;

/**
 * Class for getting the api.
 */
public class PlayerLevelsAPIProvider {
    private static PlayerLevelsAPI api;

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

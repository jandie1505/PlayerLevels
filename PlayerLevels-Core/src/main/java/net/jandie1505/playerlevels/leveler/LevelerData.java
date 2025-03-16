package net.jandie1505.playerlevels.leveler;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class LevelerData implements net.jandie1505.playerlevels.api.level.LevelerData {
    private static final LevelerData DEFAULT = new LevelerData();
    private int level;
    private double xp;
    @NotNull private HashMap<String, ReceivedReward> receivedRewards;

    public LevelerData() {
        this.level = 0;
        this.xp = 0;
        this.receivedRewards = new HashMap<>();
    }

    // ----- VALUES -----

    public int level() {
        return level;
    }

    public void level(int level) {
        this.level = level;
    }

    public double xp() {
        return xp;
    }

    public void xp(double xp) {
        this.xp = xp;
    }

    public @NotNull Map<String, net.jandie1505.playerlevels.api.level.ReceivedReward> getReceivedRewards() {
        return Collections.unmodifiableMap(this.receivedRewards);
    }

    public @NotNull ReceivedReward getOrCreateReceivedReward(@NotNull String id) {
        return this.receivedRewards.computeIfAbsent(id, k -> new ReceivedReward());
    }

    public void removeReceivedReward(@NotNull String id) {
        this.receivedRewards.remove(id);
    }

    public @Nullable ReceivedReward getReceivedReward(@NotNull String id) {
        return this.receivedRewards.get(id);
    }

    /**
     * Returns the received rewards where the changes are not tracked.
     * @return delegate of the tracked set
     */
    @ApiStatus.Internal
    public HashMap<String, ReceivedReward> internalReceivedRewards() {
        return this.receivedRewards;
    }

    // ----- MERGE -----

    public void merge(LevelerData levelerData) {

        this.level(levelerData.level());
        this.xp(levelerData.xp());

        this.receivedRewards.clear();
        for (Map.Entry<String, ReceivedReward> entry : levelerData.receivedRewards.entrySet()) {

            ReceivedReward reward = this.receivedRewards.get(entry.getKey());
            if (reward != null) {
                reward.merge(entry.getValue());
                continue;
            }

            this.receivedRewards.put(entry.getKey(), entry.getValue().clone());
        }
        this.receivedRewards.putAll(levelerData.receivedRewards);
    }

    // ----- SERIALIZATION -----

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();

        json.put("level", this.level);
        json.put("xp", this.xp);

        JSONObject receivedRewards = new JSONObject();
        for (Map.Entry<String, ReceivedReward> entry : this.receivedRewards.entrySet()) {
            if (entry.getValue().isDefault()) continue;
            receivedRewards.put(entry.getKey(), entry.getValue().toJSON());
        }
        json.put("receivedRewards", receivedRewards);

        return json;
    }

    public static LevelerData fromJSON(JSONObject json) throws JSONException {
        LevelerData levelerData = new LevelerData();

        levelerData.level = json.optInt("level", 0);
        levelerData.xp = json.optDouble("xp", 0);

        JSONObject receivedRewards = json.optJSONObject("receivedRewards", null);
        if (receivedRewards != null) {
            for (String key : receivedRewards.keySet()) {
                JSONObject entry = receivedRewards.optJSONObject(key, null);
                if (entry == null) continue;
                levelerData.receivedRewards.put(key, ReceivedReward.fromJSON(entry));
            }
        }

        return levelerData;
    }

    @Override
    public String toString() {
        return this.toJSON().toString();
    }

    // ----- COMPARE -----

    @Override
    public int hashCode() {

        // Filter default rewards since they will be removed when pushing to database
        HashMap<String, ReceivedReward> receivedRewards = new HashMap<>();
        for (Map.Entry<String, ReceivedReward> entry : this.receivedRewards.entrySet()) {
            if (entry.getValue().isDefault()) continue;
            receivedRewards.put(entry.getKey(), entry.getValue());
        }

        return Objects.hash(this.level, this.xp, receivedRewards);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LevelerData levelerData)) return false;
        return this.hashCode() == levelerData.hashCode();
    }

    public boolean isDefault() {
        return this.equals(DEFAULT);
    }

}

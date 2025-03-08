package net.jandie1505.playerlevels.leveler;

import net.jandie1505.playerlevels.api.LevelData;
import net.jandie1505.playerlevels.utils.TrackedSet;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class LevelerData implements LevelData {
    private int level;
    private double xp;
    @NotNull private TrackedSet<String> receivedRewards;
    @NotNull private Callback callback;

    public LevelerData(@Nullable Callback callback) {
        this.level = 0;
        this.xp = 0;
        this.receivedRewards = new TrackedSet<>(new HashSet<>(), (set, action, s, result) -> this.callback.onUpdate(this));
        this.callback = callback != null ? callback : data -> {};
    }

    // ----- VALUES -----

    public int level() {
        return level;
    }

    public void level(int level) {
        this.level(level, true);
    }

    public void level(int level, boolean call) {
        this.level = level;
        if (call) this.callback.onUpdate(this);
    }

    public double xp() {
        return xp;
    }

    public void xp(double xp) {
        this.xp(xp, true);
    }

    public void xp(double xp, boolean call) {
        this.xp = xp;
        if (call) this.callback.onUpdate(this);
    }

    public Set<String> receivedRewards() {
        return this.receivedRewards;
    }

    /**
     * Returns the received rewards where the changes are not tracked.
     * @return delegate of the tracked set
     */
    @ApiStatus.Internal
    public Set<String> untrackedReceivedRewards() {
        return this.receivedRewards.getDelegate();
    }

    // ----- MERGE -----

    public void merge(LevelerData levelerData, boolean call) {
        this.level(levelerData.level(), false);
        this.xp(levelerData.xp(), false);
        this.receivedRewards.getDelegate().clear();
        this.receivedRewards.getDelegate().addAll(levelerData.receivedRewards.getDelegate());
        if (call) this.callback.onUpdate(this);
    }

    // ----- JSON -----

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();

        json.put("level", this.level);
        json.put("xp", this.xp);

        JSONArray receivedRewards = new JSONArray();
        for (String reward : this.receivedRewards) {
            receivedRewards.put(reward);
        }
        json.put("receivedRewards", receivedRewards);

        return json;
    }

    public static LevelerData fromJSON(JSONObject json, @Nullable Callback callback) throws JSONException {
        LevelerData levelerData = new LevelerData(callback);

        levelerData.level = json.getInt("level");
        levelerData.xp = json.getDouble("xp");

        JSONArray receivedRewards = json.getJSONArray("receivedRewards");
        for (Object reward : receivedRewards) {
            levelerData.receivedRewards.getDelegate().add(reward.toString());
        }

        return levelerData;
    }

    // ----- COMPARE -----

    @Override
    public int hashCode() {
        return Objects.hash(level, xp);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LevelerData levelerData)) return false;
        return this.hashCode() == levelerData.hashCode();
    }

    // ----- CALLBACK -----

    public interface Callback {
        void onUpdate(LevelerData levelerData);
    }

}

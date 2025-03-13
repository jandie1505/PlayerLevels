package net.jandie1505.playerlevels.leveler;

import net.jandie1505.playerlevels.api.LevelData;
import net.jandie1505.playerlevels.api.ReceivedRewardData;
import net.jandie1505.playerlevels.utils.TrackedMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class LevelerData implements LevelData {
    private static final LevelerData DEFAULT = new LevelerData(null);
    private int level;
    private double xp;
    @NotNull private TrackedMap<String, ReceivedReward> receivedRewards;
    @NotNull private Callback callback;

    public LevelerData(@Nullable Callback callback) {
        this.level = 0;
        this.xp = 0;
        this.receivedRewards = new TrackedMap<>(
                new HashMap<>(),
                (_, _, _, _, _) -> this.callback.onUpdate(this)
        );
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

    public @NotNull Map<String, ReceivedRewardData> getReceivedRewards() {
        return Collections.unmodifiableMap(this.receivedRewards);
    }

    @ApiStatus.Internal
    public @NotNull ReceivedReward getOrCreateReceivedReward(@NotNull String id, boolean call) {
        AtomicBoolean modified = new AtomicBoolean(false);
        ReceivedReward reward = this.receivedRewards.computeIfAbsent(id, _ -> {
            modified.set(true);
            return new ReceivedReward(_ -> this.callback.onUpdate(this));
        });
        if (modified.get()) this.callback.onUpdate(this);
        return reward;
    }

    public @NotNull ReceivedReward getOrCreateReceivedReward(@NotNull String id) {
        return this.getOrCreateReceivedReward(id, true);
    }

    @ApiStatus.Internal
    public void removeReceivedReward(@NotNull String id, boolean call) {
        ReceivedRewardData out = this.receivedRewards.remove(id);
        if (call && out != null) this.callback.onUpdate(this);
    }

    public void removeReceivedReward(@NotNull String id) {
        this.removeReceivedReward(id, true);
    }

    public @Nullable ReceivedReward getReceivedReward(@NotNull String id) {
        return this.receivedRewards.get(id);
    }

    /**
     * Returns the received rewards where the changes are not tracked.
     * @return delegate of the tracked set
     */
    @ApiStatus.Internal
    public TrackedMap<String, ReceivedReward> internalReceivedRewards() {
        return this.receivedRewards;
    }

    // ----- MERGE -----

    public void merge(LevelerData levelerData, boolean call) {

        this.level(levelerData.level(), false);
        this.xp(levelerData.xp(), false);
        this.receivedRewards.getDelegate().clear();
        this.receivedRewards.getDelegate().putAll(levelerData.receivedRewards.getDelegate());
        if (call) this.callback.onUpdate(this);
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

    public static LevelerData fromJSON(JSONObject json, @Nullable Callback callback) throws JSONException {
        LevelerData levelerData = new LevelerData(callback);

        levelerData.level = json.optInt("level", 0);
        levelerData.xp = json.optDouble("xp", 0);

        JSONObject receivedRewards = json.optJSONObject("receivedRewards", null);
        if (receivedRewards != null) {
            for (Map.Entry<String, Object> entry : receivedRewards.toMap().entrySet()) {
                if (!(entry.getValue() instanceof JSONObject jsonEntry)) continue;
                levelerData.receivedRewards.getDelegate().put(entry.getKey(), ReceivedReward.fromJSON(jsonEntry, _ -> levelerData.callback.onUpdate(levelerData)));
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
        return Objects.hash(level, xp, receivedRewards);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LevelerData levelerData)) return false;
        return this.hashCode() == levelerData.hashCode();
    }

    public boolean isDefault() {
        return this.equals(DEFAULT);
    }

    // ----- CALLBACK -----

    public interface Callback {
        void onUpdate(LevelerData levelerData);
    }

}

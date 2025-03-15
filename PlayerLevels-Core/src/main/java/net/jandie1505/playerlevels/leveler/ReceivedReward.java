package net.jandie1505.playerlevels.leveler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.Objects;

public class ReceivedReward implements net.jandie1505.playerlevels.api.level.ReceivedReward {
    private static final ReceivedReward DEFAULT = new ReceivedReward(null);
    @NotNull private final Callback callback;
    private boolean blocked;
    private int level;

    public ReceivedReward(@Nullable Callback callback) {
        this.blocked = false;
        this.level = 0;
        this.callback = callback != null ? callback : reward -> {};
    }

    // ----- DATA -----

    public boolean blocked() {
        return blocked;
    }

    public void blocked(boolean blocked, boolean call) {
        this.blocked = blocked;
        if (call) this.callback.onUpdate(this);
    }

    public void blocked(boolean blocked) {
        this.blocked(blocked, true);
    }

    public int level() {
        return level;
    }

    public void level(int level, boolean call) {
        this.level = level;
        if (call) this.callback.onUpdate(this);
    }

    public void level(int level) {
        this.level(level, true);
    }

    // ----- MERGE -----

    /**
     * Replaces the data of this ReceivedReward with the data of the specified ReceivedReward.
     * @param reward other reward
     * @param call callback
     */
    public void merge(ReceivedReward reward, boolean call) {
        this.blocked(reward.blocked(), false);
        this.level(reward.level(), false);
        if (call) this.callback.onUpdate(this);
    }

    /**
     * Resets this reward to the default values.<br/>
     * An alternative to deleting the reward because default rewards will not get written to the database.
     */
    public void reset(boolean call) {
        this.merge(DEFAULT, call);
    }

    public void reset() {
        this.merge(DEFAULT, true);
    }

    public ReceivedReward clone(@Nullable Callback callback) {
        ReceivedReward reward = new ReceivedReward(callback);
        reward.merge(this, false);
        return reward;
    }

    // ----- SERIALIZATION -----

    public @NotNull JSONObject toJSON() {
        JSONObject json = new JSONObject();

        json.put("blocked", this.blocked);
        json.put("level", this.level);

        return json;
    }

    public static @NotNull ReceivedReward fromJSON(@NotNull JSONObject json, @Nullable Callback callback) {
        ReceivedReward reward = new ReceivedReward(callback);

        reward.blocked = json.optBoolean("blocked", true);
        reward.level = json.optInt("level", 0);

        return reward;
    }

    @Override
    public String toString() {
        return this.toJSON().toString();
    }

    // ----- COMPARE -----

    @Override
    public int hashCode() {
        return Objects.hash(this.blocked, this.level);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ReceivedReward reward)) return false;
        return this.hashCode() == reward.hashCode();
    }

    public boolean isDefault() {
        return this.equals(DEFAULT);
    }

    // ----- INTERFACES -----

    public interface Callback {
        void onUpdate(ReceivedReward reward);
    }

}

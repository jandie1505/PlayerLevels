package net.jandie1505.playerlevels.leveler;

import net.jandie1505.playerlevels.api.ReceivedRewardData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.Objects;

public class ReceivedReward implements ReceivedRewardData {
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
        this.level(reward.level(), false);
        if (call) this.callback.onUpdate(this);
    }

    public ReceivedReward clone(@Nullable Callback callback) {
        ReceivedReward reward = new ReceivedReward(callback);

        reward.level = this.level;

        return reward;
    }

    // ----- SERIALIZATION -----

    public @NotNull JSONObject toJSON() {
        JSONObject json = new JSONObject();

        json.put("level", this.level);

        return json;
    }

    public static @NotNull ReceivedReward fromJSON(@NotNull JSONObject json, @Nullable Callback callback) {
        ReceivedReward reward = new ReceivedReward(callback);

        reward.level = json.getInt("level");

        return reward;
    }

    @Override
    public String toString() {
        return this.toJSON().toString();
    }

    // ----- COMPARE -----

    @Override
    public int hashCode() {
        return Objects.hash(this.level);
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

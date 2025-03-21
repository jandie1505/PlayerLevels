package net.jandie1505.playerlevels.core.leveler;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.Objects;

public class ReceivedReward implements net.jandie1505.playerlevels.api.core.level.ReceivedReward {
    private static final ReceivedReward DEFAULT = new ReceivedReward();
    private boolean blocked;
    private int level;

    public ReceivedReward() {
        this.blocked = false;
        this.level = 0;
    }

    // ----- DATA -----

    public boolean blocked() {
        return blocked;
    }

    public void blocked(boolean blocked) {
        this.blocked = blocked;
    }

    public int level() {
        return level;
    }

    public void level(int level) {
        this.level = level;
    }

    // ----- MERGE -----

    /**
     * Replaces the data of this ReceivedReward with the data of the specified ReceivedReward.
     * @param reward other reward
     */
    public void merge(ReceivedReward reward) {
        this.blocked(reward.blocked());
        this.level(reward.level());
    }

    /**
     * Resets this reward to the default values.<br/>
     * An alternative to deleting the reward because default rewards will not get written to the database.
     */
    public void reset() {
        this.merge(DEFAULT);
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public ReceivedReward clone() {
        ReceivedReward reward = new ReceivedReward();
        reward.merge(this);
        return reward;
    }

    // ----- SERIALIZATION -----

    public @NotNull JSONObject toJSON() {
        JSONObject json = new JSONObject();

        json.put("blocked", this.blocked);
        json.put("level", this.level);

        return json;
    }

    public static @NotNull ReceivedReward fromJSON(@NotNull JSONObject json) {
        ReceivedReward reward = new ReceivedReward();

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

}

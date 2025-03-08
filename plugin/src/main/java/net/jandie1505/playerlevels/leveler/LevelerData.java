package net.jandie1505.playerlevels.leveler;

import net.jandie1505.playerlevels.api.LevelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class LevelerData implements LevelData {
    private int level;
    private double xp;
    @NotNull private Callback callback;

    public LevelerData(@Nullable Callback callback) {
        this.level = 0;
        this.xp = 0;
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

    // ----- MERGE -----

    public void merge(LevelerData levelerData, boolean call) {
        this.level(levelerData.level(), false);
        this.xp(levelerData.xp(), false);
        if (call) this.callback.onUpdate(this);
    }

    // ----- JSON -----

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();

        json.put("level", this.level);
        json.put("xp", this.xp);

        return json;
    }

    public static LevelerData fromJSON(JSONObject json, @Nullable Callback callback) throws JSONException {
        LevelerData levelerData = new LevelerData(callback);

        levelerData.level = json.getInt("level");
        levelerData.xp = json.getDouble("xp");

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

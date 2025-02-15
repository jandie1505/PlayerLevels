package net.jandie1505.playerlevels.leveler;

import net.jandie1505.playerlevels.api.LevelData;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class LevelerData implements LevelData {
    private int level;
    private double xp;

    public LevelerData() {
        this.level = 0;
        this.xp = 0;
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

    // ----- MERGE -----

    public void merge(LevelerData levelerData) {
        this.level(levelerData.level());
        this.xp(levelerData.xp());
    }

    // ----- JSON -----

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();

        json.put("level", this.level);
        json.put("xp", this.xp);

        return json;
    }

    public static LevelerData fromJSON(JSONObject json) throws JSONException {
        LevelerData levelerData = new LevelerData();

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

}

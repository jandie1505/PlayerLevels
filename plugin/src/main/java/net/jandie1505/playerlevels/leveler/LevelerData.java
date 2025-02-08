package net.jandie1505.playerlevels.leveler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class LevelerData {
    private int level;
    private double xp;
    
    // ----- CONSTRUCTORS -----

    public LevelerData(int level, double xp) {
        this.level = level;
        this.xp = xp;
    }

    public LevelerData() {
        this(0, 0);
    }
    
    // ----- VALUES -----

    public int level() {
        return level;
    }

    public double xp() {
        return xp;
    }

    public void level(int level) {
        this.level = level;
    }

    public void xp(double xp) {
        this.xp = xp;
    }

    // ----- MERGE -----

    public void merge(LevelerData levelerData) {
        this.level = levelerData.level;
        this.xp = levelerData.xp;
    }
    
    // ----- COMPARE -----

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        return this.hashCode() == o.hashCode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.level, this.xp);
    }

    // ----- STATIC -----
    
}

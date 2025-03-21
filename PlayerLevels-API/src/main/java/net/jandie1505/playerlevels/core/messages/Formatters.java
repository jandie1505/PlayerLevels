package net.jandie1505.playerlevels.core.messages;

/**
 * Contains message format utilities.
 */
public final class Formatters {

    private Formatters() {}

    /**
     * Formats the xp number.
     * @param xp xp
     * @return formatted xp
     */
    public static String formatXP(double xp) {
        // Wenn XP kleiner als 1000 ist, gib einfach den Wert aus
        if (xp < 1000) {
            return String.format("%.0f", xp);
        }

        // Falls die Zahl Tausende überschreitet, teile sie durch 1000 und füge "K" hinzu
        if (xp < 1000000) {
            return String.format("%.1fK", xp / 1000);
        }

        // Wenn die Zahl Millionen überschreitet, teile sie durch 1000000 und füge "M" hinzu
        if (xp < 1000000000) {
            return String.format("%.1fM", xp / 1000000);
        }

        // Falls Milliarden überschritten werden, teile sie durch 1000000000 und füge "B" hinzu
        if (xp < 1000000000000L) {
            return String.format("%.1fB", xp / 1000000000);
        }

        // Optional: Falls noch größere Zahlen existieren, könnte man auch "T" für Billionen hinzufügen
        return String.format("%.1fT", xp / 1000000000000L);
    }

}

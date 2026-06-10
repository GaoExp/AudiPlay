package exp.miniplayer.utils;

import java.util.concurrent.TimeUnit;

public class TimeUtils {

    public static String formatDuration(long millis) {
        if (millis <= 0) return "0:00";
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        long secs = seconds % 60;
        if (minutes >= 60) {
            long hours = minutes / 60;
            minutes = minutes % 60;
            return String.format("%d:%02d:%02d", hours, minutes, secs);
        }
        return String.format("%d:%02d", minutes, secs);
    }
}

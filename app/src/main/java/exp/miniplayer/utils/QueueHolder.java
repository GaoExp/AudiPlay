package exp.miniplayer.utils;

import exp.miniplayer.model.Audio;

import java.util.ArrayList;
import java.util.List;

public class QueueHolder {
    private static List<Audio> queue = new ArrayList<>();
    private static int startIndex = 0;

    public static void setQueue(List<Audio> q, int index) {
        queue = new ArrayList<>(q);
        startIndex = index;
    }

    public static List<Audio> getQueue() {
        return new ArrayList<>(queue);
    }

    public static int getStartIndex() {
        return startIndex;
    }

    public static void clear() {
        queue = new ArrayList<>();
        startIndex = 0;
    }
}

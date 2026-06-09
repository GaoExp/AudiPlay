package exp.miniplayer.utils;

import android.content.Context;
import android.os.FileObserver;
import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import exp.miniplayer.data.AudioRepository;
import exp.miniplayer.model.Audio;

public class PlaylistFileWatcher {

    private static final int MAX_DEPTH = 8;
    private static final long DEBOUNCE_MS = 2000;

    private final Context context;
    private final AudioRepository repo;
    private final Handler handler;
    private final List<FileObserver> observers = new ArrayList<>();
    private Runnable debounceTask;
    private boolean watching;

    public PlaylistFileWatcher(Context context, AudioRepository repo) {
        this.context = context.getApplicationContext();
        this.repo = repo;
        this.handler = new Handler(Looper.getMainLooper());
    }

    public void start() {
        if (watching) return;
        watching = true;

        PreferencesManager prefs = new PreferencesManager(context);
        Set<String> included = prefs.getIncludedFolders();
        Set<String> excluded = prefs.getExcludedFolders();
        boolean limitFolders = prefs.isLimitFolders();

        if (included == null) included = new HashSet<>();
        if (excluded == null) excluded = new HashSet<>();

        Set<String> dirs = new HashSet<>();
        if (limitFolders) {
            if (included.isEmpty()) return;
            dirs.addAll(included);
        } else {
            dirs.addAll(included);
            dirs.add("/storage/emulated/0/Music");
            dirs.add("/storage/emulated/0/Download");
            dirs.add("/storage/emulated/0/Playlists");
        }
        dirs.removeAll(excluded);

        for (String dir : dirs) {
            File folder = new File(dir);
            if (folder.exists() && folder.isDirectory()) {
                watchRecursive(folder, excluded, 0);
            }
        }
    }

    public void stop() {
        watching = false;
        for (FileObserver obs : observers) {
            obs.stopWatching();
        }
        observers.clear();
        if (debounceTask != null) {
            handler.removeCallbacks(debounceTask);
            debounceTask = null;
        }
    }

    private void watchRecursive(File dir, Set<String> excluded, int depth) {
        if (depth > MAX_DEPTH) return;
        if (excluded.contains(dir.getAbsolutePath())) return;

        addObserver(dir);

        File[] files = dir.listFiles();
        if (files == null) return;
        for (File f : files) {
            if (f.isDirectory()) {
                watchRecursive(f, excluded, depth + 1);
            }
        }
    }

    private void addObserver(File dir) {
        FileObserver observer = new FileObserver(dir.getAbsolutePath(),
                FileObserver.CREATE | FileObserver.CLOSE_WRITE | FileObserver.MOVED_TO) {
            @Override
            public void onEvent(int event, String path) {
                if (path == null) return;
                String lower = path.toLowerCase();
                if (lower.endsWith(".m3u") || lower.endsWith(".m3u8")) {
                    triggerRescan();
                }
            }
        };
        observer.startWatching();
        observers.add(observer);
    }

    private void triggerRescan() {
        if (debounceTask != null) {
            handler.removeCallbacks(debounceTask);
        }
        debounceTask = () -> new Thread(() -> {
            List<Audio> scannedAudio = repo.getCachedAudio();
            PlaylistScanner.scanPlaylists(context, repo, scannedAudio);
        }).start();
        handler.postDelayed(debounceTask, DEBOUNCE_MS);
    }
}

package exp.miniplayer.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.util.List;
import java.util.Map;

import exp.miniplayer.data.AudioRepository;
import exp.miniplayer.model.Audio;

public class PlaylistFileWatcher {

    private static final long POLL_INTERVAL_MS = 5000;
    private static final long DEBOUNCE_MS = 2000;

    private final Context context;
    private final AudioRepository repo;
    private final Handler handler;
    private Runnable pollTask;
    private Runnable debounceTask;
    private Map<String, Long> lastSnapshot;
    private boolean watching;

    public PlaylistFileWatcher(Context context, AudioRepository repo) {
        this.context = context.getApplicationContext();
        this.repo = repo;
        this.handler = new Handler(Looper.getMainLooper());
    }

    public void start() {
        if (watching) return;
        watching = true;

        new Thread(() -> {
            Map<String, Long> snapshot = PlaylistScanner.snapshotPlaylistFiles(context);
            synchronized (this) {
                lastSnapshot = snapshot;
            }
        }).start();

        pollTask = new Runnable() {
            @Override
            public void run() {
                if (!watching) return;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Map<String, Long> snapshot = PlaylistScanner.snapshotPlaylistFiles(context);
                        boolean changed;
                        synchronized (PlaylistFileWatcher.this) {
                            changed = !snapshot.equals(lastSnapshot);
                            if (changed) lastSnapshot = snapshot;
                        }
                        if (changed) triggerRescan();
                    }
                }).start();
                handler.postDelayed(pollTask, POLL_INTERVAL_MS);
            }
        };
        handler.postDelayed(pollTask, POLL_INTERVAL_MS);
    }

    public void stop() {
        watching = false;
        if (pollTask != null) {
            handler.removeCallbacks(pollTask);
            pollTask = null;
        }
        if (debounceTask != null) {
            handler.removeCallbacks(debounceTask);
            debounceTask = null;
        }
    }

    private void triggerRescan() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!watching) return;
                if (debounceTask != null) {
                    handler.removeCallbacks(debounceTask);
                }
                debounceTask = () -> new Thread(() -> {
                    List<Audio> scannedAudio = repo.getCachedAudio();
                    PlaylistScanner.scanPlaylists(context, repo, scannedAudio);
                }).start();
                handler.postDelayed(debounceTask, DEBOUNCE_MS);
            }
        });
    }
}
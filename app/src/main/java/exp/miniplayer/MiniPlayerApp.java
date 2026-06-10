package exp.miniplayer;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import exp.miniplayer.data.AudioRepository;
import exp.miniplayer.utils.PlaylistFileWatcher;

public class MiniPlayerApp extends Application {

    private PlaylistFileWatcher fileWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        AudioRepository repo = new AudioRepository(this);
        fileWatcher = new PlaylistFileWatcher(this, repo);
        fileWatcher.start();
    }
}

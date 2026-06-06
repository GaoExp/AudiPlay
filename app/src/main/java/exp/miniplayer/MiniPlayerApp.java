package exp.miniplayer;

import android.app.Application;

import exp.miniplayer.utils.PreferencesManager;

public class MiniPlayerApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PreferencesManager prefs = new PreferencesManager(this);
        prefs.applyTheme();
    }
}

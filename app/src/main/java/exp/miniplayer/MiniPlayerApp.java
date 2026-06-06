package exp.miniplayer;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

public class MiniPlayerApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }
}

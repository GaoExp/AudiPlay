package exp.miniplayer.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class PreferencesManager {
    private static final String PREF_NAME = "mini_player_prefs";
    private static final String KEY_DARK_MODE = "dark_mode";
    private static final String KEY_FOLLOW_SYSTEM = "follow_system";
    private static final String KEY_KEEP_SCREEN_ON = "keep_screen_on";
    private static final String KEY_DEFAULT_REPEAT_MODE = "default_repeat_mode";
    private static final String KEY_SORT_MODE = "sort_mode";
    private static final String KEY_SCAN_ALL_AUDIO = "scan_all_audio";

    private final SharedPreferences prefs;

    public PreferencesManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean isDarkMode() {
        return prefs.getBoolean(KEY_DARK_MODE, false);
    }

    public void setDarkMode(boolean enabled) {
        prefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply();
        if (!isFollowSystem()) {
            AppCompatDelegate.setDefaultNightMode(
                    enabled ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public boolean isFollowSystem() {
        return prefs.getBoolean(KEY_FOLLOW_SYSTEM, true);
    }

    public void setFollowSystem(boolean enabled) {
        prefs.edit().putBoolean(KEY_FOLLOW_SYSTEM, enabled).apply();
        if (enabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else {
            AppCompatDelegate.setDefaultNightMode(
                    isDarkMode() ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public boolean isKeepScreenOn() {
        return prefs.getBoolean(KEY_KEEP_SCREEN_ON, true);
    }

    public void setKeepScreenOn(boolean enabled) {
        prefs.edit().putBoolean(KEY_KEEP_SCREEN_ON, enabled).apply();
    }

    public int getDefaultRepeatMode() {
        return prefs.getInt(KEY_DEFAULT_REPEAT_MODE, 0);
    }

    public void setDefaultRepeatMode(int mode) {
        prefs.edit().putInt(KEY_DEFAULT_REPEAT_MODE, mode).apply();
    }

    public int getSortMode() {
        return prefs.getInt(KEY_SORT_MODE, 0);
    }

    public void setSortMode(int mode) {
        prefs.edit().putInt(KEY_SORT_MODE, mode).apply();
    }

    public boolean isScanAllAudio() {
        return prefs.getBoolean(KEY_SCAN_ALL_AUDIO, true);
    }

    public void setScanAllAudio(boolean scanAll) {
        prefs.edit().putBoolean(KEY_SCAN_ALL_AUDIO, scanAll).apply();
    }

    public void applyTheme() {
        if (isFollowSystem()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else {
            AppCompatDelegate.setDefaultNightMode(
                    isDarkMode() ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}

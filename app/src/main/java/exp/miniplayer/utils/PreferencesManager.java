package exp.miniplayer.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PreferencesManager {
    private static final String PREF_NAME = "mini_player_prefs";
    private static final String KEY_KEEP_SCREEN_ON = "keep_screen_on";
    private static final String KEY_DEFAULT_REPEAT_MODE = "default_repeat_mode";
    private static final String KEY_SORT_MODE = "sort_mode";
    private static final String KEY_SCAN_ALL_AUDIO = "scan_all_audio";
    private static final String KEY_INCLUDED_FOLDERS = "included_folders";
    private static final String KEY_EXCLUDED_FOLDERS = "excluded_folders";

    private final SharedPreferences prefs;

    public PreferencesManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
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

    public Set<String> getIncludedFolders() {
        String raw = prefs.getString(KEY_INCLUDED_FOLDERS, "");
        if (raw.isEmpty()) return new HashSet<>();
        return new HashSet<>(Arrays.asList(raw.split("\\|")));
    }

    public void setIncludedFolders(Set<String> folders) {
        prefs.edit().putString(KEY_INCLUDED_FOLDERS, String.join("|", folders)).apply();
    }

    public void addIncludedFolder(String folder) {
        Set<String> folders = getIncludedFolders();
        folders.add(folder);
        setIncludedFolders(folders);
    }

    public void removeIncludedFolder(String folder) {
        Set<String> folders = getIncludedFolders();
        folders.remove(folder);
        setIncludedFolders(folders);
    }

    public Set<String> getExcludedFolders() {
        String raw = prefs.getString(KEY_EXCLUDED_FOLDERS, "");
        if (raw.isEmpty()) return new HashSet<>();
        return new HashSet<>(Arrays.asList(raw.split("\\|")));
    }

    public void setExcludedFolders(Set<String> folders) {
        prefs.edit().putString(KEY_EXCLUDED_FOLDERS, String.join("|", folders)).apply();
    }

    public void addExcludedFolder(String folder) {
        Set<String> folders = getExcludedFolders();
        folders.add(folder);
        setExcludedFolders(folders);
    }

    public void removeExcludedFolder(String folder) {
        Set<String> folders = getExcludedFolders();
        folders.remove(folder);
        setExcludedFolders(folders);
    }
}

package exp.miniplayer.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import exp.miniplayer.model.Audio;

public class PreferencesManager {
    private static final String PREF_NAME = "mini_player_prefs";
    private static final String KEY_KEEP_SCREEN_ON = "keep_screen_on";
    private static final String KEY_PLAY_OVER_OTHER_APPS = "play_over_other_apps";
    private static final String KEY_SORT_MODE = "sort_mode";
    private static final String KEY_LIMIT_FOLDERS = "limit_folders";
    private static final String KEY_INCLUDED_FOLDERS = "included_folders";
    private static final String KEY_EXCLUDED_FOLDERS = "excluded_folders";
    private static final String KEY_AUDIO_FORMATS = "audio_formats";
    private static final String KEY_LAST_TRACK_ID = "last_track_id";
    private static final String KEY_LAST_TRACK_TITLE = "last_track_title";
    private static final String KEY_LAST_TRACK_ARTIST = "last_track_artist";
    private static final String KEY_LAST_TRACK_ALBUM = "last_track_album";
    private static final String KEY_LAST_TRACK_DURATION = "last_track_duration";
    private static final String KEY_LAST_TRACK_URI = "last_track_uri";
    private static final String KEY_LAST_TRACK_ALBUM_ART = "last_track_album_art";
    private static final String KEY_LAST_TRACK_DATE_ADDED = "last_track_date_added";

    private static final Set<String> DEFAULT_AUDIO_FORMATS = new HashSet<>(Arrays.asList(
            "mp3", "m4a"
    ));

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

    public boolean isPlayOverOtherApps() {
        return prefs.getBoolean(KEY_PLAY_OVER_OTHER_APPS, true);
    }

    public void setPlayOverOtherApps(boolean enabled) {
        prefs.edit().putBoolean(KEY_PLAY_OVER_OTHER_APPS, enabled).apply();
    }

    public int getSortMode() {
        return prefs.getInt(KEY_SORT_MODE, 0);
    }

    public void setSortMode(int mode) {
        prefs.edit().putInt(KEY_SORT_MODE, mode).apply();
    }

    public boolean isLimitFolders() {
        return prefs.getBoolean(KEY_LIMIT_FOLDERS, false);
    }

    public void setLimitFolders(boolean limit) {
        prefs.edit().putBoolean(KEY_LIMIT_FOLDERS, limit).apply();
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

    public Set<String> getAudioFormats() {
        String raw = prefs.getString(KEY_AUDIO_FORMATS, "");
        if (raw.isEmpty()) return new HashSet<>(DEFAULT_AUDIO_FORMATS);
        Set<String> formats = new HashSet<>(Arrays.asList(raw.split("\\|")));
        if (formats.isEmpty()) return new HashSet<>(DEFAULT_AUDIO_FORMATS);
        return formats;
    }

    public void setAudioFormats(Set<String> formats) {
        prefs.edit().putString(KEY_AUDIO_FORMATS, String.join("|", formats)).apply();
    }

    public void addAudioFormat(String format) {
        Set<String> formats = getAudioFormats();
        formats.add(format);
        setAudioFormats(formats);
    }

    public void removeAudioFormat(String format) {
        Set<String> formats = getAudioFormats();
        formats.remove(format);
        setAudioFormats(formats);
    }

    public void saveLastPlayedTrack(Audio audio) {
        prefs.edit()
                .putLong(KEY_LAST_TRACK_ID, audio.getId())
                .putString(KEY_LAST_TRACK_TITLE, audio.getTitle())
                .putString(KEY_LAST_TRACK_ARTIST, audio.getArtist())
                .putString(KEY_LAST_TRACK_ALBUM, audio.getAlbum())
                .putLong(KEY_LAST_TRACK_DURATION, audio.getDuration())
                .putString(KEY_LAST_TRACK_URI, audio.getUri())
                .putString(KEY_LAST_TRACK_ALBUM_ART, audio.getAlbumArt())
                .putLong(KEY_LAST_TRACK_DATE_ADDED, audio.getDateAdded())
                .apply();
    }

    public Audio getLastPlayedTrack() {
        if (!prefs.contains(KEY_LAST_TRACK_ID)) return null;
        long id = prefs.getLong(KEY_LAST_TRACK_ID, 0);
        String title = prefs.getString(KEY_LAST_TRACK_TITLE, "");
        String artist = prefs.getString(KEY_LAST_TRACK_ARTIST, "");
        String album = prefs.getString(KEY_LAST_TRACK_ALBUM, "");
        long duration = prefs.getLong(KEY_LAST_TRACK_DURATION, 0);
        String uri = prefs.getString(KEY_LAST_TRACK_URI, "");
        String albumArt = prefs.getString(KEY_LAST_TRACK_ALBUM_ART, null);
        long dateAdded = prefs.getLong(KEY_LAST_TRACK_DATE_ADDED, 0);
        if (uri.isEmpty()) return null;
        return new Audio(id, title, artist, album, duration, uri, albumArt, dateAdded);
    }

    public void clearLastPlayedTrack() {
        prefs.edit()
                .remove(KEY_LAST_TRACK_ID)
                .remove(KEY_LAST_TRACK_TITLE)
                .remove(KEY_LAST_TRACK_ARTIST)
                .remove(KEY_LAST_TRACK_ALBUM)
                .remove(KEY_LAST_TRACK_DURATION)
                .remove(KEY_LAST_TRACK_URI)
                .remove(KEY_LAST_TRACK_ALBUM_ART)
                .remove(KEY_LAST_TRACK_DATE_ADDED)
                .apply();
    }
}

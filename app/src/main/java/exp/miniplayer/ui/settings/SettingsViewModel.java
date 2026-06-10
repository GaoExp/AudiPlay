package exp.miniplayer.ui.settings;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import exp.miniplayer.data.AudioRepository;
import exp.miniplayer.utils.PreferencesManager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SettingsViewModel extends AndroidViewModel {

    private final PreferencesManager prefs;
    private final MutableLiveData<Boolean> keepScreenOn;
    private final MutableLiveData<Boolean> playOverOtherApps;
    private final MutableLiveData<Set<String>> audioFormats;
    private final MutableLiveData<Boolean> limitFolders;

    public SettingsViewModel(@NonNull Application application) {
        super(application);
        prefs = new PreferencesManager(application);
        keepScreenOn = new MutableLiveData<>(prefs.isKeepScreenOn());
        playOverOtherApps = new MutableLiveData<>(prefs.isPlayOverOtherApps());
        audioFormats = new MutableLiveData<>(prefs.getAudioFormats());
        limitFolders = new MutableLiveData<>(prefs.isLimitFolders());
    }

    public LiveData<Boolean> getKeepScreenOn() { return keepScreenOn; }
    public LiveData<Boolean> getPlayOverOtherApps() { return playOverOtherApps; }
    public LiveData<Set<String>> getAudioFormats() { return audioFormats; }
    public LiveData<Boolean> getLimitFolders() { return limitFolders; }

    public void setKeepScreenOn(boolean enabled) {
        prefs.setKeepScreenOn(enabled);
        keepScreenOn.setValue(enabled);
    }

    public void setPlayOverOtherApps(boolean enabled) {
        prefs.setPlayOverOtherApps(enabled);
        playOverOtherApps.setValue(enabled);
    }

    public void setLimitFolders(boolean enabled) {
        prefs.setLimitFolders(enabled);
        limitFolders.setValue(enabled);
    }

    public boolean isNotificationPermissionGranted() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            return androidx.core.content.ContextCompat.checkSelfPermission(
                    getApplication(), android.Manifest.permission.POST_NOTIFICATIONS)
                    == android.content.pm.PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    public void triggerScan() {
        AudioRepository.triggerRescan();
    }

    public Set<String> getIncludedFolders() {
        return prefs.getIncludedFolders();
    }

    public void addIncludedFolder(String folder) {
        prefs.addIncludedFolder(folder);
    }

    public void removeIncludedFolder(String folder) {
        prefs.removeIncludedFolder(folder);
    }

    public Set<String> getExcludedFolders() {
        return prefs.getExcludedFolders();
    }

    public void addExcludedFolder(String folder) {
        prefs.addExcludedFolder(folder);
    }

    public void removeExcludedFolder(String folder) {
        prefs.removeExcludedFolder(folder);
    }

    public void toggleAudioFormat(String format) {
        Set<String> formats = new HashSet<>(audioFormats.getValue());
        if (formats.contains(format)) {
            formats.remove(format);
        } else {
            formats.add(format);
        }
        prefs.setAudioFormats(formats);
        audioFormats.setValue(formats);
    }

    public void setAllFormats(Set<String> formats) {
        prefs.setAudioFormats(formats);
        audioFormats.setValue(new HashSet<>(formats));
    }

    public Set<String> getAllFormatExtensions() {
        Set<String> all = new HashSet<>();
        for (List<String> list : getFormatGroups().values()) {
            all.addAll(list);
        }
        return all;
    }

    public static Map<String, List<String>> getFormatGroups() {
        Map<String, List<String>> groups = new LinkedHashMap<>();
        groups.put("Audio", Arrays.asList("mp3", "aac", "m4a", "wav", "flac", "ogg",
                "opus", "wma", "ape", "alac", "aiff", "aif", "aifc", "au", "snd",
                "ra", "rm", "ac3", "dts", "mka", "pcm"));
        groups.put("Rekaman", Arrays.asList("amr", "3ga", "3gp", "caf"));
        groups.put("MIDI", Arrays.asList("mid", "midi", "rmi", "kar"));
        groups.put("Video", Arrays.asList("mp4", "mkv", "avi", "mov", "wmv", "flv",
                "f4v", "webm", "m4v", "mpeg", "mpg", "mp2", "mpe", "ts", "mts",
                "m2ts", "vob", "ogv", "rmvb", "asf", "3g2", "divx", "xvid", "dv", "dat"));
        groups.put("Stream", Arrays.asList("weba", "mks", "mxf"));
        return groups;
    }

    public static final int PLAYABLE = 0;
    public static final int MAYBE = 1;
    public static final int NOT_PLAYABLE = 2;

    public static int getFormatPlayability(String format) {
        switch (format) {
            case "mp3": case "aac": case "m4a": case "wav": case "flac":
            case "ogg": case "opus": case "ac3": case "amr": case "alac":
            case "pcm": case "mp2":
            case "mid": case "midi": case "rmi": case "kar":
                return PLAYABLE;
            case "mp4": case "m4v": case "webm": case "mkv": case "3gp":
            case "3g2": case "ts": case "mts": case "m2ts": case "mov":
            case "avi": case "flv": case "f4v": case "ogv": case "vob":
            case "mpeg": case "mpg": case "mpe": case "weba": case "mka":
            case "3ga": case "dts":
                return MAYBE;
            default:
                return NOT_PLAYABLE;
        }
    }
}

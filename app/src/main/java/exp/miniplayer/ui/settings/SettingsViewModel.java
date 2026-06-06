package exp.miniplayer.ui.settings;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import exp.miniplayer.data.AudioRepository;
import exp.miniplayer.utils.PreferencesManager;

import java.util.Set;

public class SettingsViewModel extends AndroidViewModel {

    private final PreferencesManager prefs;
    private final MutableLiveData<Boolean> keepScreenOn;
    private final MutableLiveData<Integer> defaultRepeatMode;

    public SettingsViewModel(@NonNull Application application) {
        super(application);
        prefs = new PreferencesManager(application);
        keepScreenOn = new MutableLiveData<>(prefs.isKeepScreenOn());
        defaultRepeatMode = new MutableLiveData<>(prefs.getDefaultRepeatMode());
    }

    public LiveData<Boolean> getKeepScreenOn() { return keepScreenOn; }
    public LiveData<Integer> getDefaultRepeatMode() { return defaultRepeatMode; }

    public void setKeepScreenOn(boolean enabled) {
        prefs.setKeepScreenOn(enabled);
        keepScreenOn.setValue(enabled);
    }

    public void setDefaultRepeatMode(int mode) {
        prefs.setDefaultRepeatMode(mode);
        defaultRepeatMode.setValue(mode);
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
}

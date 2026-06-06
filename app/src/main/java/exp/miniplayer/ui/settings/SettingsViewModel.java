package exp.miniplayer.ui.settings;

import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import exp.miniplayer.utils.PreferencesManager;

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return androidx.core.content.ContextCompat.checkSelfPermission(
                    getApplication(), android.Manifest.permission.POST_NOTIFICATIONS)
                    == android.content.pm.PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }
}

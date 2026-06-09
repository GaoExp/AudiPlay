package exp.miniplayer.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;

import exp.miniplayer.R;
import exp.miniplayer.ui.audioformat.FormatAudioActivity;
import exp.miniplayer.ui.documentation.DocumentationActivity;
import exp.miniplayer.ui.folders.FolderListActivity;
import exp.miniplayer.ui.folders.FolderSettingsActivity;

public class SettingsFragment extends Fragment {

    private SettingsViewModel viewModel;
    private SwitchCompat keepScreenOnSwitch;
    private SwitchCompat limitFoldersSwitch;
    private SwitchCompat playOverOtherAppsSwitch;
    private View batasiFolderRow;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        keepScreenOnSwitch = view.findViewById(R.id.keep_screen_on_switch);
        limitFoldersSwitch = view.findViewById(R.id.limit_folders_switch);
        playOverOtherAppsSwitch = view.findViewById(R.id.play_over_other_apps_switch);
        batasiFolderRow = view.findViewById(R.id.batasi_folder_row);

        viewModel.getKeepScreenOn().observe(getViewLifecycleOwner(), keepScreenOnSwitch::setChecked);
        viewModel.getPlayOverOtherApps().observe(getViewLifecycleOwner(), playOverOtherAppsSwitch::setChecked);

        keepScreenOnSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            viewModel.setKeepScreenOn(isChecked);
        });

        playOverOtherAppsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            viewModel.setPlayOverOtherApps(isChecked);
        });

        limitFoldersSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            viewModel.setLimitFolders(isChecked);
        });

        batasiFolderRow.setOnClickListener(v -> {
            boolean newState = !limitFoldersSwitch.isChecked();
            limitFoldersSwitch.setChecked(newState);
            viewModel.setLimitFolders(newState);
        });

        TextView includedFolders = view.findViewById(R.id.included_folders);
        TextView excludedFolders = view.findViewById(R.id.excluded_folders);
        TextView audioFormats = view.findViewById(R.id.audio_formats);
        TextView includedFoldersSub = view.findViewById(R.id.included_folders_subtitle);

        viewModel.getLimitFolders().observe(getViewLifecycleOwner(), limit -> {
            limitFoldersSwitch.setChecked(limit);
            batasiFolderRow.setAlpha(limit ? 1f : 0.5f);
            float dimAlpha = limit ? 0.5f : 1f;
            includedFolders.setAlpha(dimAlpha);
            includedFoldersSub.setAlpha(dimAlpha);
        });

        includedFolders.setOnClickListener(v -> {
            if (!limitFoldersSwitch.isChecked()) {
                limitFoldersSwitch.setChecked(true);
                viewModel.setLimitFolders(true);
            }
            Intent intent = new Intent(requireContext(), FolderSettingsActivity.class);
            intent.putExtra(FolderSettingsActivity.EXTRA_IS_EXCLUDED, false);
            startActivity(intent);
        });
        excludedFolders.setOnClickListener(v -> {
            if (!limitFoldersSwitch.isChecked()) {
                limitFoldersSwitch.setChecked(true);
                viewModel.setLimitFolders(true);
            }
            Intent intent = new Intent(requireContext(), FolderSettingsActivity.class);
            intent.putExtra(FolderSettingsActivity.EXTRA_IS_EXCLUDED, true);
            startActivity(intent);
        });
        audioFormats.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), FormatAudioActivity.class)));

        TextView lihatFolderAudio = view.findViewById(R.id.lihat_folder_audio);
        lihatFolderAudio.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), FolderListActivity.class)));

        TextView viewDocumentation = view.findViewById(R.id.view_documentation);
        viewDocumentation.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), DocumentationActivity.class));
        });

        TextView chooseAppIcon = view.findViewById(R.id.choose_app_icon);
        chooseAppIcon.setOnClickListener(v -> showIconDialog());
    }

    private void showIconDialog() {
        PackageManager pm = requireContext().getPackageManager();

        String[] iconSuffixes = {
                "icon_note", "icon_play",
                "icon_note_dark", "icon_play_dark", "icon_queue"
        };

        String[] iconNames = {
                getString(R.string.icon_name_note),
                getString(R.string.icon_name_play),
                getString(R.string.icon_name_note_dark),
                getString(R.string.icon_name_play_dark),
                getString(R.string.icon_name_queue)
        };

        ComponentName[] components = new ComponentName[iconSuffixes.length];
        final int[] checkedItem = {0};
        for (int i = 0; i < iconSuffixes.length; i++) {
            String fullClass = requireContext().getPackageName() + "." + iconSuffixes[i];
            components[i] = new ComponentName(requireContext(), fullClass);
            if (pm.getComponentEnabledSetting(components[i])
                    == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
                checkedItem[0] = i;
            }
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.choose_app_icon)
                .setSingleChoiceItems(iconNames, checkedItem[0], (dialog, which) -> checkedItem[0] = which)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    for (int i = 0; i < components.length; i++) {
                        pm.setComponentEnabledSetting(
                                components[i],
                                i == checkedItem[0]
                                        ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                                        : PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                                PackageManager.DONT_KILL_APP
                        );
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

}

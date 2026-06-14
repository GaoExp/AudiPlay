package exp.miniplayer.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
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
import android.net.Uri;
import android.provider.Settings;

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

        TextView managePermissions = view.findViewById(R.id.manage_permissions);
        managePermissions.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + requireContext().getPackageName()));
            startActivity(intent);
        });

        TextView batteryOptimization = view.findViewById(R.id.battery_optimization);
        batteryOptimization.setOnClickListener(v -> {
            Intent intent = new Intent(
                    Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + requireContext().getPackageName()));
            startActivity(intent);
        });
    }

    private void showIconDialog() {
        PackageManager pm = requireContext().getPackageManager();
        String pkg = requireContext().getPackageName();

        int[] iconResIds = {
                R.drawable.ic_laucher016_fg, R.drawable.ic_fg_play,
                R.drawable.ic_fg_note_dark, R.drawable.ic_fg_play_dark,
                R.drawable.ic_fg_queue,
                R.drawable.ic_laucher001_fg, R.drawable.ic_laucher002_fg,
                R.drawable.ic_laucher003_fg, R.drawable.ic_laucher004_fg,
                R.drawable.ic_laucher005_fg, R.drawable.ic_laucher006_fg,
                R.drawable.ic_laucher007_fg, R.drawable.ic_laucher008_fg,
                R.drawable.ic_laucher009_fg, R.drawable.ic_laucher010_fg,
                R.drawable.ic_laucher011_fg, R.drawable.ic_laucher012_fg,
                R.drawable.ic_laucher013_fg, R.drawable.ic_laucher014_fg,
                R.drawable.ic_laucher015_fg, R.drawable.ic_laucher016_fg,
                R.drawable.ic_laucher017_fg
        };

        String[] iconSuffixes = {
                "icon_note", "icon_play",
                "icon_note_dark", "icon_play_dark", "icon_queue",
                "icon_001", "icon_002", "icon_003", "icon_004", "icon_005",
                "icon_006", "icon_007", "icon_008", "icon_009", "icon_010",
                "icon_011", "icon_012", "icon_013", "icon_014", "icon_015",
                "icon_016", "icon_017"
        };

        final int[] checkedItem = {0};
        for (int i = 0; i < iconSuffixes.length; i++) {
            ComponentName cn = new ComponentName(requireContext(), pkg + "." + iconSuffixes[i]);
            if (pm.getComponentEnabledSetting(cn)
                    == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
                checkedItem[0] = i;
            }
        }

        View view = getLayoutInflater().inflate(R.layout.dialog_icon_picker, null);
        GridView grid = view.findViewById(R.id.icon_grid);

        IconPickerAdapter adapter = new IconPickerAdapter(iconResIds, checkedItem[0]);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener((parent, v, pos, id) -> adapter.setSelected(pos));

        grid.post(() -> {
            int maxH = (int) (getResources().getDisplayMetrics().heightPixels * 0.55);
            if (grid.getHeight() > maxH) {
                ViewGroup.LayoutParams lp = grid.getLayoutParams();
                lp.height = maxH;
                grid.setLayoutParams(lp);
            }
        });

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.choose_app_icon)
                .setView(view)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    int sel = adapter.getSelectedPosition();
                    for (int i = 0; i < iconSuffixes.length; i++) {
                        ComponentName cn = new ComponentName(requireContext(), pkg + "." + iconSuffixes[i]);
                        pm.setComponentEnabledSetting(
                                cn,
                                i == sel
                                        ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                                        : PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                                PackageManager.DONT_KILL_APP
                        );
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private static class IconPickerAdapter extends BaseAdapter {
        private final int[] iconResIds;
        private int selectedPosition;

        IconPickerAdapter(int[] iconResIds, int selected) {
            this.iconResIds = iconResIds;
            this.selectedPosition = selected;
        }

        void setSelected(int pos) {
            selectedPosition = pos;
            notifyDataSetChanged();
        }

        int getSelectedPosition() {
            return selectedPosition;
        }

        @Override
        public int getCount() { return iconResIds.length; }

        @Override
        public Object getItem(int pos) { return iconResIds[pos]; }

        @Override
        public long getItemId(int pos) { return pos; }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_icon_picker, parent, false);
            }

            ImageView icon = convertView.findViewById(R.id.icon_image);
            icon.setImageResource(iconResIds[pos]);

            convertView.setBackgroundColor(
                    pos == selectedPosition
                            ? parent.getContext().getColor(R.color.primary_container)
                            : parent.getContext().getColor(android.R.color.transparent));

            return convertView;
        }
    }

}

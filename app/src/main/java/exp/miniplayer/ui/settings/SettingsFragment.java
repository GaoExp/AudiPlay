package exp.miniplayer.ui.settings;

import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.annotation.SuppressLint;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;

import exp.miniplayer.R;
import exp.miniplayer.ui.documentation.DocumentationActivity;
import exp.miniplayer.ui.folders.FolderListActivity;

public class SettingsFragment extends Fragment {

    private SettingsViewModel viewModel;
    private SwitchCompat keepScreenOnSwitch;
    private SwitchCompat limitFoldersSwitch;
    private SwitchCompat playOverOtherAppsSwitch;
    private View batasiFolderRow;
    private boolean pendingFolderIsExcluded;

    private final ActivityResultLauncher<Uri> folderPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.OpenDocumentTree(),
            this::onFolderSelected);

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
            showFolderDialog(false);
        });
        excludedFolders.setOnClickListener(v -> {
            if (!limitFoldersSwitch.isChecked()) {
                limitFoldersSwitch.setChecked(true);
                viewModel.setLimitFolders(true);
            }
            showFolderDialog(true);
        });
        audioFormats.setOnClickListener(v -> showFormatDialog());

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

    private void showFolderDialog(boolean isExcluded) {
        Set<String> folders = isExcluded
                ? viewModel.getExcludedFolders()
                : viewModel.getIncludedFolders();

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setTitle(isExcluded ? R.string.excluded_folders_list : R.string.included_folders_list);

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_folder_list, null);
        builder.setView(dialogView);

        TextView emptyText = dialogView.findViewById(R.id.folder_empty_text);
        ViewGroup folderList = dialogView.findViewById(R.id.folder_list);

        if (folders.isEmpty()) {
            emptyText.setVisibility(View.VISIBLE);
            folderList.setVisibility(View.GONE);
        } else {
            emptyText.setVisibility(View.GONE);
            folderList.setVisibility(View.VISIBLE);
            folderList.removeAllViews();
            for (String folder : folders) {
                View itemView = getLayoutInflater().inflate(R.layout.item_folder, folderList, false);
                TextView pathText = itemView.findViewById(R.id.folder_path_text);
                View removeBtn = itemView.findViewById(R.id.folder_remove_btn);
                pathText.setText(folder);
                removeBtn.setOnClickListener(v -> {
                    if (isExcluded) {
                        viewModel.removeExcludedFolder(folder);
                    } else {
                        viewModel.removeIncludedFolder(folder);
                    }
                    showFolderDialog(isExcluded);
                });
                folderList.addView(itemView);
            }
        }

        builder.setPositiveButton(R.string.add_folder, (dialog, which) -> {
            pendingFolderIsExcluded = isExcluded;
            folderPickerLauncher.launch(null);
        });
        builder.setNegativeButton(R.string.close, null);
        builder.show();
    }

    private void onFolderSelected(Uri treeUri) {
        if (treeUri == null) return;

        String path = getPathFromTreeUri(treeUri);
        if (path == null) return;

        if (pendingFolderIsExcluded) {
            viewModel.addExcludedFolder(path);
        } else {
            viewModel.addIncludedFolder(path);
        }
        showFolderDialog(pendingFolderIsExcluded);
    }

    private String getPathFromTreeUri(Uri treeUri) {
        try {
            String docId = DocumentsContract.getTreeDocumentId(treeUri);
            String[] parts = docId.split(":");
            if (parts.length >= 2) {
                String type = parts[0];
                String relativePath = parts[1];
                if ("primary".equalsIgnoreCase(type)) {
                    return android.os.Environment.getExternalStorageDirectory().getAbsolutePath()
                            + "/" + relativePath;
                } else {
                    return "/storage/" + type + "/" + relativePath;
                }
            }
        } catch (Exception ignored) {}
        return treeUri.getPath();
    }

    @SuppressLint("InflateParams")
    private void showFormatDialog() {
        Map<String, List<String>> groups = SettingsViewModel.getFormatGroups();
        Set<String> enabled = viewModel.getAudioFormats().getValue();
        if (enabled == null) enabled = new HashSet<>();

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setTitle(R.string.audio_formats);

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_audio_formats, null);
        builder.setView(dialogView);

        LinearLayout container = dialogView.findViewById(R.id.format_container);
        container.removeAllViews();

        TextView selectAll = new TextView(requireContext());
        selectAll.setText(R.string.select_all);
        selectAll.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_BodyLarge);
        selectAll.setPadding(0, 0, 0, (int) (8 * getResources().getDisplayMetrics().density));
        selectAll.setClickable(true);
        selectAll.setFocusable(true);
        selectAll.setOnClickListener(v -> {
            viewModel.setAllFormats(viewModel.getAllFormatExtensions());
            updateFormatCheckboxes(container, viewModel.getAudioFormats().getValue());
        });
        container.addView(selectAll);

        TextView deselectAll = new TextView(requireContext());
        deselectAll.setText(R.string.deselect_all);
        deselectAll.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_BodyLarge);
        deselectAll.setPadding(0, 0, 0, (int) (8 * getResources().getDisplayMetrics().density));
        deselectAll.setClickable(true);
        deselectAll.setFocusable(true);
        deselectAll.setOnClickListener(v -> {
            viewModel.setAllFormats(new HashSet<>());
            updateFormatCheckboxes(container, viewModel.getAudioFormats().getValue());
        });
        container.addView(deselectAll);

        int headerTopPadding = (int) (16 * getResources().getDisplayMetrics().density);
        int headerBottomPadding = (int) (4 * getResources().getDisplayMetrics().density);

        for (Map.Entry<String, List<String>> entry : groups.entrySet()) {
            TextView header = new TextView(requireContext());
            header.setText(entry.getKey());
            header.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_TitleSmall);
            header.setPadding(0, headerTopPadding, 0, headerBottomPadding);
            container.addView(header);

            for (String format : entry.getValue()) {
                CheckBox checkBox = new CheckBox(requireContext());
                checkBox.setText("." + format);
                checkBox.setChecked(enabled.contains(format));
                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    viewModel.toggleAudioFormat(format);
                });
                container.addView(checkBox);
            }
        }

        builder.setPositiveButton(R.string.close, null);
        builder.show();
    }

    private void updateFormatCheckboxes(LinearLayout container, Set<String> enabled) {
        for (int i = 0; i < container.getChildCount(); i++) {
            View child = container.getChildAt(i);
            if (child instanceof CheckBox) {
                String text = ((CheckBox) child).getText().toString();
                String format = text.startsWith(".") ? text.substring(1) : text;
                ((CheckBox) child).setChecked(enabled != null && enabled.contains(format));
            }
        }
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

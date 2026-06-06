package exp.miniplayer.ui.settings;

import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Set;

import exp.miniplayer.R;

public class SettingsFragment extends Fragment {

    private SettingsViewModel viewModel;
    private SwitchCompat keepScreenOnSwitch;
    private Spinner defaultRepeatSpinner;
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
        defaultRepeatSpinner = view.findViewById(R.id.default_repeat_spinner);

        viewModel.getKeepScreenOn().observe(getViewLifecycleOwner(), keepScreenOnSwitch::setChecked);
        viewModel.getDefaultRepeatMode().observe(getViewLifecycleOwner(), defaultRepeatSpinner::setSelection);

        keepScreenOnSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            viewModel.setKeepScreenOn(isChecked);
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.repeat_modes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        defaultRepeatSpinner.setAdapter(adapter);
        defaultRepeatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                viewModel.setDefaultRepeatMode(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        TextView scanAllAudio = view.findViewById(R.id.scan_all_audio);
        TextView includedFolders = view.findViewById(R.id.included_folders);
        TextView excludedFolders = view.findViewById(R.id.excluded_folders);

        scanAllAudio.setOnClickListener(v -> {
            viewModel.triggerScan();
            Toast.makeText(requireContext(), R.string.scanning_started, Toast.LENGTH_SHORT).show();
        });

        includedFolders.setOnClickListener(v -> showFolderDialog(false));
        excludedFolders.setOnClickListener(v -> showFolderDialog(true));
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
}

package exp.miniplayer.ui.folders;

import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Set;

import exp.miniplayer.R;
import exp.miniplayer.utils.PreferencesManager;

public class FolderSettingsActivity extends AppCompatActivity {

    public static final String EXTRA_IS_EXCLUDED = "is_excluded";

    private PreferencesManager prefs;
    private boolean isExcluded;
    private LinearLayout folderList;
    private TextView emptyText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_settings);

        isExcluded = getIntent().getBooleanExtra(EXTRA_IS_EXCLUDED, false);
        prefs = new PreferencesManager(this);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(isExcluded
                    ? R.string.excluded_folders_list
                    : R.string.included_folders_list);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        folderList = findViewById(R.id.folder_list);
        emptyText = findViewById(R.id.empty_text);
        emptyText.setText(isExcluded
                ? R.string.no_excluded_folders
                : R.string.no_included_folders);

        findViewById(R.id.add_folder_fab).setOnClickListener(v -> {
            pendingIsExcluded = isExcluded;
            folderPickerLauncher.launch(null);
        });

        refreshList();
    }

    private final androidx.activity.result.ActivityResultLauncher<Uri> folderPickerLauncher =
            registerForActivityResult(
                    new androidx.activity.result.contract.ActivityResultContracts.OpenDocumentTree(),
                    this::onFolderSelected);

    private boolean pendingIsExcluded;

    private void onFolderSelected(Uri treeUri) {
        if (treeUri == null) return;

        String path = getPathFromTreeUri(treeUri);
        if (path == null) return;

        if (pendingIsExcluded) {
            prefs.addExcludedFolder(path);
        } else {
            prefs.addIncludedFolder(path);
        }
        refreshList();
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

    private void refreshList() {
        Set<String> folders = isExcluded
                ? prefs.getExcludedFolders()
                : prefs.getIncludedFolders();

        folderList.removeAllViews();
        LayoutInflater inflater = getLayoutInflater();

        if (folders.isEmpty()) {
            emptyText.setVisibility(View.VISIBLE);
            folderList.setVisibility(View.GONE);
        } else {
            emptyText.setVisibility(View.GONE);
            folderList.setVisibility(View.VISIBLE);
            for (String folder : folders) {
                View itemView = inflater.inflate(R.layout.item_folder, folderList, false);
                TextView pathText = itemView.findViewById(R.id.folder_path_text);
                View removeBtn = itemView.findViewById(R.id.folder_remove_btn);
                pathText.setText(folder);
                removeBtn.setOnClickListener(v -> {
                    if (isExcluded) {
                        prefs.removeExcludedFolder(folder);
                    } else {
                        prefs.removeIncludedFolder(folder);
                    }
                    refreshList();
                });
                folderList.addView(itemView);
            }
        }
    }
}

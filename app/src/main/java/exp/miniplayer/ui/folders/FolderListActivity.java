package exp.miniplayer.ui.folders;

import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import exp.miniplayer.R;
import exp.miniplayer.utils.PreferencesManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class FolderListActivity extends AppCompatActivity {

    private static final int VIEW_MODE_GROUPED = 0;
    private static final int VIEW_MODE_ALL = 1;
    private static final int ITEM_TYPE_FOLDER = 0;
    private static final int ITEM_TYPE_SECTION = 1;
    private static final int ITEM_TYPE_EMPTY = 2;

    private RecyclerView recyclerView;
    private TextView emptyText;
    private View tabKontrol;
    private View tabSemua;
    private TextView btnKontrol;
    private TextView btnSemua;
    private View indicatorKontrol;
    private View indicatorSemua;
    private int currentMode = VIEW_MODE_GROUPED;
    private List<FolderItem> allFolders;
    private Set<String> allowedFolders;
    private Set<String> excludedFolders;
    private boolean expandedAllowed = true;
    private boolean expandedExcluded = true;
    private boolean expandedUndetermined = true;
    private ItemTouchHelper itemTouchHelper;
    private String draggedFolderPath;
    private int dragTargetStatus = -1;
    private boolean showFullPath = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_list);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        recyclerView = findViewById(R.id.recycler_view);
        emptyText = findViewById(R.id.empty_text);
        tabKontrol = findViewById(R.id.tab_kontrol);
        tabSemua = findViewById(R.id.tab_semua);
        btnKontrol = findViewById(R.id.btn_kontrol);
        btnSemua = findViewById(R.id.btn_semua);
        indicatorKontrol = findViewById(R.id.indicator_kontrol);
        indicatorSemua = findViewById(R.id.indicator_semua);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        itemTouchHelper = new ItemTouchHelper(new DragCallback());
        itemTouchHelper.attachToRecyclerView(recyclerView);

        Button fullPathToggle = findViewById(R.id.full_path_toggle);
        fullPathToggle.setOnClickListener(v -> {
            showFullPath = !showFullPath;
            fullPathToggle.setText(showFullPath ? R.string.direktori_utuh : R.string.satu_baris);
            rebuildDisplay();
        });

        PreferencesManager prefs = new PreferencesManager(this);
        allowedFolders = prefs.getIncludedFolders();
        excludedFolders = prefs.getExcludedFolders();

        tabKontrol.setOnClickListener(v -> setMode(VIEW_MODE_GROUPED));
        tabSemua.setOnClickListener(v -> setMode(VIEW_MODE_ALL));

        loadFolders();
    }

    private void setMode(int mode) {
        currentMode = mode;
        updateModeUI();
        if (allFolders != null) {
            rebuildDisplay();
        }
    }

    private void updateModeUI() {
        boolean grup = currentMode == VIEW_MODE_GROUPED;
        indicatorKontrol.setVisibility(grup ? View.VISIBLE : View.GONE);
        indicatorSemua.setVisibility(grup ? View.GONE : View.VISIBLE);
        btnKontrol.setTextAppearance(grup
                ? com.google.android.material.R.style.TextAppearance_Material3_TitleMedium
                : com.google.android.material.R.style.TextAppearance_Material3_BodyLarge);
        btnKontrol.setTypeface(null, grup ? Typeface.BOLD : Typeface.NORMAL);
        btnKontrol.setTextColor(getColor(grup ? R.color.primary : R.color.on_surface));
        btnSemua.setTextAppearance(grup
                ? com.google.android.material.R.style.TextAppearance_Material3_BodyLarge
                : com.google.android.material.R.style.TextAppearance_Material3_TitleMedium);
        btnSemua.setTypeface(null, grup ? Typeface.NORMAL : Typeface.BOLD);
        btnSemua.setTextColor(getColor(grup ? R.color.on_surface : R.color.primary));
    }

    private void loadFolders() {
        new Thread(() -> {
            Map<String, Integer> folderMap = new LinkedHashMap<>();
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String[] projection = { MediaStore.Audio.Media.DATA };
            String selection = MediaStore.Audio.Media.DURATION + " > 0";

            try (Cursor cursor = getContentResolver().query(uri, projection, selection, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int dataCol = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                    do {
                        String filePath = dataCol >= 0 ? cursor.getString(dataCol) : null;
                        if (filePath == null || filePath.isEmpty()) continue;
                        int lastSep = filePath.lastIndexOf('/');
                        String folder = lastSep > 0 ? filePath.substring(0, lastSep) : "/";
                        folderMap.put(folder, folderMap.getOrDefault(folder, 0) + 1);
                    } while (cursor.moveToNext());
                }
            }

            List<FolderItem> items = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : folderMap.entrySet()) {
                int status = allowedFolders.contains(entry.getKey()) ? STATUS_ALLOWED
                        : excludedFolders.contains(entry.getKey()) ? STATUS_EXCLUDED
                        : STATUS_UNDETERMINED;
                items.add(new FolderItem(entry.getKey(), entry.getValue(), status));
            }

            allFolders = items;

            runOnUiThread(() -> {
                if (items.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    emptyText.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyText.setVisibility(View.GONE);
                    rebuildDisplay();
                }
            });
        }).start();
    }

    private List<Object> buildDisplayItems() {
        if (currentMode == VIEW_MODE_ALL) {
            return new ArrayList<>(allFolders);
        }
        List<Object> items = new ArrayList<>();
        List<FolderItem> allowed = new ArrayList<>();
        List<FolderItem> excluded = new ArrayList<>();
        List<FolderItem> undetermined = new ArrayList<>();

        for (FolderItem f : allFolders) {
            if (f.status == STATUS_ALLOWED) allowed.add(f);
            else if (f.status == STATUS_EXCLUDED) excluded.add(f);
            else undetermined.add(f);
        }

        items.add(new SectionItem(getString(R.string.diizinkan), expandedAllowed));
        if (expandedAllowed) {
            if (!allowed.isEmpty()) items.addAll(allowed);
            else items.add(new EmptyItem());
        }
        items.add(new SectionItem(getString(R.string.excluded_folders), expandedExcluded));
        if (expandedExcluded) {
            if (!excluded.isEmpty()) items.addAll(excluded);
            else items.add(new EmptyItem());
        }
        items.add(new SectionItem(getString(R.string.belum_ditentukan), expandedUndetermined));
        if (expandedUndetermined) {
            if (!undetermined.isEmpty()) items.addAll(undetermined);
            else items.add(new EmptyItem());
        }
        return items;
    }

    private void toggleSection(String title) {
        if (title.equals(getString(R.string.diizinkan))) {
            expandedAllowed = !expandedAllowed;
        } else if (title.equals(getString(R.string.excluded_folders))) {
            expandedExcluded = !expandedExcluded;
        } else if (title.equals(getString(R.string.belum_ditentukan))) {
            expandedUndetermined = !expandedUndetermined;
        }
        rebuildDisplay();
    }

    private void rebuildDisplay() {
        recyclerView.setAdapter(new FolderAdapter(buildDisplayItems()));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private static final int STATUS_ALLOWED = 0;
    private static final int STATUS_EXCLUDED = 1;
    private static final int STATUS_UNDETERMINED = 2;

    private static class FolderItem {
        final String path;
        final int count;
        int status;

        FolderItem(String path, int count, int status) {
            this.path = path;
            this.count = count;
            this.status = status;
        }
    }

    private static class SectionItem {
        final String title;
        final boolean expanded;
        SectionItem(String title, boolean expanded) { this.title = title; this.expanded = expanded; }
    }

    private static class EmptyItem {}

    private class DragCallback extends ItemTouchHelper.SimpleCallback {

        private int targetSectionPos = -1;

        DragCallback() {
            super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView rv, @NonNull RecyclerView.ViewHolder dragged, @NonNull RecyclerView.ViewHolder target) {
            if (currentMode != VIEW_MODE_GROUPED) return false;
            int toPos = target.getAdapterPosition();
            if (toPos < 0) return false;
            List<Object> items = buildDisplayItems();
            if (toPos >= items.size()) return false;
            Object atDragged = items.get(dragged.getAdapterPosition());
            if (!(atDragged instanceof FolderItem)) return false;
            Object atTarget = items.get(toPos);
            if (!(atTarget instanceof FolderItem) && !(atTarget instanceof SectionItem)) return false;
            String section = findSectionAt(items, toPos);
            if (section == null) return false;
            int newStatus = sectionToStatus(section);
            if (newStatus != -1) {
                draggedFolderPath = ((FolderItem) atDragged).path;
                dragTargetStatus = newStatus;
            }
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder vh, int dir) {}

        @Override
        public boolean isLongPressDragEnabled() {
            return currentMode == VIEW_MODE_GROUPED;
        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            super.onSelectedChanged(viewHolder, actionState);
            if (actionState == ItemTouchHelper.ACTION_STATE_DRAG && viewHolder != null) {
                viewHolder.itemView.setElevation(8f);
                viewHolder.itemView.setTranslationZ(8f);
            }
        }

        @Override
        public void clearView(@NonNull RecyclerView rv, @NonNull RecyclerView.ViewHolder viewHolder) {
            viewHolder.itemView.setElevation(0f);
            viewHolder.itemView.setTranslationZ(0f);
            super.clearView(rv, viewHolder);
            if (draggedFolderPath != null && dragTargetStatus != -1) {
                String path = draggedFolderPath;
                int status = dragTargetStatus;
                for (FolderItem f : allFolders) {
                    if (f.path.equals(path) && f.status != status) {
                        rv.post(() -> updateFolderStatus(path, status));
                        break;
                    }
                }
            }
            draggedFolderPath = null;
            dragTargetStatus = -1;
        }
    }

    private String findSectionAt(List<Object> items, int position) {
        for (int i = position; i >= 0; i--) {
            if (items.get(i) instanceof SectionItem) {
                return ((SectionItem) items.get(i)).title;
            }
        }
        return null;
    }

    private int sectionToStatus(String title) {
        if (title.equals(getString(R.string.diizinkan))) return STATUS_ALLOWED;
        if (title.equals(getString(R.string.excluded_folders))) return STATUS_EXCLUDED;
        if (title.equals(getString(R.string.belum_ditentukan))) return STATUS_UNDETERMINED;
        return -1;
    }

    private void showMoveDialog(FolderItem item) {
        List<Integer> availableStatuses = new ArrayList<>();
        List<String> statusLabels = new ArrayList<>();
        for (int s : new int[]{STATUS_ALLOWED, STATUS_EXCLUDED, STATUS_UNDETERMINED}) {
            if (s != item.status) {
                availableStatuses.add(s);
                switch (s) {
                    case STATUS_ALLOWED: statusLabels.add(getString(R.string.diizinkan)); break;
                    case STATUS_EXCLUDED: statusLabels.add(getString(R.string.excluded_folders)); break;
                    case STATUS_UNDETERMINED: statusLabels.add(getString(R.string.belum_ditentukan)); break;
                }
            }
        }
        new MaterialAlertDialogBuilder(this)
                .setTitle(item.path)
                .setItems(statusLabels.toArray(new CharSequence[0]), (dialog, which) ->
                        updateFolderStatus(item.path, availableStatuses.get(which)))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void updateFolderStatus(String folderPath, int newStatus) {
        PreferencesManager prefs = new PreferencesManager(this);
        Set<String> included = new HashSet<>(prefs.getIncludedFolders());
        Set<String> excluded = new HashSet<>(prefs.getExcludedFolders());
        included.remove(folderPath);
        excluded.remove(folderPath);
        if (newStatus == STATUS_ALLOWED) {
            included.add(folderPath);
        } else if (newStatus == STATUS_EXCLUDED) {
            excluded.add(folderPath);
        }
        prefs.setIncludedFolders(included);
        prefs.setExcludedFolders(excluded);
        allowedFolders = included;
        excludedFolders = excluded;
        for (FolderItem f : allFolders) {
            if (f.path.equals(folderPath)) {
                f.status = newStatus;
                break;
            }
        }
        rebuildDisplay();
    }

    private class FolderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final List<Object> items;

        FolderAdapter(List<Object> items) {
            this.items = items;
        }

        @Override
        public int getItemViewType(int position) {
            Object item = items.get(position);
            if (item instanceof SectionItem) return ITEM_TYPE_SECTION;
            if (item instanceof EmptyItem) return ITEM_TYPE_EMPTY;
            return ITEM_TYPE_FOLDER;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == ITEM_TYPE_SECTION) {
                TextView tv = new TextView(parent.getContext());
                tv.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_TitleSmall);
                tv.setTextColor(getColor(R.color.primary));
                float density = getResources().getDisplayMetrics().density;
                tv.setPadding((int)(density * 12), (int)(density * 20), (int)(density * 12), (int)(density * 6));
                tv.setClickable(true);
                tv.setFocusable(true);
                return new RecyclerView.ViewHolder(tv) {};
            }

            if (viewType == ITEM_TYPE_EMPTY) {
                TextView tv = new TextView(parent.getContext());
                tv.setText(R.string.empty_section);
                tv.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_BodySmall);
                tv.setTextColor(getColor(R.color.on_surface_variant));
                int p = (int) (getResources().getDisplayMetrics().density * 8);
                tv.setPadding(p, 0, p, (int) (getResources().getDisplayMetrics().density * 8));
                return new RecyclerView.ViewHolder(tv) {};
            }

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_folder_list, parent, false);
            return new FolderViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof FolderViewHolder) {
                FolderItem item = (FolderItem) items.get(position);
                FolderViewHolder vh = (FolderViewHolder) holder;
                vh.pathText.setText(item.path);
                vh.countText.setText(getString(R.string.song_count, item.count));
                vh.itemView.setOnLongClickListener(null);
                vh.itemView.setOnClickListener(v -> showMoveDialog(item));

                if (showFullPath) {
                    vh.pathText.setMaxLines(Integer.MAX_VALUE);
                    vh.pathText.setEllipsize(null);
                } else {
                    vh.pathText.setMaxLines(1);
                    vh.pathText.setEllipsize(TextUtils.TruncateAt.START);
                }

                if (currentMode == VIEW_MODE_GROUPED) {
                    vh.pathText.setAlpha(1f);
                    vh.pathText.setPaintFlags(vh.pathText.getPaintFlags() & ~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                    if (item.status == STATUS_ALLOWED) {
                        vh.iconText.setText("\u2611");
                        vh.iconText.setTextColor(getColor(R.color.primary));
                    } else if (item.status == STATUS_EXCLUDED) {
                        vh.iconText.setText("\u2612");
                        vh.iconText.setTextColor(getColor(R.color.error));
                    } else {
                        vh.iconText.setText("\u2610");
                        vh.iconText.setTextColor(getColor(R.color.on_surface_variant));
                    }
                    vh.iconText.setVisibility(View.VISIBLE);
                } else {
                    vh.iconText.setVisibility(View.GONE);
                    vh.pathText.setAlpha(1f);
                    vh.pathText.setPaintFlags(vh.pathText.getPaintFlags() & ~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                    if (item.status == STATUS_ALLOWED) {
                        vh.pathText.setTypeface(null, android.graphics.Typeface.BOLD);
                        vh.pathText.setTextColor(getColor(R.color.green));
                    } else if (item.status == STATUS_EXCLUDED) {
                        vh.pathText.setTypeface(null, android.graphics.Typeface.NORMAL);
                        vh.pathText.setTextColor(getColor(R.color.error));
                        vh.pathText.setPaintFlags(vh.pathText.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                        vh.pathText.setAlpha(0.5f);
                    } else {
                        vh.pathText.setTypeface(null, android.graphics.Typeface.NORMAL);
                        vh.pathText.setTextColor(getColor(R.color.on_surface));
                        vh.pathText.setAlpha(1f);
                    }
                }
            } else if (items.get(position) instanceof SectionItem) {
                SectionItem section = (SectionItem) items.get(position);
                String indicator = section.expanded ? "\u25BD " : "\u25B3 ";
                ((TextView) holder.itemView).setText(indicator + section.title);
                holder.itemView.setOnClickListener(v -> toggleSection(section.title));
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    private static class FolderViewHolder extends RecyclerView.ViewHolder {
        final TextView iconText;
        final TextView pathText;
        final TextView countText;

        FolderViewHolder(View itemView) {
            super(itemView);
            iconText = itemView.findViewById(R.id.folder_icon);
            pathText = itemView.findViewById(R.id.folder_path);
            countText = itemView.findViewById(R.id.folder_count);
        }
    }
}

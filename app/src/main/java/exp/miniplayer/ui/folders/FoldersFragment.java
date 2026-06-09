package exp.miniplayer.ui.folders;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import exp.miniplayer.MainActivity;
import exp.miniplayer.R;
import exp.miniplayer.adapter.GroupAdapter;
import exp.miniplayer.data.AudioRepository;
import exp.miniplayer.model.Audio;
import exp.miniplayer.utils.TimeUtils;

public class FoldersFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView emptyText;
    private TextView sectionHeader;
    private AudioRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_folders, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        emptyText = view.findViewById(R.id.empty_text);
        sectionHeader = view.findViewById(R.id.section_header);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        repository = new AudioRepository(requireContext());
        loadFolders();
        return view;
    }

    private static class FolderStats {
        final List<Audio> songs = new ArrayList<>();
        long totalDuration;
        long totalSize;
    }

    private void loadFolders() {
        new Thread(() -> {
            List<Audio> allAudio = repository.getCachedAudio();
            Map<String, FolderStats> folderMap = new LinkedHashMap<>();

            for (Audio audio : allAudio) {
                String path = audio.getFilePath();
                if (path == null || path.isEmpty()) continue;
                int lastSep = path.lastIndexOf('/');
                String folder = lastSep > 0 ? path.substring(0, lastSep) : "/";
                FolderStats stats = folderMap.computeIfAbsent(folder, k -> new FolderStats());
                stats.songs.add(audio);
                stats.totalDuration += audio.getDuration();
                stats.totalSize += audio.getFileSize();
            }

            List<GroupAdapter.GroupItem> items = new ArrayList<>();
            for (Map.Entry<String, FolderStats> entry : folderMap.entrySet()) {
                String folderName = entry.getKey();
                int lastSep = folderName.lastIndexOf('/');
                String displayName = lastSep > 0 ? folderName.substring(lastSep + 1) : folderName;
                FolderStats stats = entry.getValue();
                items.add(new GroupAdapter.GroupItem(displayName, stats.songs.size(),
                        stats.totalDuration, stats.totalSize));
            }

            long totalDuration = 0;
            long totalSize = 0;
            int totalSongs = 0;
            for (FolderStats s : folderMap.values()) {
                totalDuration += s.totalDuration;
                totalSize += s.totalSize;
                totalSongs += s.songs.size();
            }
            final long finalDuration = totalDuration;
            final long finalSize = totalSize;
            final int finalSongs = totalSongs;

            requireActivity().runOnUiThread(() -> {
                if (items.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    emptyText.setVisibility(View.VISIBLE);
                    sectionHeader.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyText.setVisibility(View.GONE);
                    String header = getString(R.string.section_stats,
                            items.size(), finalSongs,
                            TimeUtils.formatDuration(finalDuration),
                            GroupAdapter.formatSize(finalSize));
                    sectionHeader.setText(header);
                    sectionHeader.setVisibility(View.VISIBLE);
                    recyclerView.setAdapter(new GroupAdapter(items, (pos, item) -> {
                        showSongsByFolder(pos, folderMap);
                    }));
                }
            });
        }).start();
    }

    private void showSongsByFolder(int index, Map<String, FolderStats> folderMap) {
        int i = 0;
        String folderPath = null;
        for (String key : folderMap.keySet()) {
            if (i == index) {
                folderPath = key;
                break;
            }
            i++;
        }
        if (folderPath == null) return;

        FolderStats stats = folderMap.get(folderPath);
        if (stats == null || stats.songs.isEmpty() || getContext() == null) return;

        List<Audio> songs = stats.songs;
        String[] titles = new String[songs.size()];
        for (int j = 0; j < songs.size(); j++) {
            titles[j] = songs.get(j).getTitle();
        }

        new AlertDialog.Builder(requireContext())
                .setTitle(folderPath)
                .setItems(titles, (dialog, which) -> {
                    if (requireActivity() instanceof MainActivity) {
                        ((MainActivity) requireActivity()).playFromSongs(songs, which);
                    }
                })
                .setPositiveButton(R.string.cancel, null)
                .show();
    }
}

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

public class FoldersFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView emptyText;
    private AudioRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_folders, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        emptyText = view.findViewById(R.id.empty_text);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        repository = new AudioRepository(requireContext());
        loadFolders();
        return view;
    }

    private void loadFolders() {
        new Thread(() -> {
            List<Audio> allAudio = repository.getCachedAudio();
            Map<String, List<Audio>> folderMap = new LinkedHashMap<>();

            for (Audio audio : allAudio) {
                String path = audio.getFilePath();
                if (path == null || path.isEmpty()) continue;
                int lastSep = path.lastIndexOf('/');
                String folder = lastSep > 0 ? path.substring(0, lastSep) : "/";
                folderMap.computeIfAbsent(folder, k -> new ArrayList<>()).add(audio);
            }

            List<GroupAdapter.GroupItem> items = new ArrayList<>();
            for (Map.Entry<String, List<Audio>> entry : folderMap.entrySet()) {
                String folderName = entry.getKey();
                int lastSep = folderName.lastIndexOf('/');
                String displayName = lastSep > 0 ? folderName.substring(lastSep + 1) : folderName;
                items.add(new GroupAdapter.GroupItem(displayName, entry.getValue().size()));
            }

            requireActivity().runOnUiThread(() -> {
                if (items.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    emptyText.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyText.setVisibility(View.GONE);
                    recyclerView.setAdapter(new GroupAdapter(items, (pos, item) -> {
                        String folderPath = getFolderPathForItem(pos, folderMap);
                        if (folderPath != null) {
                            showSongsByFolder(folderPath, folderMap);
                        }
                    }));
                }
            });
        }).start();
    }

    private String getFolderPathForItem(int index, Map<String, List<Audio>> folderMap) {
        int i = 0;
        for (String key : folderMap.keySet()) {
            if (i == index) return key;
            i++;
        }
        return null;
    }

    private void showSongsByFolder(String folderPath, Map<String, List<Audio>> folderMap) {
        List<Audio> songs = folderMap.get(folderPath);
        if (songs == null || songs.isEmpty() || getContext() == null) return;

        String[] titles = new String[songs.size()];
        for (int i = 0; i < songs.size(); i++) {
            titles[i] = songs.get(i).getTitle();
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

package exp.miniplayer.ui.albums;

import android.content.Intent;
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

import exp.miniplayer.R;
import exp.miniplayer.adapter.GroupAdapter;
import exp.miniplayer.data.AudioRepository;
import exp.miniplayer.model.Audio;
import exp.miniplayer.ui.songs.SongListActivity;
import exp.miniplayer.utils.TimeUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AlbumsFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView emptyText;
    private TextView sectionHeader;
    private AudioRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_albums, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        emptyText = view.findViewById(R.id.empty_text);
        sectionHeader = view.findViewById(R.id.section_header);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        repository = new AudioRepository(requireContext());
        loadAlbums();
        return view;
    }

    private static class AlbumStats {
        final List<Audio> songs = new ArrayList<>();
        long totalDuration;
        long totalSize;
    }

    private void loadAlbums() {
        new Thread(() -> {
            List<Audio> allAudio = repository.getCachedAudio();
            Map<String, AlbumStats> albumMap = new LinkedHashMap<>();

            for (Audio audio : allAudio) {
                String album = audio.getAlbum();
                AlbumStats stats = albumMap.computeIfAbsent(album, k -> new AlbumStats());
                stats.songs.add(audio);
                stats.totalDuration += audio.getDuration();
                stats.totalSize += audio.getFileSize();
            }

            List<GroupAdapter.GroupItem> items = new ArrayList<>();
            for (Map.Entry<String, AlbumStats> entry : albumMap.entrySet()) {
                AlbumStats stats = entry.getValue();
                items.add(new GroupAdapter.GroupItem(entry.getKey(), stats.songs.size(),
                        stats.totalDuration, stats.totalSize));
            }

            long totalDuration = 0;
            long totalSize = 0;
            int totalSongs = 0;
            for (AlbumStats s : albumMap.values()) {
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
                        showSongsByAlbum(pos, albumMap);
                    }));
                }
            });
        }).start();
    }

    private void showSongsByAlbum(int index, Map<String, AlbumStats> albumMap) {
        int i = 0;
        String album = null;
        for (String key : albumMap.keySet()) {
            if (i == index) {
                album = key;
                break;
            }
            i++;
        }
        if (album == null) return;

        AlbumStats stats = albumMap.get(album);
        if (stats == null || stats.songs.isEmpty() || getContext() == null) return;

        Intent intent = new Intent(requireContext(), SongListActivity.class);
        intent.putParcelableArrayListExtra(SongListActivity.EXTRA_SONGS, new ArrayList<>(stats.songs));
        intent.putExtra(SongListActivity.EXTRA_TITLE, album);
        startActivity(intent);
    }
}

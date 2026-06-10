package exp.miniplayer.ui.artists;

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

public class ArtistsFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView emptyText;
    private TextView sectionHeader;
    private AudioRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artists, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        emptyText = view.findViewById(R.id.empty_text);
        sectionHeader = view.findViewById(R.id.section_header);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        repository = new AudioRepository(requireContext());
        loadArtists();
        return view;
    }

    private static class ArtistStats {
        final List<Audio> songs = new ArrayList<>();
        long totalDuration;
        long totalSize;
    }

    private void loadArtists() {
        new Thread(() -> {
            List<Audio> allAudio = repository.getCachedAudio();
            Map<String, ArtistStats> artistMap = new LinkedHashMap<>();

            for (Audio audio : allAudio) {
                String artist = audio.getArtist();
                ArtistStats stats = artistMap.computeIfAbsent(artist, k -> new ArtistStats());
                stats.songs.add(audio);
                stats.totalDuration += audio.getDuration();
                stats.totalSize += audio.getFileSize();
            }

            List<GroupAdapter.GroupItem> items = new ArrayList<>();
            for (Map.Entry<String, ArtistStats> entry : artistMap.entrySet()) {
                ArtistStats stats = entry.getValue();
                items.add(new GroupAdapter.GroupItem(entry.getKey(), stats.songs.size(),
                        stats.totalDuration, stats.totalSize));
            }

            long totalDuration = 0;
            long totalSize = 0;
            int totalSongs = 0;
            for (ArtistStats s : artistMap.values()) {
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
                        showSongsByArtist(pos, artistMap);
                    }));
                }
            });
        }).start();
    }

    private void showSongsByArtist(int index, Map<String, ArtistStats> artistMap) {
        int i = 0;
        String artist = null;
        for (String key : artistMap.keySet()) {
            if (i == index) {
                artist = key;
                break;
            }
            i++;
        }
        if (artist == null) return;

        ArtistStats stats = artistMap.get(artist);
        if (stats == null || stats.songs.isEmpty() || getContext() == null) return;

        Intent intent = new Intent(requireContext(), SongListActivity.class);
        intent.putParcelableArrayListExtra(SongListActivity.EXTRA_SONGS, new ArrayList<>(stats.songs));
        intent.putExtra(SongListActivity.EXTRA_TITLE, artist);
        startActivity(intent);
    }
}

package exp.miniplayer.ui.artists;

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

import exp.miniplayer.R;
import exp.miniplayer.MainActivity;
import exp.miniplayer.adapter.GroupAdapter;
import exp.miniplayer.data.AudioRepository;
import exp.miniplayer.model.Audio;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import exp.miniplayer.R;

public class ArtistsFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView emptyText;
    private AudioRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artists, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        emptyText = view.findViewById(R.id.empty_text);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        repository = new AudioRepository(requireContext());
        loadArtists();
        return view;
    }

    private void loadArtists() {
        new Thread(() -> {
            List<Audio> allAudio = repository.getCachedAudio();
            Map<String, Integer> artistMap = new LinkedHashMap<>();
            for (Audio audio : allAudio) {
                String artist = audio.getArtist();
                artistMap.put(artist, artistMap.getOrDefault(artist, 0) + 1);
            }
            List<GroupAdapter.GroupItem> items = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : artistMap.entrySet()) {
                items.add(new GroupAdapter.GroupItem(entry.getKey(), entry.getValue()));
            }
            requireActivity().runOnUiThread(() -> {
                if (items.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    emptyText.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyText.setVisibility(View.GONE);
                    recyclerView.setAdapter(new GroupAdapter(items, (pos, item) -> {
                        showSongsByArtist(item.name, allAudio);
                    }));
                }
            });
        }).start();
    }

    private void showSongsByArtist(String artist, List<Audio> allAudio) {
        List<Audio> songs = new ArrayList<>();
        for (Audio audio : allAudio) {
            if (audio.getArtist().equals(artist)) {
                songs.add(audio);
            }
        }
        if (songs.isEmpty() || getContext() == null) return;

        String[] titles = new String[songs.size()];
        for (int i = 0; i < songs.size(); i++) {
            titles[i] = songs.get(i).getTitle();
        }

        new AlertDialog.Builder(requireContext())
                .setTitle(artist)
                .setItems(titles, (dialog, which) -> {
                    if (requireActivity() instanceof MainActivity) {
                        ((MainActivity) requireActivity()).playFromSongs(songs, which);
                    }
                })
                .setPositiveButton(R.string.cancel, null)
                .show();
    }
}

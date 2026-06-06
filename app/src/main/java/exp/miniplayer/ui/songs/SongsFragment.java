package exp.miniplayer.ui.songs;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import exp.miniplayer.MainActivity;
import exp.miniplayer.R;
import exp.miniplayer.adapter.SongAdapter;
import exp.miniplayer.data.AudioRepository;
import exp.miniplayer.database.PlaylistEntity;
import exp.miniplayer.model.Audio;
import exp.miniplayer.utils.PermissionHelper;
import exp.miniplayer.utils.PreferencesManager;

import java.util.ArrayList;
import java.util.List;

public class SongsFragment extends Fragment implements SongAdapter.OnItemClickListener {

    private SongsViewModel viewModel;
    private RecyclerView recyclerView;
    private SongAdapter adapter;
    private View emptyView;
    private SearchView searchView;
    private AudioRepository repository;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_songs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.songs_recycler_view);
        emptyView = view.findViewById(R.id.empty_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        viewModel = new ViewModelProvider(this).get(SongsViewModel.class);
        repository = viewModel.getRepository();

        adapter = new SongAdapter(requireContext(), new ArrayList<>());
        adapter.setOnItemClickListener(this);
        adapter.setOnItemLongClickListener((audio, position) -> {
            showSongOptions(audio);
            return true;
        });
        recyclerView.setAdapter(adapter);

        viewModel.getSongs().observe(getViewLifecycleOwner(), audioList -> {
            if (audioList == null || audioList.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
                adapter.updateData(audioList);
            }
        });

        if (PermissionHelper.hasAudioPermission(requireContext())) {
            viewModel.scanAudio();
        } else {
            PermissionHelper.requestAudioPermission(requireActivity());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (PermissionHelper.hasAudioPermission(requireContext())) {
            viewModel.refresh();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.songs_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        if (searchItem != null) {
            View actionView = searchItem.getActionView();
            if (actionView instanceof SearchView) {
                searchView = (SearchView) actionView;
                searchView.setQueryHint(getString(R.string.search_songs));
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        viewModel.search(query);
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        viewModel.search(newText);
                        return true;
                    }
                });
            }
        }

        MenuItem sortItem = menu.findItem(R.id.action_sort);
        if (sortItem != null) {
            sortItem.setOnMenuItemClickListener(item -> {
                showSortDialog();
                return true;
            });
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onItemClick(Audio audio, int position) {
        List<Audio> queue = repository.getCachedAudio();
        Activity activity = requireActivity();
        if (activity instanceof MainActivity) {
            ((MainActivity) activity).playFromSongs(queue, position);
        }
    }

    private void showSongOptions(Audio audio) {
        PopupMenu popup = new PopupMenu(requireContext(), requireView());
        popup.getMenuInflater().inflate(R.menu.song_options_menu, popup.getMenu());

        if (repository.isFavorite(audio.getId())) {
            popup.getMenu().findItem(R.id.action_add_favorite)
                    .setTitle(getString(R.string.remove_from_favorites));
        }

        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_add_favorite) {
                repository.toggleFavorite(audio);
                String msg = repository.isFavorite(audio.getId()) ?
                        "Added to favorites" : "Removed from favorites";
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
                return true;
            } else if (item.getItemId() == R.id.action_add_to_playlist) {
                showPlaylistDialog(audio);
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void showPlaylistDialog(Audio audio) {
        repository.getPlaylists().observe(getViewLifecycleOwner(), playlists -> {
            if (playlists == null || playlists.isEmpty()) {
                Toast.makeText(requireContext(), "No playlists available", Toast.LENGTH_SHORT).show();
                return;
            }
            String[] names = new String[playlists.size()];
            for (int i = 0; i < playlists.size(); i++) {
                names[i] = playlists.get(i).getName();
            }
            new AlertDialog.Builder(requireContext())
                    .setTitle("Add to Playlist")
                    .setItems(names, (dialog, which) -> {
                        PlaylistEntity playlist = playlists.get(which);
                        boolean added = repository.addToPlaylist(playlist.getId(), audio);
                        if (added) {
                            Toast.makeText(requireContext(), "Added to " + playlist.getName(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), "Already in playlist",
                                    Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void showSortDialog() {
        PreferencesManager prefs = new PreferencesManager(requireContext());
        int currentSort = prefs.getSortMode();
        String[] options = {
                getString(R.string.name_a_z),
                getString(R.string.name_z_a),
                getString(R.string.duration),
                getString(R.string.date_added)
        };
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.sort_by)
                .setSingleChoiceItems(options, currentSort, (dialog, which) -> {
                    viewModel.sort(which);
                    dialog.dismiss();
                })
                .show();
    }
}

package exp.miniplayer.ui.playlist;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exp.miniplayer.R;
import exp.miniplayer.adapter.GroupAdapter;
import exp.miniplayer.adapter.PlaylistAdapter;
import exp.miniplayer.data.AudioRepository;
import exp.miniplayer.database.PlaylistEntity;
import exp.miniplayer.database.PlaylistSongEntity;
import exp.miniplayer.model.Audio;
import exp.miniplayer.utils.PlaylistIO;
import exp.miniplayer.utils.TimeUtils;

public class PlaylistFragment extends Fragment {

    private PlaylistViewModel viewModel;
    private RecyclerView recyclerView;
    private PlaylistAdapter adapter;
    private View emptyView;
    private TextView sectionHeader;
    private FloatingActionButton fab;
    private AudioRepository importRepo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_playlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.playlist_recycler_view);
        emptyView = view.findViewById(R.id.empty_view);
        sectionHeader = view.findViewById(R.id.section_header);
        fab = view.findViewById(R.id.fab_create_playlist);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        importRepo = new AudioRepository(requireContext());
        viewModel = new ViewModelProvider(this).get(PlaylistViewModel.class);

        viewModel.getPlaylists().observe(getViewLifecycleOwner(), playlists -> {
            if (playlists == null || playlists.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
                sectionHeader.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
                adapter = new PlaylistAdapter(requireContext(), playlists);

                Map<Integer, Integer> counts = new HashMap<>();
                Map<Integer, String> stats = new HashMap<>();
                List<Audio> allAudio = importRepo.getCachedAudio();
                Map<Long, Long> fileSizeMap = new HashMap<>();
                for (Audio audio : allAudio) {
                    fileSizeMap.put(audio.getId(), audio.getFileSize());
                }

                long grandDuration = 0;
                long grandSize = 0;
                int grandCount = 0;

                for (PlaylistEntity p : playlists) {
                    int count = importRepo.getPlaylistSongCount(p.getId());
                    counts.put(p.getId(), count);

                    long pDur = 0;
                    long pSize = 0;
                    List<PlaylistSongEntity> songs = importRepo.getPlaylistSongsSync(p.getId());
                    for (PlaylistSongEntity song : songs) {
                        pDur += song.getDuration();
                        Long sz = fileSizeMap.get(song.getAudioId());
                        if (sz != null) pSize += sz;
                    }
                    grandDuration += pDur;
                    grandSize += pSize;
                    grandCount += count;

                    String durStr = TimeUtils.formatDuration(pDur);
                    String sizeStr = GroupAdapter.formatSize(pSize);
                    stats.put(p.getId(), durStr + " \u00b7 " + sizeStr);
                }

                String header = getString(R.string.section_stats,
                        playlists.size(), grandCount,
                        TimeUtils.formatDuration(grandDuration),
                        GroupAdapter.formatSize(grandSize));
                sectionHeader.setText(header);
                sectionHeader.setVisibility(View.VISIBLE);

                adapter.setSongCounts(counts);
                adapter.setPlaylistStats(stats);
                adapter.setOnItemClickListener((playlist, position) -> {
                    Intent intent = new Intent(requireContext(), PlaylistDetailActivity.class);
                    intent.putExtra("playlist_id", playlist.getId());
                    intent.putExtra("playlist_name", playlist.getName());
                    startActivity(intent);
                });
                adapter.setOnItemLongClickListener((playlist, position) -> {
                    showPlaylistOptions(playlist);
                    return true;
                });
                recyclerView.setAdapter(adapter);
            }
        });

        fab.setOnClickListener(v -> showCreatePlaylistDialog());
        fab.setOnLongClickListener(v -> {
            showImportDialog();
            return true;
        });
    }

    private void showImportDialog() {
        String[] options = {"M3U", "JSON"};
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.import_playlist)
                .setItems(options, (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    if (which == 0) {
                        intent.setType("audio/x-mpegurl");
                    } else {
                        intent.setType("application/json");
                    }
                    importLauncher.launch(intent);
                })
                .show();
    }

    private final androidx.activity.result.ActivityResultLauncher<Intent> importLauncher =
            registerForActivityResult(
                    new androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getData() == null || result.getData().getData() == null) return;
                        Uri uri = result.getData().getData();
                        String type = result.getData().getType();
                        try {
                            if (type != null && type.contains("json")) {
                                PlaylistIO.importJSON(requireContext(), importRepo, uri);
                            } else {
                                PlaylistIO.importM3U(requireContext(), importRepo, uri, null);
                            }
                            Toast.makeText(requireContext(), R.string.playlist_imported, Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(requireContext(), R.string.playlist_import_error, Toast.LENGTH_SHORT).show();
                        }
                    });

    private void showCreatePlaylistDialog() {
        android.widget.EditText input = new android.widget.EditText(requireContext());
        input.setHint(getString(R.string.playlist_name));

        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.create_playlist)
                .setView(input)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    String name = input.getText().toString().trim();
                    if (!name.isEmpty()) {
                        viewModel.createPlaylist(name);
                        Toast.makeText(requireContext(), "Playlist created", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showPlaylistOptions(PlaylistEntity playlist) {
        PopupMenu popup = new PopupMenu(requireContext(), recyclerView);
        popup.getMenu().add(0, 1, 0, R.string.rename_playlist);
        popup.getMenu().add(0, 2, 0, R.string.delete_playlist);

        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == 1) {
                showRenameDialog(playlist);
                return true;
            } else if (item.getItemId() == 2) {
                showDeleteConfirmDialog(playlist);
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void showRenameDialog(PlaylistEntity playlist) {
        android.widget.EditText input = new android.widget.EditText(requireContext());
        input.setText(playlist.getName());
        input.setSelection(input.getText().length());

        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.rename_playlist)
                .setView(input)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    String name = input.getText().toString().trim();
                    if (!name.isEmpty()) {
                        viewModel.renamePlaylist(playlist.getId(), name);
                        Toast.makeText(requireContext(), "Playlist renamed", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showDeleteConfirmDialog(PlaylistEntity playlist) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.delete_playlist)
                .setMessage(R.string.delete_playlist_confirm)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    viewModel.deletePlaylist(playlist.getId());
                    Toast.makeText(requireContext(), "Playlist deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}

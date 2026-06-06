package exp.miniplayer.ui.playlist;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import exp.miniplayer.R;
import exp.miniplayer.adapter.PlaylistAdapter;
import exp.miniplayer.database.PlaylistEntity;

import java.util.ArrayList;

public class PlaylistFragment extends Fragment {

    private PlaylistViewModel viewModel;
    private RecyclerView recyclerView;
    private PlaylistAdapter adapter;
    private View emptyView;
    private FloatingActionButton fab;

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
        fab = view.findViewById(R.id.fab_create_playlist);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        viewModel = new ViewModelProvider(this).get(PlaylistViewModel.class);

        viewModel.getPlaylists().observe(getViewLifecycleOwner(), playlists -> {
            if (playlists == null || playlists.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
                adapter = new PlaylistAdapter(requireContext(), playlists);
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
    }

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

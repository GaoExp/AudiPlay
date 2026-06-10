package exp.miniplayer.ui.playlist;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.OutputStream;

import exp.miniplayer.MainActivity;
import exp.miniplayer.R;
import exp.miniplayer.adapter.PlaylistDetailAdapter;
import exp.miniplayer.data.AudioRepository;
import exp.miniplayer.database.PlaylistEntity;
import exp.miniplayer.database.PlaylistSongEntity;
import exp.miniplayer.model.Audio;
import exp.miniplayer.utils.PlaylistIO;
import exp.miniplayer.utils.QueueHolder;

import java.util.ArrayList;
import java.util.List;

public class PlaylistDetailActivity extends AppCompatActivity {

    private int playlistId;
    private String playlistName;
    private PlaylistDetailViewModel viewModel;
    private RecyclerView recyclerView;
    private PlaylistDetailAdapter adapter;
    private View emptyView;
    private TextView toolbarTitle;
    private AudioRepository exportRepo;

    private static final int REQUEST_EXPORT_M3U = 100;
    private static final int REQUEST_EXPORT_JSON = 101;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_detail);

        playlistId = getIntent().getIntExtra("playlist_id", -1);
        playlistName = getIntent().getStringExtra("playlist_name");
        exportRepo = new AudioRepository(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(playlistName != null ? playlistName : "Playlist");

        recyclerView = findViewById(R.id.playlist_detail_recycler_view);
        emptyView = findViewById(R.id.empty_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        viewModel = new ViewModelProvider(this).get(PlaylistDetailViewModel.class);

        viewModel.getPlaylistSongs(playlistId).observe(this, songs -> {
            if (songs == null || songs.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
                adapter = new PlaylistDetailAdapter(this, songs);
                adapter.setOnItemClickListener((song, position) -> {
                    List<Audio> queue = new ArrayList<>();
                    for (PlaylistSongEntity s : songs) {
                        queue.add(new Audio(s.getAudioId(), s.getTitle(), s.getArtist(),
                                s.getAlbum(), s.getDuration(), s.getUri(),
                                s.getAlbumArt(), s.getAddedAt()));
                    }
                    QueueHolder.setQueue(queue, position);
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                });
                adapter.setOnItemLongClickListener((song, position) -> {
                    new AlertDialog.Builder(this)
                            .setTitle("Remove from playlist?")
                            .setPositiveButton("Remove", (dialog, which) -> {
                                viewModel.removeFromPlaylist(playlistId, song.getAudioId());
                                Toast.makeText(this, "Removed", Toast.LENGTH_SHORT).show();
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                    return true;
                });
                recyclerView.setAdapter(adapter);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 1, 0, R.string.export_as_m3u)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        menu.add(0, 2, 0, R.string.export_as_json)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            exportM3U();
            return true;
        } else if (item.getItemId() == 2) {
            exportJSON();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void exportM3U() {
        String filename = (playlistName != null ? playlistName : "playlist") + ".m3u";
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("audio/x-mpegurl");
        intent.putExtra(Intent.EXTRA_TITLE, filename);
        exportLauncher.launch(intent);
        pendingExportFormat = "m3u";
    }

    private void exportJSON() {
        String filename = "playlists.json";
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/json");
        intent.putExtra(Intent.EXTRA_TITLE, filename);
        exportLauncher.launch(intent);
        pendingExportFormat = "json";
    }

    private String pendingExportFormat;

    private final androidx.activity.result.ActivityResultLauncher<Intent> exportLauncher =
            registerForActivityResult(
                    new androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getData() == null || result.getData().getData() == null) return;
                        Uri uri = result.getData().getData();
                        try {
                            OutputStream output = getContentResolver().openOutputStream(uri);
                            if (output == null) return;
                            if ("m3u".equals(pendingExportFormat)) {
                                PlaylistEntity playlist = exportRepo.getPlaylist(playlistId);
                                if (playlist != null) {
                                    PlaylistIO.exportAsM3U(this, exportRepo, playlist, output);
                                }
                            } else if ("json".equals(pendingExportFormat)) {
                                PlaylistIO.exportAllAsJSON(this, exportRepo, output);
                            }
                            output.close();
                            Toast.makeText(this, R.string.playlist_exported, Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, R.string.export_error, Toast.LENGTH_SHORT).show();
                        }
                    });

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

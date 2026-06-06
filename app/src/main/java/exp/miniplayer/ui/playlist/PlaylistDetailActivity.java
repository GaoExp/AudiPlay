package exp.miniplayer.ui.playlist;

import android.content.Intent;
import android.os.Bundle;
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

import exp.miniplayer.R;
import exp.miniplayer.adapter.PlaylistDetailAdapter;
import exp.miniplayer.database.PlaylistSongEntity;
import exp.miniplayer.model.Audio;
import exp.miniplayer.ui.nowplaying.NowPlayingActivity;
import exp.miniplayer.utils.QueueHolder;

import java.util.ArrayList;
import java.util.List;

public class PlaylistDetailActivity extends AppCompatActivity {

    private int playlistId;
    private PlaylistDetailViewModel viewModel;
    private RecyclerView recyclerView;
    private PlaylistDetailAdapter adapter;
    private View emptyView;
    private TextView toolbarTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_detail);

        playlistId = getIntent().getIntExtra("playlist_id", -1);
        String playlistName = getIntent().getStringExtra("playlist_name");

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
                    Intent intent = new Intent(this, NowPlayingActivity.class);
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

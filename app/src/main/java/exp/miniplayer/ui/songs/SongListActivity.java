package exp.miniplayer.ui.songs;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

import exp.miniplayer.MainActivity;
import exp.miniplayer.R;
import exp.miniplayer.adapter.GroupAdapter;
import exp.miniplayer.adapter.SongAdapter;
import exp.miniplayer.model.Audio;
import exp.miniplayer.utils.QueueHolder;
import exp.miniplayer.utils.TimeUtils;

public class SongListActivity extends AppCompatActivity {

    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_SONGS = "songs";

    private RecyclerView recyclerView;
    private TextView emptyView;
    private TextView sectionHeader;
    private SongAdapter adapter;
    private List<Audio> songs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        TextView titleView = findViewById(R.id.toolbar_title);
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        titleView.setText(title != null ? title : "");

        recyclerView = findViewById(R.id.song_list);
        emptyView = findViewById(R.id.empty_view);
        sectionHeader = findViewById(R.id.section_header);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        songs = getIntent().getParcelableArrayListExtra(EXTRA_SONGS);
        if (songs == null) songs = new ArrayList<>();

        adapter = new SongAdapter(this, songs);
        adapter.setOnItemClickListener((audio, position) -> {
            QueueHolder.setQueue(songs, position);
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        if (songs.isEmpty()) {
            recyclerView.setVisibility(android.view.View.GONE);
            emptyView.setVisibility(android.view.View.VISIBLE);
            sectionHeader.setVisibility(android.view.View.GONE);
        } else {
            recyclerView.setVisibility(android.view.View.VISIBLE);
            emptyView.setVisibility(android.view.View.GONE);

            long totalDuration = 0;
            long totalSize = 0;
            for (Audio a : songs) {
                totalDuration += a.getDuration();
                totalSize += a.getFileSize();
            }
            String header = getString(R.string.section_stats,
                    songs.size(), songs.size(),
                    TimeUtils.formatDuration(totalDuration),
                    GroupAdapter.formatSize(totalSize));
            sectionHeader.setText(header);
            sectionHeader.setVisibility(android.view.View.VISIBLE);
        }
    }
}

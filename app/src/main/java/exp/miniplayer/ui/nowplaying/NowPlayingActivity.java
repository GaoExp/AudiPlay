package exp.miniplayer.ui.nowplaying;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import exp.miniplayer.R;
import exp.miniplayer.model.Audio;
import exp.miniplayer.utils.PreferencesManager;
import exp.miniplayer.utils.QueueHolder;
import exp.miniplayer.utils.TimeUtils;

import java.io.InputStream;
import java.util.List;

public class NowPlayingActivity extends AppCompatActivity {

    private NowPlayingViewModel viewModel;
    private ImageView albumArt;
    private TextView titleText;
    private TextView artistText;
    private TextView currentTimeText;
    private TextView totalTimeText;
    private SeekBar seekBar;
    private ImageButton prevButton;
    private ImageButton playPauseButton;
    private ImageButton nextButton;
    private ImageButton shuffleButton;
    private ImageButton repeatButton;
    private ImageView favoriteButton;

    private void initViews() {
        albumArt = findViewById(R.id.now_playing_album_art);
        titleText = findViewById(R.id.now_playing_title);
        artistText = findViewById(R.id.now_playing_artist);
        currentTimeText = findViewById(R.id.current_time);
        totalTimeText = findViewById(R.id.total_time);
        seekBar = findViewById(R.id.seek_bar);
        prevButton = findViewById(R.id.prev_button);
        playPauseButton = findViewById(R.id.play_pause_button);
        nextButton = findViewById(R.id.next_button);
        shuffleButton = findViewById(R.id.shuffle_button);
        repeatButton = findViewById(R.id.repeat_button);
        favoriteButton = findViewById(R.id.favorite_button);
    }

    private void observeViewModel() {
        viewModel.getCurrentAudio().observe(this, audio -> {
            if (audio != null) {
                titleText.setText(audio.getTitle());
                artistText.setText(audio.getArtist());
                loadAlbumArt(audio.getAlbumArt());
            }
        });

        viewModel.getIsFavorite().observe(this, isFav -> {
            Audio current = viewModel.getCurrentAudio().getValue();
            if (current != null) {
                if (isFav) {
                    favoriteButton.setImageResource(R.drawable.ic_favorite);
                    favoriteButton.setColorFilter(ContextCompat.getColor(this, R.color.primary));
                } else {
                    favoriteButton.setImageResource(R.drawable.ic_favorite_border);
                    favoriteButton.setColorFilter(ContextCompat.getColor(this, R.color.on_surface_variant));
                }
            }
        });

        viewModel.getIsPlaying().observe(this, playing -> {
            playPauseButton.setImageResource(
                    playing ? R.drawable.ic_pause : R.drawable.ic_play);
        });

        viewModel.getCurrentPosition().observe(this, position -> {
            currentTimeText.setText(TimeUtils.formatDuration(position));
            Long dur = viewModel.getDuration().getValue();
            if (dur != null && dur > 0) {
                seekBar.setProgress((int) (position * 1000 / dur));
            }
        });

        viewModel.getDuration().observe(this, dur -> {
            totalTimeText.setText(TimeUtils.formatDuration(dur));
        });

        viewModel.getShuffleEnabled().observe(this, enabled -> {
            shuffleButton.setColorFilter(
                    enabled ? ContextCompat.getColor(this, R.color.primary)
                            : ContextCompat.getColor(this, R.color.on_surface_variant));
        });

        viewModel.getRepeatMode().observe(this, mode -> {
            switch (mode) {
                case androidx.media3.common.Player.REPEAT_MODE_OFF:
                    repeatButton.setImageResource(R.drawable.ic_repeat);
                    repeatButton.setColorFilter(ContextCompat.getColor(this, R.color.on_surface_variant));
                    break;
                case androidx.media3.common.Player.REPEAT_MODE_ALL:
                    repeatButton.setImageResource(R.drawable.ic_repeat);
                    repeatButton.setColorFilter(ContextCompat.getColor(this, R.color.primary));
                    break;
                case androidx.media3.common.Player.REPEAT_MODE_ONE:
                    repeatButton.setImageResource(R.drawable.ic_repeat_one);
                    repeatButton.setColorFilter(ContextCompat.getColor(this, R.color.primary));
                    break;
            }
        });
    }

    private void setupListeners() {
        playPauseButton.setOnClickListener(v -> viewModel.togglePlayPause());
        prevButton.setOnClickListener(v -> viewModel.previous());
        nextButton.setOnClickListener(v -> viewModel.next());
        shuffleButton.setOnClickListener(v -> viewModel.toggleShuffle());
        repeatButton.setOnClickListener(v -> viewModel.cycleRepeatMode());

        favoriteButton.setOnClickListener(v -> {
            Audio current = viewModel.getCurrentAudio().getValue();
            if (current != null) {
                viewModel.toggleFavorite(current);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    Long dur = viewModel.getDuration().getValue();
                    if (dur != null && dur > 0) {
                        long position = (long) progress * dur / 1000;
                        viewModel.seekTo(position);
                        currentTimeText.setText(TimeUtils.formatDuration(position));
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void loadQueueAndPlay() {
        List<Audio> queue = QueueHolder.getQueue();
        int startIndex = QueueHolder.getStartIndex();
        QueueHolder.clear();

        if (queue != null && !queue.isEmpty()) {
            viewModel.playQueue(queue, startIndex);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_now_playing);

            PreferencesManager prefs = new PreferencesManager(this);
            if (prefs.isKeepScreenOn()) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }

            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }

            initViews();
            viewModel = new ViewModelProvider(this).get(NowPlayingViewModel.class);
            viewModel.bindService(this);
            loadQueueAndPlay();
            observeViewModel();
            setupListeners();
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    @Override
    protected void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        loadQueueAndPlay();
    }

    @Override
    protected void onDestroy() {
        viewModel.unbindService(this);
        super.onDestroy();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void loadAlbumArt(String albumArtUri) {
        if (albumArtUri == null) {
            albumArt.setImageResource(R.drawable.ic_album_default);
            return;
        }
        try {
            Uri uri = Uri.parse(albumArtUri);
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
                if (bitmap != null) {
                    albumArt.setImageBitmap(bitmap);
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        albumArt.setImageResource(R.drawable.ic_album_default);
    }

}

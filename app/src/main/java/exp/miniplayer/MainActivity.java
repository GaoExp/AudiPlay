package exp.miniplayer;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;

import exp.miniplayer.data.AudioRepository;
import exp.miniplayer.model.Audio;
import exp.miniplayer.player.MusicPlayer;
import exp.miniplayer.service.MusicService;
import exp.miniplayer.ui.favorites.FavoritesFragment;
import exp.miniplayer.ui.playlist.PlaylistFragment;
import exp.miniplayer.ui.settings.SettingsFragment;
import exp.miniplayer.ui.songs.SongsFragment;
import exp.miniplayer.utils.QueueHolder;
import exp.miniplayer.utils.TimeUtils;

import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MusicPlayer.PlayerListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FragmentManager fragmentManager;
    private AudioRepository repository;

    private MusicService musicService;
    private boolean bound = false;
    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicService = binder.getService();
            bound = true;
            musicService.getMusicPlayer().addListener(MainActivity.this);
            updateNowPlaying();
            startProgressUpdates();
            if (pendingQueue != null && pendingStartIndex >= 0) {
                musicService.playQueue(pendingQueue, pendingStartIndex);
                pendingQueue = null;
                pendingStartIndex = -1;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (musicService != null) {
                musicService.getMusicPlayer().removeListener(MainActivity.this);
            }
            musicService = null;
            bound = false;
        }
    };

    private BottomSheetBehavior<View> sheetBehavior;

    private List<Audio> pendingQueue;
    private int pendingStartIndex = -1;

    private View miniPlayerOverlay;
    private ConstraintLayout miniPlayer;
    private ImageView miniPlayerAlbumArt;
    private TextView miniPlayerTitle;
    private TextView miniPlayerArtist;
    private ImageButton miniPlayerPlayPause;
    private ImageButton miniPlayerPrev;
    private ImageButton miniPlayerNext;

    private ImageView nowPlayingAlbumArt;
    private TextView nowPlayingTitle;
    private TextView nowPlayingArtist;
    private TextView currentTimeText;
    private TextView totalTimeText;
    private SeekBar seekBar;
    private ImageButton prevButton;
    private ImageButton playPauseButton;
    private ImageButton nextButton;
    private ImageButton shuffleButton;
    private ImageButton repeatButton;
    private ImageButton favoriteButton;
    private ImageButton closeSheetButton;

    private Handler progressHandler;
    private Runnable progressRunnable;
    private boolean seekBarTouching = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        repository = new AudioRepository(getApplication());

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_songs) {
                loadFragment(new SongsFragment(), "songs");
            } else if (itemId == R.id.nav_favorites) {
                loadFragment(new FavoritesFragment(), "favorites");
            } else if (itemId == R.id.nav_playlists) {
                loadFragment(new PlaylistFragment(), "playlists");
            } else if (itemId == R.id.nav_settings) {
                loadFragment(new SettingsFragment(), "settings");
            }
            drawerLayout.closeDrawers();
            return true;
        });

        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.nav_songs);
            loadFragment(new SongsFragment(), "songs");
        }

        startService(new Intent(this, MusicService.class));

        initBottomSheet();
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, MusicService.class), connection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (bound) {
            if (musicService != null) {
                musicService.getMusicPlayer().removeListener(this);
            }
            unbindService(connection);
            bound = false;
        }
        stopProgressUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPendingQueue();
    }

    @Override
    protected void onDestroy() {
        stopProgressUpdates();
        super.onDestroy();
    }

    private void checkPendingQueue() {
        List<Audio> queue = QueueHolder.getQueue();
        int startIndex = QueueHolder.getStartIndex();
        if (queue != null && !queue.isEmpty()) {
            QueueHolder.clear();
            playFromSongs(queue, startIndex);
        }
    }

    private void initBottomSheet() {
        View sheetView = findViewById(R.id.now_playing_sheet);
        sheetBehavior = BottomSheetBehavior.from(sheetView);
        float density = getResources().getDisplayMetrics().density;
        sheetBehavior.setPeekHeight((int) (80 * density));
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        miniPlayerOverlay = findViewById(R.id.mini_player_overlay);
        miniPlayer = findViewById(R.id.mini_player);
        miniPlayerAlbumArt = findViewById(R.id.mini_player_album_art);
        miniPlayerTitle = findViewById(R.id.mini_player_title);
        miniPlayerArtist = findViewById(R.id.mini_player_artist);
        miniPlayerPlayPause = findViewById(R.id.mini_player_play_pause);
        miniPlayerPrev = findViewById(R.id.mini_player_prev);
        miniPlayerNext = findViewById(R.id.mini_player_next);

        nowPlayingAlbumArt = findViewById(R.id.now_playing_album_art);
        nowPlayingTitle = findViewById(R.id.now_playing_title);
        nowPlayingArtist = findViewById(R.id.now_playing_artist);
        currentTimeText = findViewById(R.id.current_time);
        totalTimeText = findViewById(R.id.total_time);
        seekBar = findViewById(R.id.seek_bar);
        prevButton = findViewById(R.id.prev_button);
        playPauseButton = findViewById(R.id.play_pause_button);
        nextButton = findViewById(R.id.next_button);
        shuffleButton = findViewById(R.id.shuffle_button);
        repeatButton = findViewById(R.id.repeat_button);
        favoriteButton = findViewById(R.id.favorite_button);
        closeSheetButton = findViewById(R.id.close_sheet_button);

        closeSheetButton.setOnClickListener(v -> {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        });

        miniPlayer.setOnClickListener(v -> {
            if (sheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        miniPlayerPlayPause.setOnClickListener(v -> togglePlayPause());
        miniPlayerPrev.setOnClickListener(v -> previous());
        miniPlayerNext.setOnClickListener(v -> next());

        playPauseButton.setOnClickListener(v -> togglePlayPause());
        prevButton.setOnClickListener(v -> previous());
        nextButton.setOnClickListener(v -> next());
        shuffleButton.setOnClickListener(v -> toggleShuffle());
        repeatButton.setOnClickListener(v -> cycleRepeatMode());
        favoriteButton.setOnClickListener(v -> toggleFavorite());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && musicService != null) {
                    long dur = musicService.getMusicPlayer().getDuration();
                    if (dur > 0) {
                        long position = (long) progress * dur / 1000;
                        musicService.getMusicPlayer().seekTo(position);
                        currentTimeText.setText(TimeUtils.formatDuration(position));
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekBarTouching = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBarTouching = false;
            }
        });

        sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View sheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    miniPlayerOverlay.setVisibility(View.INVISIBLE);
                } else {
                    miniPlayerOverlay.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSlide(View sheet, float slideOffset) {
                float miniAlpha = 1f - Math.max(0, Math.min(1, slideOffset));
                miniPlayerOverlay.setAlpha(miniAlpha);
            }
        });

        GestureDetector albumArtGesture = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        previous();
                    } else {
                        next();
                    }
                    return true;
                }
                return false;
            }
        });

        nowPlayingAlbumArt.setOnTouchListener((v, event) -> albumArtGesture.onTouchEvent(event));

        progressHandler = new Handler(Looper.getMainLooper());
        progressRunnable = new Runnable() {
            @Override
            public void run() {
                if (musicService != null && musicService.getMusicPlayer() != null
                        && !seekBarTouching) {
                    MusicPlayer player = musicService.getMusicPlayer();
                    long position = player.getCurrentPosition();
                    long dur = player.getDuration();
                    currentTimeText.setText(TimeUtils.formatDuration(position));
                    totalTimeText.setText(TimeUtils.formatDuration(dur));
                    if (dur > 0) {
                        seekBar.setProgress((int) (position * 1000 / dur));
                    }
                }
                progressHandler.postDelayed(this, 500);
            }
        };
    }

    private void startProgressUpdates() {
        progressHandler.post(progressRunnable);
    }

    private void stopProgressUpdates() {
        progressHandler.removeCallbacks(progressRunnable);
    }

    private void updateNowPlaying() {
        if (musicService == null) return;
        MusicPlayer player = musicService.getMusicPlayer();
        Audio current = player.getCurrentAudio();
        if (current != null) {
            String title = current.getTitle();
            String artist = current.getArtist();

            miniPlayerTitle.setText(title);
            miniPlayerArtist.setText(artist);
            miniPlayerPlayPause.setImageResource(
                    player.isPlaying() ? R.drawable.ic_pause : R.drawable.ic_play);

            nowPlayingTitle.setText(title);
            nowPlayingArtist.setText(artist);
            playPauseButton.setImageResource(
                    player.isPlaying() ? R.drawable.ic_pause : R.drawable.ic_play);

            loadMiniPlayerAlbumArt(current.getAlbumArt());
            loadNowPlayingAlbumArt(current.getAlbumArt());

            updateShuffleButton(player.isShuffleEnabled());
            updateRepeatButton(player.getRepeatMode());
            updateFavorite(current.getId());

            if (sheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        } else {
            miniPlayerTitle.setText(R.string.app_name);
            miniPlayerArtist.setText(R.string.select_song);
            miniPlayerAlbumArt.setImageResource(R.drawable.ic_album_default);
            miniPlayerPlayPause.setImageResource(R.drawable.ic_play);

            nowPlayingTitle.setText(R.string.app_name);
            nowPlayingArtist.setText(R.string.select_song);
            nowPlayingAlbumArt.setImageResource(R.drawable.ic_album_default);
            playPauseButton.setImageResource(R.drawable.ic_play);

            updateShuffleButton(false);
            updateRepeatButton(androidx.media3.common.Player.REPEAT_MODE_OFF);
            favoriteButton.setImageResource(R.drawable.ic_favorite_border);
            favoriteButton.setColorFilter(
                    ContextCompat.getColor(this, R.color.on_surface_variant));
        }
    }

    private void togglePlayPause() {
        if (musicService != null) {
            musicService.getMusicPlayer().togglePlayPause();
        }
    }

    private void previous() {
        if (musicService != null) {
            musicService.getMusicPlayer().previous();
        }
    }

    private void next() {
        if (musicService != null) {
            musicService.getMusicPlayer().next();
        }
    }

    private void toggleShuffle() {
        if (musicService != null) {
            MusicPlayer player = musicService.getMusicPlayer();
            player.setShuffleMode(!player.isShuffleEnabled());
        }
    }

    private void cycleRepeatMode() {
        if (musicService != null) {
            musicService.getMusicPlayer().cycleRepeatMode();
        }
    }

    private void toggleFavorite() {
        Audio current = musicService != null ?
                musicService.getMusicPlayer().getCurrentAudio() : null;
        if (current != null) {
            repository.toggleFavorite(current);
            updateFavorite(current.getId());
        }
    }

    private void updateShuffleButton(boolean enabled) {
        shuffleButton.setColorFilter(
                enabled ? ContextCompat.getColor(this, R.color.primary)
                        : ContextCompat.getColor(this, R.color.on_surface_variant));
    }

    private void updateRepeatButton(int mode) {
        switch (mode) {
            case androidx.media3.common.Player.REPEAT_MODE_OFF:
                repeatButton.setImageResource(R.drawable.ic_repeat);
                repeatButton.setColorFilter(
                        ContextCompat.getColor(this, R.color.on_surface_variant));
                break;
            case androidx.media3.common.Player.REPEAT_MODE_ALL:
                repeatButton.setImageResource(R.drawable.ic_repeat);
                repeatButton.setColorFilter(
                        ContextCompat.getColor(this, R.color.primary));
                break;
            case androidx.media3.common.Player.REPEAT_MODE_ONE:
                repeatButton.setImageResource(R.drawable.ic_repeat_one);
                repeatButton.setColorFilter(
                        ContextCompat.getColor(this, R.color.primary));
                break;
        }
    }

    private void updateFavorite(long audioId) {
        boolean isFav = repository.isFavorite(audioId);
        if (isFav) {
            favoriteButton.setImageResource(R.drawable.ic_favorite);
            favoriteButton.setColorFilter(
                    ContextCompat.getColor(this, R.color.primary));
        } else {
            favoriteButton.setImageResource(R.drawable.ic_favorite_border);
            favoriteButton.setColorFilter(
                    ContextCompat.getColor(this, R.color.on_surface_variant));
        }
    }

    private void loadMiniPlayerAlbumArt(String albumArtUri) {
        if (albumArtUri == null) {
            miniPlayerAlbumArt.setImageResource(R.drawable.ic_album_default);
            return;
        }
        try {
            Uri uri = Uri.parse(albumArtUri);
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
                if (bitmap != null) {
                    miniPlayerAlbumArt.setImageBitmap(bitmap);
                    return;
                }
            }
        } catch (Exception ignored) {}
        miniPlayerAlbumArt.setImageResource(R.drawable.ic_album_default);
    }

    private void loadNowPlayingAlbumArt(String albumArtUri) {
        if (albumArtUri == null) {
            nowPlayingAlbumArt.setImageResource(R.drawable.ic_album_default);
            return;
        }
        try {
            Uri uri = Uri.parse(albumArtUri);
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
                if (bitmap != null) {
                    nowPlayingAlbumArt.setImageBitmap(bitmap);
                    return;
                }
            }
        } catch (Exception ignored) {}
        nowPlayingAlbumArt.setImageResource(R.drawable.ic_album_default);
    }

    public void playFromSongs(List<Audio> queue, int startIndex) {
        if (musicService != null) {
            musicService.playQueue(queue, startIndex);
        } else {
            pendingQueue = queue;
            pendingStartIndex = startIndex;
        }
    }

    @Override
    public void onTrackChanged(Audio audio, int index) {
        runOnUiThread(this::updateNowPlaying);
    }

    @Override
    public void onPlayStateChanged(boolean isPlaying) {
        runOnUiThread(() -> {
            int icon = isPlaying ? R.drawable.ic_pause : R.drawable.ic_play;
            miniPlayerPlayPause.setImageResource(icon);
            playPauseButton.setImageResource(icon);
        });
    }

    @Override
    public void onProgress(long position, long duration) {}

    @Override
    public void onShuffleModeChanged(boolean enabled) {
        runOnUiThread(() -> updateShuffleButton(enabled));
    }

    @Override
    public void onRepeatModeChanged(int mode) {
        runOnUiThread(() -> updateRepeatButton(mode));
    }

    @Override
    public void onQueueEnded() {
        runOnUiThread(this::updateNowPlaying);
    }

    private void loadFragment(Fragment fragment, String tag) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment, tag);
        transaction.commit();
    }
}

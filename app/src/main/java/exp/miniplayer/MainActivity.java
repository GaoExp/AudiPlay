package exp.miniplayer;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import exp.miniplayer.model.Audio;
import exp.miniplayer.player.MusicPlayer;
import exp.miniplayer.service.MusicService;
import exp.miniplayer.ui.favorites.FavoritesFragment;
import exp.miniplayer.ui.nowplaying.NowPlayingActivity;
import exp.miniplayer.ui.playlist.PlaylistFragment;
import exp.miniplayer.ui.settings.SettingsFragment;
import exp.miniplayer.ui.songs.SongsFragment;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements MusicPlayer.PlayerListener {

    private BottomNavigationView bottomNavigationView;
    private FragmentManager fragmentManager;

    private MusicService musicService;
    private boolean bound = false;
    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicService = binder.getService();
            bound = true;
            musicService.getMusicPlayer().addListener(MainActivity.this);
            updateMiniPlayer();
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

    private ConstraintLayout miniPlayer;
    private ImageView miniPlayerAlbumArt;
    private TextView miniPlayerTitle;
    private TextView miniPlayerArtist;
    private ImageButton miniPlayerPlayPause;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_songs) {
                loadFragment(new SongsFragment(), "songs");
                return true;
            } else if (itemId == R.id.nav_favorites) {
                loadFragment(new FavoritesFragment(), "favorites");
                return true;
            } else if (itemId == R.id.nav_playlists) {
                loadFragment(new PlaylistFragment(), "playlists");
                return true;
            } else if (itemId == R.id.nav_settings) {
                loadFragment(new SettingsFragment(), "settings");
                return true;
            }
            return false;
        });

        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_songs);
        }

        startService(new Intent(this, MusicService.class));

        initMiniPlayer();
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
    }

    private void initMiniPlayer() {
        miniPlayer = findViewById(R.id.mini_player);
        miniPlayerAlbumArt = findViewById(R.id.mini_player_album_art);
        miniPlayerTitle = findViewById(R.id.mini_player_title);
        miniPlayerArtist = findViewById(R.id.mini_player_artist);
        miniPlayerPlayPause = findViewById(R.id.mini_player_play_pause);

        miniPlayer.setOnClickListener(v -> openNowPlaying());
        miniPlayerPlayPause.setOnClickListener(v -> {
            if (musicService != null) {
                musicService.getMusicPlayer().togglePlayPause();
            }
        });
    }

    private void updateMiniPlayer() {
        if (musicService == null) return;
        MusicPlayer player = musicService.getMusicPlayer();
        Audio current = player.getCurrentAudio();
        if (current != null) {
            miniPlayerTitle.setText(current.getTitle());
            miniPlayerArtist.setText(current.getArtist());
            loadMiniPlayerAlbumArt(current.getAlbumArt());
            miniPlayerPlayPause.setImageResource(
                    player.isPlaying() ? R.drawable.ic_pause : R.drawable.ic_play);
            miniPlayer.setVisibility(View.VISIBLE);
        } else {
            miniPlayer.setVisibility(View.GONE);
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

    private void openNowPlaying() {
        Intent intent = new Intent(this, NowPlayingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    @Override
    public void onTrackChanged(Audio audio, int index) {
        runOnUiThread(this::updateMiniPlayer);
    }

    @Override
    public void onPlayStateChanged(boolean isPlaying) {
        runOnUiThread(() -> {
            if (miniPlayer.getVisibility() == View.VISIBLE) {
                miniPlayerPlayPause.setImageResource(
                        isPlaying ? R.drawable.ic_pause : R.drawable.ic_play);
            }
        });
    }

    @Override
    public void onProgress(long position, long duration) {}

    @Override
    public void onShuffleModeChanged(boolean enabled) {}

    @Override
    public void onRepeatModeChanged(int mode) {}

    @Override
    public void onQueueEnded() {
        runOnUiThread(() -> miniPlayer.setVisibility(View.GONE));
    }

    private void loadFragment(Fragment fragment, String tag) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment, tag);
        transaction.commit();
    }
}

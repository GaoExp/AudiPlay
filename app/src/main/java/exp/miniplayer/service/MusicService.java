package exp.miniplayer.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.media3.common.Player;
import androidx.media3.session.MediaSession;

import exp.miniplayer.MainActivity;
import exp.miniplayer.R;
import exp.miniplayer.model.Audio;
import exp.miniplayer.player.MusicPlayer;

import exp.miniplayer.utils.PermissionHelper;
import exp.miniplayer.utils.PreferencesManager;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MusicService extends android.app.Service implements
        AudioManager.OnAudioFocusChangeListener, MusicPlayer.PlayerListener {

    private static final String CHANNEL_ID = "playback_channel";
    private static final int NOTIFICATION_ID = 1;
    public static final String ACTION_PLAY = "exp.miniplayer.action.PLAY";
    public static final String ACTION_PAUSE = "exp.miniplayer.action.PAUSE";
    public static final String ACTION_NEXT = "exp.miniplayer.action.NEXT";
    public static final String ACTION_PREVIOUS = "exp.miniplayer.action.PREVIOUS";

    private MusicPlayer musicPlayer;
    private AudioManager audioManager;
    private AudioFocusRequest audioFocusRequest;
    private MediaSession mediaSession;
    private final IBinder binder = new MusicBinder();
    private boolean isForeground = false;
    private PreferencesManager prefs;

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        musicPlayer = new MusicPlayer(this);
        musicPlayer.addListener(this);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        prefs = new PreferencesManager(this);
        restorePlayerState();
        createNotificationChannel();
        registerBecomingNoisyReceiver();
        setupMediaSession();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build())
                    .setOnAudioFocusChangeListener(this)
                    .build();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case ACTION_PLAY:
                        musicPlayer.play();
                        break;
                    case ACTION_PAUSE:
                        musicPlayer.pause();
                        break;
                    case ACTION_NEXT:
                        musicPlayer.next();
                        break;
                    case ACTION_PREVIOUS:
                        musicPlayer.previous();
                        break;
                }
            }
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public MusicPlayer getMusicPlayer() {
        return musicPlayer;
    }

    public void playQueue(List<Audio> queue, int startIndex) {
        if (!isForeground) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
                    || checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED) {
                try {
                    startForeground(NOTIFICATION_ID, buildBasicNotification());
                    isForeground = true;
                } catch (Exception ignored) {}
            }
        }
        requestAudioFocus();
        musicPlayer.setQueue(queue, startIndex);
        musicPlayer.play();
    }

    private void requestAudioFocus() {
        if (prefs.isPlayOverOtherApps()) {
            abandonAudioFocus();
            return;
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                audioManager.requestAudioFocus(audioFocusRequest);
            } else {
                audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void abandonAudioFocus() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && audioFocusRequest != null) {
                audioManager.abandonAudioFocusRequest(audioFocusRequest);
            } else {
                audioManager.abandonAudioFocus(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS:
                if (musicPlayer != null) musicPlayer.pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if (musicPlayer != null) musicPlayer.pause();
                break;
            case AudioManager.AUDIOFOCUS_GAIN:
                break;
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    getString(R.string.channel_playback),
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Playback controls");
            channel.setShowBadge(false);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void updateNotification() {
        Audio current = musicPlayer.getCurrentAudio();
        if (current == null) return;

        Intent nowPlayingIntent = new Intent(this, MainActivity.class);
        nowPlayingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(
                this, 0, nowPlayingIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent playPauseIntent = new Intent(this, MusicService.class);
        playPauseIntent.setAction(musicPlayer.isPlaying() ? ACTION_PAUSE : ACTION_PLAY);
        PendingIntent playPausePending = PendingIntent.getService(
                this, 1, playPauseIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent nextIntent = new Intent(this, MusicService.class);
        nextIntent.setAction(ACTION_NEXT);
        PendingIntent nextPending = PendingIntent.getService(
                this, 2, nextIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent prevIntent = new Intent(this, MusicService.class);
        prevIntent.setAction(ACTION_PREVIOUS);
        PendingIntent prevPending = PendingIntent.getService(
                this, 3, prevIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Bitmap albumArt = getAlbumArtBitmap(current.getAlbumArt());

        try {
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle(current.getTitle())
                    .setContentText(current.getArtist())
                    .setSmallIcon(R.drawable.ic_music_note)
                    .setLargeIcon(albumArt)
                    .setContentIntent(contentIntent)
                    .setOngoing(true)
                    .setShowWhen(false)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            .setMediaSession(mediaSession != null ?
                                    mediaSession.getSessionCompatToken() : null)
                            .setShowActionsInCompactView(0, 1, 2))
                    .addAction(R.drawable.ic_skip_previous, "Previous", prevPending)
                    .addAction(musicPlayer.isPlaying() ? R.drawable.ic_pause : R.drawable.ic_play,
                            musicPlayer.isPlaying() ? "Pause" : "Play", playPausePending)
                    .addAction(R.drawable.ic_skip_next, "Next", nextPending)
                    .build();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                    && checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            startForeground(NOTIFICATION_ID, notification);
            isForeground = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap getAlbumArtBitmap(String albumArtUri) {
        if (albumArtUri == null) return null;
        try {
            Uri uri = Uri.parse(albumArtUri);
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
                return bitmap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setupMediaSession() {
        try {
            mediaSession = new MediaSession.Builder(this, musicPlayer.getExoPlayer())
                    .setSessionActivity(PendingIntent.getActivity(this, 0,
                            new Intent(this, MainActivity.class),
                            PendingIntent.FLAG_IMMUTABLE))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                if (musicPlayer != null && musicPlayer.isPlaying()) {
                    musicPlayer.pause();
                }
            }
        }
    };

    private void registerBecomingNoisyReceiver() {
        try {
            registerReceiver(becomingNoisyReceiver,
                    new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTrackChanged(Audio audio, int index) {
        if (audio != null) {
            prefs.saveLastPlayedTrack(audio);
        }
        updateNotification();
    }

    @Override
    public void onPlayStateChanged(boolean isPlaying) {
        if (isPlaying) {
            if (!isForeground) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                        && checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                try {
                    startForeground(NOTIFICATION_ID, buildBasicNotification());
                    isForeground = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
            requestAudioFocus();
        }
        updateNotification();
    }

    @Override
    public void onProgress(long position, long duration) {}

    @Override
    public void onShuffleModeChanged(boolean enabled) {}

    @Override
    public void onRepeatModeChanged(int mode) {}

    @Override
    public void onQueueEnded() {
        Audio current = musicPlayer.getCurrentAudio();
        if (current != null) {
            prefs.saveLastPlayedTrack(current);
        }
    }

    private Notification buildBasicNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.now_playing))
                .setSmallIcon(R.drawable.ic_music_note)
                .setOngoing(true)
                .setShowWhen(false)
                .build();
    }

    private void restorePlayerState() {
        if (musicPlayer.getCurrentAudio() != null) return;
        Audio lastTrack = prefs.getLastPlayedTrack();
        if (lastTrack != null) {
            List<Audio> singleQueue = new ArrayList<>();
            singleQueue.add(lastTrack);
            musicPlayer.setQueue(singleQueue, 0);
        }
    }

    @Override
    public void onDestroy() {
        if (isForeground) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                stopForeground(STOP_FOREGROUND_REMOVE);
            }
            isForeground = false;
        }
        if (mediaSession != null) {
            mediaSession.release();
        }
        if (musicPlayer != null) {
            musicPlayer.removeListener(this);
            musicPlayer.release();
        }
        try {
            unregisterReceiver(becomingNoisyReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}

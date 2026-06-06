package exp.miniplayer.ui.nowplaying;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import exp.miniplayer.data.AudioRepository;
import exp.miniplayer.model.Audio;
import exp.miniplayer.player.MusicPlayer;
import exp.miniplayer.service.MusicService;

import java.util.List;

public class NowPlayingViewModel extends AndroidViewModel implements MusicPlayer.PlayerListener {

    private MusicService musicService;
    private boolean bound = false;
    private final MutableLiveData<Audio> currentAudio;
    private final MutableLiveData<Boolean> isPlaying;
    private final MutableLiveData<Long> currentPosition;
    private final MutableLiveData<Long> duration;
    private final MutableLiveData<Boolean> shuffleEnabled;
    private final MutableLiveData<Integer> repeatMode;
    private final MutableLiveData<Boolean> isFavorite;
    private final Handler progressHandler;
    private Runnable progressRunnable;
    private final AudioRepository repository;
    private final ServiceConnection connection;

    private List<Audio> pendingQueue;
    private int pendingStartIndex = -1;

    public NowPlayingViewModel(@NonNull Application application) {
        super(application);
        currentAudio = new MutableLiveData<>();
        isPlaying = new MutableLiveData<>(false);
        currentPosition = new MutableLiveData<>(0L);
        duration = new MutableLiveData<>(0L);
        shuffleEnabled = new MutableLiveData<>(false);
        repeatMode = new MutableLiveData<>(0);
        isFavorite = new MutableLiveData<>(false);
        repository = new AudioRepository(application);
        progressHandler = new Handler(Looper.getMainLooper());

        progressRunnable = new Runnable() {
            @Override
            public void run() {
                if (musicService != null && musicService.getMusicPlayer() != null) {
                    MusicPlayer player = musicService.getMusicPlayer();
                    currentPosition.postValue(player.getCurrentPosition());
                    duration.postValue(player.getDuration());
                    isPlaying.postValue(player.isPlaying());
                }
                progressHandler.postDelayed(this, 500);
            }
        };

        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
                musicService = binder.getService();
                bound = true;
                musicService.getMusicPlayer().addListener(NowPlayingViewModel.this);
                Audio current = musicService.getMusicPlayer().getCurrentAudio();
                if (current != null) {
                    currentAudio.postValue(current);
                    isFavorite.postValue(repository.isFavorite(current.getId()));
                }
                isPlaying.postValue(musicService.getMusicPlayer().isPlaying());
                shuffleEnabled.postValue(musicService.getMusicPlayer().isShuffleEnabled());
                repeatMode.postValue(musicService.getMusicPlayer().getRepeatMode());

                if (pendingQueue != null && pendingStartIndex >= 0) {
                    musicService.playQueue(pendingQueue, pendingStartIndex);
                    pendingQueue = null;
                    pendingStartIndex = -1;
                }

                startProgressUpdates();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                musicService = null;
                bound = false;
            }
        };
    }

    public void bindService(Context context) {
        Intent intent = new Intent(context, MusicService.class);
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    public void unbindService(Context context) {
        if (bound) {
            try {
                context.unbindService(connection);
            } catch (Exception ignored) {}
            bound = false;
        }
        stopProgressUpdates();
        if (musicService != null) {
            musicService.getMusicPlayer().removeListener(this);
        }
    }

    public void playQueue(List<Audio> queue, int startIndex) {
        if (musicService != null) {
            musicService.playQueue(queue, startIndex);
            pendingQueue = null;
            pendingStartIndex = -1;
        } else {
            pendingQueue = queue;
            pendingStartIndex = startIndex;
        }
    }

    public void togglePlayPause() {
        if (musicService != null) {
            musicService.getMusicPlayer().togglePlayPause();
        }
    }

    public void next() {
        if (musicService != null) {
            musicService.getMusicPlayer().next();
        }
    }

    public void previous() {
        if (musicService != null) {
            musicService.getMusicPlayer().previous();
        }
    }

    public void seekTo(long position) {
        if (musicService != null) {
            musicService.getMusicPlayer().seekTo(position);
        }
    }

    public void toggleShuffle() {
        if (musicService != null) {
            MusicPlayer player = musicService.getMusicPlayer();
            player.setShuffleMode(!player.isShuffleEnabled());
        }
    }

    public void cycleRepeatMode() {
        if (musicService != null) {
            musicService.getMusicPlayer().cycleRepeatMode();
        }
    }

    public LiveData<Audio> getCurrentAudio() { return currentAudio; }
    public LiveData<Boolean> getIsPlaying() { return isPlaying; }
    public LiveData<Long> getCurrentPosition() { return currentPosition; }
    public LiveData<Long> getDuration() { return duration; }
    public LiveData<Boolean> getShuffleEnabled() { return shuffleEnabled; }
    public LiveData<Integer> getRepeatMode() { return repeatMode; }
    public LiveData<Boolean> getIsFavorite() { return isFavorite; }

    public boolean isFavorite(long audioId) {
        return repository.isFavorite(audioId);
    }

    public void toggleFavorite(Audio audio) {
        repository.toggleFavorite(audio);
        isFavorite.postValue(repository.isFavorite(audio.getId()));
    }

    public AudioRepository getRepository() { return repository; }

    private void startProgressUpdates() {
        progressHandler.post(progressRunnable);
    }

    private void stopProgressUpdates() {
        progressHandler.removeCallbacks(progressRunnable);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        stopProgressUpdates();
    }

    @Override
    public void onTrackChanged(Audio audio, int index) {
        currentAudio.postValue(audio);
        duration.postValue(audio != null ? audio.getDuration() : 0L);
        if (audio != null) {
            isFavorite.postValue(repository.isFavorite(audio.getId()));
        }
    }

    @Override
    public void onPlayStateChanged(boolean playing) {
        isPlaying.postValue(playing);
    }

    @Override
    public void onProgress(long position, long duration1) {
        currentPosition.postValue(position);
        duration.postValue(duration1);
    }

    @Override
    public void onShuffleModeChanged(boolean enabled) {
        shuffleEnabled.postValue(enabled);
    }

    @Override
    public void onRepeatModeChanged(int mode) {
        repeatMode.postValue(mode);
    }

    @Override
    public void onQueueEnded() {
        isPlaying.postValue(false);
        currentPosition.postValue(0L);
        duration.postValue(0L);
    }
}

package exp.miniplayer.player;

import android.content.Context;
import android.net.Uri;

import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;

import exp.miniplayer.model.Audio;

import java.util.ArrayList;
import java.util.List;

public class MusicPlayer {

    private final Context context;
    private ExoPlayer exoPlayer;
    private List<Audio> queue;
    private int currentIndex;
    private final List<PlayerListener> listeners;

    public interface PlayerListener {
        void onTrackChanged(Audio audio, int index);
        void onPlayStateChanged(boolean isPlaying);
        void onProgress(long position, long duration);
        void onShuffleModeChanged(boolean enabled);
        void onRepeatModeChanged(int mode);
        void onQueueEnded();
    }

    public MusicPlayer(Context context) {
        this.context = context.getApplicationContext();
        this.queue = new ArrayList<>();
        this.currentIndex = -1;
        this.listeners = new ArrayList<>();
        initializePlayer();
    }

    private void initializePlayer() {
        exoPlayer = new ExoPlayer.Builder(context).build();
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                notifyPlayStateChanged(isPlaying);
            }

            @Override
            public void onMediaItemTransition(MediaItem mediaItem, int reason) {
                int newIndex = exoPlayer.getCurrentMediaItemIndex();
                if (newIndex >= 0 && newIndex < queue.size()) {
                    currentIndex = newIndex;
                    notifyTrackChanged(queue.get(currentIndex), currentIndex);
                }
            }

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_ENDED) {
                    notifyQueueEnded();
                }
            }
        });
    }

    public void setQueue(List<Audio> audioList, int startIndex) {
        if (audioList == null || audioList.isEmpty()) return;

        queue = new ArrayList<>(audioList);

        List<MediaItem> mediaItems = new ArrayList<>();
        for (Audio audio : audioList) {
            Uri audioUri;
            try {
                audioUri = Uri.parse(audio.getUri());
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            MediaItem mediaItem = new MediaItem.Builder()
                    .setUri(audioUri)
                    .setMediaMetadata(new MediaMetadata.Builder()
                            .setTitle(audio.getTitle())
                            .setArtist(audio.getArtist())
                            .build())
                    .build();
            mediaItems.add(mediaItem);
        }

        if (mediaItems.isEmpty()) return;

        try {
            exoPlayer.stop();
            exoPlayer.clearMediaItems();
            exoPlayer.setMediaItems(mediaItems, Math.max(0, startIndex), 0);
            exoPlayer.prepare();
            currentIndex = Math.max(0, startIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void play() {
        try {
            exoPlayer.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        try {
            exoPlayer.pause();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void togglePlayPause() {
        if (exoPlayer.isPlaying()) {
            exoPlayer.pause();
        } else {
            exoPlayer.play();
        }
    }

    public void next() {
        try {
            exoPlayer.seekToNextMediaItem();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void previous() {
        try {
            long currentPos = exoPlayer.getCurrentPosition();
            if (currentPos > 10000) {
                exoPlayer.seekTo(0);
            } else {
                exoPlayer.seekToPreviousMediaItem();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void seekTo(long position) {
        try {
            exoPlayer.seekTo(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isPlaying() {
        return exoPlayer.isPlaying();
    }

    public Audio getCurrentAudio() {
        if (currentIndex >= 0 && currentIndex < queue.size()) {
            return queue.get(currentIndex);
        }
        return null;
    }

    public long getCurrentPosition() {
        return exoPlayer.getCurrentPosition();
    }

    public long getDuration() {
        return exoPlayer.getDuration();
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public List<Audio> getQueue() {
        return new ArrayList<>(queue);
    }

    public void setShuffleMode(boolean enabled) {
        exoPlayer.setShuffleModeEnabled(enabled);
        notifyShuffleModeChanged(enabled);
    }

    public boolean isShuffleEnabled() {
        return exoPlayer.getShuffleModeEnabled();
    }

    public void cycleRepeatMode() {
        int currentMode = exoPlayer.getRepeatMode();
        int newMode;
        switch (currentMode) {
            case Player.REPEAT_MODE_OFF:
                newMode = Player.REPEAT_MODE_ALL;
                break;
            case Player.REPEAT_MODE_ALL:
                newMode = Player.REPEAT_MODE_ONE;
                break;
            case Player.REPEAT_MODE_ONE:
            default:
                newMode = Player.REPEAT_MODE_OFF;
                break;
        }
        exoPlayer.setRepeatMode(newMode);
        notifyRepeatModeChanged(newMode);
    }

    public int getRepeatMode() {
        return exoPlayer.getRepeatMode();
    }

    public void setRepeatMode(int mode) {
        exoPlayer.setRepeatMode(mode);
        notifyRepeatModeChanged(mode);
    }

    public void addListener(PlayerListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(PlayerListener listener) {
        listeners.remove(listener);
    }

    public void release() {
        try {
            exoPlayer.stop();
            exoPlayer.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ExoPlayer getExoPlayer() {
        return exoPlayer;
    }

    private void notifyTrackChanged(Audio audio, int index) {
        for (PlayerListener listener : listeners) {
            try { listener.onTrackChanged(audio, index); } catch (Exception ignored) {}
        }
    }

    private void notifyPlayStateChanged(boolean isPlaying) {
        for (PlayerListener listener : listeners) {
            try { listener.onPlayStateChanged(isPlaying); } catch (Exception ignored) {}
        }
    }

    private void notifyShuffleModeChanged(boolean enabled) {
        for (PlayerListener listener : listeners) {
            try { listener.onShuffleModeChanged(enabled); } catch (Exception ignored) {}
        }
    }

    private void notifyRepeatModeChanged(int mode) {
        for (PlayerListener listener : listeners) {
            try { listener.onRepeatModeChanged(mode); } catch (Exception ignored) {}
        }
    }

    private void notifyQueueEnded() {
        for (PlayerListener listener : listeners) {
            try { listener.onQueueEnded(); } catch (Exception ignored) {}
        }
    }
}

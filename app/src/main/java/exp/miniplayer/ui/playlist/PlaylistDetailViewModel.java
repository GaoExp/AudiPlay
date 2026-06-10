package exp.miniplayer.ui.playlist;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import exp.miniplayer.data.AudioRepository;
import exp.miniplayer.database.PlaylistSongEntity;

import java.util.List;

public class PlaylistDetailViewModel extends AndroidViewModel {

    private final AudioRepository repository;

    public PlaylistDetailViewModel(@NonNull Application application) {
        super(application);
        repository = new AudioRepository(application);
    }

    public LiveData<List<PlaylistSongEntity>> getPlaylistSongs(int playlistId) {
        return repository.getPlaylistSongs(playlistId);
    }

    public void removeFromPlaylist(int playlistId, long audioId) {
        repository.removeFromPlaylist(playlistId, audioId);
    }

    public AudioRepository getRepository() {
        return repository;
    }
}

package exp.miniplayer.ui.playlist;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import exp.miniplayer.data.AudioRepository;
import exp.miniplayer.database.PlaylistEntity;

import java.util.List;

public class PlaylistViewModel extends AndroidViewModel {

    private final AudioRepository repository;

    public PlaylistViewModel(@NonNull Application application) {
        super(application);
        repository = new AudioRepository(application);
    }

    public LiveData<List<PlaylistEntity>> getPlaylists() {
        return repository.getPlaylists();
    }

    public long createPlaylist(String name) {
        return repository.createPlaylist(name);
    }

    public void renamePlaylist(int id, String name) {
        repository.renamePlaylist(id, name);
    }

    public void deletePlaylist(int id) {
        repository.deletePlaylist(id);
    }

    public AudioRepository getRepository() {
        return repository;
    }
}

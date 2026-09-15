package exp.miniplayer.ui.songs;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import exp.miniplayer.data.AudioRepository;
import exp.miniplayer.model.Audio;

import java.util.List;

public class SongsViewModel extends AndroidViewModel {

    private final AudioRepository repository;
    private final MutableLiveData<List<Audio>> songs;
    private final MutableLiveData<String> searchQuery;
    private boolean scanned = false;

    public SongsViewModel(@NonNull Application application) {
        super(application);
        repository = new AudioRepository(application);
        songs = new MutableLiveData<>();
        searchQuery = new MutableLiveData<>("");
    }

    public void scanAudio() {
        if (!scanned) {
            scanned = true;
            new Thread(() -> {
                List<Audio> audioList = repository.scanAudio();
                songs.postValue(audioList);
            }).start();
        }
    }

    public void refresh() {
        scanned = false;
        scanAudio();
    }

    public LiveData<List<Audio>> getSongs() {
        return songs;
    }

    public void search(String query) {
        new Thread(() -> {
            searchQuery.postValue(query);
            List<Audio> results = repository.searchAudio(query);
            songs.postValue(results);
        }).start();
    }

    public void sort(int sortMode) {
        new Thread(() -> {
            repository.sortAudioList(sortMode);
            List<Audio> allAudio = repository.getCachedAudio();
            String query = searchQuery.getValue();
            if (query != null && !query.trim().isEmpty()) {
                songs.postValue(repository.searchAudio(query));
            } else {
                songs.postValue(allAudio);
            }
        }).start();
    }

    public AudioRepository getRepository() {
        return repository;
    }
}

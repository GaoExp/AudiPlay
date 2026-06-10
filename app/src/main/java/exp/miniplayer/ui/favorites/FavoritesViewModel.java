package exp.miniplayer.ui.favorites;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import exp.miniplayer.data.AudioRepository;
import exp.miniplayer.database.FavoriteEntity;
import exp.miniplayer.model.Audio;

import java.util.ArrayList;
import java.util.List;

public class FavoritesViewModel extends AndroidViewModel {

    private final AudioRepository repository;
    private final LiveData<List<FavoriteEntity>> favoriteEntities;

    public FavoritesViewModel(@NonNull Application application) {
        super(application);
        repository = new AudioRepository(application);
        favoriteEntities = repository.getFavorites();
    }

    public LiveData<List<FavoriteEntity>> getFavoriteEntities() {
        return favoriteEntities;
    }

    public void removeFavorite(long audioId) {
        repository.removeFavorite(audioId);
    }

    public boolean isFavorite(long audioId) {
        return repository.isFavorite(audioId);
    }

    public AudioRepository getRepository() {
        return repository;
    }
}

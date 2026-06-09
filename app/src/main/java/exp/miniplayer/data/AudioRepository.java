package exp.miniplayer.data;

import android.content.Context;

import androidx.lifecycle.LiveData;

import exp.miniplayer.database.AppDatabase;
import exp.miniplayer.database.FavoriteDao;
import exp.miniplayer.database.FavoriteEntity;
import exp.miniplayer.database.PlaylistDao;
import exp.miniplayer.database.PlaylistEntity;
import exp.miniplayer.database.PlaylistSongDao;
import exp.miniplayer.database.PlaylistSongEntity;
import exp.miniplayer.model.Audio;
import exp.miniplayer.utils.MusicScanner;
import exp.miniplayer.utils.PlaylistScanner;
import exp.miniplayer.utils.PreferencesManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AudioRepository {
    private static boolean forceRescan = false;

    private final Context context;
    private final AppDatabase database;
    private final FavoriteDao favoriteDao;
    private final PlaylistDao playlistDao;
    private final PlaylistSongDao playlistSongDao;
    private final PreferencesManager prefs;

    private List<Audio> cachedAudioList;
    private int currentSortMode;

    public AudioRepository(Context context) {
        this.context = context.getApplicationContext();
        this.database = AppDatabase.getInstance(context);
        this.favoriteDao = database.favoriteDao();
        this.playlistDao = database.playlistDao();
        this.playlistSongDao = database.playlistSongDao();
        this.prefs = new PreferencesManager(context);
        this.cachedAudioList = new ArrayList<>();
        this.currentSortMode = prefs.getSortMode();
    }

    public static void triggerRescan() {
        forceRescan = true;
    }

    public List<Audio> scanAudio() {
        cachedAudioList = MusicScanner.scanAudio(context, prefs);
        sortAudioList(currentSortMode);
        forceRescan = false;
        PlaylistScanner.scanPlaylists(context, this, cachedAudioList);
        return cachedAudioList;
    }

    public List<Audio> getCachedAudio() {
        if (cachedAudioList.isEmpty() || forceRescan) {
            scanAudio();
        }
        return cachedAudioList;
    }

    public List<Audio> searchAudio(String query) {
        List<Audio> allAudio = getCachedAudio();
        if (query == null || query.trim().isEmpty()) {
            return allAudio;
        }
        String lowerQuery = query.toLowerCase().trim();
        List<Audio> results = new ArrayList<>();
        for (Audio audio : allAudio) {
            if (audio.getTitle().toLowerCase().contains(lowerQuery)
                    || audio.getArtist().toLowerCase().contains(lowerQuery)
                    || audio.getAlbum().toLowerCase().contains(lowerQuery)) {
                results.add(audio);
            }
        }
        return results;
    }

    public void sortAudioList(int sortMode) {
        currentSortMode = sortMode;
        prefs.setSortMode(sortMode);
        switch (sortMode) {
            case 0:
                Collections.sort(cachedAudioList, (a, b) ->
                        a.getTitle().compareToIgnoreCase(b.getTitle()));
                break;
            case 1:
                Collections.sort(cachedAudioList, (a, b) ->
                        b.getTitle().compareToIgnoreCase(a.getTitle()));
                break;
            case 2:
                Collections.sort(cachedAudioList, (a, b) ->
                        Long.compare(a.getDuration(), b.getDuration()));
                break;
            case 3:
                Collections.sort(cachedAudioList, (a, b) ->
                        Long.compare(b.getDateAdded(), a.getDateAdded()));
                break;
        }
    }

    public List<Audio> getAudioForMediaItems(List<Audio> fullList, List<Integer> indices) {
        List<Audio> result = new ArrayList<>();
        for (int index : indices) {
            if (index >= 0 && index < fullList.size()) {
                result.add(fullList.get(index));
            }
        }
        return result;
    }

    public boolean isFavorite(long audioId) {
        return favoriteDao.isFavorite(audioId);
    }

    public void toggleFavorite(Audio audio) {
        if (favoriteDao.isFavorite(audio.getId())) {
            favoriteDao.deleteByAudioId(audio.getId());
        } else {
            FavoriteEntity entity = new FavoriteEntity(
                    audio.getId(), audio.getTitle(), audio.getArtist(),
                    audio.getAlbum(), audio.getDuration(), audio.getUri(),
                    audio.getAlbumArt(), System.currentTimeMillis());
            favoriteDao.insert(entity);
        }
    }

    public void addFavorite(Audio audio) {
        if (!favoriteDao.isFavorite(audio.getId())) {
            FavoriteEntity entity = new FavoriteEntity(
                    audio.getId(), audio.getTitle(), audio.getArtist(),
                    audio.getAlbum(), audio.getDuration(), audio.getUri(),
                    audio.getAlbumArt(), System.currentTimeMillis());
            favoriteDao.insert(entity);
        }
    }

    public void removeFavorite(long audioId) {
        favoriteDao.deleteByAudioId(audioId);
    }

    public LiveData<List<FavoriteEntity>> getFavorites() {
        return favoriteDao.getAllFavorites();
    }

    public List<FavoriteEntity> getFavoritesSync() {
        return favoriteDao.getAllFavorites().getValue();
    }

    public LiveData<List<PlaylistEntity>> getPlaylists() {
        return playlistDao.getAllPlaylists();
    }

    public List<PlaylistEntity> getPlaylistsSync() {
        return playlistDao.getAllPlaylistsSync();
    }

    public long createPlaylist(String name) {
        PlaylistEntity entity = new PlaylistEntity(name, System.currentTimeMillis());
        return playlistDao.insert(entity);
    }

    public void renamePlaylist(int id, String name) {
        playlistDao.rename(id, name);
    }

    public void deletePlaylist(int id) {
        playlistDao.deleteById(id);
        playlistSongDao.deleteAllForPlaylist(id);
    }

    public PlaylistEntity getPlaylist(int id) {
        return playlistDao.getPlaylist(id);
    }

    public PlaylistEntity findPlaylistByName(String name) {
        return playlistDao.findByName(name);
    }

    public boolean addToPlaylist(int playlistId, Audio audio) {
        if (playlistSongDao.exists(playlistId, audio.getId())) {
            return false;
        }
        int sortOrder = playlistSongDao.getMaxSortOrder(playlistId) + 1;
        PlaylistSongEntity entity = new PlaylistSongEntity(
                playlistId, audio.getId(), audio.getTitle(), audio.getArtist(),
                audio.getAlbum(), audio.getDuration(), audio.getUri(),
                audio.getAlbumArt(), sortOrder, System.currentTimeMillis());
        playlistSongDao.insert(entity);
        return true;
    }

    public void removeFromPlaylist(int playlistId, long audioId) {
        playlistSongDao.deleteByPlaylistAndAudio(playlistId, audioId);
    }

    public LiveData<List<PlaylistSongEntity>> getPlaylistSongs(int playlistId) {
        return playlistSongDao.getSongsForPlaylist(playlistId);
    }

    public int getPlaylistSongCount(int playlistId) {
        return playlistSongDao.getSongCount(playlistId);
    }

    public List<PlaylistSongEntity> getPlaylistSongsSync(int playlistId) {
        return playlistSongDao.getSongsForPlaylistSync(playlistId);
    }

    public PlaylistSongDao getPlaylistSongDao() {
        return playlistSongDao;
    }
}

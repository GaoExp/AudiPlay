package exp.miniplayer.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PlaylistSongDao {
    @Query("SELECT * FROM playlist_songs WHERE playlistId = :playlistId ORDER BY sortOrder ASC")
    LiveData<List<PlaylistSongEntity>> getSongsForPlaylist(int playlistId);

    @Query("SELECT * FROM playlist_songs WHERE playlistId = :playlistId ORDER BY sortOrder ASC")
    List<PlaylistSongEntity> getSongsForPlaylistSync(int playlistId);

    @Query("SELECT COUNT(*) FROM playlist_songs WHERE playlistId = :playlistId")
    int getSongCount(int playlistId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(PlaylistSongEntity song);

    @Delete
    void delete(PlaylistSongEntity song);

    @Query("DELETE FROM playlist_songs WHERE id = :id")
    void deleteById(int id);

    @Query("DELETE FROM playlist_songs WHERE playlistId = :playlistId AND audioId = :audioId")
    void deleteByPlaylistAndAudio(int playlistId, long audioId);

    @Query("DELETE FROM playlist_songs WHERE playlistId = :playlistId")
    void deleteAllForPlaylist(int playlistId);

    @Query("SELECT MAX(sortOrder) FROM playlist_songs WHERE playlistId = :playlistId")
    int getMaxSortOrder(int playlistId);

    @Query("SELECT EXISTS(SELECT 1 FROM playlist_songs WHERE playlistId = :playlistId AND audioId = :audioId)")
    boolean exists(int playlistId, long audioId);
}

package exp.miniplayer.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PlaylistDao {
    @Query("SELECT * FROM playlists ORDER BY createdAt DESC")
    LiveData<List<PlaylistEntity>> getAllPlaylists();

    @Query("SELECT * FROM playlists ORDER BY createdAt DESC")
    List<PlaylistEntity> getAllPlaylistsSync();

    @Query("SELECT * FROM playlists WHERE id = :id LIMIT 1")
    PlaylistEntity getPlaylist(int id);

    @Query("SELECT * FROM playlists WHERE name = :name LIMIT 1")
    PlaylistEntity findByName(String name);

    @Insert
    long insert(PlaylistEntity playlist);

    @Update
    void update(PlaylistEntity playlist);

    @Delete
    void delete(PlaylistEntity playlist);

    @Query("DELETE FROM playlists WHERE id = :id")
    void deleteById(int id);

    @Query("UPDATE playlists SET name = :name WHERE id = :id")
    void rename(int id, String name);
}

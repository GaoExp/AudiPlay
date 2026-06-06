package exp.miniplayer.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FavoriteDao {
    @Query("SELECT * FROM favorites ORDER BY addedAt DESC")
    LiveData<List<FavoriteEntity>> getAllFavorites();

    @Query("SELECT * FROM favorites WHERE audioId = :audioId LIMIT 1")
    FavoriteEntity getFavorite(long audioId);

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE audioId = :audioId)")
    boolean isFavorite(long audioId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FavoriteEntity favorite);

    @Delete
    void delete(FavoriteEntity favorite);

    @Query("DELETE FROM favorites WHERE audioId = :audioId")
    void deleteByAudioId(long audioId);

    @Query("DELETE FROM favorites")
    void deleteAll();
}
